package com.example.mobilnevezbe.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * VEZBA 7: Singleton klasa za konfiguraciju Retrofit-a.
 *
 * Retrofit je HTTP klijent koji:
 *  1. Šalje zahteve ka REST API-ju
 *  2. Prima JSON odgovor
 *  3. Automatski konvertuje JSON → Java objekat (putem Gson konvertera)
 *
 * Singleton osigurava da se jedna Retrofit instanca koristi kroz celu aplikaciju.
 */
public class RetrofitClient {

    // VEZBA 7: Baza URL-a — mora se završiti sa "/"
    private static final String BASE_URL = "https://dummy-json.mock.beeceptor.com/";

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        // VEZBA 7: OkHttp logger — ispisuje svaki zahtev i odgovor u Logcat-u
        // Veoma korisno za debugging tokom razvoja
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // VEZBA 7: Kreiranje Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)                          // adresa servera
                .client(okHttpClient)                       // HTTP klijent sa loggerom
                .addConverterFactory(GsonConverterFactory.create()) // JSON → Java konverzija
                .build();

        // Kreiranje implementacije ApiService interfejsa
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
