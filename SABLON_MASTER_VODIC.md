# MASTER VODIČ — Kolokvijum mobilne aplikacije

> **Otvori OVAJ fajl prvo.** Pročitaj zadatak → nađi reč u tabeli → otvori odgovarajući šablon → prati redosled koraka.

---

## 3 PRAVILA koja važe UVEK

### Pravilo 1 — Jedna klasa
Na kolokvijumu skoro uvek imaš **samo `MainActivity`**.  
Kod iz šablona kopiraš kao **field-ove i metode** — **NE** kao `public class TODO_Activity` unutar MainActivity.

### Pravilo 2 — Gde ide dependency
U Android Studiju: **Gradle Scripts → `build.gradle (Module :app)`** → `dependencies { }` → **Sync Now**  
**NE** u root `build.gradle (Project: ...)`.

### Pravilo 3 — Struktura MainActivity
```
public class MainActivity extends AppCompatActivity [implements ...] {

    // 1. FIELDS + LAUNCHER-i (van onCreate!)
    // 2. onCreate() — findViewById, inicijalizacija, listeneri
    // 3. METODE (private void ...) — na nivou klase
    // 4. OVERRIDE metode — na nivou klase, NE unutar {} lambde!

}  // jedna zagrada zatvara klasu
```

### Pravilo 4 — Spajanje više šablona u jedan MainActivity

Kad zadatak traži GPS + kameru + senzor, **ne lepiš cele KORAK blokove** — svaki šablon daje delove:

| Iz šablona | Uzmi | NE kopiraj |
|------------|------|------------|
| Lokacija K4 | `dohvatiLokaciju`, dozvole | ceo `onCreate` ponovo |
| Senzori K3 | fields 3b, init 3c, metode 3d–3e | ceo blok sa `setContentView` |
| Senzori K5 | launcher, `otvoriKameru` | `public class ...` unutar klase |

**Uvek:** jedan `onCreate`, jedna klasa, jedna zatvarajuća `}`.

---

## UNIVERZALNO vs KONKRETNO — ceo projekat

> Detaljno po svakom šablonu: **`SABLON_UNIVERZALNO_VS_KONKRETNO.md`**

| UNIVERZALNO (kopiraj) | KONKRETNO (menjaj po zadatku) |
|-----------------------|-------------------------------|
| Struktura MainActivity, Gradle u Module :app, Manifest redosled | Layout ID-evi, dozvole koje trebaš, listener logika |
| Obrazci: dozvole, launcher, SensorEventListener, Retrofit singleton | BASE_URL, JSON polja, tip senzora, SharedPreferences ključ |
| `TODO` **nema** u kodu | `TODO_...` **mora** da se zameni |

---

---

## Mapa fajlova — šta ide gde

> **Puna šema foldera (drvo + Android Studio panel + dijagram):** `VODIC_DAN_KOLOKVIJUMA.md` → sekcija **„Šema — gde koji fajl ide“**

| Fajl | Putanja u Android Studiju | Šta ide unutra |
|------|---------------------------|----------------|
| Layout | `res/layout/activity_main.xml` | UI elementi (TextView, Button...) |
| Manifest | `app/src/main/AndroidManifest.xml` | dozvole + provider + activity |
| File paths | `res/xml/file_paths.xml` | FileProvider (kamera) |
| Gradle | `Gradle Scripts → build.gradle (Module :app)` | biblioteke |
| Activity | `java/.../MainActivity.java` | sav Java kod |
| Model | `java/.../Post.java` | glavni paket — klasa sa poljima |
| Baza | `java/.../DatabaseHelper.java` | glavni paket — SQLite CRUD |
| API | `java/.../network/ApiService.java` | paket **network** — Interface |
| API klijent | `java/.../network/RetrofitClient.java` | paket **network** — BASE_URL |

### Brzo drvo (kolokvijum 2 — sve)

```
app/build.gradle (Module :app)
app/src/main/AndroidManifest.xml
app/src/main/res/layout/activity_main.xml
app/src/main/res/xml/file_paths.xml
app/src/main/java/com/example/kolokvijum2/MainActivity.java
app/src/main/java/com/example/kolokvijum2/Post.java
app/src/main/java/com/example/kolokvijum2/DatabaseHelper.java
app/src/main/java/com/example/kolokvijum2/network/ApiService.java
app/src/main/java/com/example/kolokvijum2/network/RetrofitClient.java
```

---

## AndroidManifest — tačan redosled (uvek isti)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ...>

    <!-- KORAK A: DOZVOLE — ovde, PRE <application> -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- dodaj ostale dozvole po potrebi -->

    <!-- KORAK B: APPLICATION -->
    <application ...>

        <activity android:name=".MainActivity" ... />

        <!-- KORAK C: FileProvider — samo ako ima kamera, UNUTAR application -->
        <provider ... android:resource="@xml/file_paths" />

    </application>
</manifest>
```

| Dozvola | Kada treba |
|---------|------------|
| `INTERNET` | Retrofit, HTTP |
| `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` | GPS, lat/lng |
| `CAMERA` | Snimanje fotografije |
| `READ_CONTACTS` | Čitanje kontakata |
| `POST_NOTIFICATIONS` | Notifikacije (Android 13+) |

---

## UNIVERZALNI REDOSLED — bilo koji zadatak

Radi **uvek ovim redom** — ne preskači korake:

```
KORAK 0  Novi projekat (File → New → Empty Views Activity)
KORAK 1  Layout XML — elementi iz zadatka          → SABLON_Layout_XML.md
KORAK 2  app/build.gradle — dependencies + Sync
KORAK 3  AndroidManifest — dozvole (+ provider ako treba)
KORAK 4  res/xml/file_paths.xml — samo ako ima kamera
KORAK 5  Model + DatabaseHelper — ako ima baza     → SABLON_SQLite_...md
KORAK 6  Retrofit — ako ima HTTP/API                → SABLON_Retrofit_...md
KORAK 7  MainActivity — fields na vrhu
KORAK 8  MainActivity — onCreate (findViewById + init)
KORAK 9  MainActivity — metode (private void ...)
KORAK 10 MainActivity — override metode + listeneri
KORAK 11 Run + testiraj
```

---

## TABELA — pročitaj zadatak → otvori šablon

| Ako zadatak kaže... | Otvori šablon | Koraci u šablonu |
|---------------------|---------------|------------------|
| TextView, Button, Switch, ImageView, "jedno ispod drugog" | `SABLON_Layout_XML.md` | Tablica elemenata → kopiraj blok → zameni ID |
| geografska širina/dužina, lat/lng, lokacija, GPS | `SABLON_Lokacija_GoogleMaps.md` | KORAK 1b (samo GPS) ili pun KORAK 1–4 (sa mapom) |
| Google Maps, mapa, marker | `SABLON_Lokacija_GoogleMaps.md` | KORAK 1–4 (puna verzija + API ključ) |
| kamera, fotografija, ImageView, slika | `SABLON_Senzori_Kamera.md` | KORAK 1, 2, 5 + Manifest + file_paths |
| senzor, akcelerometar, žiroskop, shake | `SABLON_Senzori_Kamera.md` | KORAK 3 (+ KORAK 4 za shake) |
| SQLite, baza, tabela, CRUD, upiši/obriši | `SABLON_SQLite_...md` | KORAK 1–2 |
| SharedPreferences, sačuvaj, sesija, podešavanja | `SABLON_SQLite_...md` | KORAK 3 |
| ContentProvider, kontakti, Contacts | `SABLON_SQLite_...md` | KORAK 6 |
| Retrofit, HTTP, GET, API, JSON, sajt | `SABLON_Retrofit_...md` | KORAK 1–6 |
| Switch ON/OFF logika, prvi put / svaki put | `SABLON_KOLOKVIJUM_2.md` | Zadaci 6, 9 |
| Notifikacija | `SABLON_KOLOKVIJUM_2.md` + projekat `NotificationHelper` | Zadatak 7 |
| Ceo kolokvijum 2 (sve zajedno) | `SABLON_KOLOKVIJUM_2.md` | Redosled zadataka 1–9 |

---

## Kombinacije — više stvari u jednom zadatku

| Kombinacija | Redosled rada |
|-------------|---------------|
| Layout + GPS | Layout → Gradle (location) → Manifest (location) → MainActivity GPS |
| Layout + Kamera | Layout → Gradle (nema) → Manifest (CAMERA + provider) → file_paths → MainActivity kamera |
| Layout + Senzor | Layout → MainActivity + `implements SensorEventListener` + onResume/onPause |
| Baza + Retrofit | Post.java → DatabaseHelper → ApiService → RetrofitClient → Gradle (retrofit) → Manifest (INTERNET) → MainActivity callback |
| GPS + Kamera + Senzor | Sve u **jednoj** MainActivity, `implements SensorEventListener` |
| SharedPreferences + Kontakti | Manifest READ_CONTACTS → runtime dozvola → query → SharedPreferences edit |

---

## build.gradle — šta dodati za koji zadatak

**Fajl:** `build.gradle (Module :app)` → unutar `dependencies { }` → **Sync Now**

| Zadatak | Linija |
|---------|--------|
| GPS / lokacija | `implementation 'com.google.android.gms:play-services-location:21.2.0'` |
| Google Maps | `implementation 'com.google.android.gms:play-services-maps:18.2.0'` |
| Retrofit | `implementation 'com.squareup.retrofit2:retrofit:2.9.0'` |
| Retrofit Gson | `implementation 'com.squareup.retrofit2:converter-gson:2.9.0'` |
| Kamera | ništa extra (FileProvider je u androidx) |
| Senzori | ništa extra (ugrađeno u Android) |
| SQLite | ništa extra (ugrađeno u Android) |

---

## MainActivity — gde šta ide (redosled pisanja koda)

Kad pišeš `MainActivity.java`, **uvek ovim redom**:

```java
// 1. package + importi

// 2. deklaracija klase (+ implements ako treba senzor)
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // 3. KONSTANTE (REQUEST_LOCATION = 300, itd.)

    // 4. FIELDS — svi private na vrhu
    private TextView lokacijaTextView;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper dbHelper;
    // ...

    // 5. LAUNCHER-i — VAN onCreate, kao field!
    private final ActivityResultLauncher<Intent> kameraLauncher = registerForActivityResult(...);

    // 6. onCreate()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 6a. findViewById za SVE elemente
        // 6b. inicijalizacija (dbHelper, sensorManager, apiService...)
        // 6c. pozovi metode (dohvatiLokaciju())
        // 6d. setOnClickListener / setOnCheckedChangeListener
    }

    // 7. PRIVATE METODE — jedna pored druge
    private void dohvatiLokaciju() { }
    private void otvoriKameru() { }
    private boolean imaDozvolu() { }

    // 8. OVERRIDE METODE — posle private metoda
    @Override protected void onResume() { }
    @Override protected void onPause() { }
    @Override public void onSensorChanged(SensorEvent event) { }
    @Override public void onRequestPermissionsResult(...) { }

} // KRAJ klase — samo JEDNA zagrada
```

---

## ČESTE GREŠKE — proveri pre predaje

| ❌ Greška | ✅ Ispravno |
|----------|------------|
| Nalepiš ceo KORAK 3 blok (Senzori) | Uzmi delove 3a–3e, dodaj u postojeći MainActivity |
| Dva `onCreate()` u MainActivity | Jedan onCreate — spoji init iz svih šablona |
| `public class X` unutar MainActivity | Metode direktno u MainActivity |
| Metode unutar `lokacija -> { }` lambde | Metode posle `});` zatvaranja lambde |
| Dependency u root build.gradle | `build.gradle (Module :app)` |
| `<provider>` posle `</application>` | Unutar `<application>` |
| `<paths>` u Manifest-u | `res/xml/file_paths.xml` |
| `TODO_textView` ostao u kodu | Zameni sa pravim ID iz layouta |
| Senzor bez `onResume`/`onPause` | Obavezno register/unregister |
| Retrofit bez INTERNET dozvole | Manifest `<uses-permission INTERNET>` |
| Kamera bez FileProvider | Manifest provider + file_paths.xml |
| Zaboravljen Sync Now | Posle svake Gradle izmene |

---

## Brzi checklist pre predaje

- [ ] Layout ima sve elemente iz zadatka sa `@+id/...`
- [ ] Gradle dependency u **Module :app** + Sync
- [ ] Manifest dozvole **pre** `<application>`
- [ ] FileProvider **unutar** `<application>` (ako kamera)
- [ ] `file_paths.xml` postoji u `res/xml/`
- [ ] MainActivity — jedna klasa, metode na nivou klase
- [ ] `findViewById` ID-evi se poklapaju sa layoutom
- [ ] Senzori: `implements SensorEventListener` + onResume/onPause
- [ ] Retrofit: INTERNET + BASE_URL sa `/` na kraju
- [ ] App se build-uje bez crvenih grešaka

---

## Lista svih šablona

| Fajl | Za šta |
|------|--------|
| **`SABLON_MASTER_VODIC.md`** | **Ovaj fajl — počni ovde** |
| **`VODIC_DAN_KOLOKVIJUMA.md`** | **Dan kolokvijuma — korak po korak šta radiš kad dobiješ zadatak** |
| **`VODIC_KOLOKVIJUM_JEDNOSTAVNO.md`** | **Kolokvijum — šta ide gde, TODO objašnjenja, gotov kod** |
| **`PRIprema_KOLOKVIJUM_2_KOMPLETNO.md`** | **Celo rešenje — svaka klasa + za koji zadatak** |
| **`SABLON_UNIVERZALNO_VS_KONKRETNO.md`** | **Šta kopiraš bukvalno vs šta menjaš po zadatku** |
| `SABLON_Layout_XML.md` | UI elementi u XML-u |
| `SABLON_Lokacija_GoogleMaps.md` | GPS lat/lng + Google Maps |
| `SABLON_Senzori_Kamera.md` | Senzori + kamera + FileProvider |
| `SABLON_SQLite_SharedPreferences_ContentProvider.md` | Baza + prefs + kontakti |
| `SABLON_Retrofit_HTTP_Zahtevi.md` | HTTP zahtevi, JSON |
| `SABLON_KOLOKVIJUM_2.md` | Primer celog kolokvijuma 2 |
