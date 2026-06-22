package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * VEZBA 6: Ekran za podešavanja sinhronizacije.
 * Korisnik bira interval i vrijednost se čuva u SharedPreferences.
 * InternetCheckService automatski koristi taj interval.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private RadioGroup syncRadioGroup;
    private RadioButton radioNikad;
    private RadioButton radio1Min;
    private RadioButton radio15Min;
    private RadioButton radio30Min;
    private Button      saveButton;
    private Toolbar     toolbar;

    private SharedPreferencesManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Log.d(TAG, "onCreate called");

        spManager = SharedPreferencesManager.getInstance(this);

        initViews();
        ucitajPodešavanja();
        setupClickListeners();
    }

    private void initViews() {
        syncRadioGroup = findViewById(R.id.syncRadioGroup);
        radioNikad     = findViewById(R.id.radioNikad);
        radio1Min      = findViewById(R.id.radio1Min);
        radio15Min     = findViewById(R.id.radio15Min);
        radio30Min     = findViewById(R.id.radio30Min);
        saveButton     = findViewById(R.id.saveButton);
        toolbar        = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Podešavanja");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * VEZBA 6: Učitaj sačuvani interval iz SharedPreferences i označi odgovarajući RadioButton.
     */
    private void ucitajPodešavanja() {
        long savedInterval = spManager.getSyncInterval();
        Log.d(TAG, "Učitan interval: " + savedInterval + " ms");

        if (savedInterval == SharedPreferencesManager.SYNC_NEVER) {
            radioNikad.setChecked(true);
        } else if (savedInterval == SharedPreferencesManager.SYNC_15_MIN) {
            radio15Min.setChecked(true);
        } else if (savedInterval == SharedPreferencesManager.SYNC_30_MIN) {
            radio30Min.setChecked(true);
        } else {
            radio1Min.setChecked(true); // default
        }
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> {
            long odabraniInterval = getOdabraniInterval();

            // VEZBA 6: Čuvanje u SharedPreferences
            spManager.sacuvajSyncInterval(odabraniInterval);
            Log.d(TAG, "Sačuvan interval: " + odabraniInterval + " ms");

            // VEZBA 5/6: Restart servisa sa novim intervalom
            InternetCheckService.stopService(this);
            if (odabraniInterval != SharedPreferencesManager.SYNC_NEVER) {
                InternetCheckService.startService(this);
            }

            Toast.makeText(this, "Podešavanja sačuvana: " + spManager.getSyncIntervalNaziv(),
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private long getOdabraniInterval() {
        int selectedId = syncRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioNikad) return SharedPreferencesManager.SYNC_NEVER;
        if (selectedId == R.id.radio15Min) return SharedPreferencesManager.SYNC_15_MIN;
        if (selectedId == R.id.radio30Min) return SharedPreferencesManager.SYNC_30_MIN;
        return SharedPreferencesManager.SYNC_1_MIN; // default: 1 min
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}
