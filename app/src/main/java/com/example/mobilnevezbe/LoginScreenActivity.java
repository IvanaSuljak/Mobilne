package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoginScreenActivity extends AppCompatActivity {

    private static final String TAG = "LoginScreenActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button   loginButton;
    private Button   registerButton;
    private Toolbar  toolbar;

    // VEZBA 6: SQLite baza i SharedPreferences
    private DatabaseHelper          dbHelper;
    private SharedPreferencesManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Log.d(TAG, "onCreate called");

        // VEZBA 6: Inicijalizacija DB-a i SharedPreferences
        dbHelper   = DatabaseHelper.getInstance(this);
        spManager  = SharedPreferencesManager.getInstance(this);

        // VEZBA 6: Ako je korisnik već ulogovan, preskoči LoginScreen
        if (spManager.jeUlogovan()) {
            navigirajPoUlozi(spManager.getUloga());
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        emailEditText    = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton      = findViewById(R.id.loginButton);
        registerButton   = findViewById(R.id.registerButton);
        toolbar          = findViewById(R.id.toolbar);
        setupToolbar();
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                Log.d(TAG, "Login attempt: " + email);

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreenActivity.this,
                            getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show();
                    return;
                }

                // VEZBA 6: Provjera u SQLite bazi
                User loggedInUser = dbHelper.pronadjiKorisnikaZaLogin(email, password);

                if (loggedInUser != null) {
                    Toast.makeText(LoginScreenActivity.this,
                            getString(R.string.success_login), Toast.LENGTH_SHORT).show();

                    // VEZBA 6: Čuvanje podataka u SharedPreferences
                    spManager.sacuvajUlogovanogKorisnika(loggedInUser);
                    Log.d(TAG, "Ulogovan korisnik: " + loggedInUser.getName()
                            + ", uloga: " + loggedInUser.getUloga());

                    // VEZBA 6: Navigacija na odgovarajući ekran prema ulozi
                    navigirajPoUlozi(loggedInUser.getUloga());
                    finish();
                } else {
                    Toast.makeText(LoginScreenActivity.this,
                            getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigacija na RegisterScreen");
                startActivity(new Intent(LoginScreenActivity.this, RegisterScreenActivity.class));
            }
        });
    }

    /**
     * VEZBA 6: Navigacija na odgovarajući ekran u zavisnosti od uloge korisnika.
     * - administrator → HomeScreenActivity (admin panel)
     * - vozac         → DriverScreenActivity
     * - putnik        → PassengerScreenActivity
     */
    private void navigirajPoUlozi(String uloga) {
        Intent intent;
        switch (uloga) {
            case "administrator":
                intent = new Intent(this, HomeScreenActivity.class);
                break;
            case "vozac":
                intent = new Intent(this, DriverScreenActivity.class);
                break;
            case "putnik":
            default:
                intent = new Intent(this, PassengerScreenActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Prijava");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
