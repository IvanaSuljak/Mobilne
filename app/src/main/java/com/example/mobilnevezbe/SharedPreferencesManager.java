package com.example.mobilnevezbe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * VEZBA 6: Centralna klasa za rad sa SharedPreferences.
 * Čuva:
 *   1) Podatke o ulogovanom korisniku (ime, email, telefon, uloga)
 *   2) Podešavanja sinhronizacije (interval u ms)
 *
 * SharedPreferences = lagana key-value baza podataka u XML fajlu,
 * čuva se u /data/data/com.example.mobilnevezbe/shared_prefs/
 */
public class SharedPreferencesManager {

    private static final String PREF_NAME = "MobilneVezbePref";

    // Ključevi za ulogovanog korisnika
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID      = "user_id";
    private static final String KEY_USER_NAME    = "user_name";
    private static final String KEY_USER_EMAIL   = "user_email";
    private static final String KEY_USER_PHONE   = "user_phone";
    private static final String KEY_USER_ULOGA   = "user_uloga";

    // Ključ za interval sinhronizacije
    private static final String KEY_SYNC_INTERVAL = "sync_interval";

    // Konstante za interval sinhronizacije (u milisekundama)
    public static final long SYNC_NEVER  = -1;
    public static final long SYNC_1_MIN  = 60_000L;
    public static final long SYNC_15_MIN = 15 * 60_000L;
    public static final long SYNC_30_MIN = 30 * 60_000L;

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // Singleton
    private static SharedPreferencesManager instance;

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    private SharedPreferencesManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // =============================================================
    // METODE ZA KORISNIKA
    // =============================================================

    /** Sačuvaj podatke ulogovanog korisnika */
    public void sacuvajUlogovanogKorisnika(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID,          user.getId());
        editor.putString(KEY_USER_NAME,     user.getName());
        editor.putString(KEY_USER_EMAIL,    user.getEmail());
        editor.putString(KEY_USER_PHONE,    user.getPhone());
        editor.putString(KEY_USER_ULOGA,    user.getUloga());
        editor.apply(); // apply() je asinhrono (brže od commit())
    }

    /** Ukloni sve podatke o ulogovanom korisniku (odjava) */
    public void odjaviKorisnika() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_ULOGA);
        editor.apply();
    }

    public boolean jeUlogovan()        { return prefs.getBoolean(KEY_IS_LOGGED_IN, false); }
    public int    getKorisnikId()      { return prefs.getInt(KEY_USER_ID, -1); }
    public String getKorisnickoIme()   { return prefs.getString(KEY_USER_NAME, ""); }
    public String getKorisnickoEmail() { return prefs.getString(KEY_USER_EMAIL, ""); }
    public String getKorisnickoTelefon() { return prefs.getString(KEY_USER_PHONE, ""); }

    /**
     * Vraća ulogu ulogovanog korisnika.
     * Moguće vrednosti: "vozac", "putnik", "administrator"
     */
    public String getUloga()           { return prefs.getString(KEY_USER_ULOGA, "putnik"); }

    // =============================================================
    // METODE ZA SINHRONIZACIJU
    // =============================================================

    /**
     * Sačuvaj interval sinhronizacije.
     * @param intervalMs konstanta SYNC_NEVER, SYNC_1_MIN, SYNC_15_MIN ili SYNC_30_MIN
     */
    public void sacuvajSyncInterval(long intervalMs) {
        editor.putLong(KEY_SYNC_INTERVAL, intervalMs);
        editor.apply();
    }

    /**
     * Vrati sačuvani interval sinhronizacije.
     * Default je 1 minut ako ništa nije izabrano.
     */
    public long getSyncInterval() {
        return prefs.getLong(KEY_SYNC_INTERVAL, SYNC_1_MIN);
    }

    /** Vrati čitljiv naziv izabranog intervala */
    public String getSyncIntervalNaziv() {
        long interval = getSyncInterval();
        if (interval == SYNC_NEVER)       return "Nikad";
        if (interval == SYNC_1_MIN)       return "Na svakih 1 min";
        if (interval == SYNC_15_MIN)      return "Na svakih 15 min";
        if (interval == SYNC_30_MIN)      return "Na svakih 30 min";
        return "Nepoznato";
    }
}
