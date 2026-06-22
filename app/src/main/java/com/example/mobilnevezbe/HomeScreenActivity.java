package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * VEZBA 3/4/5/6: Ekran za ADMINISTRATORA.
 * VEZBA 6: Čita podatke iz SharedPreferences, nudi navigaciju na Settings i Contacts.
 */
public class HomeScreenActivity extends AppCompatActivity {

    private static final String TAG = "HomeScreenActivity";

    private TextView welcomeTextView;
    private TextView emailValueTextView;
    private TextView phoneValueTextView;
    private Button   settingsButton;
    private Button   logoutButton;
    private Toolbar  toolbar;

    // VEZBA 5
    private NotificationHelper notificationHelper;

    // VEZBA 6
    private SharedPreferencesManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.d(TAG, "onCreate called");

        spManager = SharedPreferencesManager.getInstance(this);

        initViews();
        setupClickListeners();
        prikaziPodatkeKorisnika();
    }

    private void initViews() {
        welcomeTextView    = findViewById(R.id.welcomeTextView);
        emailValueTextView = findViewById(R.id.emailValueTextView);
        phoneValueTextView = findViewById(R.id.phoneValueTextView);
        settingsButton     = findViewById(R.id.settingsButton);
        logoutButton       = findViewById(R.id.logoutButton);
        toolbar            = findViewById(R.id.toolbar);

        setupToolbar();

        notificationHelper = new NotificationHelper(this);

        // VEZBA 5: Pokretanje servisa (interval se čita iz SharedPreferences)
        InternetCheckService.startService(this);
        InternetStatusReceiver.register(this);

        welcomeTextView.setText(getString(R.string.home_welcome));
    }

    private void setupClickListeners() {
        // VEZBA 6: Otvori ekran za podešavanja sinhronizacije
        settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "Otvaranje SettingsActivity");
            startActivity(new Intent(HomeScreenActivity.this, SettingsActivity.class));
        });

        // VEZBA 6: Odjava - briše SharedPreferences i vraća na Login
        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Odjava korisnika");
            spManager.odjaviKorisnika();
            Toast.makeText(this, "Uspešno odjavljen", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * VEZBA 6: Prikaži podatke ulogovanog korisnika iz SharedPreferences.
     * Ako postoje Intent extras (iz registracije), koristi ih umesto toga.
     */
    private void prikaziPodatkeKorisnika() {
        String name  = spManager.getKorisnickoIme();
        String email = spManager.getKorisnickoEmail();
        String phone = spManager.getKorisnickoTelefon();

        // Ako su podaci prosleđeni preko Intent-a, koristi ih
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("USER_NAME")  != null) name  = intent.getStringExtra("USER_NAME");
            if (intent.getStringExtra("USER_EMAIL") != null) email = intent.getStringExtra("USER_EMAIL");
            if (intent.getStringExtra("USER_PHONE") != null) phone = intent.getStringExtra("USER_PHONE");
        }

        if (!name.isEmpty())  welcomeTextView.setText("Dobrodošli, " + name + "!");
        if (!email.isEmpty()) emailValueTextView.setText(email);
        if (!phone.isEmpty()) phoneValueTextView.setText(phone);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Početna (Admin)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_users) {
            startActivity(new Intent(HomeScreenActivity.this, UserScreenActivity.class));
            return true;
        } else if (id == R.id.action_retrofit) {
            // VEZBA 7: Navigacija na Retrofit ekran
            startActivity(new Intent(HomeScreenActivity.this, RetrofitActivity.class));
            return true;
        } else if (id == R.id.action_maps) {
            startActivity(new Intent(HomeScreenActivity.this, MapsActivity.class));
            return true;
        } else if (id == R.id.action_senzori) {
            // VEZBA 9: Navigacija na Senzori ekran
            startActivity(new Intent(HomeScreenActivity.this, SenzoriActivity.class));
            return true;
        } else if (id == R.id.action_kamera) {
            // VEZBA 9: Navigacija na Kamera ekran
            startActivity(new Intent(HomeScreenActivity.this, KameraActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            spManager.odjaviKorisnika();
            Toast.makeText(this, "Uspešno odjavljen", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        InternetCheckService.stopService(this);
        InternetStatusReceiver.unregister(this);
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
}

