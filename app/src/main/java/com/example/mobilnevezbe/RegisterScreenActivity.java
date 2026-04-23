package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterScreenActivity extends AppCompatActivity {
    
    private static final String TAG = "RegisterScreenActivity";
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText; // VEZBA 3: Dodato polje za broj telefona
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    
    // VEZBA 4: UserManager za čuvanje korisnika
    private UserManager userManager;
    
    // VEZBA 4: Toolbar za navigaciju
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        Log.d(TAG, "onCreate called");
        
        // VEZBA 4: Inicijalizacija UserManager-a
        userManager = UserManager.getInstance();
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText); // VEZBA 3: Inicijalizacija polja za telefon
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        toolbar = findViewById(R.id.toolbar); // VEZBA 4: Inicijalizacija toolbar-a
        
        // VEZBA 4: Postavljanje toolbar-a
        setupToolbar();
    }
    
    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim(); // VEZBA 3: Uzimanje broja telefona
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            
            Log.d(TAG, "Registration attempt with name: " + name + ", email: " + email + ", phone: " + phone);
            
            // VEZBA 3: Provera svih polja ukljuèujuæi telefon
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "Please fill all fields");
                Toast.makeText(this, getString(R.string.register_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Log.d(TAG, "Passwords do not match");
                Toast.makeText(this, getString(R.string.register_password_mismatch), Toast.LENGTH_SHORT).show();
                return;
            }
            
            // VEZBA 4: Kreiranje novog korisnika i čuvanje u UserManager
            User newUser = new User(name, email, phone, password);
            
            // VEZBA 4: Provera da li email već postoji
            if (!userManager.addUser(newUser)) {
                Toast.makeText(this, getString(R.string.email_exists), Toast.LENGTH_SHORT).show();
                return;
            }
            
            // VEZBA 4: Uspešna registracija
            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(RegisterScreenActivity.this, HomeScreenActivity.class);
            // VEZBA 3: Prosleđivanje podataka na HomeScreen
            intent.putExtra("USER_NAME", name);
            intent.putExtra("USER_EMAIL", email);
            intent.putExtra("USER_PHONE", phone);
            startActivity(intent);
            finish(); // Zatvara RegisterScreen
        });
    }
    
    // VEZBA 4: Postavljanje toolbar-a
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registracija");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // VEZBA 4: Prikaz back dugmeta
        }
    }
    
    // VEZBA 4: Menu item click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish(); // VEZBA 4: Nazad na LoginScreen
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart called");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}
