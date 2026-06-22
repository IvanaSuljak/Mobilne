# VEZBA 8 — Lociranje i mape — Kompletna dokumentacija

---

## Sadržaj

1. [Šta je FusedLocationProviderClient?](#1-šta-je-fusedlocationproviderclient)
2. [Podešavanje — build.gradle i API ključ](#2-podešavanje)
3. [Dozvole za lokaciju](#3-dozvole-za-lokaciju)
4. [Google Maps — SupportMapFragment i OnMapReadyCallback](#4-google-maps)
5. [Dohvatanje lokacije — getLastLocation](#5-dohvatanje-lokacije)
6. [Realtime praćenje — requestLocationUpdates](#6-realtime-praćenje)
7. [Markeri i kamera](#7-markeri-i-kamera)
8. [Geocoding — koordinate u adresu](#8-geocoding)
9. [Kreirani fajlovi](#9-kreirani-fajlovi)

---

## 1. Šta je FusedLocationProviderClient?

`FusedLocationProviderClient` je Google-ov API koji **kombinuje** više izvora lokacije:

| Izvor | Tačnost | Potrošnja baterije |
|-------|---------|-------------------|
| GPS | Visoka (~5m) | Visoka |
| WiFi | Srednja (~50m) | Srednja |
| Mobilna mreža | Niska (~300m) | Niska |

Fused automatski bira **najtačniji dostupni izvor** u datom trenutku.

```
Fused Location Provider
    ├── GPS sateliti          → najtačnije, sporo uključivanje
    ├── WiFi lokacija         → brzo, umereno tačno
    └── Mobilna mreža / BLE   → najbrže, najmanje tačno
         ↓
    Daje jednu "fusovanu" lokaciju — ti ne brineš koji izvor se koristi
```

---

## 2. Podešavanje

### build.gradle

```groovy
// Google Maps SDK
implementation 'com.google.android.gms:play-services-maps:18.2.0'
// Fused Location Provider
implementation 'com.google.android.gms:play-services-location:21.2.0'
```

### Google Maps API ključ

**OBAVEZNO** — bez API ključa mapa se neće prikazati (siva ili greška).

**Koraci za dobijanje ključa:**
1. Idi na [console.cloud.google.com](https://console.cloud.google.com)
2. Napravi novi projekat (ili koristi postojeći)
3. Idi na: **APIs & Services → Enable APIs → Maps SDK for Android** → Uključi
4. Idi na: **APIs & Services → Credentials → Create Credentials → API Key**
5. Kopiraj ključ

**Upiši ključ u `AndroidManifest.xml`** (unutar `<application>`):
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSy_TVOJ_KLJUC_OVDE" />
```

---

## 3. Dozvole za lokaciju

### AndroidManifest.xml

```xml
<!-- Van <application> taga -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Razlika između FINE i COARSE

| Dozvola | Izvor | Tačnost |
|---------|-------|---------|
| `ACCESS_FINE_LOCATION` | GPS + WiFi | ~5–50m |
| `ACCESS_COARSE_LOCATION` | Samo WiFi/mreža | ~300m+ |

> Ako zatražiš `ACCESS_FINE_LOCATION`, automatski dobijaš i COARSE.

### Runtime provjera i zahtjev

```java
private static final int REQUEST_LOCATION = 300;

// 1. Provjeri da li imamo dozvolu
private boolean imaLokacijskuDozvolu() {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;
}

// 2. Zatraži dozvolu od korisnika
private void zatraziLokacijskuDozvolu() {
    ActivityCompat.requestPermissions(this,
            new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            },
            REQUEST_LOCATION);
}

// 3. Obradi korisnikov odgovor
@Override
public void onRequestPermissionsResult(int requestCode,
                                       String[] permissions,
                                       int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_LOCATION) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Dozvola odobrena — nastavi
            dohvatiLokaciju();
        } else {
            Toast.makeText(this, "Dozvola odbijena!", Toast.LENGTH_LONG).show();
        }
    }
}
```

---

## 4. Google Maps

### Layout (XML) — SupportMapFragment

```xml
<!-- fragment je specijalni tag za ugradnju Fragmenta u Layout -->
<fragment
    android:id="@+id/mapFragment"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1" />
```

### Activity — OnMapReadyCallback

```java
// 1. Implementiraj interfejs
public class MojActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // 2. Uzmi fragment i zatraži async inicijalizaciju mape
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // poziva onMapReady() kada je mapa načitana
        }
    }

    // 3. Ovde imaš pristup GoogleMap objektu — sav rad ide ovde
    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
    }
}
```

### Tipovi mape

```java
googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);     // standardna ulična mapa
googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);  // satelitska slika
googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);     // satelit + ulice
googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);    // reljef/teren
```

---

## 5. Dohvatanje lokacije

### Inicijalizacija klijenta

```java
// UVEK u onCreate(), pre poziva metoda
FusedLocationProviderClient fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(this);
```

### getLastLocation() — poslednja poznata lokacija

```java
// Brzo, ali može biti stara lokacija (ili null ako GPS još nije radio)
fusedLocationClient.getLastLocation()
        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location lokacija) {
                if (lokacija != null) {
                    double lat = lokacija.getLatitude();
                    double lng = lokacija.getLongitude();
                    float tacnost = lokacija.getAccuracy(); // u metrima

                    textView.setText("Lat: " + lat + ", Lng: " + lng);

                    // Dodaj marker na mapi
                    LatLng pozicija = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions().position(pozicija).title("Ja"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pozicija, 15));
                } else {
                    // null = GPS nikad nije radio ili je isključen
                    Toast.makeText(ctx, "Uključi GPS!", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

> **Zašto može biti `null`?** Ako uređaj nikad nije koristio GPS od poslednjeg restarta,
> `getLastLocation()` vraća `null`. Rešenje: koristiti `requestLocationUpdates()`.

---

## 6. Realtime praćenje

### LocationCallback + requestLocationUpdates

```java
private LocationCallback locationCallback;

// Definiši callback (jednom, npr. u onCreate)
locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult result) {
        Location lokacija = result.getLastLocation();
        if (lokacija != null) {
            double lat = lokacija.getLatitude();
            double lng = lokacija.getLongitude();
            // Ažuriraj UI...
        }
    }
};

// Pokreni praćenje
private void startajPracenje() {
    LocationRequest request = new LocationRequest.Builder(5000) // interval u ms
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(2000) // najbrže svakih 2s
            .build();

    fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
}

// OBAVEZNO zaustavi u onStop() — štedi bateriju!
@Override
protected void onStop() {
    super.onStop();
    fusedLocationClient.removeLocationUpdates(locationCallback);
}
```

### Priority opcije

| Priority | Opis | Baterija |
|----------|------|---------|
| `PRIORITY_HIGH_ACCURACY` | GPS | Troši najviše |
| `PRIORITY_BALANCED_POWER_ACCURACY` | WiFi + mreža | Uravnoteženo |
| `PRIORITY_LOW_POWER` | Samo mreža | Štedi |
| `PRIORITY_PASSIVE` | Samo kada drugi koriste | Minimalna |

---

## 7. Markeri i kamera

```java
// Dodaj marker
googleMap.addMarker(new MarkerOptions()
        .position(new LatLng(44.8176, 20.4569))  // koordinate
        .title("Beograd")                          // naslov (klik na marker)
        .snippet("Glavni grad Srbije"));           // podnaslov

// Premesti kameru (bez animacije)
googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
        new LatLng(44.8176, 20.4569), 12));  // zoom: 1=svet, 10=grad, 15=ulica

// Premesti kameru (sa animacijom)
googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
        new LatLng(44.8176, 20.4569), 15));

// Ukloni sve markere
googleMap.clear();

// Plava tačka "moja lokacija" (zahteva dozvolu)
googleMap.setMyLocationEnabled(true);
googleMap.getUiSettings().setMyLocationButtonEnabled(true);

// Zoom in / zoom out programski
googleMap.animateCamera(CameraUpdateFactory.zoomIn());
googleMap.animateCamera(CameraUpdateFactory.zoomOut());
```

---

## 8. Geocoding

Geocoding = pretvaranje koordinata u čitljivu adresu (i obrnuto).

```java
// Koordinate → Adresa (Reverse Geocoding)
private void koordinateUAdresu(double lat, double lng) {
    try {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> adrese = geocoder.getFromLocation(lat, lng, 1); // max 1 rezultat

        if (adrese != null && !adrese.isEmpty()) {
            Address adresa = adrese.get(0);
            String grad    = adresa.getLocality();       // npr. "Beograd"
            String drzava  = adresa.getCountryName();    // npr. "Serbia"
            String ulica   = adresa.getAddressLine(0);   // puna adresa

            textView.setText(ulica + ", " + grad + ", " + drzava);
        }
    } catch (IOException e) {
        Log.e(TAG, "Geocoder greška: " + e.getMessage());
    }
}

// Adresa → Koordinate (Forward Geocoding)
private LatLng adresaUKoordinate(String adresaString) {
    try {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> adrese = geocoder.getFromLocationName(adresaString, 1);
        if (adrese != null && !adrese.isEmpty()) {
            double lat = adrese.get(0).getLatitude();
            double lng = adrese.get(0).getLongitude();
            return new LatLng(lat, lng);
        }
    } catch (IOException e) {
        Log.e(TAG, "Geocoder greška: " + e.getMessage());
    }
    return null;
}
```

---

## 9. Kreirani fajlovi

| Fajl | Opis |
|------|------|
| `MapsActivity.java` | Aktivnost sa mapom, GPS lokacijom, markerima i geocodingom |
| `activity_maps_screen.xml` | Layout sa SupportMapFragment i info panelom |
| `app/build.gradle` | Dodate play-services-maps i play-services-location zavisnosti |
| `AndroidManifest.xml` | Dodate lokacijske dozvole, MapsActivity, `meta-data` za API ključ |

---

## Kompletna checklista za kolokvijum

- [ ] `build.gradle` — dodaj `play-services-maps` i `play-services-location`
- [ ] `AndroidManifest.xml` — `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- [ ] `AndroidManifest.xml` — `<meta-data android:name="com.google.android.geo.API_KEY">`
- [ ] Layout — `<fragment android:name="...SupportMapFragment">`
- [ ] Activity — `implements OnMapReadyCallback`
- [ ] `mapFragment.getMapAsync(this)` — u `onCreate()`
- [ ] Override `onMapReady(GoogleMap map)` — sav rad sa mapom ovde
- [ ] `FusedLocationProviderClient` — `LocationServices.getFusedLocationProviderClient(this)`
- [ ] Runtime dozvola — `checkSelfPermission()` → `requestPermissions()` → `onRequestPermissionsResult()`
- [ ] `getLastLocation()` ili `requestLocationUpdates()` za lokaciju
- [ ] `removeLocationUpdates()` u `onStop()` — OBAVEZNO!
