package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * VEZBA 6: Ekran koji se prikazuje korisniku sa ulogom "vozac".
 * Prikazuje informacije i opcije relevantne za vozača.
 */
public class DriverScreenActivity extends AppCompatActivity {

    private static final String TAG = "DriverScreenActivity";

    private TextView  welcomeTextView;
    private TextView  ulogaTextView;
    private TextView  syncInfoTextView;
    private Button    settingsButton;
    private Button    contactsButton;
    private Button    logoutButton;
    private Toolbar   toolbar;

    private SharedPreferencesManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_screen);
        Log.d(TAG, "onCreate called");

        spManager = SharedPreferencesManager.getInstance(this);

        initViews();
        setupClickListeners();
        prikaziPodatke();
    }

    private void initViews() {
        welcomeTextView  = findViewById(R.id.welcomeTextView);
        ulogaTextView    = findViewById(R.id.ulogaTextView);
        syncInfoTextView = findViewById(R.id.syncInfoTextView);
        settingsButton   = findViewById(R.id.settingsButton);
        contactsButton   = findViewById(R.id.contactsButton);
        logoutButton     = findViewById(R.id.logoutButton);
        toolbar          = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ekran Vozača");
        }
    }

    private void setupClickListeners() {
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        contactsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            spManager.odjaviKorisnika();
            Toast.makeText(this, "Uspešno odjavljen", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void prikaziPodatke() {
        // VEZBA 6: Podaci se čitaju iz SharedPreferences
        String ime   = spManager.getKorisnickoIme();
        String uloga = spManager.getUloga();
        String sync  = spManager.getSyncIntervalNaziv();

        welcomeTextView.setText("Dobrodošli, " + ime + "!");
        ulogaTextView.setText("Uloga: " + uloga.toUpperCase());
        syncInfoTextView.setText("Sinhronizacija: " + sync);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Osvježi prikaz sinhronizacije kada se vrati sa Settings ekrana
        prikaziPodatke();
    }

    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}
