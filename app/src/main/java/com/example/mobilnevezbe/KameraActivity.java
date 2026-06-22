package com.example.mobilnevezbe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * VEZBA 9 — Zadatak 4: Kamera — snimanje, prikaz i brisanje slike.
 *
 * Mogućnosti:
 *  - Snimanje fotografije kamerom (otvara Camera app)
 *  - Odabir slike iz galerije
 *  - Prikaz u ImageView-u
 *  - Brisanje slike iz ImageView-a
 *
 * NAPOMENA: Na Androidu 7+ nema direktnog URI-ja ka fajlu — koristi se FileProvider!
 */
public class KameraActivity extends AppCompatActivity {

    private static final String TAG              = "KameraActivity";
    private static final int    REQUEST_KAMERA   = 400;

    private ImageView slikaImageView;
    private Button    snimiFotoButton;
    private Button    odaberiGalerijaButton;
    private Button    obrisiSlikuButton;
    private Toolbar   toolbar;

    // URI do privremenog fajla slike (za full-resolution foto)
    private Uri  fotografijUri;

    // ActivityResultLauncher za kamera (moderni pristup umesto startActivityForResult)
    private final ActivityResultLauncher<Intent> kameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                // Prikaži full-resolution sliku iz URI-ja
                                slikaImageView.setImageURI(null); // clear cache
                                slikaImageView.setImageURI(fotografijUri);
                                Log.d(TAG, "Fotografija prikazana: " + fotografijUri);
                                Toast.makeText(KameraActivity.this,
                                        "Fotografija sačuvana!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Snimanje otkazano");
                                Toast.makeText(KameraActivity.this,
                                        "Snimanje otkazano.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    // ActivityResultLauncher za galeriju
    private final ActivityResultLauncher<Intent> galerijaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri odabraniUri = result.getData().getData();
                            slikaImageView.setImageURI(odabraniUri);
                            Log.d(TAG, "Slika iz galerije: " + odabraniUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamera_screen);
        Log.d(TAG, "onCreate");

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        slikaImageView        = findViewById(R.id.slikaImageView);
        snimiFotoButton       = findViewById(R.id.snimiFotoButton);
        odaberiGalerijaButton = findViewById(R.id.odaberiGalerijaButton);
        obrisiSlikuButton     = findViewById(R.id.obrisiSlikuButton);
        toolbar               = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kamera i Slika");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        // ZADATAK 4: Dodavanje slike — otvori Camera app
        snimiFotoButton.setOnClickListener(v -> {
            if (imaKameruDozvolu()) {
                otvoriKameru();
            } else {
                zatraziKameruDozvolu();
            }
        });

        // ZADATAK 4: Izmena slike — odaberi iz galerije
        odaberiGalerijaButton.setOnClickListener(v -> otvoriGaleriju());

        // ZADATAK 4: Brisanje slike — ukloni iz ImageView-a
        obrisiSlikuButton.setOnClickListener(v -> obrisiSliku());
    }

    /**
     * VEZBA 9 — Zadatak 4 (Dodaj/Izmeni): Otvori Camera aplikaciju.
     *
     * Koraci:
     * 1. Napravi prazan fajl za sliku u privremenom direktorijumu
     * 2. Dobij URI za taj fajl putem FileProvider-a (sigurno deljenje fajla sa Camera app)
     * 3. Pošalji Intent ka Camera app sa tim URI-jem
     */
    private void otvoriKameru() {
        try {
            // Napravi privremeni fajl za sliku
            File slikaFajl = napraviFajlZaSliku();
            // Dobij URI putem FileProvider-a (authority mora da se poklapa sa Manifest-om)
            fotografijUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    slikaFajl
            );

            // Intent koji otvara Camera aplikaciju
            Intent kameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Kaži Camera app gdje da sačuva full-resolution sliku
            kameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotografijUri);

            kameraLauncher.launch(kameraIntent);
        } catch (IOException e) {
            Log.e(TAG, "Greška pri kreiranju fajla: " + e.getMessage());
            Toast.makeText(this, "Greška pri otvaranju kamere!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Kreira prazan fajl u privatnom direktorijumu aplikacije.
     * Naziv sadrži timestamp da se ne prepišu stare fotografije.
     */
    private File napraviFajlZaSliku() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imeFile = "FOTO_" + timestamp + "_";

        // getExternalFilesDir() — privatni direktorijum aplikacije (ne treba WRITE_EXTERNAL_STORAGE za ovo)
        File direktorijum = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imeFile, ".jpg", direktorijum);
    }

    /**
     * VEZBA 9 — Zadatak 4 (Izmeni): Odaberi sliku iz galerije.
     */
    private void otvoriGaleriju() {
        Intent galerijaIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galerijaIntent.setType("image/*");
        galerijaLauncher.launch(galerijaIntent);
    }

    /**
     * VEZBA 9 — Zadatak 4 (Briši): Ukloni sliku iz ImageView-a.
     */
    private void obrisiSliku() {
        slikaImageView.setImageResource(R.drawable.ic_slika_placeholder);
        fotografijUri = null;
        Toast.makeText(this, "Slika uklonjena!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Slika obrisana iz ImageView-a");
    }

    // ==========================================================
    // DOZVOLA ZA KAMERU
    // ==========================================================

    private boolean imaKameruDozvolu() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void zatraziKameruDozvolu() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_KAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_KAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                otvoriKameru();
            } else {
                Toast.makeText(this, "Dozvola za kameru odbijena!", Toast.LENGTH_LONG).show();
            }
        }
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
