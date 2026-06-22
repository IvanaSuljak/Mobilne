package com.example.mobilnevezbe;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobilnevezbe.model.ApiUser;
import com.example.mobilnevezbe.model.Comment;
import com.example.mobilnevezbe.model.Post;
import com.example.mobilnevezbe.network.ApiService;
import com.example.mobilnevezbe.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * VEZBA 7: Glavni ekran za HTTP zahteve putem Retrofit-a.
 *
 * Tok jednog zahteva:
 *   1. RetrofitClient.getInstance().getApiService()  → uzmi ApiService
 *   2. apiService.getPostById(1)                     → napravi Call objekat
 *   3. call.enqueue(new Callback<Post>(){...})        → pošalji ASINHRONO
 *   4. onResponse() / onFailure()                    → obradi odgovor
 *
 * VAŽNO: enqueue() šalje zahtev na pozadinskoj niti.
 * UI se ažurira u onResponse() koji se izvršava na GLAVNOJ niti.
 */
public class RetrofitActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitActivity";

    private TextView postTextView;
    private TextView komentarTextView;
    private Button   ucitajPostButton;
    private Button   ucitajKomentarButton;
    private Button   ucitajKorisnikeButton;
    private Toolbar  toolbar;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_screen);
        Log.d(TAG, "onCreate called");

        // VEZBA 7: Uzmi ApiService iz RetrofitClient singleton-a
        apiService = RetrofitClient.getInstance().getApiService();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        postTextView           = findViewById(R.id.postTextView);
        komentarTextView       = findViewById(R.id.komentarTextView);
        ucitajPostButton       = findViewById(R.id.ucitajPostButton);
        ucitajKomentarButton   = findViewById(R.id.ucitajKomentarButton);
        ucitajKorisnikeButton  = findViewById(R.id.ucitajKorisnikeButton);
        toolbar                = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Retrofit — HTTP Zahtevi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        ucitajPostButton.setOnClickListener(v -> dohvatiPrviPost());
        ucitajKomentarButton.setOnClickListener(v -> dohvatiDrugiKomentar());
        ucitajKorisnikeButton.setOnClickListener(v -> dohvatiKorisnikeIBrojUToastu());
    }

    // =====================================================================
    // ZADATAK 3a: GET /posts/1 — Dohvati PRVI post i prikaži u TextView-u
    // =====================================================================
    private void dohvatiPrviPost() {
        postTextView.setText("Učitavanje...");

        // Kreiraj Call objekat (zahtev još nije poslat)
        Call<Post> call = apiService.getPostById(1);

        // enqueue() = pošalji zahtev ASINHRONO (ne blokira UI)
        call.enqueue(new Callback<Post>() {

            // Poziva se kada server vrati odgovor (čak i 404, 500...)
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body(); // Gson je već konvertovao JSON → Post
                    Log.d(TAG, "Post dobijen: " + post.getTitle());

                    // Prikaži u TextView
                    String prikaz =
                            "ID: " + post.getId() + "\n" +
                            "Naslov: " + post.getTitle() + "\n\n" +
                            "Sadržaj:\n" + post.getBody() + "\n\n" +
                            "Link: " + post.getLink() + "\n" +
                            "Komentara: " + post.getCommentCount();

                    postTextView.setText(prikaz);
                } else {
                    Log.e(TAG, "Greška: HTTP " + response.code());
                    postTextView.setText("Greška: HTTP " + response.code());
                }
            }

            // Poziva se samo pri mrežnoj grešci (nema interneta, timeout...)
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e(TAG, "Mrežna greška: " + t.getMessage());
                postTextView.setText("Mrežna greška: " + t.getMessage());
                Toast.makeText(RetrofitActivity.this,
                        "Nema veze sa serverom!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =====================================================================
    // ZADATAK 3b: GET /comments/2 — Dohvati DRUGI komentar i prikaži u TextView-u
    // =====================================================================
    private void dohvatiDrugiKomentar() {
        komentarTextView.setText("Učitavanje...");

        Call<Comment> call = apiService.getKomentarById(2);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Comment komentar = response.body();
                    Log.d(TAG, "Komentar dobijen od: " + komentar.getName());

                    String prikaz =
                            "ID komentara: " + komentar.getId() + "\n" +
                            "ID posta: " + komentar.getPostId() + "\n" +
                            "Autor: " + komentar.getName() + "\n" +
                            "Email: " + komentar.getEmail() + "\n\n" +
                            "Tekst:\n" + komentar.getBody();

                    komentarTextView.setText(prikaz);
                } else {
                    komentarTextView.setText("Greška: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e(TAG, "Mrežna greška: " + t.getMessage());
                komentarTextView.setText("Mrežna greška: " + t.getMessage());
            }
        });
    }

    // =====================================================================
    // ZADATAK 4: GET /users — Dohvati listu korisnika i prikaži BROJ u Toastu
    // =====================================================================
    private void dohvatiKorisnikeIBrojUToastu() {
        // Call sa List<ApiUser> — API vraća JSON niz [...]
        Call<List<ApiUser>> call = apiService.getSveKorisnike();

        call.enqueue(new Callback<List<ApiUser>>() {
            @Override
            public void onResponse(Call<List<ApiUser>> call, Response<List<ApiUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApiUser> korisnici = response.body();
                    int broj = korisnici.size();
                    Log.d(TAG, "Broj korisnika: " + broj);

                    // ZADATAK 4: Prikaži broj u Toast poruci
                    Toast.makeText(RetrofitActivity.this,
                            "Ukupno korisnika: " + broj,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RetrofitActivity.this,
                            "Greška pri učitavanju korisnika", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ApiUser>> call, Throwable t) {
                Log.e(TAG, "Greška: " + t.getMessage());
                Toast.makeText(RetrofitActivity.this,
                        "Mrežna greška!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
