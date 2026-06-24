# REČNIK TODO — šta staviti umesto `TODO_...`

> Kad u šablonu vidiš **`TODO_nešto`** → to **nije** gotov kod.  
> Moraš zameniti vrednošću iz **teksta zadatka** ili iz **svog layouta**.

**Pravilo:** posle zamene **nigde ne sme** da ostane reč `TODO` u kodu.

---

## Odakle uzimaš vrednost (3 izvora)

| Izvor | Primer |
|-------|--------|
| **Tekst zadatka** | URL sajta, ime tabele, „žiroskop“, „prvih 10 postova“ |
| **Tvoj layout XML** | `@+id/lokacijaTextView` → u Javi `R.id.lokacijaTextView` |
| **JSON / API** | polja iz zadatka: `title`, `body`, `comment_count` |

Ne izmišljaj — ako zadatak kaže `lokacijaTextView`, koristi tačno to.

---

## Kolokvijum 2 — gotove vrednosti (kopiraj ove)

| TODO u šablonu | Stavi ovo (kolokvijum 2) |
|----------------|--------------------------|
| `TODO_IME_PAKETA` | `com.example.kolokvijum2` |
| `TODO_Activity` | **ignoriši** — koristi `MainActivity` |
| `TODO_textView` (lokacija) | `lokacijaTextView` |
| `TODO_imageButton` | `kameraImageButton` |
| `TODO_imageView` | `slikaImageView` |
| `TODO_switch` | `postSwitch` |
| `TODO_button` / dugme | `obrisiButton` |
| `TODO_TIP_SENZORA` (Toast posle slike) | `Sensor.TYPE_GYROSCOPE` |
| `TODO_TIP_SENZORA` (tekst na dugmetu) | `Sensor.TYPE_ACCELEROMETER` |
| `TODO_BASE_URL` | `https://app.beeceptor.com/mock-server/dummy-json/` |
| `TODO_endpoint` | `posts` |
| `TODO_Model` / `TODO_ImeKlase` | `Post` |
| `TODO_tabela` | `postovi` |
| `TODO_ime_baze` | `kolokvijum2.db` |

---

## Svi TODO — šta znači i šta staviš

### Paket i klase

| TODO | Gde | Šta staviš | Primer |
|------|-----|------------|--------|
| `TODO_IME_PAKETA` | `package com.example....` | paket projekta | `com.example.kolokvijum2` |
| `TODO_Activity` | `public class ...` | ime **nove** Activity | `MapsActivity` — **na kolokvijumu ignoriši** |
| `TODO_KameraActivity` | senzori šablon | **ne pravi** — kod ide u MainActivity | — |
| `TODO_Model` / `TODO_ImeKlase` | model klasa | ime iz zadatka | `Post`, `Student`, `Proizvod` |
| `TODO_NovaActivity` | Manifest | ime novog ekrana | `SettingsActivity` |

### Layout — ID-evi (XML i Java moraju biti ISTI)

| TODO | Gde | Šta staviš | Odakle |
|------|-----|------------|--------|
| `TODO_textView` | `android:id` + findViewById | smislen ID | zadatak ili smisli: `lokacijaTextView` |
| `TODO_imageButton` | isto | npr. `kameraImageButton` | layout |
| `TODO_imageView` | isto | npr. `slikaImageView` | layout |
| `TODO_switch` | isto | npr. `postSwitch` | layout |
| `TODO_button` | isto | npr. `obrisiButton` | layout |
| `TODO_editText` | isto | npr. `emailEditText` | layout |
| `TODO_listView` | isto | npr. `kontaktiListView` | layout |
| `TODO_pocetni_tekst` | `android:text` | početni tekst na ekranu | npr. `"Lokacija"` |
| `TODO_labela` | Switch/CheckBox text | labela pored elementa | npr. `"Ucitaj postove"` |
| `TODO_opis` | contentDescription | kratak opis (pristupačnost) | `"Kamera"` |
| `activity_TODO_mapa.xml` | ime layout fajla | npr. `activity_maps.xml` | — |

**Pravilo ID-a:** u XML `@+id/lokacijaTextView` → u Javi `R.id.lokacijaTextView` i field `lokacijaTextView`.

### GPS i mapa

| TODO | Šta staviš | Primer |
|------|------------|--------|
| `TODO_lat` | geografska širina | `44.8176` |
| `TODO_lng` | geografska dužina | `20.4569` |
| `YOUR_API_KEY_OVDE` | Google Maps ključ | samo ako ima **mapu** |

### Senzori

| TODO | Šta staviš | Kada |
|------|------------|------|
| `TODO_TIP_SENZORA` | `Sensor.TYPE_GYROSCOPE` | žiroskop (Toast posle slike) |
| `TODO_TIP_SENZORA` | `Sensor.TYPE_ACCELEROMETER` | akcelerometar (tekst dugmeta) |
| `TODO_TIP_SENZORA` | `Sensor.TYPE_LIGHT` | itd. — **tačno ono što zadatak kaže** |

### Retrofit / API

| TODO | Šta staviš | Odakle |
|------|------------|--------|
| `TODO_BASE_URL` | adresa servera, **sa `/` na kraju** | tekst zadatka |
| `TODO_endpoint` | putanja bez base URL | `"posts"`, `"users"` |
| `TODO_polje1`, `TODO_atribut1` | JSON ključ = Java polje | `title`, `body` |
| `TODO_polje2` | drugo polje | `body`, `email` |
| `comment_count` | `@SerializedName("comment_count")` | kad JSON ima `_` |

**Primer zamene u Post.java:**

```java
// ❌ šablon
@SerializedName("TODO_polje1")
private String TODO_atribut1;

// ✅ kolokvijum 2
private String title;   // JSON već kaže "title" — anotacija ne treba
```

### SQLite / baza

| TODO | Šta staviš | Primer |
|------|------------|--------|
| `TODO_ime_baze` | ime .db fajla | `kolokvijum2.db` |
| `TODO_tabela` | ime tabele | `postovi` |
| `TODO_kolona1` | kolona u CREATE TABLE | `title`, `ime` |
| `TODO_kolona2` | druga kolona | `body`, `email` |
| `TODO_polje1` | polje u model klasi | isto ime kao kolona |
| `TODO_default_uloga` | default vrednost | `"korisnik"` — samo ako ima uloge |

### SharedPreferences / navigacija (vežbe sa loginom)

| TODO | Šta staviš |
|------|------------|
| `TODO_AppPrefs` | ime fajla prefs | `"AppPrefs"` |
| `TODO_uloga1`, `TODO_uloga2` | vrednosti uloga | `"vozac"`, `"putnik"` |
| `TODO_Ekran1Activity` | ime Activity za navigaciju | `HomeActivity` |
| `TODO_ImeServisa` | ime Service klase | `SyncService` |

---

## Šta IGNORISATI na kolokvijumu

| TODO / linija | Zašto |
|---------------|--------|
| `public class TODO_Activity` | ne praviš novu klasu — sve u MainActivity |
| `<activity android:name=".TODO_Activity"/>` | MainActivity već u Manifest-u |
| `TODO_Ekran1Activity`, login navigacija | nema više ekrana na kolokvijumu |
| `TODO_KameraActivity` | kamera ide u MainActivity |

---

## Primer — pre i posle (layout + Java)

**Zadatak kaže:** „TextView sa id lokacijaTextView za GPS“

```xml
<!-- ❌ -->
android:id="@+id/TODO_textView"

<!-- ✅ -->
android:id="@+id/lokacijaTextView"
```

```java
// ❌
private TextView TODO_textView;
TODO_textView = findViewById(R.id.TODO_textView);

// ✅
private TextView lokacijaTextView;
lokacijaTextView = findViewById(R.id.lokacijaTextView);
```

---

## Primer — Retrofit

**Zadatak kaže:** „GET sa https://app.beeceptor.com/mock-server/dummy-json/posts“

```java
// ❌
private static final String BASE_URL = "TODO_BASE_URL";
@GET("TODO_endpoint")

// ✅
private static final String BASE_URL =
        "https://app.beeceptor.com/mock-server/dummy-json/";
@GET("posts")
```

---

## Brza provera pre predaje

- [ ] Nema reči `TODO` nigde u projektu (Search in Files → `TODO`)
- [ ] Svaki `findViewById(R.id.xxx)` — `xxx` postoji u layout XML
- [ ] `package` isti u svim Java fajlovima (osim `network` — ima `.network` na kraju)
- [ ] BASE_URL se završava sa `/`
- [ ] Senzor tip tačno onaj iz zadatka (gyro vs accel)

---

## Kad zadatak kaže DRUGAČIJE (ne Post, ne obrisiButton…)

Obrazac je uvek isti — menjaš **imena** i **šta radi listener**, ne strukturu projekta.

### `TODO_Activity` → uvek `MainActivity`?

| Situacija | Šta pišeš |
|-----------|-----------|
| Kolokvijum — jedan ekran (kao kol. 2) | **`MainActivity`** — ignoriši `TODO_Activity` |
| Zadatak eksplicitno: „napravi MapsActivity“ | **tada** pravi tu klasu + `<activity .MapsActivity/>` |
| Retrofit šablon sa `<activity .TODO_Activity/>` | **ne dodaj** — samo INTERNET dozvolu |

> Na kolokvijumu: **da, praktično uvek samo MainActivity.**

---

### Nije `Post` nego `User`, `Comment`, `Proizvod`…

| Deo | Šta menjaš |
|-----|------------|
| Klasa | `User.java` umesto `Post.java` — polja iz **JSON-a u zadatku** |
| ApiService | `@GET("users")` + `Call<List<User>>` umesto `posts` / `Post` |
| DatabaseHelper | ime tabele/kolona prema modelu (npr. `korisnici`, `ime`, `email`) |
| MainActivity | import `User`, isti `enqueue` obrazac |

**Primer — zadatak kaže korisnike:**

```java
// ApiService
@GET("users")
Call<List<User>> getSviKorisnici();

// MainActivity — isto kao za postove, samo tip
apiService.getSviKorisnici().enqueue(new Callback<List<User>>() { ... });
```

Vidi `SABLON_Retrofit_...md` — u ApiService su primeri za `comments`, `users`.

---

### `GET` vs `POST` (HTTP metoda — ne mešaj sa klasom `Post`!)

| Zadatak kaže | ApiService |
|--------------|------------|
| dohvati, učitaj, GET, sa sajta | `@GET("...")` — **kolokvijum skoro uvek ovo** |
| pošalji, kreiraj na serveru, POST | `@POST("...")` + `@Body` |

Kolokvijum 2 = samo **GET** lista postova.  
Ako zadatak traži POST → `SABLON_Retrofit_...md` → tabela anotacija (`@POST`, `@Body`).

---

### Nije `obrisiButton` nego `kreirajButton` (ili bilo koji ID)

Menja se **samo ime** — logika u listeneru zavisi od zadatka.

| Gde | Bilo | Zadatak kaže `kreirajButton` |
|-----|------|------------------------------|
| XML | `android:id="@+id/obrisiButton"` | `android:id="@+id/kreirajButton"` |
| MainActivity field | `private Button obrisiButton` | `private Button kreirajButton` |
| findViewById | `R.id.obrisiButton` | `R.id.kreirajButton` |
| Listener | `obrisiButton.setOnClickListener(...)` | `kreirajButton.setOnClickListener(...)` |
| **Šta radi klik** | `dbHelper.obrisiPrviPost()` | ono što zadatak traži — npr. `dbHelper.dodaj(...)`, otvaranje kamere… |

**Primer — zadatak: „Button kreirajButton dodaje novi red u bazu“:**

```java
kreirajButton.setOnClickListener(v -> {
    Post p = new Post(); // ili iz EditText polja
    dbHelper.dodajPost(p);
});
```

**Primer — zadatak: „Button sacuvajButton snima u SharedPreferences“:**

```java
sacuvajButton.setOnClickListener(v -> {
    getSharedPreferences("AppPrefs", MODE_PRIVATE)
        .edit().putString("kljuc", nekiEditText.getText().toString()).apply();
});
```

> **Pravilo:** ID iz zadatka = ID u XML = `R.id.istiId` u Javi. **Šta dugme radi** = tekst zadatka (obriši / kreiraj / sačuvaj).

---

### Nije `postSwitch` nego `syncSwitch`, nije `lokacijaTextView` nego `statusText`…

Isti princip kao za dugme — zameni ID **svuda isto**. Switch listener logika ostaje obrazac (ON/OFF), menja se **šta radi** u `if (isChecked)`.

---

### Brza mapa — zadatak kaže X → menjaš Y

| Zadatak kaže | Menjaš |
|--------------|--------|
| drugi endpoint (`users`, `comments`) | `ApiService` @GET + model klasa |
| drugi JSON ključevi | polja u modelu + `@SerializedName` |
| drugi ID dugmeta/TextView-a | layout + findViewById + field ime |
| dugme **briše** | listener → `delete` / `obrisi...` |
| briše **prvi** | `obrisiPrviPost()` → `ORDER BY id ASC LIMIT 1` |
| briše **poslednji** | `obrisiPosledniPost()` → `ORDER BY id DESC LIMIT 1` |
| briše **po ID** | `obrisiPoId(id)` → `WHERE id=?` |
| čita **prvi** za Toast | `getPrviPost()` |
| čita **poslednji** | `getPoslednjiPost()` |
| dugme **kreira / dodaje** | listener → `insert` / `dodaj...` |
| dugme **čuva** | listener → SharedPreferences |
| HTTP POST umesto GET | `@POST` + `@Body` u ApiService |

---

## Povezani fajlovi

| Fajl | Za šta |
|------|--------|
| `VODIC_DAN_KOLOKVIJUMA.md` | dan kolokvijuma — redosled |
| `PRIprema_KOLOKVIJUM_2_KOMPLETNO.md` | kolokvijum 2 **bez** TODO — gotov kod |
| `SABLON_UNIVERZALNO_VS_KONKRETNO.md` | šta kopiraš vs menjaš |

> **Savet:** za kolokvijum 2 otvori **`PRIprema_KOLOKVIJUM_2_KOMPLETNO.md`** — tamo nema TODO, sve je već zamenjeno.
