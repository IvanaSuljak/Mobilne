package com.example.mobilnevezbe;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

/**
 * VEZBA 4: UserScreen - prikaz, sortiranje i filtriranje korisnika
 * Omogućava pregled svih registrovanih korisnika sa opcijama za sortiranje i filtriranje
 */
public class UserScreenActivity extends AppCompatActivity {
    
    private static final String TAG = "UserScreenActivity";
    
    // VEZBA 4: UI komponente
    private ListView usersListView;
    private Spinner sortSpinner;
    private EditText filterEditText;
    private Button filterButton;
    private Button clearFilterButton;
    private TextView userCountTextView;
    private Toolbar toolbar;
    
    // VEZBA 4: Podaci
    private UserManager userManager;
    private ArrayAdapter<String> usersAdapter;
    private List<User> currentUsers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);
        Log.d(TAG, "onCreate called");
        
        // VEZBA 4: Inicijalizacija
        userManager = UserManager.getInstance();
        initViews();
        setupToolbar();
        setupSpinner();
        setupClickListeners();
        loadUsers();
    }
    
    // VEZBA 4: Inicijalizacija view-ova
    private void initViews() {
        usersListView = findViewById(R.id.usersListView);
        sortSpinner = findViewById(R.id.sortSpinner);
        filterEditText = findViewById(R.id.filterEditText);
        filterButton = findViewById(R.id.filterButton);
        clearFilterButton = findViewById(R.id.clearFilterButton);
        userCountTextView = findViewById(R.id.userCountTextView);
        toolbar = findViewById(R.id.toolbar);
    }
    
    // VEZBA 4: Postavljanje toolbar-a
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Korisnici");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    // VEZBA 4: Postavljanje spinner-a za sortiranje
    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        
        // VEZBA 4: Listener za promenu sortiranja
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting(position);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    // VEZBA 4: Postavljanje click listener-a
    private void setupClickListeners() {
        filterButton.setOnClickListener(v -> applyFilter());
        clearFilterButton.setOnClickListener(v -> clearFilter());
    }
    
    // VEZBA 4: Učitavanje korisnika
    private void loadUsers() {
        currentUsers = userManager.getAllUsers();
        updateUsersList();
        updateUserCount();
    }
    
    // VEZBA 4: Primena sortiranja
    private void applySorting(int sortType) {
        switch (sortType) {
            case 0: // Bez sortiranja
                currentUsers = userManager.getAllUsers();
                break;
            case 1: // Po imenu
                currentUsers = userManager.sortUsersByName();
                break;
            case 2: // Po email-u
                currentUsers = userManager.sortUsersByEmail();
                break;
            case 3: // Po datumu registracije
                currentUsers = userManager.sortUsersByRegistrationDate();
                break;
        }
        updateUsersList();
    }
    
    // VEZBA 4: Primena filter-a
    private void applyFilter() {
        String filterText = filterEditText.getText().toString().trim();
        if (filterText.isEmpty()) {
            Toast.makeText(this, "Unesite tekst za filtriranje", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // VEZBA 4: Filtriranje po imenu (može se proširiti na druga polja)
        currentUsers = userManager.filterUsersByName(filterText);
        updateUsersList();
        updateUserCount();
        
        Toast.makeText(this, "Pronađeno " + currentUsers.size() + " korisnika", Toast.LENGTH_SHORT).show();
    }
    
    // VEZBA 4: Čišćenje filter-a
    private void clearFilter() {
        filterEditText.setText("");
        loadUsers();
        Toast.makeText(this, "Filter uklonjen", Toast.LENGTH_SHORT).show();
    }
    
    // VEZBA 4: Ažuriranje liste korisnika
    private void updateUsersList() {
        List<String> userDisplayNames = new java.util.ArrayList<>();
        for (User user : currentUsers) {
            userDisplayNames.add(user.getDisplayName());
        }
        
        if (usersAdapter == null) {
            usersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userDisplayNames);
            usersListView.setAdapter(usersAdapter);
        } else {
            usersAdapter.clear();
            usersAdapter.addAll(userDisplayNames);
            usersAdapter.notifyDataSetChanged();
        }
    }
    
    // VEZBA 4: Ažuriranje broja korisnika
    private void updateUserCount() {
        userCountTextView.setText("Ukupno korisnika: " + currentUsers.size());
    }
    
    // VEZBA 4: Menu inflacija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_screen_menu, menu);
        return true;
    }
    
    // VEZBA 4: Menu item click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish(); // VEZBA 4: Nazad na prethodni ekran
            return true;
        } else if (id == R.id.action_refresh) {
            loadUsers();
            Toast.makeText(this, "Lista osvežena", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_clear_all) {
            userManager.clearAllUsers();
            loadUsers();
            Toast.makeText(this, "Svi korisnici obrisani", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    // VEZBA 4: Lifecycle metode
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        // VEZBA 4: Osvežavanje liste kada se aktivnost vrati u fokus
        loadUsers();
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
