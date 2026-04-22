package com.example.mobilnevezbe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeScreenActivity extends AppCompatActivity {
    
    private static final String TAG = "HomeScreenActivity";
    private static final int MICROPHONE_PERMISSION_REQUEST = 100;
    
    private TextView welcomeTextView;
    private Button microphoneButton;
    private Button logoutButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.d(TAG, "onCreate called");
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView);
        microphoneButton = findViewById(R.id.microphoneButton);
        logoutButton = findViewById(R.id.logoutButton);
        
        welcomeTextView.setText("Welcome to Home Screen!");
    }
    
    private void setupClickListeners() {
        microphoneButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MICROPHONE_PERMISSION_REQUEST);
            } else {
                useMicrophone();
            }
        });
        
        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Logout clicked");
            finish();
        });
    }
    
    private void useMicrophone() {
        Log.d(TAG, "Microphone permission granted - using microphone");
        Toast.makeText(this, "Microphone access granted!", Toast.LENGTH_SHORT).show();
    }
    
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
