# VEZBA 6 — Dobavljači sadržaja (ContentProvider) — Kompletna dokumentacija

> **Svrha ovog dokumenta:** Sablon za kolokvijum. Svaki zadatak je objašnjen: ŠTA je urađeno, ZAŠTO, KAKO i ŠTA svaki red koda radi.

---

## Sadržaj

1. [SQLite baza podataka — DatabaseHelper](#1-sqlite-baza-podataka--databasehelper)
2. [SharedPreferences — korisnik i uloga](#2-sharedpreferences--korisnik-i-uloga)
3. [SharedPreferences — podešavanja sinhronizacije](#3-sharedpreferences--pode%C5%A1avanja-sinhronizacije)
4. [ContentProvider — čitanje kontakata](#4-contentprovider--čitanje-kontakata)
5. [Navigacija po ulozi](#5-navigacija-po-ulozi)
6. [Kompletan tok aplikacije](#6-kompletan-tok-aplikacije)
7. [Šablon za kolokvijum — brzi podsetnik](#7-%C5%A1ablon-za-kolokvijum--brzi-podsetnik)

---

## 1. SQLite baza podataka — DatabaseHelper

### Šta je SQLite?

SQLite je **ugrađena relacijska baza podataka** koja dolazi sa svakim Android uređajem.  
Podaci se čuvaju u fajlu na putanji:  
`/data/data/com.example.TVOJA_APLIKACIJA/databases/ime_baze.db`

### Kada koristiti SQLite umesto SharedPreferences?

| Situacija | Koristiti |
|-----------|-----------|
| Lista korisnika, produkta, poruka | **SQLite** |
| Token za login, tema, jezik | **SharedPreferences** |
| Kompleksni upiti, relacije | **SQLite** |
| Jedno/dva jednostavna podešavanja | **SharedPreferences** |

### Kreiranje klase DatabaseHelper

**Pravilo:** Klasa mora da `extends SQLiteOpenHelper`.

```java
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "mobilne_vezbe.db"; // ime .db fajla
    private static final int    DATABASE_VERSION = 1; // povećaj kada promeniš strukturu

    // Nazivi tabele i kolona — koristiti konstante, ne hardkodovane stringove!
    public static final String TABLE_KORISNICI = "korisnici";
    public static final String COLUMN_ID       = "id";
    public static final String COLUMN_IME      = "ime";
    public static final String COLUMN_EMAIL    = "email";
    public static final String COLUMN_TELEFON  = "telefon";
    public static final String COLUMN_LOZINKA  = "lozinka";
    public static final String COLUMN_ULOGA    = "uloga";
```

### SQL za kreiranje tabele

```java
private static final String SQL_CREATE_TABLE =
    "CREATE TABLE korisnici (" +
    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // auto-increment ID
    "ime TEXT NOT NULL, " +                      // obavezno polje
    "email TEXT UNIQUE NOT NULL, " +             // UNIQUE = ne mogu dva ista emaila
    "telefon TEXT, " +                           // nije obavezno
    "lozinka TEXT NOT NULL, " +
    "uloga TEXT NOT NULL DEFAULT 'putnik'" +     // default vrednost
    ")";
```

### Singleton pattern

Baza se pravi **jednom** i deli se kroz celu aplikaciju:

```java
private static DatabaseHelper instance;

public static synchronized DatabaseHelper getInstance(Context context) {
    if (instance == null) {
        // Koristi applicationContext da ne čuvaš referencu na Activity
        instance = new DatabaseHelper(context.getApplicationContext());
    }
    return instance;
}
```

### Obavezne override metode

```java
@Override
public void onCreate(SQLiteDatabase db) {
    // Poziva se JEDNOM kada se baza kreira prvi put
    db.execSQL(SQL_CREATE_TABLE);
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Poziva se kada se poveća DATABASE_VERSION
    // Standardni pristup: obriši staro, napravi novo
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_KORISNICI);
    onCreate(db);
}
```

### CRUD Operacije

#### CREATE — Dodavanje korisnika

```java
public long dodajKorisnika(User user) {
    SQLiteDatabase db = this.getWritableDatabase(); // otvori bazu za pisanje
    ContentValues values = new ContentValues();     // key-value mapa podataka
    values.put(COLUMN_IME,     user.getName());
    values.put(COLUMN_EMAIL,   user.getEmail());
    values.put(COLUMN_TELEFON, user.getPhone());
    values.put(COLUMN_LOZINKA, user.getPassword());
    values.put(COLUMN_ULOGA,   user.getUloga());

    long id = db.insert(TABLE_KORISNICI, null, values);
    // id = -1 ako UNIQUE constraint zabroni unos (dupli email)
    db.close(); // UVEK zatvori bazu!
    return id;
}
```

#### READ — Čitanje svih korisnika

```java
public List<User> getSviKorisnici() {
    List<User> lista = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase(); // otvori za čitanje

    // query() = SELECT * FROM korisnici ORDER BY ime
    Cursor cursor = db.query(TABLE_KORISNICI, null, null, null, null, null, COLUMN_IME);
    //                          tabela     kolone WHERE  args  GROUP HAVING ORDER

    if (cursor.moveToFirst()) {  // pomeri se na prvi red
        do {
            lista.add(cursorToUser(cursor));
        } while (cursor.moveToNext()); // idi na sledeći red
    }
    cursor.close(); // UVEK zatvori cursor!
    db.close();
    return lista;
}
```

#### READ — Login provjera (email + lozinka)

```java
public User pronadjiKorisnikaZaLogin(String email, String lozinka) {
    SQLiteDatabase db = this.getReadableDatabase();

    // SELECT * FROM korisnici WHERE email=? AND lozinka=?
    Cursor cursor = db.query(
        TABLE_KORISNICI, null,
        COLUMN_EMAIL + "=? AND " + COLUMN_LOZINKA + "=?",
        new String[]{email, lozinka}, // ? se zamenjuju ovim vrednostima
        null, null, null
    );

    User user = null;
    if (cursor.moveToFirst()) {
        user = cursorToUser(cursor); // konvertuj red u User objekat
    }
    cursor.close();
    db.close();
    return user; // null = pogrešni podaci
}
```

#### UPDATE — Ažuriranje korisnika

```java
public int azurirajKorisnika(User user) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_IME, user.getName());
    // ... ostala polja ...

    // UPDATE korisnici SET ... WHERE id=?
    int rows = db.update(TABLE_KORISNICI, values,
        COLUMN_ID + "=?",
        new String[]{String.valueOf(user.getId())});
    db.close();
    return rows; // broj ažuriranih redova (1 = uspeh)
}
```

#### DELETE — Brisanje korisnika

```java
public void obrisiKorisnika(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    // DELETE FROM korisnici WHERE id=?
    db.delete(TABLE_KORISNICI, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    db.close();
}
```

#### Helper — Cursor u User objekat

```java
private User cursorToUser(Cursor cursor) {
    // getColumnIndexOrThrow() vraća indeks kolone po imenu
    int    id      = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
    String ime     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IME));
    String email   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
    String telefon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFON));
    String lozinka = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOZINKA));
    String uloga   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ULOGA));
    return new User(id, ime, email, telefon, lozinka, uloga);
}
```

### Korišćenje u Aktivnosti

```java
// Inicijalizacija (jednom, u onCreate)
DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

// Dodavanje
User noviKorisnik = new User("Ana", "ana@mail.com", "065...", "lozinka", "putnik");
long id = dbHelper.dodajKorisnika(noviKorisnik);
if (id == -1) { /* email već postoji */ }

// Login provjera
User user = dbHelper.pronadjiKorisnikaZaLogin(email, lozinka);
if (user != null) { /* uspješan login */ }

// Svi korisnici
List<User> lista = dbHelper.getSviKorisnici();

// Brisanje
dbHelper.obrisiKorisnika(userId);
```

---

## 2. SharedPreferences — Korisnik i uloga

### Šta su SharedPreferences?

SharedPreferences je **key-value skladište** za male količine podataka.  
Čuva se kao XML fajl: `/data/data/com.example.APP/shared_prefs/MobilneVezbePref.xml`

### Kada koristiti?

- Korisnikovi podaci sesije (ime, email, uloga)  
- Login status (je li korisnik ulogovan?)  
- Podešavanja (tema, jezik, interval sinhronizacije)  
- Tokeni, flagovi

### Kreiranje SharedPreferencesManager

```java
public class SharedPreferencesManager {

    private static final String PREF_NAME = "MobilneVezbePref"; // ime XML fajla

    // Ključevi = nazivi promenljivih u XML fajlu
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID      = "user_id";
    private static final String KEY_USER_NAME    = "user_name";
    private static final String KEY_USER_EMAIL   = "user_email";
    private static final String KEY_USER_PHONE   = "user_phone";
    private static final String KEY_USER_ULOGA   = "user_uloga";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    private SharedPreferencesManager(Context context) {
        // MODE_PRIVATE = samo ova aplikacija može da čita ovaj fajl
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
```

### Čuvanje podataka

```java
public void sacuvajUlogovanogKorisnika(User user) {
    editor.putBoolean(KEY_IS_LOGGED_IN, true);
    editor.putInt(KEY_USER_ID,          user.getId());
    editor.putString(KEY_USER_NAME,     user.getName());
    editor.putString(KEY_USER_EMAIL,    user.getEmail());
    editor.putString(KEY_USER_PHONE,    user.getPhone());
    editor.putString(KEY_USER_ULOGA,    user.getUloga());
    editor.apply(); // VAZNO: apply() je asinhrono (brzo), commit() je sinhrono (sporo)
}
```

### Čitanje podataka

```java
public boolean jeUlogovan() {
    return prefs.getBoolean(KEY_IS_LOGGED_IN, false); // false = default ako ključ ne postoji
}

public String getUloga() {
    return prefs.getString(KEY_USER_ULOGA, "putnik"); // "putnik" = default
}

public String getKorisnickoIme() {
    return prefs.getString(KEY_USER_NAME, ""); // "" = default
}
```

### Brisanje (odjava)

```java
public void odjaviKorisnika() {
    editor.putBoolean(KEY_IS_LOGGED_IN, false);
    editor.remove(KEY_USER_NAME);   // briše specifičan ključ
    editor.remove(KEY_USER_EMAIL);
    editor.remove(KEY_USER_ULOGA);
    editor.apply();
    // Alternativa za brisanje svega: editor.clear().apply();
}
```

### Auto-login u LoginScreenActivity

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    spManager = SharedPreferencesManager.getInstance(this);

    // Ako je korisnik već ulogovan, preskoči login ekran
    if (spManager.jeUlogovan()) {
        navigirajPoUlozi(spManager.getUloga());
        finish(); // zatvori LoginScreen
        return;
    }

    // ... inače prikaži login formu
}
```

---

## 3. SharedPreferences — Podešavanja sinhronizacije

### Konstante za interval

```java
public static final long SYNC_NEVER  = -1;
public static final long SYNC_1_MIN  = 60_000L;        // 60 * 1000 ms
public static final long SYNC_15_MIN = 15 * 60_000L;   // 900 000 ms
public static final long SYNC_30_MIN = 30 * 60_000L;   // 1 800 000 ms
```

### Čuvanje i čitanje intervala

```java
private static final String KEY_SYNC_INTERVAL = "sync_interval";

public void sacuvajSyncInterval(long intervalMs) {
    editor.putLong(KEY_SYNC_INTERVAL, intervalMs);
    editor.apply();
}

public long getSyncInterval() {
    return prefs.getLong(KEY_SYNC_INTERVAL, SYNC_1_MIN); // default: 1 minut
}
```

### Ekran za podešavanja — SettingsActivity

Korisnik bira interval putem RadioButton-a:

```java
// Layout: RadioGroup sa 4 RadioButton-a
// radioNikad, radio1Min, radio15Min, radio30Min

private void ucitajPodešavanja() {
    long savedInterval = spManager.getSyncInterval();
    // Označi RadioButton koji odgovara sačuvanom intervalu
    if (savedInterval == SYNC_NEVER)       radioNikad.setChecked(true);
    else if (savedInterval == SYNC_15_MIN) radio15Min.setChecked(true);
    else if (savedInterval == SYNC_30_MIN) radio30Min.setChecked(true);
    else                                   radio1Min.setChecked(true);
}

saveButton.setOnClickListener(v -> {
    long interval = getOdabraniInterval(); // čita koji je RadioButton oznacen
    spManager.sacuvajSyncInterval(interval);

    // Restartuj servis sa novim intervalom
    InternetCheckService.stopService(this);
    if (interval != SYNC_NEVER) {
        InternetCheckService.startService(this);
    }
});
```

### Korišćenje u InternetCheckService

```java
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    // Svaki put kada se servis pokrene, čita interval iz SharedPreferences
    long interval = SharedPreferencesManager.getInstance(this).getSyncInterval();

    if (interval == SharedPreferencesManager.SYNC_NEVER) {
        stopSelf(); // zaustavi servis ako je odabrano "Nikad"
        return START_NOT_STICKY;
    }

    startPeriodicCheck(interval);
    return START_STICKY; // restart ako Android ugasi servis
}
```

---

## 4. ContentProvider — Čitanje kontakata

### Šta je ContentProvider?

ContentProvider je **Android komponenta** koja **deli podatke između aplikacija**.

```
Aplikacija A ──→ ContentResolver ──→ ContentProvider ──→ SQLite baza Aplikacije B
```

Android sistem isporučuje ContentProvider-e za:

| ContentProvider | URI | Klasa |
|-----------------|-----|-------|
| Kontakti | `content://com.android.contacts/...` | `ContactsContract` |
| Kalendar | `content://com.android.calendar/...` | `CalendarContract` |
| SMS poruke | `content://sms/...` | `Telephony.Sms` |
| Medija fajlovi | `content://media/...` | `MediaStore` |

### Dozvola za čitanje kontakata

**AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
```

**READ_CONTACTS je "dangerous" dozvola** → mora se tražiti i runtime (od Android 6.0+)!

### Runtime zahtjev dozvole

```java
// Provjeri da li dozvola već postoji
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        == PackageManager.PERMISSION_GRANTED) {
    ucitajKontakte(); // imamo dozvolu, idemo odmah
} else {
    // Traži dozvolu od korisnika — pojavljuje se dialog
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.READ_CONTACTS},
        REQUEST_READ_CONTACTS // request code = broj koji ti identifikuje zahtjev
    );
}

// Callback koji se poziva kada korisnik klikne Allow/Deny
@Override
public void onRequestPermissionsResult(int requestCode,
                                       String[] permissions,
                                       int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_READ_CONTACTS) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ucitajKontakte(); // korisnik dozvolio
        } else {
            Toast.makeText(this, "Dozvola odbijena!", Toast.LENGTH_SHORT).show();
        }
    }
}
```

### Čitanje podataka iz ContentProvider-a

```java
private void ucitajKontakte() {
    // 1. URI ContentProvider-a — adresa tabele sa brojevima telefona
    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    // 2. Projekcija — koje KOLONE hoćemo (kao SELECT kolona1, kolona2)
    String[] projekcija = {
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, // ime kontakta
        ContactsContract.CommonDataKinds.Phone.NUMBER         // broj telefona
    };

    // 3. Sortiranje
    String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

    // 4. KLJUČNI POZIV — getContentResolver().query() je kao executeQuery() u JDBC
    //    Ovo je JEDINI način za pristup ContentProvider-u
    Cursor cursor = getContentResolver().query(
        uri,         // šta čitamo
        projekcija,  // koje kolone (null = sve)
        null,        // WHERE uslov (null = bez filtera)
        null,        // WHERE argumenti
        sortOrder    // ORDER BY
    );

    // 5. Iteracija kroz rezultate (isti princip kao SQLite Cursor)
    if (cursor != null && cursor.moveToFirst()) {
        do {
            String ime  = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String broj = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER));
            // koristi ime i broj...
        } while (cursor.moveToNext());
        cursor.close(); // UVEK zatvori!
    }
}
```

### Šema korišćenih URI-ja

```
ContactsContract.Contacts.CONTENT_URI
    → lista svih kontakata (bez telefona)
    → content://com.android.contacts/contacts

ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    → lista svih telefonskih brojeva (sa imenima)
    → content://com.android.contacts/data/phones

ContactsContract.CommonDataKinds.Email.CONTENT_URI
    → lista svih emailova kontakata
```

---

## 5. Navigacija po ulozi

### Tok navigacije

```
LoginScreenActivity
    │
    ├─ uloga = "administrator" ──→ HomeScreenActivity  (admin panel)
    ├─ uloga = "vozac"         ──→ DriverScreenActivity
    └─ uloga = "putnik"        ──→ PassengerScreenActivity
```

### Implementacija u LoginScreenActivity

```java
private void navigirajPoUlozi(String uloga) {
    Intent intent;
    switch (uloga) {
        case "administrator":
            intent = new Intent(this, HomeScreenActivity.class);
            break;
        case "vozac":
            intent = new Intent(this, DriverScreenActivity.class);
            break;
        case "putnik":
        default:
            intent = new Intent(this, PassengerScreenActivity.class);
            break;
    }
    startActivity(intent);
    finish(); // zatvori LoginScreen da se ne može back-om vratiti
}
```

### Registracija sa izborom uloge

U `activity_register_screen.xml` dodat `Spinner`:
```xml
<Spinner
    android:id="@+id/ulogaSpinner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

U `arrays.xml`:
```xml
<string-array name="uloge">
    <item>Putnik</item>
    <item>Vozac</item>
    <item>Administrator</item>
</string-array>
```

U `RegisterScreenActivity.java`:
```java
ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
    this,
    R.array.uloge,                             // string-array iz arrays.xml
    android.R.layout.simple_spinner_item       // Android ugrađeni layout za spinner
);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
ulogaSpinner.setAdapter(adapter);

// Čitanje izabrane vrednosti:
String uloga = ulogaSpinner.getSelectedItem().toString().toLowerCase();
// "Vozac" → "vozac", "Putnik" → "putnik", itd.
```

---

## 6. Kompletan tok aplikacije

```
SplashScreenActivity (5s)
         ↓
LoginScreenActivity
   ├── [jeUlogovan()?] ─YES→ navigirajPoUlozi()
   ├── [login] → DB.pronadjiKorisnika() → SP.sacuvaj() → navigirajPoUlozi()
   └── [registracija] → RegisterScreenActivity
                              ├── DB.dodajKorisnika()
                              ├── SP.sacuvaj()
                              └── navigirajPoUlozi()

navigirajPoUlozi():
   ├── "administrator" → HomeScreenActivity
   │        ├── [Settings] → SettingsActivity → SP.sacuvajSyncInterval()
   │        ├── [Korisnici] → UserScreenActivity
   │        └── [Odjava] → SP.odjavi() → LoginScreenActivity
   ├── "vozac" → DriverScreenActivity
   │        ├── [Settings] → SettingsActivity
   │        ├── [Kontakti] → ContactsActivity → ContentProvider
   │        └── [Odjava] → SP.odjavi() → LoginScreenActivity
   └── "putnik" → PassengerScreenActivity
            ├── [Settings] → SettingsActivity
            ├── [Kontakti] → ContactsActivity → ContentProvider
            └── [Odjava] → SP.odjavi() → LoginScreenActivity
```

---

## 7. Šablon za kolokvijum — brzi podsetnik

### A) Dodati SQLite bazu — minimalni koraci

1. Napravi `DatabaseHelper.java` — `extends SQLiteOpenHelper`
2. Definiši konstante: `DATABASE_NAME`, `DATABASE_VERSION`, nazive kolona
3. Napiši `SQL_CREATE_TABLE` string
4. Override `onCreate()` — `db.execSQL(CREATE_TABLE)`
5. Override `onUpgrade()` — `DROP TABLE IF EXISTS` + `onCreate()`
6. Dodaj CRUD metode + `cursorToUser()` helper
7. Koristi `Singleton` pattern — `getInstance(Context)`

### B) Koristiti SharedPreferences — minimalni koraci

```java
SharedPreferences prefs = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = prefs.edit();

// Pisanje
editor.putString("kljuc", "vrednost");
editor.putBoolean("flag", true);
editor.putLong("broj", 60000L);
editor.apply(); // UVEK na kraju!

// Čitanje
String v = prefs.getString("kljuc", "default_vrednost");
boolean b = prefs.getBoolean("flag", false);
long n = prefs.getLong("broj", 0L);

// Brisanje jednog ključa
editor.remove("kljuc").apply();

// Brisanje svega
editor.clear().apply();
```

### C) Čitati ContentProvider — minimalni koraci

```java
// 1. Dozvola u Manifest-u
// <uses-permission android:name="android.permission.READ_CONTACTS" />

// 2. Runtime provjera i zahtjev
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
    return;
}

// 3. Query
Cursor cursor = getContentResolver().query(
    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  // URI
    new String[]{                                         // projekcija (kolone)
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    },
    null, null,   // WHERE, WHERE args
    null          // ORDER BY
);

// 4. Iteracija
while (cursor != null && cursor.moveToNext()) {
    String ime  = cursor.getString(cursor.getColumnIndex(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
    String broj = cursor.getString(cursor.getColumnIndex(
        ContactsContract.CommonDataKinds.Phone.NUMBER));
}
if (cursor != null) cursor.close();
```

### D) AndroidManifest.xml — šta dodati

```xml
<!-- Dozvole -->
<uses-permission android:name="android.permission.READ_CONTACTS" />

<!-- Nova Activity -->
<activity
    android:name=".NovaActivity"
    android:exported="false" />
```

### E) Odjava korisnika (standardni pattern)

```java
spManager.odjaviKorisnika(); // briše SharedPreferences

Intent intent = new Intent(this, LoginScreenActivity.class);
// Ove flags brišu back stack — korisnik ne može back-om da se vrati
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
```

---

## Kreirani fajlovi u ovoj vežbi

| Fajl | Tip | Opis |
|------|-----|------|
| `DatabaseHelper.java` | Nova klasa | SQLite CRUD za tabelu `korisnici` |
| `SharedPreferencesManager.java` | Nova klasa | Centralizovano upravljanje SharedPreferences |
| `DriverScreenActivity.java` | Nova Activity | Ekran za ulogu "vozac" |
| `PassengerScreenActivity.java` | Nova Activity | Ekran za ulogu "putnik" |
| `SettingsActivity.java` | Nova Activity | Podešavanja sinhronizacije (RadioGroup) |
| `ContactsActivity.java` | Nova Activity | Čitanje kontakata putem ContentProvider-a |
| `activity_driver_screen.xml` | Layout | Layout za DriverScreenActivity |
| `activity_passenger_screen.xml` | Layout | Layout za PassengerScreenActivity |
| `activity_settings_screen.xml` | Layout | Layout sa RadioGroup za interval |
| `activity_contacts_screen.xml` | Layout | Layout sa ListView za kontakte |
| `User.java` | Izmenjena klasa | Dodati `id` i `uloga` polja |
| `LoginScreenActivity.java` | Izmenjena Activity | Koristi DB + SharedPrefs, auto-login, navigacija po ulozi |
| `RegisterScreenActivity.java` | Izmenjena Activity | Koristi DB + SharedPrefs, dodati Spinner za ulogu |
| `HomeScreenActivity.java` | Izmenjena Activity | Fix duplog onDestroy, čita SharedPrefs, Settings dugme |
| `InternetCheckService.java` | Izmenjen servis | Čita interval sinhronizacije iz SharedPreferences |
| `AndroidManifest.xml` | Izmenjen manifest | Dodati READ_CONTACTS dozvola i 4 nove Activity |
| `arrays.xml` | Izmenjen resurs | Dodat `uloge` string-array za Spinner |
| `strings.xml` | Izmenjen resurs | Dodati stringovi za Vezbu 6 |

---

## Testni korisnici (ubačeni automatski u bazu pri prvom pokretanju)

| Email | Lozinka | Uloga |
|-------|---------|-------|
| admin@app.com | admin123 | administrator |
| vozac@app.com | vozac123 | vozac |
| putnik@app.com | putnik123 | putnik |

---

## Pregled svega pređenog — Vezbe 1–6

### Vezba 1 — Osnove, prvi projekat
- [x] Kreiranje Android projekta u Android Studiu
- [x] Struktura projekta (java, res, manifest)
- [x] Prva Activity, `setContentView()`, pokretanje na emulatoru

### Vezba 2 — Životni ciklus i osnovna navigacija
- [x] Životni ciklus Activity-ja: `onCreate → onStart → onResume → onPause → onStop → onDestroy`
- [x] Logovanje svakog koraka sa `Log.d(TAG, "...")`
- [x] `Intent` za prelaz između ekrana (`startActivity`, `finish`)
- [x] Splash Screen sa `Handler.postDelayed()` odlaganjem

### Vezba 3 — UI, rasporedi, string resursi
- [x] Rasporedi: `LinearLayout`, `ConstraintLayout`
- [x] UI elementi: `TextView`, `EditText`, `Button`
- [x] String resursi u `strings.xml` — nikad hardkodovani tekst
- [x] Prosleđivanje podataka između ekrana: `intent.putExtra()` / `intent.getStringExtra()`
- [x] Ekrani: Splash, Login, Register, Home

### Vezba 4 — Toolbar, meni, korisnički model
- [x] `Toolbar` umesto podrazumevano ActionBar
- [x] Opcioni meni: `onCreateOptionsMenu()`, `onOptionsItemSelected()`
- [x] Model klasa `User.java` (ime, email, telefon, lozinka)
- [x] `UserManager.java` — Singleton, in-memory lista korisnika
- [x] `UserScreenActivity` — prikaz, sortiranje i filtriranje liste korisnika (`ListView`, `ArrayAdapter`, `Spinner`)
- [x] `addUser()`, `loginUser()`, `sortUsers()`, `filterUsers()` metode

### Vezba 5 — Servisi, Broadcast Receiver, Notifikacije, Dozvole
- [x] **Service** — `InternetCheckService.java` (pozadinski servis, `START_STICKY`)
- [x] Periodično izvršavanje sa `Handler.postDelayed()`
- [x] **BroadcastReceiver** — `InternetStatusReceiver.java` (prima custom broadcast)
- [x] Slanje broadcast-a: `sendBroadcast(intent)`
- [x] Registracija i odjava receiver-a: `registerReceiver()` / `unregisterReceiver()`
- [x] **Notifikacije** — `NotificationHelper.java`
- [x] Notification Channels (obavezno za Android 8+): `NotificationChannel`, `NotificationManager`
- [x] `NotificationCompat.Builder` — title, text, BigTextStyle, actions, PendingIntent
- [x] **Runtime dozvole** — `checkSelfPermission()`, `requestPermissions()`, `onRequestPermissionsResult()`
- [x] `AndroidManifest.xml` — `<service>`, `<receiver>`, `<uses-permission>`

### Vezba 6 — Dobavljači sadržaja (ContentProvider), SQLite, SharedPreferences
- [x] **SQLite baza podataka** — `DatabaseHelper.java`
  - [x] `extends SQLiteOpenHelper`, `onCreate()`, `onUpgrade()`
  - [x] `ContentValues` za unos podataka
  - [x] `Cursor` za čitanje rezultata
  - [x] Kompletne CRUD metode: `dodaj()`, `getSve()`, `pronadji()`, `azuriraj()`, `obrisi()`
  - [x] Singleton pattern za deljenje instance kroz aplikaciju
- [x] **SharedPreferences** — `SharedPreferencesManager.java`
  - [x] Čuvanje podataka ulogovanog korisnika (ime, email, uloga)
  - [x] Auto-login — ako je korisnik već ulogovan, preskoči Login ekran
  - [x] Odjava korisnika (`editor.remove()` / `editor.clear()`)
  - [x] Čuvanje podešavanja sinhronizacije (nikad / 1 min / 15 min / 30 min)
- [x] **Navigacija po ulozi** — različit ekran za svaku ulogu
  - [x] `administrator` → `HomeScreenActivity`
  - [x] `vozac` → `DriverScreenActivity`
  - [x] `putnik` → `PassengerScreenActivity`
- [x] **SettingsActivity** — `RadioGroup` sa intervalima, restart servisa
- [x] **ContentProvider** — `ContactsActivity.java`
  - [x] `ContactsContract.CommonDataKinds.Phone` — čitanje kontakata
  - [x] `getContentResolver().query()` — jedini način pristupa ContentProvider-u
  - [x] Runtime dozvola `READ_CONTACTS`
  - [x] Prikaz rezultata u `ListView`
- [x] Spinner za izbor uloge na ekranu registracije (`arrays.xml`)
- [x] Integracija SQLite + SharedPreferences — pri loginu čita iz baze, piše u SP

---

## Kompletna lista kreiranih fajlova (sve vezbe)

| Fajl | Vezba | Tip |
|------|-------|-----|
| `SplashScreenActivity.java` | 2 | Activity |
| `LoginScreenActivity.java` | 3/6 | Activity |
| `RegisterScreenActivity.java` | 3/6 | Activity |
| `HomeScreenActivity.java` | 3/6 | Activity (admin) |
| `UserScreenActivity.java` | 4 | Activity |
| `DriverScreenActivity.java` | 6 | Activity (vozač) |
| `PassengerScreenActivity.java` | 6 | Activity (putnik) |
| `SettingsActivity.java` | 6 | Activity |
| `ContactsActivity.java` | 6 | Activity |
| `User.java` | 4/6 | Model klasa |
| `UserManager.java` | 4 | In-memory storage |
| `DatabaseHelper.java` | 6 | SQLite helper |
| `SharedPreferencesManager.java` | 6 | SP wrapper |
| `InternetCheckService.java` | 5/6 | Service |
| `InternetStatusReceiver.java` | 5 | BroadcastReceiver |
| `NotificationHelper.java` | 5 | Helper klasa |
