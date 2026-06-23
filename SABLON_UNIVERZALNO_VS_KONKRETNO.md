# UNIVERZALNO vs KONKRETNO — šta kopiraš, šta menjaš

> **MASTER:** Otvori prvo `SABLON_MASTER_VODIC.md`.  
> Ovaj fajl objašnjava **pravilo koje važi u svim šablonima**: deo koda je uvek isti, deo zavisi od zadatka.

---

## Jedno pravilo za sve šablone

| Tip | Znači | Šta radiš |
|-----|-------|-----------|
| **UNIVERZALNO** | Obrazac se **ne menja** između zadataka | Kopiraš bukvalno (možda samo ime paketa) |
| **KONKRETNO** | Zavisi od **teksta zadatka** | Menjaš `TODO_...` ili biraš varijantu A/B/C |

> Ako u kodu vidiš `TODO_...` → to je **konkretno** — moraš zameniti.  
> Ako nema `TODO` → to je **univerzalno** — kopiraj kako jeste.

---

## Opšte — važi za ceo projekat

### UNIVERZALNO (uvek isto)

| Deo | Gde | Napomena |
|-----|-----|----------|
| Struktura MainActivity | jedna klasa, jedan `onCreate` | Pravilo iz MASTER vodiča |
| Gradle fajl | `build.gradle (Module :app)` | NE root build.gradle |
| Sync Now | posle svake Gradle izmene | obavezno |
| Redosled u Manifest-u | dozvole **pre** `<application>` | FileProvider **unutar** `<application>` |
| `findViewById` obrazac | `findViewById(R.id.xxx)` | ID mora postojati u layoutu |
| Runtime dozvole | `checkSelfPermission` → `requestPermissions` → `onRequestPermissionsResult` | obrazac isti, ime dozvole se menja |
| `registerForActivityResult` | launcher kao **field** van `onCreate` | kamera, dozvole |

### KONKRETNO (menjaš po zadatku)

| Deo | Od čega zavisi |
|-----|----------------|
| Layout elementi i `@+id/...` | šta zadatak traži na ekranu |
| Koje dozvole u Manifest-u | GPS / kamera / internet / kontakti / notifikacije |
| Koje Gradle linije | location / maps / retrofit — vidi MASTER tabelu |
| Imena polja u modelu | kolone u bazi ili polja u JSON-u |
| Listener logika | šta se dešava na klik / Switch ON / Switch OFF |
| Koji senzor | akcelerometar, žiroskop — iz teksta zadatka |
| BASE_URL, `@GET` endpoint | adresa i putanja iz zadatka |

---

## Layout (`SABLON_Layout_XML.md`)

| UNIVERZALNO | KONKRETNO |
|-------------|-----------|
| `LinearLayout` vertikalno, `match_parent`, `padding` | Koji elementi: TextView, Button, Switch... |
| `android:id="@+id/..."` obrazac | Tačan ID (npr. `lokacijaTextView`, `postSwitch`) |
| `findViewById(R.id.xxx)` u MainActivity | Ime mora da odgovara layoutu |

---

## GPS / Lokacija (`SABLON_Lokacija_GoogleMaps.md`)

| UNIVERZALNO | KONKRETNO |
|-------------|-----------|
| `play-services-location` u Gradle | — |
| `FusedLocationProviderClient`, `getLastLocation()` | — |
| Metode `dohvatiLokaciju()`, `imaDozvolu()`, `zatraziDozvolu()` | — |
| `onRequestPermissionsResult` obrazac | `REQUEST_CODE` broj (npr. 300) |
| Manifest: FINE + COARSE location | — |
| **KORAK 1b** — samo GPS bez mape | **KORAK 1–4** — ako zadatak traži Google Maps |
| — | ID TextView-a za prikaz koordinata |
| — | Google Maps API ključ (samo ako ima mapu) |
| — | Koordinate markera na mapi (samo ako ima mapu) |

---

## Senzori i Kamera (`SABLON_Senzori_Kamera.md`)

| UNIVERZALNO | KONKRETNO |
|-------------|-----------|
| Manifest: `CAMERA` + FileProvider + `file_paths.xml` | — |
| `ActivityResultLauncher`, `otvoriKameru()`, FileProvider URI | — |
| `implements SensorEventListener` | — |
| `onResume` / `onPause` register/unregister | — |
| Obrazac `onSensorChanged(SensorEvent event)` | — |
| KORAK 3 delovi **3a–3e** (ne ceo blok odjednom!) | — |
| — | `Sensor.TYPE_ACCELEROMETER` vs `TYPE_GYROSCOPE` |
| — | Gde prikazuješ vrednosti (Button text, Toast, TextView) |
| — | Kada Toast (npr. posle fotografije — Kolokvijum 2) |
| — | ID ImageButton / ImageView iz layouta |
| KORAK 4 shake | samo ako zadatak kaže "protresi" |

---

## SQLite / SharedPreferences / Kontakti (`SABLON_SQLite_...md`)

| UNIVERZALNO | KONKRETNO |
|-------------|-----------|
| `SQLiteOpenHelper`, `getWritableDatabase()`, CRUD obrazac | — |
| Singleton `getInstance(context)` u DatabaseHelper | — |
| SharedPreferences: `getSharedPreferences` + `edit().put...().apply()` | — |
| ContentProvider query obrazac | — |
| — | Ime tabele, kolona (`CREATE TABLE`) |
| — | Polja model klase (title, body, cena...) |
| — | Imena CRUD metoda (`dodajPost`, `obrisiPrvi`...) |
| — | Ključ u SharedPreferences (npr. `"tekst"`) |
| — | Koji kontakt prikazuješ (prvi, po imenu...) |
| KORAK 4–5 (uloga, RadioGroup) | samo za vežbe sa loginom — ne Kolokvijum 2 |

> **Napomena:** `Post.java` za **bazu** može imati konstruktore za upis/čitanje.  
> `Post.java` za **Retrofit** ima prazan konstruktor + `@SerializedName` — isto ime klase, druga svrha polja.

---

## Retrofit (`SABLON_Retrofit_HTTP_Zahtevi.md`)

| UNIVERZALNO | KONKRETNO |
|-------------|-----------|
| 3 Gradle linije (retrofit, gson, logging) + Sync | — |
| Manifest `INTERNET` | — |
| `network` paket: `ApiService` (Interface) + `RetrofitClient` (Class) | — |
| Singleton u RetrofitClient | — |
| `enqueue` + `Callback` + `onResponse` / `onFailure` | — |
| Init: `apiService = RetrofitClient.getInstance().getApiService()` | — |
| — | `BASE_URL` (mora `/` na kraju) |
| — | `@GET("posts")` vs `@GET("users")` vs `@GET("posts/{id}")` |
| — | `Call<List<Post>>` vs `Call<Post>` |
| — | Polja u modelu = JSON ključevi |
| — | `@SerializedName` samo kad JSON ima `_` (npr. `comment_count`) |
| — | **KORAK 5 varijanta** — vidi tabelu ispod |

### Retrofit KORAK 5 — biraš jednu varijantu (KONKRETNO)

| Zadatak traži | Varijanta |
|---------------|-----------|
| Jedan objekat u TextView | **A** |
| Broj elemenata u Toast | **B** |
| Svi naslovi u TextView (petlja) | **C** |
| API → SQLite (Switch, prvih N) | **D** |

---

## Kolokvijum 2 — šta je univerzalno, šta samo tu

> Detaljan checklist: `SABLON_KOLOKVIJUM_2.md`

| Zadatak | UNIVERZALNO (iz šablona) | KONKRETNO (samo Kolokvijum 2) |
|---------|--------------------------|-------------------------------|
| 1 Layout | vertikalni LinearLayout | ID-evi: `lokacijaTextView`, `kameraImageButton`, `slikaImageView`, `postSwitch`, `obrisiButton` |
| 2 MainActivity | jedna klasa, findViewById | — |
| 3 GPS | KORAK 1b Lokacija | prikaz u `lokacijaTextView` |
| 4 Kamera + žiroskop | Kamera KORAK 1,2,5 + Senzori 3a–3e | Toast X,Y,Z **posle** fotografije |
| 5 Model + Retrofit + SQLite | Retrofit 1–4 + SQL model/helper | beeceptor URL, Post polja, test Toast "Postova: 10" |
| 6 Switch ON | Retrofit **D** + SQL insert | prvih **10** postova; drugi put Toast `title` iz baze |
| 7 Obriši dugme | SQL delete + notifikacija obrazac | poruka "Nema više postova!" |
| 8 Akcelerometar | Senzori 3a–3e | vrednosti na **tekstu dugmeta** |
| 9 Switch OFF | SharedPreferences + kontakti | ključ `"tekst"`, prvi kontakt u TextView |

---

## Brza odluka — da li uopšte treba taj šablon?

| Zadatak spominje | Otvori šablon | UNIVERZALNI koraci u njemu |
|------------------|---------------|----------------------------|
| UI elementi | Layout | XML struktura |
| lat/lng, GPS | Lokacija | 1b (bez mape) |
| mapa, marker | Lokacija | 1–4 (sa mapom) |
| kamera, slika | Senzori | KORAK 1, 2, 5 |
| senzor, akcelerometar, žiroskop | Senzori | KORAK 3 |
| baza, SQLite, obriši, upiši | SQLite | KORAK 1–2 |
| SharedPreferences, sačuvaj | SQLite | KORAK 3 |
| kontakti | SQLite | KORAK 6 |
| Retrofit, HTTP, JSON, API | Retrofit | KORAK 1–6 |
| ceo kolokvijum | KOLOKVIJUM_2 + gornji šabloni | kombinacija |

---

## Najčešća greška

| ❌ Pomešano | ✅ Ispravno |
|------------|------------|
| Menjaš singleton RetrofitClient za svaki zadatak | Menjaš samo `BASE_URL` |
| Menjaš `enqueue` obrazac | Menjaš samo telo `onResponse` |
| Kopiraš ceo KORAK 3 Senzori odjednom | Uzimaš delove 3a, 3b, 3c... |
| Praviš novu Activity za svaku stvar | Sve u **MainActivity** na kolokvijumu |
| `Post.java` iz Retrofit-a sa konstruktorima za bazu | Retrofit model = Gson polja; baza = DatabaseHelper |

---

## Povezani fajlovi

| Fajl | Uloga |
|------|-------|
| `SABLON_MASTER_VODIC.md` | redosled koraka, mapa fajlova |
| **`SABLON_UNIVERZALNO_VS_KONKRETNO.md`** | **ovaj fajl — šta kopiraš vs šta menjaš** |
| `SABLON_Retrofit_HTTP_Zahtevi.md` | Retrofit detalji + varijante A–D |
| `SABLON_KOLOKVIJUM_2.md` | checklist zadataka 1–9 |
