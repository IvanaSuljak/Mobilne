package com.example.mobilnevezbe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * VEZBA 6: SQLite baza podataka za čuvanje korisnika.
 * Singleton pattern - jedna instanca za celu aplikaciju.
 * Sadrži sve CRUD operacije (Create, Read, Update, Delete).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Ime fajla baze podataka (čuva se u /data/data/com.example.mobilnevezbe/databases/)
    private static final String DATABASE_NAME = "mobilne_vezbe.db";
    private static final int DATABASE_VERSION = 1;

    // Naziv tabele i nazivi kolona
    public static final String TABLE_KORISNICI = "korisnici";
    public static final String COLUMN_ID       = "id";
    public static final String COLUMN_IME      = "ime";
    public static final String COLUMN_EMAIL    = "email";
    public static final String COLUMN_TELEFON  = "telefon";
    public static final String COLUMN_LOZINKA  = "lozinka";
    public static final String COLUMN_ULOGA    = "uloga"; // vozac / putnik / administrator

    // SQL naredba za kreiranje tabele
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_KORISNICI + " (" +
            COLUMN_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IME     + " TEXT NOT NULL, " +
            COLUMN_EMAIL   + " TEXT UNIQUE NOT NULL, " +
            COLUMN_TELEFON + " TEXT, " +
            COLUMN_LOZINKA + " TEXT NOT NULL, " +
            COLUMN_ULOGA   + " TEXT NOT NULL DEFAULT 'putnik'" +
            ")";

    // Singleton instanca
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Poziva se jednom - kada se baza kreira prvi put
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        Log.d(TAG, "Tabela '" + TABLE_KORISNICI + "' kreirana.");
        dodajDefaultneKorisnike(db);
    }

    // Poziva se kada se promeni DATABASE_VERSION - briše staru i kreira novu
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KORISNICI);
        onCreate(db);
    }

    // Unos testnih korisnika pri prvom pokretanju
    private void dodajDefaultneKorisnike(SQLiteDatabase db) {
        ubaciKorisnikaDirectly(db, "Administrator", "admin@app.com", "0600000000", "admin123", "administrator");
        ubaciKorisnikaDirectly(db, "Marko Markovic", "vozac@app.com", "0611111111", "vozac123", "vozac");
        ubaciKorisnikaDirectly(db, "Ana Anic", "putnik@app.com", "0622222222", "putnik123", "putnik");
        Log.d(TAG, "Default korisnici ubačeni u bazu.");
    }

    private void ubaciKorisnikaDirectly(SQLiteDatabase db, String ime, String email,
                                         String telefon, String lozinka, String uloga) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IME, ime);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TELEFON, telefon);
        values.put(COLUMN_LOZINKA, lozinka);
        values.put(COLUMN_ULOGA, uloga);
        db.insert(TABLE_KORISNICI, null, values);
    }

    // =========================================================
    // CREATE - Dodaj novog korisnika
    // Vraća ID novog reda, ili -1 ako email već postoji (UNIQUE)
    // =========================================================
    public long dodajKorisnika(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IME,     user.getName());
        values.put(COLUMN_EMAIL,   user.getEmail());
        values.put(COLUMN_TELEFON, user.getPhone());
        values.put(COLUMN_LOZINKA, user.getPassword());
        values.put(COLUMN_ULOGA,   user.getUloga());

        long id = db.insert(TABLE_KORISNICI, null, values);
        db.close();
        Log.d(TAG, "dodajKorisnika: id=" + id + ", email=" + user.getEmail());
        return id;
    }

    // =========================================================
    // READ - Svi korisnici (sortirani po imenu)
    // =========================================================
    public List<User> getSviKorisnici() {
        List<User> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KORISNICI, null, null, null, null, null, COLUMN_IME);

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getSviKorisnici: pronađeno " + lista.size() + " korisnika.");
        return lista;
    }

    // =========================================================
    // READ - Jedan korisnik po email-u i lozinki (za login)
    // Vraća null ako kombinacija ne postoji
    // =========================================================
    public User pronadjiKorisnikaZaLogin(String email, String lozinka) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_KORISNICI, null,
                COLUMN_EMAIL + "=? AND " + COLUMN_LOZINKA + "=?",
                new String[]{email, lozinka},
                null, null, null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        db.close();
        return user;
    }

    // =========================================================
    // READ - Jedan korisnik po ID
    // =========================================================
    public User getKorisnikById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_KORISNICI, null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        db.close();
        return user;
    }

    // =========================================================
    // UPDATE - Ažuriraj korisnika (pronalazi ga po ID)
    // Vraća broj ažuriranih redova (1 = uspeh, 0 = nije pronađen)
    // =========================================================
    public int azurirajKorisnika(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IME,     user.getName());
        values.put(COLUMN_EMAIL,   user.getEmail());
        values.put(COLUMN_TELEFON, user.getPhone());
        values.put(COLUMN_LOZINKA, user.getPassword());
        values.put(COLUMN_ULOGA,   user.getUloga());

        int rows = db.update(TABLE_KORISNICI, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        Log.d(TAG, "azurirajKorisnika: ažurirano " + rows + " red(ova), id=" + user.getId());
        return rows;
    }

    // =========================================================
    // DELETE - Obriši korisnika po ID
    // =========================================================
    public void obrisiKorisnika(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_KORISNICI, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        Log.d(TAG, "obrisiKorisnika: obrisano " + rows + " red(ova), id=" + id);
    }

    // =========================================================
    // Proveri da li email već postoji u bazi
    // =========================================================
    public boolean emailPostoji(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KORISNICI, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // =========================================================
    // Helper: pretvara trenutni red Cursor-a u User objekat
    // =========================================================
    private User cursorToUser(Cursor cursor) {
        int    id      = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String ime     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IME));
        String email   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        String telefon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFON));
        String lozinka = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOZINKA));
        String uloga   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ULOGA));
        return new User(id, ime, email, telefon, lozinka, uloga);
    }
}
