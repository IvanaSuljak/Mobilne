package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterScreenActivity extends AppCompatActivity {

    private static final String TAG = "RegisterScreenActivity";

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner  ulogaSpinner;      // VEZBA 6: Izbor uloge
    private Button   registerButton;
    private Toolbar  toolbar;

    // VEZBA 6: SQLite baza, SharedPreferences i NotificationHelper
    private DatabaseHelper          dbHelper;
    private SharedPreferencesManager spManager;
    private NotificationHelper      notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        Log.d(TAG, "onCreate called");

        dbHelper           = DatabaseHelper.getInstance(this);
        spManager          = SharedPreferencesManager.getInstance(this);
        notificationHelper = new NotificationHelper(this);

        initViews();
        setupUlogaSpinner();
        setupClickListeners();
    }

    private void initViews() {
        nameEditText            = findViewById(R.id.nameEditText);
        emailEditText           = findViewById(R.id.emailEditText);
        phoneEditText           = findViewById(R.id.phoneEditText);
        passwordEditText        = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        ulogaSpinner            = findViewById(R.id.ulogaSpinner);
        registerButton          = findViewById(R.id.registerButton);
        toolbar                 = findViewById(R.id.toolbar);
        setupToolbar();
    }

    /**
     * VEZBA 6: Populiši Spinner sa listom uloga.
     * Vrednosti u arrays.xml -> string-array name="uloge"
     */
    private void setupUlogaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.uloge,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ulogaSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> {
            String name            = nameEditText.getText().toString().trim();
            String email           = emailEditText.getText().toString().trim();
            String phone           = phoneEditText.getText().toString().trim();
            String password        = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            // VEZBA 6: Uzmi izabranu ulogu iz Spinner-a
            String uloga           = ulogaSpinner.getSelectedItem().toString().toLowerCase();

            Log.d(TAG, "Registracija: " + name + ", " + email + ", uloga=" + uloga);

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.register_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, getString(R.string.register_password_mismatch), Toast.LENGTH_SHORT).show();
                return;
            }

            // VEZBA 6: Provjera da li email već postoji u SQLite bazi
            if (dbHelper.emailPostoji(email)) {
                Toast.makeText(this, getString(R.string.email_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            // VEZBA 6: Kreiranje User objekta SA ulogom
            User noviKorisnik = new User(name, email, phone, password, uloga);

            // VEZBA 6: Čuvanje u SQLite bazu
            long newId = dbHelper.dodajKorisnika(noviKorisnik);
            if (newId == -1) {
                Toast.makeText(this, "Greška pri registraciji!", Toast.LENGTH_SHORT).show();
                return;
            }
            noviKorisnik.setId((int) newId);

            Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show();

            // VEZBA 5: Notifikacija o novom korisniku
            if (notificationHelper.areNotificationsEnabled()) {
                notificationHelper.showUserRegisteredNotification(name);
            }

            // VEZBA 6: Čuvanje u SharedPreferences (automatski uloguj)
            spManager.sacuvajUlogovanogKorisnika(noviKorisnik);

            // VEZBA 6: Navigacija prema ulozi
            Intent intent;
            switch (uloga) {
                case "administrator":
                    intent = new Intent(this, HomeScreenActivity.class);
                    break;
                case "vozac":
                    intent = new Intent(this, DriverScreenActivity.class);
                    break;
                default:
                    intent = new Intent(this, PassengerScreenActivity.class);
                    break;
            }
            intent.putExtra("USER_NAME",  name);
            intent.putExtra("USER_EMAIL", email);
            intent.putExtra("USER_PHONE", phone);
            startActivity(intent);
            finish();
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registracija");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}

}
