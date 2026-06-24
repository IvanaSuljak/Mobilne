# ŠABLON — SQLite, SharedPreferences, ContentProvider

> **MASTER:** Otvori prvo `SABLON_MASTER_VODIC.md` → nađi "baza", "SharedPreferences" ili "kontakti".

> Svuda gde vidiš `TODO` → zameni sa svojim nazivima.
> Sve ostalo kopiraš bukvalno.

---

## KADA KORISTITI

| Zadatak kaže | Koristi KORAK |
|--------------|---------------|
| SQLite, baza, tabela, CRUD, upiši/obriši/čitaj | KORAK 1 + KORAK 2 |
| SharedPreferences, sačuvaj vrednost, sesija, podešavanja | KORAK 3 |
| Navigacija po ulozi (admin/vozač/putnik) | KORAK 4 |
| Interval sinhronizacije, RadioGroup | KORAK 5 |
| ContentProvider, kontakti, Contacts aplikacija | KORAK 6 |
| Nova Activity u Manifest-u | KORAK 7 |
| Odjava korisnika | KORAK 8 |

---

## TAČAN REDOSLED — SQLite (baza)

| # | Gde | Šta radiš | Kad |
|---|-----|-----------|-----|
| 1 | `java/.../Post.java` (ili drugi model) | Model klasa sa poljima (KORAK 1) | Pre DatabaseHelper |
| 2 | `java/.../DatabaseHelper.java` | Tabela, CRUD metode (KORAK 2) | Posle modela |
| 3 | `MainActivity.java` | `dbHelper = DatabaseHelper.getInstance(this)` | u onCreate |
| 4 | `MainActivity.java` | `dbHelper.dodaj()` / `getSve()` / `obrisi()` | U listenerima |

## TAČAN REDOSLED — SharedPreferences

| # | Gde | Šta | Kad |
|---|-----|-----|-----|
| 1 | `SharedPreferencesManager.java` ili direktno u Activity | KORAK 3 | Kad zadatak kaže "sačuvaj u SharedPreferences" |
| 2 | `MainActivity.java` | `prefs.edit().putString("kljuc", vrednost).apply()` | U Switch OFF ili dugmetu |

## TAČAN REDOSLED — ContentProvider (kontakti)

| # | Gde | Šta | Kad |
|---|-----|-----|-----|
| 1 | `AndroidManifest.xml` | `READ_CONTACTS` pre `<application>` | Pre koda |
| 2 | `MainActivity.java` | `provjeriDozvolu()` → `ucitajKontakte()` (KORAK 6) | U listeneru ili onCreate |
| 3 | `MainActivity.java` | `getContentResolver().query(...)` | Unutar ucitajKontakte |

> Gradle dependency **ne treba** za SQLite, SharedPreferences ni ContentProvider.

---

## KORAK 1 — Model klasa (npr. `Proizvod.java`, `Student.java`...)

```java
package com.example.TODO_IME_PAKETA;

public class TODO_ImeKlase {                    // npr. Proizvod, Student, Voznja...

    private int    id;
    private String TODO_polje1;                 // npr. naziv, ime, naslov...
    private String TODO_polje2;                 // npr. cena, email, datum...
    private String uloga;                       // ako nema uloge, izbaci ovo polje

    // Konstruktor za NOVU registraciju (bez ID)
    public TODO_ImeKlase(String TODO_polje1, String TODO_polje2) {
        this.TODO_polje1 = TODO_polje1;
        this.TODO_polje2 = TODO_polje2;
        this.uloga       = "TODO_default_uloga"; // npr. "korisnik", "putnik"
    }

    // Konstruktor za ČITANJE IZ BAZE (sa ID-om i ulogom)
    public TODO_ImeKlase(int id, String TODO_polje1, String TODO_polje2, String uloga) {
        this.id          = id;
        this.TODO_polje1 = TODO_polje1;
        this.TODO_polje2 = TODO_polje2;
        this.uloga       = uloga;
    }

    // Getters i Setters (Alt+Insert u Android Studiju → Generate → Getter and Setter)
    public int    getId()            { return id; }
    public String getTODO_polje1()   { return TODO_polje1; }
    public String getTODO_polje2()   { return TODO_polje2; }
    public String getUloga()         { return uloga; }

    public void setId(int id)                          { this.id = id; }
    public void setTODO_polje1(String TODO_polje1)     { this.TODO_polje1 = TODO_polje1; }
    public void setTODO_polje2(String TODO_polje2)     { this.TODO_polje2 = TODO_polje2; }
    public void setUloga(String uloga)                 { this.uloga = uloga; }

    @Override
    public String toString() {
        return TODO_polje1 + " - " + TODO_polje2;
    }
}
```

---

## KORAK 2 — SQLite baza (`DatabaseHelper.java`)

```java
package com.example.TODO_IME_PAKETA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "TODO_ime_baze.db";  // npr. "app.db"
    private static final int    DATABASE_VERSION = 1;

    // TODO: Promeni naziv tabele i kolona prema svom zadatku
    public static final String TABLE_TODO     = "TODO_tabela";          // npr. "korisnici"
    public static final String COLUMN_ID      = "id";                   // ovo ostaje isto
    public static final String COLUMN_TODO_1  = "TODO_kolona1";         // npr. "ime"
    public static final String COLUMN_TODO_2  = "TODO_kolona2";         // npr. "email"
    public static final String COLUMN_ULOGA   = "uloga";                // ako nema uloge, izbaci

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_TODO + " (" +
            COLUMN_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TODO_1 + " TEXT NOT NULL, " +
            COLUMN_TODO_2 + " TEXT UNIQUE NOT NULL, " +  // UNIQUE ako ne sme biti duplikata
            COLUMN_ULOGA  + " TEXT NOT NULL DEFAULT 'TODO_default_uloga'" +
            ")";

    // Singleton
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        // TODO: ubaci testne podatke ovde ako treba
        // ubaciTestne(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    // ---- CREATE ----
    public long dodaj(TODO_ImeKlase objekat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_1, objekat.getTODO_polje1());
        values.put(COLUMN_TODO_2, objekat.getTODO_polje2());
        values.put(COLUMN_ULOGA,  objekat.getUloga());
        long id = db.insert(TABLE_TODO, null, values);
        db.close();
        return id; // -1 = greška (npr. dupli UNIQUE)
    }

    // ---- READ ALL ----
    public List<TODO_ImeKlase> getSve() {
        List<TODO_ImeKlase> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, null, null, null, null, null, COLUMN_TODO_1);
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToObjekat(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // ---- READ ONE (za login) ----
    // TODO: Promeni parametar u ono po čemu tražiš (npr. email+lozinka, korisnickoIme...)
    public TODO_ImeKlase pronadjiZaLogin(String TODO_polje2, String TODO_lozinka) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, null,
                COLUMN_TODO_2 + "=? AND TODO_kolona_lozinka=?",
                new String[]{TODO_polje2, TODO_lozinka},
                null, null, null);
        TODO_ImeKlase obj = null;
        if (cursor.moveToFirst()) obj = cursorToObjekat(cursor);
        cursor.close();
        db.close();
        return obj;
    }

    // ---- UPDATE ----
    public int azuriraj(TODO_ImeKlase objekat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_1, objekat.getTODO_polje1());
        values.put(COLUMN_TODO_2, objekat.getTODO_polje2());
        values.put(COLUMN_ULOGA,  objekat.getUloga());
        int rows = db.update(TABLE_TODO, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(objekat.getId())});
        db.close();
        return rows;
    }

    // ---- DELETE ----
    public void obrisi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ---- HELPER: Cursor → objekat ----
    private TODO_ImeKlase cursorToObjekat(Cursor cursor) {
        int    id    = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String val1  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_1));
        String val2  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_2));
        String uloga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ULOGA));
        return new TODO_ImeKlase(id, val1, val2, uloga);
    }

    // ---- BONUS: Provjeri duplikat ----
    public boolean vrednostPostoji(String vrednost) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, new String[]{COLUMN_ID},
                COLUMN_TODO_2 + "=?", new String[]{vrednost}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
```

---

## KORAK 3 — SharedPreferences (`SharedPreferencesManager.java`)

```java
package com.example.TODO_IME_PAKETA;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "TODO_AppPrefs";   // može biti bilo šta

    // TODO: Definiši ključeve za sve što hoćeš da sačuvaš
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID      = "user_id";
    private static final String KEY_USER_NAME    = "user_name";     // TODO: promeni
    private static final String KEY_USER_ROLE    = "user_role";
    private static final String KEY_SYNC_INTERVAL = "sync_interval";

    // Konstante za interval sinhronizacije
    public static final long SYNC_NEVER  = -1;
    public static final long SYNC_1_MIN  = 60_000L;
    public static final long SYNC_15_MIN = 15 * 60_000L;
    public static final long SYNC_30_MIN = 30 * 60_000L;

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    private static SharedPreferencesManager instance;
    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) instance = new SharedPreferencesManager(context.getApplicationContext());
        return instance;
    }
    private SharedPreferencesManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // --- Korisnik ---
    public void sacuvajKorisnika(TODO_ImeKlase obj) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID,          obj.getId());
        editor.putString(KEY_USER_NAME,     obj.getTODO_polje1()); // TODO: promeni
        editor.putString(KEY_USER_ROLE,     obj.getUloga());
        editor.apply();
    }

    public void odjavi() {
        editor.clear().apply();   // briše SVE ključeve
    }

    public boolean jeUlogovan()  { return prefs.getBoolean(KEY_IS_LOGGED_IN, false); }
    public int    getId()        { return prefs.getInt(KEY_USER_ID, -1); }
    public String getIme()       { return prefs.getString(KEY_USER_NAME, ""); }
    public String getUloga()     { return prefs.getString(KEY_USER_ROLE, "TODO_default"); }

    // --- Sinhronizacija ---
    public void sacuvajSyncInterval(long intervalMs) {
        editor.putLong(KEY_SYNC_INTERVAL, intervalMs);
        editor.apply();
    }

    public long getSyncInterval() {
        return prefs.getLong(KEY_SYNC_INTERVAL, SYNC_1_MIN);
    }

    public String getSyncNaziv() {
        long i = getSyncInterval();
        if (i == SYNC_NEVER)  return "Nikad";
        if (i == SYNC_1_MIN)  return "Na svakih 1 min";
        if (i == SYNC_15_MIN) return "Na svakih 15 min";
        if (i == SYNC_30_MIN) return "Na svakih 30 min";
        return "Nepoznato";
    }
}
```

---

## KORAK 4 — Navigacija po ulozi (u LoginActivity)

```java
// TODO: Ovo ide u onClick() dugmeta za login

DatabaseHelper db = DatabaseHelper.getInstance(this);
SharedPreferencesManager sp = SharedPreferencesManager.getInstance(this);

// 1. Provjeri u bazi
TODO_ImeKlase obj = db.pronadjiZaLogin(emailEditText.getText().toString(),
                                        passwordEditText.getText().toString());

if (obj != null) {
    // 2. Sačuvaj u SharedPreferences
    sp.sacuvajKorisnika(obj);

    // 3. Navigiraj prema ulozi
    navigirajPoUlozi(obj.getUloga());
    finish();
} else {
    Toast.makeText(this, "Pogrešni podaci!", Toast.LENGTH_SHORT).show();
}

// ---

private void navigirajPoUlozi(String uloga) {
    Intent intent;
    switch (uloga) {
        case "TODO_uloga1": intent = new Intent(this, TODO_Ekran1Activity.class); break;
        case "TODO_uloga2": intent = new Intent(this, TODO_Ekran2Activity.class); break;
        default:            intent = new Intent(this, TODO_DefaultActivity.class); break;
    }
    startActivity(intent);
}
```

---

## KORAK 5 — SettingsActivity (interval sinhronizacije)

```java
// TODO: Layout treba da ima RadioGroup sa ID-jem syncRadioGroup
// i 4 RadioButton-a: radioNikad, radio1Min, radio15Min, radio30Min

SharedPreferencesManager sp = SharedPreferencesManager.getInstance(this);

// Učitaj sačuvano pri otvaranju ekrana:
long saved = sp.getSyncInterval();
if      (saved == SharedPreferencesManager.SYNC_NEVER)  radioNikad.setChecked(true);
else if (saved == SharedPreferencesManager.SYNC_15_MIN) radio15Min.setChecked(true);
else if (saved == SharedPreferencesManager.SYNC_30_MIN) radio30Min.setChecked(true);
else                                                     radio1Min.setChecked(true);

// Sačuvaj kada korisnik klikne dugme:
saveButton.setOnClickListener(v -> {
    long interval;
    int checkedId = syncRadioGroup.getCheckedRadioButtonId();
    if      (checkedId == R.id.radioNikad) interval = SharedPreferencesManager.SYNC_NEVER;
    else if (checkedId == R.id.radio15Min) interval = SharedPreferencesManager.SYNC_15_MIN;
    else if (checkedId == R.id.radio30Min) interval = SharedPreferencesManager.SYNC_30_MIN;
    else                                   interval = SharedPreferencesManager.SYNC_1_MIN;

    sp.sacuvajSyncInterval(interval);

    // Restartuj servis sa novim intervalom
    TODO_ImeServisa.stopService(this);
    if (interval != SharedPreferencesManager.SYNC_NEVER) {
        TODO_ImeServisa.startService(this);
    }

    Toast.makeText(this, "Sačuvano: " + sp.getSyncNaziv(), Toast.LENGTH_SHORT).show();
    finish();
});
```

---

## KORAK 6 — ContentProvider (čitanje kontakata)

```java
// TODO: Dodaj u AndroidManifest.xml:
// <uses-permission android:name="android.permission.READ_CONTACTS" />

// ---- Provjera i zahtjev dozvole ----
private static final int REQUEST_CONTACTS = 200;

private void provjeriDozvolu() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
        ucitajKontakte();
    } else {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        ucitajKontakte();
    }
}

// ---- Čitanje iz ContentProvider-a ----
private void ucitajKontakte() {
    List<String> lista = new ArrayList<>();

    // URI = adresa ContentProvider-a
    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    // Projekcija = koje kolone hoćemo (kao SELECT kolona1, kolona2)
    String[] projekcija = {
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    // Query = čitaj podatke
    Cursor cursor = getContentResolver().query(
        uri,                                                           // šta čitamo
        projekcija,                                                    // koje kolone
        null,                                                          // WHERE (null=sve)
        null,                                                          // WHERE args
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" // ORDER BY
    );

    if (cursor != null) {
        while (cursor.moveToNext()) {
            String ime  = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String broj = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            lista.add(ime + " — " + broj);
        }
        cursor.close();
    }

    // Prikaži u ListView
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, lista);
    TODO_listView.setAdapter(adapter);
}
```

---

## KORAK 7 — AndroidManifest.xml (šta dodati)

```xml
<!-- Dozvole (unutar <manifest>, van <application>) -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_CONTACTS" />   <!-- za ContentProvider -->

<!-- Nova Activity (unutar <application>) -->
<activity android:name=".TODO_NovaActivity"   android:exported="false" />
<activity android:name=".SettingsActivity"     android:exported="false" />
<activity android:name=".ContactsActivity"     android:exported="false" />
```

---

## KORAK 8 — Odjava korisnika (universalni pattern)

```java
// Uvek isto, samo promeni TojeActivity u ime aktivnosti gde si
logoutButton.setOnClickListener(v -> {
    SharedPreferencesManager.getInstance(this).odjavi();
    Intent intent = new Intent(this, LoginScreenActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    // finish() nije potreban zbog FLAG_ACTIVITY_CLEAR_TASK
});
```

---

## Brza referenca — tipovi podataka u SharedPreferences

| Tip podatka | putString | getBoolean | getLong |
|---|---|---|---|
| String (ime, email) | `editor.putString("kljuc", vrednost)` | — | — |
| boolean (ulogovan?) | — | `editor.putBoolean("kljuc", true)` | — |
| int (id) | — | — | `editor.putInt("kljuc", 5)` |
| long (interval ms) | — | — | `editor.putLong("kljuc", 60000L)` |

> **Uvek na kraju:** `editor.apply();`

---

## Brza referenca — ContentProvider URI-ji

| Šta čitaš | URI klasa | URI konstanta |
|---|---|---|
| Kontakti (sa brojem) | `ContactsContract.CommonDataKinds.Phone` | `.CONTENT_URI` |
| Kontakti (bez broja) | `ContactsContract.Contacts` | `.CONTENT_URI` |
| Email kontakata | `ContactsContract.CommonDataKinds.Email` | `.CONTENT_URI` |
| Kalendar | `CalendarContract.Events` | `.CONTENT_URI` |
| Slike/Video | `MediaStore.Images.Media` | `.EXTERNAL_CONTENT_URI` |

---

## Kolokvijum 2 — DatabaseHelper za postove (tabela `postovi`)

**Fajl:** `DatabaseHelper.java` u glavnom paketu (pored `Post.java`)

Ključne metode za zadatke 6–7:

| Metoda | Za šta (zadatak) |
|--------|------------------|
| `dodajPost(Post p)` | Zadatak 6 — upiši post iz API-ja u bazu |
| `getSviPostovi()` | Lista svih postova iz baze |
| `getPrviPost()` | Zadatak 6 — prvi red u tabeli (ORDER BY id ASC LIMIT 1) |
| `obrisiPrviPost()` | Zadatak 7 — obriši prvi red, vrati `false` ako je prazno |

```java
// CREATE TABLE
"CREATE TABLE postovi (" +
"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
"userId INTEGER, title TEXT, body TEXT, link TEXT, comment_count INTEGER)"

// dodajPost — ContentValues iz Post objekta
public long dodajPost(Post post) {
    ContentValues v = new ContentValues();
    v.put("userId", post.getUserId());
    v.put("title", post.getTitle());
    v.put("body", post.getBody());
    v.put("link", post.getLink());
    v.put("comment_count", post.getCommentCount());
    return db.insert("postovi", null, v);
}

// getPrviPost — NE po id=1, već prvi u tabeli!
Cursor c = db.query("postovi", null, null, null, null, null, "id ASC", "1");

// obrisiPrviPost
Post prvi = getPrviPost();
if (prvi == null) return false;  // → zadatak 7: notifikacija
db.delete("postovi", "id=?", new String[]{String.valueOf(prvi.getId())});
return true;
```

### Prvi vs poslednji vs po ID — samo menjaš ORDER BY

| Zadatak kaže | Metoda | SQL sort u `query()` |
|--------------|--------|----------------------|
| **prvi** u tabeli | `getPrviPost()` / `obrisiPrviPost()` | `"id ASC", "1"` |
| **poslednji** u tabeli | `getPoslednjiPost()` / `obrisiPosledniPost()` | `"id DESC", "1"` |
| **po ID=5** (npr. treći dodati) | `getPostById(5)` / `obrisiPoId(5)` | `WHERE id=?` |

**Poslednji post — primer:**

```java
public Post getPoslednjiPost() {
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.query(TABLE, null, null, null, null, null, "id DESC", "1");
    // ... isto cursorToPost kao getPrviPost ...
}

public boolean obrisiPosledniPost() {
    Post p = getPoslednjiPost();
    if (p == null) return false;
    getWritableDatabase().delete(TABLE, "id=?", new String[]{String.valueOf(p.getId())});
    return true;
}
```

U MainActivity listener samo zameni metodu:

```java
// prvi  → dbHelper.obrisiPrviPost()
// poslednji → dbHelper.obrisiPosledniPost()
obrisiButton.setOnClickListener(v -> {
    if (!dbHelper.obrisiPosledniPost()) {
        prikaziNotifikaciju("Nema više postova!");
    }
});
```

> **Obrazac isti** — menja se samo `ASC` ↔ `DESC` ili `WHERE id=?`.

U MainActivity:
```java
dbHelper = DatabaseHelper.getInstance(this);
```

> **Zajedno sa Retrofit-om:** vidi `SABLON_Retrofit_...md` sekcija "Kolokvijum 2 — Post model"

---

## TODO lista — šta zameniti

- [ ] `TODO_IME_PAKETA` → tvoj paket (npr. `com.example.mojaplikacija`)
- [ ] `TODO_ImeKlase` → naziv modela (npr. `Korisnik`, `Proizvod`, `Student`)
- [ ] `TODO_ime_baze` → naziv .db fajla (npr. `moja_app.db`)
- [ ] `TODO_tabela` → naziv tabele (npr. `korisnici`, `proizvodi`)
- [ ] `TODO_kolona1`, `TODO_kolona2` → nazivi kolona (npr. `ime`, `email`)
- [ ] `TODO_polje1`, `TODO_polje2` → nazivi atributa klase (isto kao kolone)
- [ ] `TODO_uloga1`, `TODO_uloga2` → vrednosti uloga (npr. `"vozac"`, `"putnik"`)
- [ ] `TODO_Ekran1Activity`, `TODO_DefaultActivity` → nazivi tvojih ekrana
- [ ] `TODO_listView` → ID ListView-a u layoutu
- [ ] `TODO_ImeServisa` → naziv tvog Service-a


Jedino što moraš ručno za Vezbu 8 je staviti pravi Google Maps API ključ u AndroidManifest.xml na mesto YOUR_API_KEY_OVDE.

Kako dobiti Google Maps API ključ (5 koraka)
1. Idi na → console.cloud.google.com
2. Gore levo → "Select a project" → "New Project" → daj ime → Create
3. Levo meni → "APIs & Services" → "Enable APIs and Services" → traži "Maps SDK for Android" → klikni → Enable
4. Levo meni → "APIs & Services" → "Credentials" → "+ Create Credentials" → "API Key" → kopiraj ključ koji izgleda ovako: AIzaSyB3x...
5. U projektu otvori AndroidManifest.xml i zameni:

android:value="YOUR_API_KEY_OVDE"
sa:

android:value="AIzaSyB3x_TVOJ_PRAVI_KLJUC"
Treba ti Google nalog. Kreditna kartica se može tražiti pri registraciji ali Maps SDK ima besplatni tier koji je sasvim dovoljan za vežbe i kolokvijum.

