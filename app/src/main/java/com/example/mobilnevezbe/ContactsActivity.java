package com.example.mobilnevezbe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * VEZBA 6: Čitanje podataka iz sistemskog ContentProvider-a (Kontakti).
 *
 * ContentProvider je Android komponenta koja omogućava aplikacijama da
 * DELE podatke jedne sa drugima. Sistem ih koristi za Kontakte, Kalendar,
 * SMS poruke itd.
 *
 * URI za pristup: content://com.android.contacts/...
 * Klasa: android.provider.ContactsContract
 *
 * POTREBNA DOZVOLA: READ_CONTACTS (u AndroidManifest.xml + runtime request)
 */
public class ContactsActivity extends AppCompatActivity {

    private static final String TAG                   = "ContactsActivity";
    private static final int    REQUEST_READ_CONTACTS = 200;

    private ListView contactsListView;
    private Toolbar  toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_screen);
        Log.d(TAG, "onCreate called");

        contactsListView = findViewById(R.id.contactsListView);
        toolbar          = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kontakti (ContentProvider)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // VEZBA 6: Provjera runtime dozvole za čitanje kontakata
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            ucitajKontakte();
        } else {
            // Zatraži dozvolu od korisnika
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    /**
     * VEZBA 6: Čitanje kontakata iz ContentProvider-a.
     *
     * Koraci:
     * 1. Definišemo URI (adresu) ContentProvider-a
     * 2. Definišemo koje kolone (projekciju) hoćemo
     * 3. Pozovemo getContentResolver().query() - isto kao SQL SELECT
     * 4. Iteriramo kroz Cursor i čitamo podatke
     */
    private void ucitajKontakte() {
        List<String> kontakti = new ArrayList<>();

        // URI ContentProvider-a za kontakte
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // Kolone koje hoćemo da učitamo (projekcija = SELECT kolone)
        String[] projekcija = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, // Ime kontakta
                ContactsContract.CommonDataKinds.Phone.NUMBER         // Broj telefona
        };

        // Sortiranje po imenu
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        // KLJUČNI POZIV: query() = ContentResolver čita iz ContentProvider-a
        Cursor cursor = getContentResolver().query(
                uri,         // Šta čitamo (koji ContentProvider)
                projekcija,  // Koje kolone (null = sve)
                null,        // WHERE uslov (null = sve)
                null,        // WHERE argumenti
                sortOrder    // ORDER BY
        );

        if (cursor != null && cursor.getCount() > 0) {
            int imeIndex    = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int brojIndex   = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String ime  = cursor.getString(imeIndex);
                String broj = cursor.getString(brojIndex);
                kontakti.add(ime + "\n" + broj);
                Log.d(TAG, "Kontakt: " + ime + " - " + broj);
            }
            cursor.close();
        } else {
            kontakti.add("Nema kontakata na uređaju.");
            Log.d(TAG, "Nema kontakata ili cursor je null.");
        }

        // Prikaži u ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                kontakti
        );
        contactsListView.setAdapter(adapter);

        Toast.makeText(this, "Učitano " + kontakti.size() + " kontakata", Toast.LENGTH_SHORT).show();
    }

    /**
     * VEZBA 6: Callback koji se poziva kada korisnik odgovori na zahtjev za dozvolom.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "READ_CONTACTS dozvola odobrena");
                ucitajKontakte();
            } else {
                Log.w(TAG, "READ_CONTACTS dozvola odbijena");
                Toast.makeText(this, "Dozvola za čitanje kontakata odbijena!", Toast.LENGTH_LONG).show();
            }
        }
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
