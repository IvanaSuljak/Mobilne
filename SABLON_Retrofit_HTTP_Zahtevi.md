# ŠABLON — Retrofit (HTTP zahtevi)

> **MASTER:** Otvori prvo `SABLON_MASTER_VODIC.md` → nađi "Retrofit, HTTP, API" u tabeli.  
> **Univerzalno vs konkretno:** `SABLON_UNIVERZALNO_VS_KONKRETNO.md` → sekcija Retrofit.

> Svuda gde vidiš `TODO` → zameni sa svojim podacima.
> Sve ostalo kopiraš bukvalno.

---

## Retrofit — šta je šta (pročitaj prvo)

Kad zadatak traži **Retrofit / HTTP / API / JSON sa sajta**, praviš **6 delova**. Svaki ima tačnu ulogu — ne mešaj ih.

| Deo | Gde je fajl | Šta radi (jednom rečenicom) | Menjaš? |
|-----|-------------|-----------------------------|---------|
| **KORAK 1** Gradle | `build.gradle (Module :app)` | Ubacuje Retrofit biblioteku u projekat | ❌ uvek iste 3 linije + Sync |
| **KORAK 6** Manifest | `AndroidManifest.xml` | Dozvoljava aplikaciji internet | ❌ jedna linija `INTERNET` |
| **KORAK 2** Model | `Post.java` (glavni paket) | Opisuje **oblika JSON-a** — polja kao na serveru | ✅ polja iz zadatka |
| **KORAK 3** ApiService | `network/ApiService.java` | **Lista URL adresa** koje možeš da zoveš (`@GET`) | ✅ endpoint + `Call<>` tip |
| **KORAK 4** RetrofitClient | `network/RetrofitClient.java` | **Spaja se na server** (BASE_URL + Gson) | ✅ samo `BASE_URL` |
| **KORAK 5** Callback | `MainActivity.java` | **Šalje zahtev** i **radi nešto sa odgovorom** | ✅ TextView / Toast / baza |

### Kako teče podatak (zapamti ovaj red)

```
Server (JSON)
    ↓  HTTP GET
RetrofitClient  ←  adresa servera (BASE_URL)
    ↓
ApiService      ←  koja putanja (@GET "posts")
    ↓
Gson            ←  JSON → Java objekat
    ↓
Post.java       ←  model (title, body, id...)
    ↓
Callback        ←  TI odlučuješ: TextView? Toast? SQLite?
```

> **Model (Post)** = šta **stigne** sa servera.  
> **ApiService** = **koji** URL zoveš.  
> **RetrofitClient** = **kako** se povezuješ.  
> **Callback (KORAK 5)** = **šta radiš** kad odgovor stigne.

### Šta ide u koji fajl — brza provera

| Pitanje | Odgovor — gde pišeš |
|---------|---------------------|
| Koja je adresa sajta? | `RetrofitClient` → `BASE_URL` (mora `/` na kraju) |
| Da li zovem `posts` ili `users`? | `ApiService` → `@GET("posts")` |
| Da li API vraća niz ili jedan objekat? | `ApiService` → `Call<List<Post>>` ili `Call<Post>` |
| Koja polja ima JSON? | `Post.java` → `private String title;` itd. |
| JSON ima `comment_count`? | `Post.java` → `@SerializedName("comment_count")` |
| Gde kliknem "pošalji zahtev"? | `MainActivity` → `apiService.metoda().enqueue(...)` |
| Šta uradim kad stigne odgovor? | `MainActivity` → unutar `onResponse` (KORAK 5 A/B/C/D) |

---

## Kada ti treba ceo šablon, a kada ne

| Situacija | Treba Retrofit? |
|-----------|-----------------|
| "dohvati postove sa sajta", "Retrofit", "beeceptor", "HTTP GET" | ✅ DA — svi koraci 1–6 |
| "upiši u SQLite" **bez** interneta | ❌ NE — samo SQL šablon |
| "SharedPreferences", senzori, kamera, GPS | ❌ NE |
| SQLite **+** "dohvati sa API-ja pa sačuvaj" (Kolokvijum 2) | ✅ Retrofit **+** SQL šablon |

---

## Ako zadatak kaže... → otvori ovaj KORAK

| Zadatak kaže | Šta radiš | Gde u šablonu |
|--------------|-----------|---------------|
| "Podesiti Retrofit" / "GET zahtev" | Gradle → Manifest → network paket → init u MainActivity | KORAK 1, 6, 3, 4, 5 init |
| "Kreirati model za postove" | Polja = JSON ključevi + prazan konstruktor | **KORAK 2** |
| "Dohvati **sve** postove" | `Call<List<Post>>` + `@GET("posts")` | **KORAK 3** varijanta lista |
| "Dohvati **jedan** po ID" | `Call<Post>` + `@GET("posts/{id}")` + `@Path` | **KORAK 3** varijanta jedan |
| "Prikaži u **TextView**" | Jedan objekat ili petlja kroz listu | **KORAK 5 — A** ili **C** |
| "Koliko ima u **Toast**" | `response.body().size()` | **KORAK 5 — B** |
| "Upiši u **bazu**" / Switch prvih 10 | Petlja + `dbHelper.dodajPost()` | **KORAK 5 — D** + SQL šablon |
| JSON ključ sa `_` (`comment_count`) | Anotacija na polju | **KORAK 2** — `@SerializedName` |

---

## UNIVERZALNO vs SPECIFIČNO ZA ZADATAK

### UNIVERZALNO — kopiraš obrazac, menjaš TODO

| Deo | Gde | Menjaš |
|-----|-----|--------|
| **KORAK 1** Gradle (3 linije) | `build.gradle (Module :app)` | ništa — uvek isto + Sync |
| **KORAK 6** INTERNET dozvola | Manifest pre `<application>` | ništa |
| **KORAK 4** RetrofitClient | `network/RetrofitClient.java` | samo `BASE_URL` |
| **Singleton pattern** | RetrofitClient | ništa |
| **enqueue + Callback** | MainActivity | ime metode iz ApiService |
| **network paket** | `com.example.PAKET.network` | ime paketa |

### MENJAŠ PO ZADATKU

| Deo | Šta zavisi od zadatka |
|-----|----------------------|
| **KORAK 2** Model klasa | polja iz JSON-a (title, body, userId...) |
| **KORAK 3** ApiService | `@GET("posts")` vs `@GET("users")` vs `@GET("posts/{id}")` |
| **KORAK 5** Callback | šta radiš sa odgovorom (TextView / Toast / baza) |
| `@SerializedName` | samo kad JSON ključ ≠ Java ime (npr. `comment_count`) |

### SAMO ZA Kolokvijum 2 (beeceptor postovi)

| Deo | Specifično |
|-----|------------|
| Sekcija **"Kolokvijum 2 — Post model"** na kraju | tačan Post.java + BASE_URL |
| `@GET("posts")` + `Call<List<Post>>` | endpoint iz zadatka |
| KORAK 5 + **SQLite** | zadatak 6 — upiši prvih 10 u bazu (vidi SQL šablon) |

---

## TAČAN REDOSLED

| # | Gde | Šta radiš | Kad |
|---|-----|-----------|-----|
| 1 | `build.gradle (Module :app)` | retrofit + converter-gson + logging (KORAK 1) | Pre pisanja network klasa |
| 2 | Isti fajl | **Sync Now** | Odmah |
| 3 | `AndroidManifest.xml` | `<uses-permission INTERNET>` pre `<application>` | Posle Gradle |
| 4 | `java/.../Post.java` | Model klasa — polja = JSON ključevi (KORAK 2) | Nova Java klasa u glavnom paketu |
| 5 | `java/.../network/` paket | **Kreiraj paket** → ApiService + RetrofitClient | Vidi "Gde kreirati network" ispod |
| 6 | `java/.../network/ApiService.java` | Interfejs sa `@GET` (KORAK 3) | Interface, ne Class |
| 7 | `java/.../network/RetrofitClient.java` | Singleton, zameni BASE_URL (KORAK 4) | Class |
| 8 | `MainActivity.java` | `apiService = RetrofitClient.getInstance().getApiService()` | u onCreate |
| 9 | `MainActivity.java` | `api.metoda().enqueue(new Callback(){...})` (KORAK 5) | U listeneru ili test u onCreate |

> BASE_URL **mora** da se završi sa `/`  
> npr. `https://app.beeceptor.com/mock-server/dummy-json/`

---

## Gde kreirati `network` paket (Android Studio)

```
app → src → main → java → com.example.TVOJ_PAKET
                              ├── MainActivity.java
                              ├── Post.java
                              └── network          ← desni klik → New → Package → "network"
                                   ├── ApiService.java      (New → Java Class → Interface)
                                   └── RetrofitClient.java  (New → Java Class → Class)
```

Na vrhu network fajlova:
```java
package com.example.kolokvijum2.network;
```

U MainActivity import:
```java
import com.example.kolokvijum2.network.ApiService;
import com.example.kolokvijum2.network.RetrofitClient;
```

---

## TODO lista — šta zameniti

- [ ] `TODO_IME_PAKETA` → tvoj paket (npr. `com.example.mojaplikacija`)
- [ ] `TODO_BASE_URL` → adresa servera (npr. `https://dummy-json.mock.beeceptor.com/`)
- [ ] `TODO_Model` → naziv modela (npr. `Post`, `Comment`, `User`)
- [ ] `TODO_endpoint` → putanja bez base URL-a (npr. `posts`, `comments/2`)
- [ ] `TODO_polje` → naziv JSON ključa / Java polja (npr. `title`, `body`)
- [ ] `TODO_textView` → ID TextView-a u layoutu

---

## KORAK 1 — build.gradle (Module :app)

> **Fajl:** Gradle Scripts → **`build.gradle (Module :app)`** → unutar `dependencies { }` → **Sync Now**

```groovy
dependencies {
    // ... postojeće zavisnosti ...

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

---

## KORAK 2 — Model klasa

> **Gde:** glavni paket `com.example.PAKET/Post.java` (može i `model/` podpaket — oba rade)
> **Pravilo:** za svako polje u JSON-u → jedno Java polje + getter
> **Gson:** prazan konstruktor `public Post() { }` obavezan

```java
package com.example.TODO_IME_PAKETA;   // ili .model ako koristiš podpaket

import com.google.gson.annotations.SerializedName;

public class TODO_Model {

    // Isto ime kao JSON → anotacija NIJE obavezna
    private int id;
    private int userId;

    // Isto ime kao JSON
    @SerializedName("TODO_polje1")       // npr. "title"
    private String TODO_atribut1;

    @SerializedName("TODO_polje2")       // npr. "body"
    private String TODO_atribut2;

    // JSON ima underscore, Java ne sme:
    @SerializedName("comment_count")     // primer — samo ako JSON ima _
    private int commentCount;

    public TODO_Model() { }   // OBAVEZNO za Gson

    // Getters (Generate → Getter u Android Studiju)
    public int getId() { return id; }
    public String getTODO_atribut1() { return TODO_atribut1; }
    // ... ostali getteri
}
```

**Kako mapirati JSON → Java:**

| JSON | Java | Anotacija |
|------|------|-----------|
| `"title"` | `private String title;` | ne treba |
| `"userId"` | `private int userId;` | ne treba |
| `"comment_count"` | `private int commentCount;` | `@SerializedName("comment_count")` |

> Više tipova u API-ju (Post, Comment, User) → **jedna klasa po tipu**.


## KORAK 3 — ApiService (`network/ApiService.java`)

> **Tip fajla:** Interface (New → Java Class → **Interface**)
> **Pravilo:** `@GET` putanja + tip u `Call<>` mora da odgovara JSON-u

**Izaberi prema zadatku:**

| API vraća | ApiService piše |
|-----------|-----------------|
| niz `[{...},{...}]` | `Call<List<Post>> getSve()` |
| jedan objekat `{...}` | `Call<Post> getJedan(@Path("id") int id)` |

```java
package com.example.TODO_IME_PAKETA.network;

import com.example.TODO_IME_PAKETA.Post;   // tvoj model
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // VARIJANTA A — lista (zadatak: "svi postovi", upiši u bazu, broj u Toast)
    @GET("TODO_endpoint")                    // npr. "posts"
    Call<List<Post>> getSvePostove();

    // VARIJANTA B — jedan po ID (zadatak: "prvi post", "drugi komentar")
    @GET("TODO_endpoint/{id}")               // npr. "posts/{id}"
    Call<Post> getJedan(@Path("id") int id);

    // Vežba 7 primeri:
    // @GET("posts")           Call<List<Post>>    getSvePosts();
    // @GET("posts/{id}")      Call<Post>          getPostById(@Path("id") int id);
    // @GET("comments/{id}")   Call<Comment>       getKomentarById(@Path("id") int id);
    // @GET("users")           Call<List<ApiUser>> getSveKorisnike();
}
```

> **Puni URL** = BASE_URL + putanja → `https://...dummy-json/` + `posts` = `https://...dummy-json/posts`

---

## KORAK 4 — RetrofitClient (`network/RetrofitClient.java`)

```java
package com.example.TODO_IME_PAKETA.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // TODO: Promeni URL — MORA se završiti sa "/"
    private static final String BASE_URL = "TODO_BASE_URL";

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }

    public ApiService getApiService() { return apiService; }
}
```

> Ovu klasu kopiraš skoro bukvalno — jedino menjaš `BASE_URL`.

---

## KORAK 5 — Slanje zahteva (u MainActivity)

> **Uvek isto:** `apiService.metoda().enqueue(new Callback<>(){ onResponse / onFailure })`  
> **Razlika:** šta radiš u `onResponse` — izaberi **jednu** varijantu ispod.

### KORAK 5 — koja varijanta? (samo ovde biraš)

| Zadatak traži | ApiService poziva | Varijanta |
|---------------|-------------------|-----------|
| Prikaži **jedan** post/komentar u TextView | `getJedan(id)` | **A** |
| Broj postova/korisnika u **Toast** | `getSvePostove()` → `.size()` | **B** |
| Svi naslovi u **TextView** (petlja) | `getSvePostove()` → for | **C** |
| Dohvati sa API-ja → **SQLite** (Kolokvijum Switch) | `getSvePostove()` → `dbHelper.dodajPost()` | **D** |

> Koraci 1–4 su gotovi? Prvo uradi **init** ispod, pa tek onda varijantu A/B/C/D.

### Inicijalizacija (uvek — u `onCreate`)

```java
private ApiService apiService;

// u onCreate:
apiService = RetrofitClient.getInstance().getApiService();
```

---

### VARIJANTA A — GET jedan → prikaži u TextView
**Koristi kad zadatak kaže:** "dohvati prvi post / drugi komentar i prikaži u TextView"

```java
apiService.getJedan(1).enqueue(new Callback<Post>() {   // 1 = ID iz zadatka
    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        if (response.isSuccessful() && response.body() != null) {
            Post obj = response.body();
            textView.setText(obj.getTitle() + "\n" + obj.getBody());
        }
    }
    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        Toast.makeText(MainActivity.this, "Greška!", Toast.LENGTH_SHORT).show();
    }
});
```

---

### VARIJANTA B — GET lista → broj u Toast
**Koristi kad zadatak kaže:** "prikaži koliko ima korisnika/postova u Toast-u"

```java
apiService.getSvePostove().enqueue(new Callback<List<Post>>() {
    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        if (response.isSuccessful() && response.body() != null) {
            Toast.makeText(MainActivity.this,
                    "Ukupno: " + response.body().size(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) { }
});
```

---

### VARIJANTA C — GET lista → prikaži sve u TextView (petlja)
**Koristi kad zadatak kaže:** "ispiši sve naslove u TextView"

```java
apiService.getSvePostove().enqueue(new Callback<List<Post>>() {
    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        if (response.isSuccessful() && response.body() != null) {
            StringBuilder sb = new StringBuilder();
            for (Post p : response.body()) {
                sb.append(p.getTitle()).append("\n");
            }
            textView.setText(sb.toString());
        }
    }
    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) { }
});
```

---

### VARIJANTA D — GET lista → upiši u SQLite bazu
**Koristi kad zadatak kaže:** "dohvati postove i upiši u bazu" (Kolokvijum 2 zadatak 6)

> Retrofit dohvati → SQLite sačuva. Vidi `SABLON_SQLite_...md` → `dodajPost()`.

```java
apiService.getSvePostove().enqueue(new Callback<List<Post>>() {
    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        if (response.isSuccessful() && response.body() != null) {
            List<Post> svi = response.body();
            for (int i = 0; i < Math.min(10, svi.size()); i++) {  // prvih 10
                dbHelper.dodajPost(svi.get(i));
            }
        }
    }
    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) { }
});
```

---

## KORAK 6 — AndroidManifest.xml

```xml
<!-- Dozvola za internet (van <application>) -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Nova Activity (unutar <application>) -->
<activity android:name=".TODO_Activity" android:exported="false" />
```

> Internet dozvola je NORMAL — NE traži se runtime, samo u Manifest-u!

---

## Brza referenca

### Tipovi povratnih vrednosti

| API odgovor | Java tip u Call<> |
|-------------|-------------------|
| `{...}` (jedan objekat) | `Call<MojModel>` |
| `[...]` (niz objekata) | `Call<List<MojModel>>` |

### Anotacije za zahteve

| Anotacija | Opis | Primer |
|-----------|------|--------|
| `@GET("putanja")` | HTTP GET | `@GET("posts")` |
| `@POST("putanja")` | HTTP POST | `@POST("posts")` |
| `@Path("x")` | `{x}` u URL | `@Path("id") int id` |
| `@Query("x")` | `?x=vrednost` u URL | `@Query("page") int page` |
| `@Body` | JSON telo zahteva | `@Body PostRequest body` |

### Greška ili uspeh?

```java
// U onResponse():
response.isSuccessful()    // true ako je HTTP kod 200-299
response.code()            // HTTP status kod (200, 404, 500...)
response.body()            // Java objekat (null ako nema tela)
response.errorBody()       // telo greške (za 4xx, 5xx)

// onFailure() se poziva samo za:
// - nema interneta
// - timeout
// - DNS greška
```

### Redosled koraka koji se NE MENJA

```
1. build.gradle (Module :app)  →  dodaj 3 zavisnosti + Sync Now
2. Manifest  →  INTERNET dozvola
3. Post.java   →  polja = JSON ključevi, @SerializedName za comment_count
4. network paket  →  ApiService (Interface) + RetrofitClient (Class)
5. MainActivity  →  apiService init + enqueue Callback
```

---

## Kolokvijum 2 — koji KORAK za koji zadatak

| Zadatak | Retrofit deo | SQL / ostalo |
|---------|--------------|--------------|
| **5** — model + Retrofit + SQLite podesi | KORAK 1–4 + init KORAK 5; test Toast "Postova: 10" (**B**) | `Post.java`, `DatabaseHelper` (SQL šablon) |
| **6** — Switch ON prvi put | **KORAK 5 — D** (prvih 10 u bazu) | Switch listener + `dbHelper` |
| **6** — Switch ON kasnije | ❌ nema Retrofit | Toast sa `title` iz baze |
| **7–9** | ❌ nema Retrofit | notifikacija, senzor, SharedPreferences |

> Gotov primer na dnu fajla: `Post.java`, `BASE_URL`, `@GET("posts")`.

### JSON sa API-ja
```json
{ "userId": 1, "id": 1, "title": "...", "body": "...", "link": "...", "comment_count": 8 }
```

**Fajl:** `com.example.kolokvijum2/Post.java` (u glavnom paketu, NE u network)

```java
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;

public class Post {
    private int id;
    private int userId;
    private String title;
    private String body;
    private String link;

    @SerializedName("comment_count")   // JSON ima _, Java ne sme
    private int commentCount;

    public Post() { }  // obavezno za Gson

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getLink() { return link; }
    public int getCommentCount() { return commentCount; }
}
```

**ApiService za kolokvijum:**
```java
@GET("posts")
Call<List<Post>> getSviPostovi();
```

**RetrofitClient BASE_URL:**
```java
private static final String BASE_URL =
        "https://app.beeceptor.com/mock-server/dummy-json/";
```

**Test (zadatak 5):** Toast "Postova: 10" → Retrofit radi ✅

**Zadatak 6:** prebaci na **VARIJANTA D** (upiši u bazu) unutar Switch listenera.

