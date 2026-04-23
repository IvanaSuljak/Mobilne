package com.example.mobilnevezbe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeScreenActivity extends AppCompatActivity {
    
    private static final String TAG = "HomeScreenActivity";
    private static final int MICROPHONE_PERMISSION_REQUEST = 100;
    
    private TextView welcomeTextView;
    private TextView emailValueTextView;
    private TextView phoneValueTextView;
    private Button settingsButton;
    private Button logoutButton;
    
    // VEZBA 4: Toolbar za navigaciju
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.d(TAG, "onCreate called");
        
        initViews();
        setupClickListeners();
        
        // VEZBA 3: Priimanje i prikaz podataka iz RegisterScreen-a
        receiveAndDisplayUserData();
    }
    
    private void initViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView);
        emailValueTextView = findViewById(R.id.emailValueTextView);
        phoneValueTextView = findViewById(R.id.phoneValueTextView);
        settingsButton = findViewById(R.id.settingsButton);
        logoutButton = findViewById(R.id.logoutButton);
        toolbar = findViewById(R.id.toolbar); // VEZBA 4: Inicijalizacija toolbar-a
        
        // VEZBA 4: Postavljanje toolbar-a
        setupToolbar();
        
        // VEZBA 3: Koristi string resurs za welcome poruku
        welcomeTextView.setText(getString(R.string.home_welcome));
    }
    
    private void setupClickListeners() {
        // VEZBA 3: Settings dugme funkcionalnost
        settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "Settings clicked");
            Toast.makeText(this, "Settings opcija - u razvoju", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Logout clicked");
            Toast.makeText(this, "Logout - povratak na LoginScreen", Toast.LENGTH_SHORT).show();
            finish(); // Zatvara HomeScreen i vraća na prethodni
        });
    }
    
    // VEZBA 4: Postavljanje toolbar-a
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Početna");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    // VEZBA 4: Menu inflacija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen_menu, menu);
        return true;
    }
    
    // VEZBA 4: Menu item click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish(); // VEZBA 4: Nazad na prethodni ekran
            return true;
        } else if (id == R.id.action_users) {
            // VEZBA 4: Navigacija na UserScreen
            Intent intent = new Intent(HomeScreenActivity.this, UserScreenActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // VEZBA 4: Logout funkcionalnost
            Toast.makeText(this, "Uspešno ste se odjavili", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    // VEZBA 3: Metoda za prijem i prikaz podataka iz RegisterScreen-a
    private void receiveAndDisplayUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("USER_NAME");
            String userEmail = intent.getStringExtra("USER_EMAIL");
            String userPhone = intent.getStringExtra("USER_PHONE");
            
            Log.d(TAG, "Received data - Name: " + userName + ", Email: " + userEmail + ", Phone: " + userPhone);
            
            // Prikaz podataka u HomeScreen
            if (userName != null) {
                welcomeTextView.setText("Dobrodo\u0161li, " + userName + "!");
            }
            if (userEmail != null) {
                emailValueTextView.setText(userEmail);
            }
            if (userPhone != null) {
                phoneValueTextView.setText(userPhone);
            }
        }
    }
    
    // Ova metoda se automatski aktivira nakon \u0161to korisnik klikne "Allow" ili "Deny" na prozorèi\u0107u za mikrofon.
    // Ako je korisnik dozvolio (PERMISSION_GRANTED), pokre\u0107e se funkcija useMicrophone().
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == MICROPHONE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                useMicrophone();
            } else {
                Log.d(TAG, "Microphone permission denied");
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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
