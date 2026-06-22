package com.example.mobilnevezbe;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * VEZBA 9: Ekran za demonstraciju senzora uređaja.
 *
 * Senzori koji se koriste:
 *   1. TYPE_ACCELEROMETER  — ubrzanje na X, Y, Z osama (zadatak 1 + 2 shake)
 *   2. TYPE_GYROSCOPE      — rotacija uređaja (zadatak 3 — senzor po izboru)
 *
 * Tok rada sa senzorima:
 *   1. Uzmi SensorManager iz sistema
 *   2. Pronađi željeni senzor getDefaultSensor(tip)
 *   3. Registruj SensorEventListener u onResume()
 *   4. Odradi onSensorChanged() za obradu podataka
 *   5. UVEK odjavi listener u onPause() — štedi bateriju!
 */
public class SenzoriActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SenzoriActivity";

    // ========== SHAKE PARAMETRI ==========
    // Minimalno ubrzanje koje se smatra "shake" (m/s²)
    // Gravitacija je ~9.8 m/s², shake prag treba biti iznad toga
    private static final float SHAKE_PRAG          = 12.0f;
    // Minimalni razmak između dva shake događaja (ms) — sprečava spam
    private static final long  SHAKE_GAP_MS        = 1000;
    // Koliko brza promena ubrzanja = shake
    private static final float SHAKE_DELTA         = 0.5f;

    // SensorManager — sistemska usluga za pristup senzorima
    private SensorManager sensorManager;

    // ZADATAK 1: Akcelerometar
    private Sensor akcelerometar;
    // ZADATAK 3: Drugi senzor — žiroskop
    private Sensor ziroskop;

    // Za shake detekciju
    private long  poslednjIShakeVreme = 0;
    private float poslednaXVrednost   = 0;
    private float poslednaYVrednost   = 0;
    private float poslednaZVrednost   = 0;

    // UI
    private TextView akcelerometarTextView;
    private TextView ziroskopTextView;
    private TextView shakeStatusTextView;
    private Toolbar  toolbar;

    // NotificationHelper za shake notifikaciju
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senzori_screen);
        Log.d(TAG, "onCreate");

        notificationHelper = new NotificationHelper(this);

        initViews();
        initSenzori();
    }

    private void initViews() {
        akcelerometarTextView = findViewById(R.id.akcelerometarTextView);
        ziroskopTextView      = findViewById(R.id.ziroskopTextView);
        shakeStatusTextView   = findViewById(R.id.shakeStatusTextView);
        toolbar               = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Senzori");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * VEZBA 9 — Zadatak 1: Dobavi senzore iz SensorManager-a.
     * getDefaultSensor() vraća null ako senzor ne postoji na uređaju!
     */
    private void initSenzori() {
        // 1. Uzmi SensorManager — pristupna tačka za sve senzore
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager == null) {
            Toast.makeText(this, "SensorManager nije dostupan!", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Dobavi akcelerometar (TYPE_ACCELEROMETER)
        akcelerometar = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (akcelerometar == null) {
            akcelerometarTextView.setText("Akcelerometar nije dostupan na ovom uređaju!");
            Log.w(TAG, "Akcelerometar nije pronađen");
        } else {
            Log.d(TAG, "Akcelerometar pronađen: " + akcelerometar.getName());
        }

        // 3. Dobavi žiroskop (TYPE_GYROSCOPE) — senzor po izboru (Zadatak 3)
        ziroskop = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (ziroskop == null) {
            ziroskopTextView.setText("Žiroskop nije dostupan na ovom uređaju!");
            Log.w(TAG, "Žiroskop nije pronađen");
        } else {
            Log.d(TAG, "Žiroskop pronađen: " + ziroskop.getName());
        }
    }

    /**
     * VEZBA 9: Registruj listenere u onResume() — aktivno slušanje senzora.
     * Parametar SENSOR_DELAY_NORMAL znači ~5 ažuriranja u sekundi.
     * Ostale opcije: SENSOR_DELAY_UI (~16/s), SENSOR_DELAY_GAME (~50/s), SENSOR_DELAY_FASTEST (max)
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager == null) return;

        // Registruj akcelerometar
        if (akcelerometar != null) {
            sensorManager.registerListener(this, akcelerometar, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Akcelerometar listener registrovan");
        }

        // Registruj žiroskop
        if (ziroskop != null) {
            sensorManager.registerListener(this, ziroskop, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Žiroskop listener registrovan");
        }
    }

    /**
     * VEZBA 9: OBAVEZNO odjavi listenere u onPause()!
     * Ako ne odjaviš, senzori rade u pozadini i TROŠE BATERIJU.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Svi senzor listeneri odjavljeni");
        }
    }

    /**
     * VEZBA 9 — Zadatak 1 + 2 + 3: Ovde primamo podatke sa senzora.
     * Poziva se svaki put kada senzor dobije novo očitavanje.
     *
     * event.sensor.getType() → koji senzor je poslao podatke
     * event.values[]         → niz vrednosti (zavisi od senzora)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Vrednosti: event.values[0]=X, event.values[1]=Y, event.values[2]=Z (sve u m/s²)
            obradiAkcelerometar(event);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Vrednosti: event.values[0]=X, event.values[1]=Y, event.values[2]=Z (u rad/s)
            obradiZiroskop(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Opciono — poziva se kada se preciznost senzora promeni
        Log.d(TAG, "Tačnost senzora promenjena: " + sensor.getName() + " = " + accuracy);
    }

    // ==========================================================
    // ZADATAK 1 + 2: Akcelerometar + Shake detekcija
    // ==========================================================

    private void obradiAkcelerometar(SensorEvent event) {
        float x = event.values[0]; // levo/desno
        float y = event.values[1]; // gore/dole
        float z = event.values[2]; // napred/nazad

        // Prikaz vrednosti u TextView (Zadatak 1)
        String prikaz = String.format(
                "AKCELEROMETAR\nX: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²",
                x, y, z);
        akcelerometarTextView.setText(prikaz);

        // ZADATAK 2: Shake detekcija
        detektujShake(x, y, z);
    }

    /**
     * VEZBA 9 — Zadatak 2: Algoritam za detekciju shake događaja.
     *
     * Logika:
     *  - Izračunaj ukupno ubrzanje (bez gravitacije): sqrt(x² + y² + z²)
     *  - Ako je iznenada iznad praga → shake!
     *  - Provjeri vremenski razmak da ne bi spam notifikacija
     */
    private void detektujShake(float x, float y, float z) {
        long trenutnoVreme = System.currentTimeMillis();

        // Izračunaj promjenu ubrzanja u odnosu na prethodno očitavanje
        float deltaX = Math.abs(x - poslednaXVrednost);
        float deltaY = Math.abs(y - poslednaYVrednost);
        float deltaZ = Math.abs(z - poslednaZVrednost);

        poslednaXVrednost = x;
        poslednaYVrednost = y;
        poslednaZVrednost = z;

        // Ukupna promena
        float ukupnaPromeina = (deltaX + deltaY + deltaZ) / 3;

        // Ukupno ubrzanje (vektor magnitude)
        float ukupnoUbrzanje = (float) Math.sqrt(x * x + y * y + z * z);

        // Shake = brza promena + visoko ukupno ubrzanje + dovoljno vremena od zadnjeg
        boolean jeShake = (ukupnoUbrzanje > SHAKE_PRAG)
                && (ukupnaPromeina > SHAKE_DELTA)
                && (trenutnoVreme - poslednjIShakeVreme > SHAKE_GAP_MS);

        if (jeShake) {
            poslednjIShakeVreme = trenutnoVreme;
            Log.d(TAG, "SHAKE DETEKTOVAN! Ubrzanje: " + ukupnoUbrzanje);

            shakeStatusTextView.setText("SHAKE DETEKTOVAN! (" + 
                    String.format("%.1f", ukupnoUbrzanje) + " m/s²)");

            // ZADATAK 2: Pošalji notifikaciju
            posaljiShakeNotifikaciju();
        }
    }

    /**
     * VEZBA 9 — Zadatak 2: Slanje notifikacije pri shake događaju.
     */
    private void posaljiShakeNotifikaciju() {
        if (notificationHelper.areNotificationsEnabled()) {
            notificationHelper.showShakeNotification();
            Toast.makeText(this, "Shake detektovan!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Shake detektovan! (Notifikacije nisu dozvoljene)", Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================================
    // ZADATAK 3: Drugi senzor — Žiroskop
    // ==========================================================

    private void obradiZiroskop(SensorEvent event) {
        float x = event.values[0]; // rotacija oko X ose (pitch, nagib napred/nazad) u rad/s
        float y = event.values[1]; // rotacija oko Y ose (roll, nagib levo/desno) u rad/s
        float z = event.values[2]; // rotacija oko Z ose (yaw, okretanje u ravni) u rad/s

        String prikaz = String.format(
                "ŽIROSKOP\nX (pitch): %.3f rad/s\nY (roll):  %.3f rad/s\nZ (yaw):   %.3f rad/s",
                x, y, z);
        ziroskopTextView.setText(prikaz);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
