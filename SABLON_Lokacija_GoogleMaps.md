# ŠABLON — Lokacija i Google Maps

> **MASTER:** Otvori prvo `SABLON_MASTER_VODIC.md` → nađi "GPS" ili "Maps" u tabeli.

> Svuda gde vidiš `TODO` → zameni sa svojim podacima.
> Sve ostalo kopiraš bukvalno.

---

## KADA KORISTITI

| Zadatak kaže | Koristi verziju |
|--------------|-----------------|
| lat/lng, geografska širina/dužina, lokacija uređaja, GPS | **KORAK 1b** (samo GPS, bez mape) |
| Google Maps, mapa, marker, kamera na mapi | **KORAK 1–4** (puna verzija + API ključ) |

---

## TAČAN REDOSLED — samo GPS (kolokvijum)

| # | Gde | Šta radiš | Kad |
|---|-----|-----------|-----|
| 1 | `build.gradle (Module :app)` | `play-services-location` u `dependencies` | Posle layouta |
| 2 | Isti fajl | **Sync Now** | Odmah posle dodavanja |
| 3 | `AndroidManifest.xml` | `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` **pre** `<application>` | Posle Gradle |
| 4 | Layout XML | TextView sa `android:id="@+id/lokacijaTextView"` | Već urađeno u Layout šablonu |
| 5 | `MainActivity.java` | Fields: `fusedLocationClient`, `lokacijaTextView` | Na vrhu klase |
| 6 | `MainActivity.java` | u `onCreate`: init + `dohvatiLokaciju()` | Posle findViewById |
| 7 | `MainActivity.java` | Metode: `dohvatiLokaciju`, `imaDozvolu`, `zatraziDozvolu`, `onRequestPermissionsResult` | **Na nivou klase**, ne u lambdi |

## TAČAN REDOSLED — sa mapom (Vežba 8)

| # | Gde | Šta | Kad |
|---|-----|-----|-----|
| 1–2 | Gradle | location + maps + Sync | Prvo |
| 3 | Manifest | dozvole + API ključ meta-data | Posle Gradle |
| 4 | Layout | `<fragment SupportMapFragment>` | Posle Manifest |
| 5 | Activity | `implements OnMapReadyCallback` + KORAK 4 kod | Posle layouta |

---

## TODO lista — šta zameniti

- [ ] `TODO_IME_PAKETA` → tvoj paket (npr. `com.example.mojaplikacija`)
- [ ] `YOUR_API_KEY_OVDE` → pravi Google Maps API ključ
- [ ] `TODO_lat`, `TODO_lng` → koordinate lokacije na kojoj hoćeš da pokažeš mapu
- [ ] `TODO_textView` → ID TextView-a za prikaz koordinata/adrese
- [ ] `TODO_Activity` → ime tvoje Aktivnosti

---

## KORAK 1 — build.gradle (Module :app)

> **VAŽNO:** dependency ide u **`app/build.gradle`**, NE u root `build.gradle (Project)`!
>
> U Android Studiju: levo → **Gradle Scripts** → **`build.gradle (Module :app)`**
> Posle izmene klikni **Sync Now**.

```groovy
// Unutar dependencies { } bloka u app/build.gradle:

// Google Maps SDK (samo ako koristiš mapu)
implementation 'com.google.android.gms:play-services-maps:18.2.0'

// GPS lokacija (uvek za lat/lng)
implementation 'com.google.android.gms:play-services-location:21.2.0'
```

---

## KORAK 1b — SAMO GPS bez mape (za kolokvijum: lat/lng u TextView)

Ako zadatak traži **samo koordinate u TextView-u** (nema mape), dovoljno je:

**app/build.gradle** — samo ova linija:
```groovy
implementation 'com.google.android.gms:play-services-location:21.2.0'
```

**AndroidManifest.xml** — samo dozvole (bez API ključa):
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**MainActivity** — kopiraj samo ove delove iz KORAK 4:
- `private FusedLocationProviderClient fusedLocationClient;`
- `private TextView TODO_textView;`
- u `onCreate()`: inicijalizacija + `dohvatiLokaciju();`
- metode: `dohvatiLokaciju()`, `imaDozvolu()`, `zatraziDozvolu()`, `onRequestPermissionsResult()`

**NE treba ti:** `OnMapReadyCallback`, `googleMap`, `SupportMapFragment`, API ključ.

---

## KORAK 2 — AndroidManifest.xml

```xml
<!-- Van <application> taga — dozvole -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Unutar <application> taga -->

<!-- Nova Activity -->
<activity android:name=".TODO_Activity" android:exported="false" />

<!-- API ključ — BEZ OVOGA MAPA NEĆE RADITI -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_OVDE" />
```

---

## KORAK 3 — Layout (activity_TODO_mapa.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- MAPA - zauzima slobodan prostor -->
    <fragment
        android:id="@+id/mapFragment"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <!-- Dugme za lokaciju -->
    <Button
        android:id="@+id/TODO_dugme"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Moja lokacija" />

    <!-- Prikaz koordinata / adrese -->
    <TextView
        android:id="@+id/TODO_textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Pritisni dugme..." />

</LinearLayout>
```

---

## KORAK 4 — Activity (kopiraš celu klasu, menjaš TODO)

```java
package com.example.TODO_IME_PAKETA;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// VAZNO: implements OnMapReadyCallback
public class TODO_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 300; // bilo koji broj

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView TODO_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_TODO_mapa);

        // 1. Inicijalizuj Fused Location klijent
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 2. Pronađi fragment i traži async inicijalizaciju mape
        TODO_textView = findViewById(R.id.TODO_textView);
        Button dugme  = findViewById(R.id.TODO_dugme);
        dugme.setOnClickListener(v -> dohvatiLokaciju());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // poziva onMapReady kada mapa bude gotova
        }
    }

    // 3. Mapa je učitana — OVde i SAMO OVDE radiš sa googleMap objektom
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Postavi početni prikaz
        // TODO: Promeni koordinate i naziv prema svom zadatku
        LatLng pocetnaLokacija = new LatLng(TODO_lat, TODO_lng); // npr. new LatLng(44.81, 20.45)
        googleMap.addMarker(new MarkerOptions()
                .position(pocetnaLokacija)
                .title("TODO_naziv_markera"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pocetnaLokacija, 12));
    }

    // 4. Dohvati poslednju poznatu lokaciju
    private void dohvatiLokaciju() {
        if (!imaDozvolu()) {
            zatraziDozvolu();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, lokacija -> {
                    if (lokacija != null) {
                        double lat = lokacija.getLatitude();
                        double lng = lokacija.getLongitude();

                        TODO_textView.setText("Lat: " + lat + "\nLng: " + lng);

                        // Dodaj marker na mapi i centriraj
                        LatLng moja = new LatLng(lat, lng);
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(moja).title("Ja sam ovde"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moja, 15));
                    } else {
                        Toast.makeText(this, "Uključi GPS i pokušaj ponovo!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 5. Dozvola — provjera
    private boolean imaDozvolu() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 5. Dozvola — zahtjev
    private void zatraziDozvolu() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                             Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION);
    }

    // 5. Dozvola — odgovor korisnika
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dohvatiLokaciju(); // ponovi zahtjev
            } else {
                Toast.makeText(this, "Dozvola odbijena!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
```

> **ČESTA GREŠKA:** `imaDozvolu()`, `zatraziDozvolu()` i `onRequestPermissionsResult()`
> pišu se **na nivou klase** (pored `dohvatiLokaciju()`), NE unutar `{}` lambde
> `lokacija -> { ... }` i NE unutar `dohvatiLokaciju()`.
>
> Lambda se zatvara sa `});` — tek POSLE toga pišeš sledeće metode.
>
> **ČESTA GREŠKA 2:** Android Studio ponekad ubaci dupli `if (ActivityCompat.checkSelfPermission...)`
> posle `imaDozvolu()` — **obriši ga**, dovoljna je samo `imaDozvolu()` provera.
> Za lint upozorenje dodaj `@SuppressLint("MissingPermission")` iznad `dohvatiLokaciju()`.

---

## BONUS — Dodaj marker na fiksnu lokaciju (bez GPS-a)

```java
// Ovo ne zahteva nikakvu dozvolu!
LatLng beograd = new LatLng(44.8176, 20.4569);
googleMap.addMarker(new MarkerOptions()
        .position(beograd)
        .title("Beograd")
        .snippet("Opis markera koji se vidi pri kliknu"));
googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(beograd, 13));
```

---

## BONUS — Geocoding (koordinate → adresa)

```java
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

private void prikaziAdresu(double lat, double lng) {
    try {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> adrese = geocoder.getFromLocation(lat, lng, 1);
        if (adrese != null && !adrese.isEmpty()) {
            String adresa = adrese.get(0).getAddressLine(0);
            TODO_textView.setText("Adresa: " + adresa);
        }
    } catch (IOException e) {
        TODO_textView.setText("Adresa nedostupna");
    }
}
```

---

## Redosled koji se NE MENJA

### Samo GPS (kolokvijum — lat/lng u TextView)
```
1. app/build.gradle (Module :app) → play-services-location + Sync Now
2. Manifest           → ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION
3. Layout             → TextView sa android:id="@+id/lokacijaTextView"
4. Activity           → fields + onCreate() + dohvatiLokaciju()
5. Dozvola            → imaDozvolu() → zatraziDozvolu() → onRequestPermissionsResult()
   (metode na nivou klase, NE unutar lambde!)
```

### Sa mapom (Vežba 8)
```
1. app/build.gradle (Module :app) → play-services-maps + play-services-location + Sync Now
2. Manifest           → dozvole + API_KEY meta-data
3. Layout             → <fragment android:name="...SupportMapFragment">
4. Activity           → implements OnMapReadyCallback
5. onCreate()         → FusedLocationProviderClient + mapFragment.getMapAsync(this)
6. onMapReady()       → sav rad sa mapom ovde (googleMap je dostupan)
7. dohvatiLokaciju()  → provjeri dozvolu → getLastLocation() → marker + kamera
8. Dozvola            → imaDozvolu() → zatraziDozvolu() → onRequestPermissionsResult()
```
