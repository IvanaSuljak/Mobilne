# ŠABLON — Kolokvijum 2 (primer — sve u jednoj MainActivity)

> **Jednostavan vodič (preporučeno):** `VODIC_KOLOKVIJUM_JEDNOSTAVNO.md` — šta ide u koji fajl, TODO objašnjenja, gotov kod za zadatke 6–9.  
> **MASTER:** `SABLON_MASTER_VODIC.md` — opšti redosled.

> **NAJVAŽNIJE PRAVILO:** Jedna klasa `MainActivity`. Kod iz drugih šablona ubacuješ kao metode/field-ove.

---

## KADA KORISTITI

- Kad dobiješ zadatak sličan kolokvijumu 2 (GPS + kamera + senzori + baza + Retrofit + Switch logika)
- Kao **checklist** — prođi zadatke 1–9 redom
- Za pojedinačne delove koristi specijalizovane šablone (linkovi ispod)

---

## TAČAN REDOSLED — ceo kolokvijum (KORAK po KORAK)

| # | Gde | Šta | Koji šablon |
|---|-----|-----|-------------|
| 0 | Android Studio | Novi projekat Empty Views Activity | ovde — Zadatak 1 |
| 1 | `activity_main.xml` | 5 UI elementa | `SABLON_Layout_XML.md` — sekcija "Kolokvijum 2" |
| 1b | `MainActivity.java` | fields + findViewById | `SABLON_Layout_XML.md` — sekcija "MainActivity findViewById" |
| 2 | `build.gradle (Module :app)` | location + retrofit + Sync | Lokacija K1 + Retrofit K1 |
| 3 | `AndroidManifest.xml` | sve dozvole + FileProvider | Senzori K1 + Lokacija K2 |
| 4 | `res/xml/file_paths.xml` | FileProvider putanje | Senzori K2 |
| 5 | `Post.java` + `DatabaseHelper.java` | model + baza | SQLite K1–2 |
| 6 | `ApiService.java` + `RetrofitClient.java` | HTTP | Retrofit K3–4 |
| 7 | `MainActivity.java` | GPS lat/lng u TextView | Lokacija K1b + K4 |
| 8 | `MainActivity.java` | kamera + žiroskop Toast | Senzori K3 + K5 |
| 9 | `MainActivity.java` | Switch ON → fetch/upiši/Toast | ovde — Zadatak 6 |
| 10 | `MainActivity.java` | Button → obriši + notifikacija | ovde — Zadatak 7 + SQLite |
| 11 | `MainActivity.java` | akcelerometar na Button tekst | Senzori K3 |
| 12 | `MainActivity.java` | Switch OFF → prefs + kontakt | SQLite K3 + K6 |

---

| Fajl | Šta ide unutra |
|------|----------------|
| `app/build.gradle (Module :app)` | dependencies (location, retrofit...) |
| `AndroidManifest.xml` | dozvole + FileProvider **unutar** `<application>` |
| `res/xml/file_paths.xml` | FileProvider putanje (poseban fajl!) |
| `res/layout/activity_main.xml` | TextView, ImageButton, ImageView, Switch, Button |
| `MainActivity.java` | SVE — GPS, kamera, senzori, switch, button |
| `Post.java` | model klasa |
| `DatabaseHelper.java` | SQLite CRUD |
| `network/ApiService.java` | Retrofit endpointi |
| `network/RetrofitClient.java` | BASE_URL |

---

## ZADATAK 1 — Novi projekat

```
File → New → New Project → Empty Views Activity
Name: Kolokvijum2
Package: com.example.kolokvijum2
Language: Java
Min SDK: API 24
```

Glavna aktivnost se automatski zove `MainActivity`.

---

## ZADATAK 2 — Layout (`res/layout/activity_main.xml`)

Kopiraj iz `SABLON_Layout_XML.md` — sekcija "Gotov layout sa svih 5 elemenata".

Zameni ID-eve (koristi ove tačne nazive):
```
lokacijaTextView
kameraImageButton
slikaImageView
postSwitch
obrisiButton
```

---

## ZADATAK 3 — GPS u TextView

### app/build.gradle (Module :app)
```groovy
implementation 'com.google.android.gms:play-services-location:21.2.0'
```
→ **Sync Now**

### AndroidManifest.xml (PRE `<application>`)
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### MainActivity — dodaj ove delove u JEDNU klasu

```java
// === FIELDS (na vrhu klase, pored ostalih) ===
private static final int REQUEST_LOCATION = 300;
private FusedLocationProviderClient fusedLocationClient;
private TextView lokacijaTextView;

// === u onCreate() ===
fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
lokacijaTextView = findViewById(R.id.lokacijaTextView);
dohvatiLokaciju();

// === METODE (na nivou klase, NE unutar lambde!) ===
private void dohvatiLokaciju() { ... }      // iz SABLON_Lokacija KORAK 4
private boolean imaDozvolu() { ... }
private void zatraziDozvolu() { ... }
@Override onRequestPermissionsResult(...) { ... }
```

---

## ZADATAK 4 — Kamera + žiroskop Toast

> **Šablon:** `SABLON_Senzori_Kamera.md` — KORAK 1, 2, 5 + **KORAK 3 delovi 3a–3e Varijanta B**
> **NE kopiraj** ceo KORAK 3 odjednom — vidi upozorenje na vrhu Senzori šablona.

### AndroidManifest.xml

```xml
<!-- 1. Dozvole — PRE <application> -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />

<!-- 2. FileProvider — UNUTAR <application>, PRE </application> -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

> **GREŠKA koju ne smeš:** `<provider>` i `<uses-permission>` POSLE `</application>` — NE RADI!

### res/xml/file_paths.xml (NOVI fajl — desni klik res → New → Android Resource File → xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="moje_slike" path="Pictures/" />
</paths>
```

> **GREŠKA:** `<paths>` NE ide u Manifest — ide u poseban XML fajl!

### MainActivity — šta dodati u POSTOJEĆI fajl (ne novi onCreate!)

Iz **KORAK 3a–3e Varijanta B** + **KORAK 5**:

```java
// Na klasi: implements SensorEventListener

// Fields (pored GPS field-ova):
private Uri fotografijUri;
private SensorManager sensorManager;
private Sensor ziroskop;
private float[] ziroskopVrednosti = new float[3];

// Launcher — VAN onCreate (KORAK 5):
private final ActivityResultLauncher<Intent> kameraLauncher = ... // sa Toast žiroskopom

// U POSTOJEĆI onCreate dodaj:
kameraImageButton.setOnClickListener(v -> otvoriKameru());
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
ziroskop = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

// Metode na nivou klase:
private void otvoriKameru() { ... }  // KORAK 5
@Override onResume() { ... }         // KORAK 3d
@Override onPause() { ... }
@Override onSensorChanged() { ... }  // Varijanta B — clone u niz
```

### Referenca — Toast u kameraLauncher (KORAK 5)

```java
private final ActivityResultLauncher<Intent> kameraLauncher =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                slikaImageView.setImageURI(null);
                slikaImageView.setImageURI(fotografijUri);
                Toast.makeText(this,
                    "X: " + ziroskopVrednosti[0] +
                    " Y: " + ziroskopVrednosti[1] +
                    " Z: " + ziroskopVrednosti[2],
                    Toast.LENGTH_LONG).show();
            }
        });

@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        ziroskopVrednosti = event.values.clone();
    }
}
```

---

## Struktura MainActivity — kako izgleda ceo fajl

```
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // ──── FIELDS ────
    // GPS
    // Kamera
    // Senzori
    // Retrofit/SQLite (za zadatke 5-9)

    // ──── LAUNCHER (van onCreate!) ────
    private final ActivityResultLauncher<Intent> kameraLauncher = ...

    // ──── onCreate() ────
    // findViewById za SVE elemente
    // inicijalizacija GPS, senzora, Retrofit-a
    // click listeneri

    // ──── METODE (sve ovde, jedna pored druge) ────
    private void dohvatiLokaciju() { }
    private boolean imaDozvolu() { }
    private void zatraziDozvolu() { }
    private void otvoriKameru() { }
    // ... ostale metode za zadatke 5-9

    // ──── OVERRIDE metode ────
    @Override onRequestPermissionsResult() { }
    @Override onResume() { }
    @Override onPause() { }
    @Override onSensorChanged() { }
    @Override onAccuracyChanged() { }

}  // ← JEDNA zagrada zatvara klasu
```

---

## ZADATAK 5 — SQLite + Retrofit

> **Retrofit:** `SABLON_Retrofit_HTTP_Zahtevi.md` — TAČAN REDOSLED + sekcija "Kolokvijum 2 — Post model"  
> **Baza:** `SABLON_SQLite_...md` — sekcija "Kolokvijum 2 — DatabaseHelper za postove"

### Redosled fajlova

| # | Fajl | Gde kreirati |
|---|------|--------------|
| 1 | Gradle + Sync | `build.gradle (Module :app)` — retrofit + gson |
| 2 | Manifest | `INTERNET` pre `<application>` |
| 3 | `Post.java` | `com.example.kolokvijum2` (glavni paket) |
| 4 | `DatabaseHelper.java` | `com.example.kolokvijum2` (glavni paket) |
| 5 | `ApiService.java` | `com.example.kolokvijum2.network` (Interface!) |
| 6 | `RetrofitClient.java` | `com.example.kolokvijum2.network` |
| 7 | MainActivity | init `apiService` + `dbHelper` u onCreate |

### Mapiranje JSON → Post

| JSON | Java | Napomena |
|------|------|----------|
| `id` | `id` | |
| `userId` | `userId` | |
| `title` | `title` | zadatak 6 Toast |
| `body` | `body` | |
| `link` | `link` | |
| `comment_count` | `commentCount` | `@SerializedName("comment_count")` |

### BASE_URL (iz zadatka)

```java
"https://app.beeceptor.com/mock-server/dummy-json/"
```

Endpoint: `@GET("posts")` → pun URL = BASE_URL + `posts`

### MainActivity — dodaj u POSTOJEĆI onCreate

```java
apiService = RetrofitClient.getInstance().getApiService();
dbHelper = DatabaseHelper.getInstance(this);
```

**Test:** privremeni `enqueue` → Toast "Postova: 10" znači da radi ✅

---

## ZADATAK 6 — Switch ON

```java
postSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
    if (isChecked) {
        if (!vecFetchovano) {
            apiService.getSviPostovi().enqueue(new Callback<List<Post>>() {
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Post> svi = response.body();
                        for (int i = 0; i < Math.min(10, svi.size()); i++) {
                            dbHelper.dodajPost(svi.get(i));
                        }
                        vecFetchovano = true;
                    }
                }
                public void onFailure(Call<List<Post>> call, Throwable t) { }
            });
        } else {
            // Čitaj prvi iz baze → Toast sa title (NE id=1!)
            Post prvi = dbHelper.getPrviPost();
            if (prvi != null)
                Toast.makeText(this, prvi.getTitle(), Toast.LENGTH_LONG).show();
        }
    }
});
```

---

## ZADATAK 7 — Button briše prvi post

```java
obrisiButton.setOnClickListener(v -> {
    boolean obrisano = dbHelper.obrisiPrviPost();
    if (!obrisano) {
        Toast.makeText(this, "Nema više postova!", Toast.LENGTH_LONG).show();
        // ili NotificationHelper notifikacija
    }
});
```

---

## ZADATAK 8 — Button tekst = akcelerometar

Registruj i `TYPE_ACCELEROMETER` u `onResume()`.
U `onSensorChanged()`:
```java
if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    obrisiButton.setText(String.format("X:%.1f Y:%.1f Z:%.1f",
        event.values[0], event.values[1], event.values[2]));
}
```

---

## ZADATAK 9 — Switch OFF → SharedPreferences + kontakt

```java
} else { // Switch OFF
    getSharedPreferences("AppPrefs", MODE_PRIVATE)
        .edit().putString("tekst", lokacijaTextView.getText().toString()).apply();
    lokacijaTextView.setText(getPrviKontakt());
}
```

Kontakti: `SABLON_SQLite_...md` KORAK 6 — samo prvi kontakt.

---

## ČESTE GREŠKE — proveri pre predaje

| Greška | Ispravno |
|--------|----------|
| `public class TODO_Activity` unutar MainActivity | Sve metode direktno u MainActivity |
| Metode unutar `{}` lambde | Metode na nivou klase, posle `});` |
| `<provider>` posle `</application>` | `<provider>` unutar `<application>` |
| `<paths>` u Manifest-u | Poseban fajl `res/xml/file_paths.xml` |
| dependency u root build.gradle | `app/build.gradle (Module :app)` |
| `TODO_imageView`, `TODO_layout` | Pravi ID-evi iz activity_main.xml |
| Nema `implements SensorEventListener` | Dodaj na deklaraciju klase |
| Nema `onResume`/`onPause` za senzor | Obavezno — inače senzor ne radi |

---

## Redosled rada na kolokvijumu

```
1. Novi projekat
2. activity_main.xml (5 elemenata)
3. app/build.gradle dependencies + Sync
4. AndroidManifest dozvole + FileProvider
5. res/xml/file_paths.xml
6. MainActivity — GPS (zadatak 3)
7. MainActivity — kamera + žiroskop (zadatak 4)
8. Post.java + DatabaseHelper + Retrofit (zadatak 5)
9. Switch logika (zadatak 6)
10. Button briši + notifikacija (zadatak 7)
11. Akcelerometar na Button (zadatak 8)
12. Switch OFF + SharedPreferences + kontakt (zadatak 9)
```
