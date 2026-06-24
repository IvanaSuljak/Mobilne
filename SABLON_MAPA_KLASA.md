# MAPA — šta ide u koju klasu / fajl

> **Jedan pregled za ceo kolokvijum.**  
> Detaljan kod: `PRIprema_KOLOKVIJUM_2_KOMPLETNO.md`  
> Folderi: `VODIC_DAN_KOLOKVIJUMA.md` → šema drveta

---

## Pravilo za kolokvijum

| Tip fajla | Koliko klasa | Napomena |
|-----------|--------------|----------|
| `MainActivity.java` | **1** — skoro uvek samo ova Activity | GPS, kamera, senzori, listeneri, Retrofit **poziv** |
| `Post.java`, `DatabaseHelper.java` | po 1 klasa u glavnom paketu | model + baza |
| `network/` | 2 fajla: Interface + Class | ApiService + RetrofitClient |
| XML, Manifest, Gradle | nisu Java klase | layout, dozvole, biblioteke |

**NE praviš:** `RetrofitActivity`, `MapsActivity`, `TODO_Activity` — osim ako zadatak **eksplicitno** traži novi ekran.

---

## Master tabela — funkcija → gde pišeš

| Šta radiš | Fajl / klasa | Šta tačno ide unutra |
|-----------|--------------|----------------------|
| UI elementi, ID-evi | `activity_main.xml` | TextView, Button, Switch, `@+id/...` |
| Biblioteke (GPS, Retrofit) | `build.gradle (Module :app)` | `implementation '...'` + Sync |
| Dozvole, FileProvider | `AndroidManifest.xml` | `<uses-permission>`, `<provider>` |
| Putanje za kameru | `res/xml/file_paths.xml` | `<paths>` |
| Ekran, klikovi, senzori | **`MainActivity.java`** | fields, onCreate, metode, override, listeneri |
| Oblik JSON sa API-ja | **`Post.java`** (ili User, Comment…) | polja, getteri, `@SerializedName`, prazan konstruktor |
| SQLite tabela, CRUD | **`DatabaseHelper.java`** | CREATE TABLE, `dodajPost`, `getPrviPost`, `obrisi...` |
| Koji URL zoveš | **`ApiService.java`** (Interface) | `@GET("posts")`, `Call<List<Post>>` |
| Adresa servera | **`RetrofitClient.java`** | `BASE_URL`, singleton, Gson |
| HTTP poziv + odgovor | **`MainActivity.java`** | `apiService.get...().enqueue(Callback)` |
| Inicijalizacija baze/API | **`MainActivity.onCreate`** | `dbHelper = ...`, `apiService = ...` |

---

## MainActivity — šta IDE ovde (ne u drugu klasu)

| Deo zadatka | U MainActivity ide |
|-------------|-------------------|
| findViewById | ✅ |
| GPS `dohvatiLokaciju`, dozvole | ✅ |
| Kamera launcher, `otvoriKameru` | ✅ |
| `implements SensorEventListener`, onResume/onPause | ✅ |
| Switch / Button listeneri | ✅ |
| SharedPreferences `edit().put...` | ✅ |
| Kontakti `getContentResolver().query` | ✅ |
| Notifikacija | ✅ |
| Retrofit `enqueue` + Callback | ✅ |
| CREATE TABLE, SQL upit | ❌ → **DatabaseHelper** |
| `@GET`, BASE_URL | ❌ → **ApiService / RetrofitClient** |
| JSON polja `title`, `body` | ❌ → **Post.java** |

---

## Post.java — šta IDE ovde

| IDE | NE ide |
|-----|--------|
| `private` polja = JSON ključevi | SQL `CREATE TABLE` |
| `@SerializedName("comment_count")` | `enqueue`, Callback |
| getteri | Android UI kod |
| `public Post() { }` za Gson | `extends SQLiteOpenHelper` |

---

## DatabaseHelper.java — šta IDE ovde

| IDE | NE ide |
|-----|--------|
| `CREATE TABLE`, `insert`, `delete`, `query` | Retrofit, `@GET` |
| `dodajPost`, `getPrviPost`, `obrisiPrviPost` | findViewById, Toast |
| Singleton `getInstance` | SensorEventListener |
| `ContentValues`, `Cursor` | layout XML |

---

## network/ApiService.java — šta IDE ovde

| IDE | NE ide |
|-----|--------|
| `interface ApiService` | `class` (mora Interface!) |
| `@GET("posts")` | BASE_URL (to je u RetrofitClient) |
| `Call<List<Post>> getSviPostovi()` | telo metode — nema `{}` implementacije |
| import `Post` | SQLite kod |

---

## network/RetrofitClient.java — šta IDE ovde

| IDE | NE ide |
|-----|--------|
| `BASE_URL` | `@GET` endpointi |
| Retrofit.Builder, Gson | Callback, Toast |
| `getInstance()`, `getApiService()` | CRUD baze |

---

## Po zadatku kolokvijuma 2 — koja klasa

| Zadatak | Klase / fajlovi koje diraš |
|---------|----------------------------|
| 1 Projekt | Android Studio (automatski MainActivity) |
| 2 Layout | `activity_main.xml` + fields u **MainActivity** |
| 3 GPS | Gradle, Manifest, **MainActivity** |
| 4 Kamera + žiroskop | Manifest, `file_paths.xml`, **MainActivity** |
| 5 Model + Retrofit + baza | **Post**, **DatabaseHelper**, **ApiService**, **RetrofitClient**, Gradle, Manifest, init u **MainActivity** |
| 6 Switch ON | **MainActivity** listener + **DatabaseHelper** + **ApiService** (enqueue) |
| 7 Obriši | **MainActivity** listener + **DatabaseHelper** |
| 8 Akcelerometar | **MainActivity** onSensorChanged |
| 9 Switch OFF | **MainActivity** (prefs + kontakti), Manifest READ_CONTACTS |

---

## Gde koji šablon objašnjava klasu

| Klasa / fajl | Glavni šablon | Jasnoća |
|--------------|---------------|---------|
| `activity_main.xml` | `SABLON_Layout_XML.md` | ✅ |
| `MainActivity` | svi šabloni + MASTER Pravilo 1 | ✅ (paziti TODO_Activity u Lokacija/Retrofit) |
| `Post.java` | `SABLON_Retrofit` + `SABLON_SQLite` K1 | ✅ |
| `DatabaseHelper` | `SABLON_SQLite` K2 + kolokvijum sekcija | ✅ |
| `ApiService` | `SABLON_Retrofit` K3 | ✅ |
| `RetrofitClient` | `SABLON_Retrofit` K4 | ✅ |
| Manifest / Gradle | MASTER + po šablonu | ✅ |
| `SharedPreferencesManager` | SQL K3 (vežbe login) | ⚠️ na kolokvijumu 2 ide **direktno u MainActivity** |
| Nova Activity (Maps…) | `SABLON_Lokacija` K4 | ⚠️ samo ako zadatak traži mapu na posebnom ekranu |

---

## Česte greške — pogrešna klasa

| ❌ Greška | ✅ Ispravno |
|----------|------------|
| SQL u MainActivity | DatabaseHelper |
| `@GET` u MainActivity | ApiService |
| BASE_URL u MainActivity | RetrofitClient |
| `enqueue` u DatabaseHelper | MainActivity |
| Cela `class TODO_Activity` unutar MainActivity | metode u MainActivity |
| CREATE TABLE u Post.java | DatabaseHelper |

---

## Brza provera

Pre predaje pitaj za svaki deo koda:

> **„Da li ovo pripada UI, dozvoli, modelu, bazi, API definiciji ili ponašanju ekrana?“**

| Odgovor | Fajl |
|---------|------|
| UI | XML |
| dozvola | Manifest |
| biblioteka | Gradle |
| oblik podataka | Post.java |
| čuvanje lokalno | DatabaseHelper |
| URL definicija | ApiService + RetrofitClient |
| ponašanje, klik, fetch | MainActivity |

---

## Povezani fajlovi

| Fajl | Uloga |
|------|-------|
| **`SABLON_MAPA_KLASA.md`** | **ovaj fajl** |
| `VODIC_DAN_KOLOKVIJUMA.md` | dan kolokvijuma + folder drvo |
| `PRIprema_KOLOKVIJUM_2_KOMPLETNO.md` | gotov kod po fajlovima |
| `SABLON_TODO_RECNIK.md` | zamena TODO imena |
| `SABLON_UNIVERZALNO_VS_KONKRETNO.md` | šta kopiraš vs menjaš |
