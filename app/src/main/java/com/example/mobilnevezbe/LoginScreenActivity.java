package com.example.mobilnevezbe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginScreenActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginScreenActivity";
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    
    // VEZBA 4: UserManager za proveru login podataka
    private UserManager userManager;
    
    // VEZBA 4: Toolbar za navigaciju
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Log.d(TAG, "onCreate called");
        
        // VEZBA 4: Inicijalizacija UserManager-a
        userManager = UserManager.getInstance();
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        toolbar = findViewById(R.id.toolbar); // VEZBA 4: Inicijalizacija toolbar-a
        
        // VEZBA 4: Postavljanje toolbar-a
        setupToolbar();
    }
    
    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                
                Log.d(TAG, "Login attempt with email: " + email);
                
                // VEZBA 4: Provera da li su polja prazna
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreenActivity.this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // VEZBA 4: Provera login podataka sa UserManager
                User loggedInUser = userManager.loginUser(email, password);
                
                if (loggedInUser != null) {
                    // VEZBA 4: Uspešan login
                    Toast.makeText(LoginScreenActivity.this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    
                    // Navigate to HomeScreen sa podacima korisnika
                    Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                    intent.putExtra("USER_NAME", loggedInUser.getName());
                    intent.putExtra("USER_EMAIL", loggedInUser.getEmail());
                    intent.putExtra("USER_PHONE", loggedInUser.getPhone());
                    startActivity(intent);
                    finish();
                } else {
                    // VEZBA 4: Neuspešan login
                    Toast.makeText(LoginScreenActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to RegisterScreen");
                Intent intent = new Intent(LoginScreenActivity.this, RegisterScreenActivity.class);
                startActivity(intent);
            }
        });
    }
    
    // VEZBA 4: Postavljanje toolbar-a
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Prijava");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // VEZBA 4: Ne prikazuj back dugme na login screen
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
