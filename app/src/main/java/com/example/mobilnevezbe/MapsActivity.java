package com.example.mobilnevezbe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * VEZBA 8: Ekran za prikaz Google Mape i lokacije korisnika.
 *
 * Koraci koji se uvek izvršavaju:
 *   1. implements OnMapReadyCallback → čekamo da se mapa učita
 *   2. SupportMapFragment → fragment koji sadrži mapu
 *   3. FusedLocationProviderClient → daje najtačniju lokaciju (GPS + WiFi + mreža)
 *   4. Runtime dozvola ACCESS_FINE_LOCATION → bez nje nema lokacije
 *   5. getLastLocation() → brzo, ali možda stara lokacija
 *   6. requestLocationUpdates() → prati promene lokacije u realnom vremenu
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG                    = "MapsActivity";
    private static final int    REQUEST_LOCATION       = 300;

    // Referenca na Google Maps objekat (dostupna tek u onMapReady())
    private GoogleMap googleMap;

    // Fused Location Provider — kombinuje GPS, WiFi i mobilnu mrežu
    private FusedLocationProviderClient fusedLocationClient;

    // Callback za praćenje promene lokacije
    private LocationCallback locationCallback;

    private TextView koordinateTextView;
    private TextView adresaTextView;
    private Button   mojaPozicijaButton;
    private Button   noviSadiButton;
    private Toolbar  toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        Log.d(TAG, "onCreate called");

        // VEZBA 8: Inicijalizacija Fused Location klijenta
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        initMapa();
        setupLocationCallback();
    }

    private void initViews() {
        koordinateTextView = findViewById(R.id.koordinateTextView);
        adresaTextView     = findViewById(R.id.adresaTextView);
        mojaPozicijaButton = findViewById(R.id.mojaPozicijaButton);
        noviSadiButton     = findViewById(R.id.noviSadiButton);
        toolbar            = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mapa i Lokacija");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mojaPozicijaButton.setOnClickListener(v -> prikaziMojuLokaciju());
        noviSadiButton.setOnClickListener(v -> dodajMarkerZaNoviSad());
    }

    /**
     * VEZBA 8: Inicijalizacija mape.
     * SupportMapFragment traži mapu i kada je spremna, poziva onMapReady().
     */
    private void initMapa() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // asinhrono — poziva onMapReady() kada je mapa spremna
        }
    }

    /**
     * VEZBA 8: Callback koji se poziva kada je Google Maps spreman za korišćenje.
     * Sav rad sa mapom ide OVDE ili nakon što se ova metoda pozove.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "Mapa je spremna");

        // Podešavanje tipa mape
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Uključi zoom kontrole
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // Postavi default prikaz na Beograd
        LatLng beograd = new LatLng(44.8176, 20.4569);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beograd, 12));
        googleMap.addMarker(new MarkerOptions()
                .position(beograd)
                .title("Beograd"));

        // Provjeri dozvolu za lokaciju
        if (imaLokacijskuDozvolu()) {
            ukljuciMojuLokacijuNaMapi();
        }
    }

    /**
     * VEZBA 8: Prikazuje plavu tačku za "moja lokacija" na mapi.
     * Zahteva ACCESS_FINE_LOCATION dozvolu.
     */
    private void ukljuciMojuLokacijuNaMapi() {
        if (googleMap == null) return;
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "Nema dozvole za lokaciju: " + e.getMessage());
        }
    }

    /**
     * VEZBA 8: Dohvati POSLEDNJU poznatu lokaciju — brzo, ali može biti stara.
     * Za realtime koristiti requestLocationUpdates().
     */
    private void prikaziMojuLokaciju() {
        if (!imaLokacijskuDozvolu()) {
            zatraziLokacijskuDozvolu();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lokacija) {
                        if (lokacija != null) {
                            double lat = lokacija.getLatitude();
                            double lng = lokacija.getLongitude();
                            Log.d(TAG, "Lokacija: " + lat + ", " + lng);

                            // Prikaz koordinata
                            koordinateTextView.setText(
                                    "Lat: " + String.format("%.6f", lat) +
                                    "\nLng: " + String.format("%.6f", lng) +
                                    "\nTačnost: " + String.format("%.1f", lokacija.getAccuracy()) + "m");

                            // Geocoding — koordinate → adresa
                            prikaziAdresu(lat, lng);

                            // Dodaj marker i centriraj mapu
                            if (googleMap != null) {
                                LatLng moja = new LatLng(lat, lng);
                                googleMap.clear(); // ukloni stare markere
                                googleMap.addMarker(new MarkerOptions()
                                        .position(moja)
                                        .title("Moja lokacija"));
                                googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(moja, 15));
                            }
                        } else {
                            Toast.makeText(MapsActivity.this,
                                    "Lokacija nije dostupna. Uključi GPS!", Toast.LENGTH_LONG).show();
                            koordinateTextView.setText("Lokacija nedostupna — uključi GPS");
                        }
                    }
                });
    }

    /**
     * VEZBA 8: Postavi marker na fiksnu lokaciju (Novi Sad).
     * Ovo ne zahteva nikakvu dozvolu.
     */
    private void dodajMarkerZaNoviSad() {
        if (googleMap == null) {
            Toast.makeText(this, "Mapa još nije učitana!", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng noviSad = new LatLng(45.2671, 19.8335);
        googleMap.addMarker(new MarkerOptions()
                .position(noviSad)
                .title("Novi Sad")
                .snippet("Drugi najveći grad Srbije"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(noviSad, 13));
        Toast.makeText(this, "Marker dodat za Novi Sad!", Toast.LENGTH_SHORT).show();
    }

    /**
     * VEZBA 8: Geocoding — pretvara koordinate u čitljivu adresu.
     * Radi i bez interneta (lokalno na uređaju).
     */
    private void prikaziAdresu(double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> adrese = geocoder.getFromLocation(lat, lng, 1);
            if (adrese != null && !adrese.isEmpty()) {
                Address adresa = adrese.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= adresa.getMaxAddressLineIndex(); i++) {
                    sb.append(adresa.getAddressLine(i)).append("\n");
                }
                adresaTextView.setText("Adresa:\n" + sb.toString().trim());
            } else {
                adresaTextView.setText("Adresa nije pronađena");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder greška: " + e.getMessage());
            adresaTextView.setText("Geocoding nije dostupan");
        }
    }

    /**
     * VEZBA 8: Praćenje lokacije u realnom vremenu (LocationCallback).
     * Koristiti kada treba kontinuirano ažuriranje.
     */
    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location lokacija = result.getLastLocation();
                if (lokacija != null) {
                    Log.d(TAG, "Novi update lokacije: " + lokacija.getLatitude()
                            + ", " + lokacija.getLongitude());
                    // Ovde možeš ažurirati UI u realnom vremenu
                }
            }
        };
    }

    /**
     * VEZBA 8: Pokretanje praćenja lokacije u realnom vremenu.
     */
    private void startajLocationUpdates() {
        if (!imaLokacijskuDozvolu()) return;

        LocationRequest locationRequest = new LocationRequest.Builder(5000) // interval: 5 sekundi
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000) // najbrže svakih 2 sekunde
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d(TAG, "Location updates pokrenuti");
        } catch (SecurityException e) {
            Log.e(TAG, "Nema dozvole za location updates: " + e.getMessage());
        }
    }

    /**
     * VEZBA 8: Zaustavljanje praćenja (UVEK zaustavi u onStop!).
     */
    private void stopajLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "Location updates zaustavljeni");
    }

    // ==========================================================
    // UPRAVLJANJE DOZVOLAMA
    // ==========================================================

    private boolean imaLokacijskuDozvolu() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void zatraziLokacijskuDozvolu() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Lokacijska dozvola odobrena");
                Toast.makeText(this, "Dozvola odobrena!", Toast.LENGTH_SHORT).show();
                ukljuciMojuLokacijuNaMapi();
                prikaziMojuLokaciju();
            } else {
                Toast.makeText(this, "Dozvola za lokaciju odbijena!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ==========================================================
    // ŽIVOTNI CIKLUS — bitno za battery management
    // ==========================================================

    @Override
    protected void onResume() {
        super.onResume();
        // Opciono: startajLocationUpdates() ako hoćeš realtime
    }

    @Override
    protected void onStop() {
        super.onStop();
        // VAŽNO: uvek zaustavi updates kada Activity nije vidljiva
        stopajLocationUpdates();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
