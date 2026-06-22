# VEZBA 7 — Retrofit (HTTP zahtevi) — Kompletna dokumentacija

> **API koji se koristi:** `https://dummy-json.mock.beeceptor.com`

---

## Sadržaj

1. [Šta je Retrofit?](#1-šta-je-retrofit)
2. [Podešavanje — build.gradle](#2-podešavanje--buildgradle)
3. [Struktura projekta — novi fajlovi](#3-struktura-projekta--novi-fajlovi)
4. [Model klase (Post, Comment, ApiUser)](#4-model-klase)
5. [ApiService interfejs — definicija endpointa](#5-apiservice-interfejs)
6. [RetrofitClient — Singleton konfiguracija](#6-retrofitclient--singleton)
7. [Slanje zahteva — enqueue i Callback](#7-slanje-zahteva--enqueue-i-callback)
8. [Kompletni primeri iz koda](#8-kompletni-primeri)
9. [Šablon za kolokvijum](#9-šablon-za-kolokvijum)

---

## 1. Šta je Retrofit?

Retrofit je **HTTP klijent biblioteka** koja olakšava slanje zahteva ka REST API-jima.

**Bez Retrofit-a** bi trebalo:
- Ručno praviti `HttpURLConnection`
- Ručno parsirati JSON string
- Ručno upravljati nitima (threading)

**Sa Retrofit-om:**
- Definišeš interfejs sa anotacijama (`@GET`, `@POST`...)
- Gson automatski konvertuje JSON → Java objekat
- `enqueue()` automatski radi na pozadinskoj niti

```
Retrofit = HTTP klijent + JSON parser + Thread management
```

### Tok jednog zahteva

```
Kod                          Mreža
────────────────────────────────────────────────────────
apiService.getPostById(1)
    ↓
call.enqueue(callback)  ──→  GET https://...dummy-json.mock.beeceptor.com/posts/1
                         ←── {"id":1, "title":"...", "body":"..."}
    ↓
Gson konvertuje JSON → Post objekat
    ↓
onResponse(call, response) — izvršava se na GLAVNOJ NITI
    ↓
response.body() → Post post
    ↓
postTextView.setText(post.getTitle())
```

---

## 2. Podešavanje — build.gradle

```groovy
dependencies {
    // Retrofit — HTTP klijent
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    // Gson konverter — JSON automatski → Java objekat
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    // OkHttp logger — prikazuje zahteve u Logcat-u (za debug)
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

**AndroidManifest.xml** — dozvola za internet:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

> Internet dozvola je **NORMAL** (ne DANGEROUS) — ne treba runtime zahtjev!

---

## 3. Struktura projekta — novi fajlovi

```
java/com/example/mobilnevezbe/
├── model/
│   ├── Post.java          ← model za post
│   ├── Comment.java       ← model za komentar
│   └── ApiUser.java       ← model za korisnika sa API-ja
├── network/
│   ├── ApiService.java    ← interfejs sa svim endpointima
│   └── RetrofitClient.java ← singleton konfiguracija
└── RetrofitActivity.java  ← ekran koji šalje zahteve
```

---

## 4. Model klase

Model klase su **obične Java klase** sa poljima koja odgovaraju JSON ključevima.

### @SerializedName

Ako se ime Java polja razlikuje od JSON ključa, koristimo `@SerializedName`:

```java
// JSON: { "comment_count": 8 }
// Java: private int commentCount;  ← ne poklapa se!

@SerializedName("comment_count")   // ← govori Gson-u koji JSON ključ da mapira
private int commentCount;
```

### Post.java

```java
public class Post {
    @SerializedName("id")            private int    id;
    @SerializedName("userId")        private int    userId;
    @SerializedName("title")         private String title;
    @SerializedName("body")          private String body;
    @SerializedName("link")          private String link;
    @SerializedName("comment_count") private int    commentCount;

    // getters...
}
```

**JSON koji dolazi sa API-ja:**
```json
{
  "userId": 1,
  "id": 1,
  "title": "Introduction to Artificial Intelligence",
  "body": "Learn the basics...",
  "link": "https://example.com/article1",
  "comment_count": 8
}
```

### Comment.java

```java
public class Comment {
    @SerializedName("id")     private int    id;
    @SerializedName("postId") private int    postId;
    @SerializedName("name")   private String name;
    @SerializedName("email")  private String email;
    @SerializedName("body")   private String body;

    // getters...
}
```

### ApiUser.java

```java
public class ApiUser {
    @SerializedName("id")       private int    id;
    @SerializedName("name")     private String name;
    @SerializedName("company")  private String company;
    @SerializedName("username") private String username;
    @SerializedName("email")    private String email;
    @SerializedName("phone")    private String phone;
    // ...ostala polja
}
```

---

## 5. ApiService interfejs

ApiService je **interfejs** (ne klasa!) u kome definišeš sve endpointe.

```java
public interface ApiService {

    // GET /posts  → vraća JSON niz [...] → List<Post>
    @GET("posts")
    Call<List<Post>> getSvePosts();

    // GET /posts/1  → vraća JSON objekat {...} → Post
    // @Path("id") zamenjuje {id} u URL-u sa vrednosti parametra
    @GET("posts/{id}")
    Call<Post> getPostById(@Path("id") int id);

    // GET /comments
    @GET("comments")
    Call<List<Comment>> getSveKomentare();

    // GET /comments/2
    @GET("comments/{id}")
    Call<Comment> getKomentarById(@Path("id") int id);

    // GET /users
    @GET("users")
    Call<List<ApiUser>> getSveKorisnike();
}
```

### Pravila za tip povratne vrednosti

| API vraća | Java tip |
|-----------|----------|
| JSON objekat `{...}` | `Call<Post>`, `Call<Comment>`... |
| JSON niz `[...]` | `Call<List<Post>>`, `Call<List<Comment>>`... |

### Ostale HTTP anotacije

```java
@GET("putanja")                          // HTTP GET
@POST("putanja")                         // HTTP POST
@PUT("putanja/{id}")                     // HTTP PUT
@DELETE("putanja/{id}")                  // HTTP DELETE

@Path("id") int id                       // zamenjuje {id} u URL-u
@Query("search") String term             // dodaje ?search=term u URL
@Body PostRequest body                   // šalje objekat kao JSON telo
```

---

## 6. RetrofitClient — Singleton

```java
public class RetrofitClient {

    // BASE_URL mora se završiti sa "/"!
    private static final String BASE_URL = "https://dummy-json.mock.beeceptor.com/";

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        // Logger za Logcat (opciono, ali korisno)
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();

        // Kreiranje Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // JSON → Java
                .build();

        // Retrofit generiše implementaciju ApiService interfejsa
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }

    public ApiService getApiService() { return apiService; }
}
```

---

## 7. Slanje zahteva — enqueue i Callback

### Asinhrono (enqueue) — UVEK koristiti ovo

```java
// 1. Uzmi ApiService
ApiService apiService = RetrofitClient.getInstance().getApiService();

// 2. Napravi Call objekat (zahtev JOŠ NIJE poslat)
Call<Post> call = apiService.getPostById(1);

// 3. Pošalji ASINHRONO — ne blokira UI
call.enqueue(new Callback<Post>() {

    // Poziva se kada server odgovori (čak i 404, 500...)
    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        if (response.isSuccessful() && response.body() != null) {
            Post post = response.body();         // Gson je već parsirao JSON
            postTextView.setText(post.getTitle()); // ažuriranje UI-ja je OK ovde
        } else {
            // Server je odgovorio ali sa greškom (4xx, 5xx)
            Log.e(TAG, "HTTP greška: " + response.code());
        }
    }

    // Poziva se SAMO pri mrežnoj grešci (nema interneta, timeout...)
    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        Log.e(TAG, "Greška: " + t.getMessage());
        Toast.makeText(ctx, "Nema veze sa serverom", Toast.LENGTH_SHORT).show();
    }
});
```

### Za listu korisnika (List)

```java
Call<List<ApiUser>> call = apiService.getSveKorisnike();

call.enqueue(new Callback<List<ApiUser>>() {
    @Override
    public void onResponse(Call<List<ApiUser>> call, Response<List<ApiUser>> response) {
        if (response.isSuccessful() && response.body() != null) {
            List<ApiUser> lista = response.body();
            int broj = lista.size();
            Toast.makeText(ctx, "Ukupno korisnika: " + broj, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<List<ApiUser>> call, Throwable t) { }
});
```

### Razlika: onResponse vs onFailure

| Situacija | Callback |
|-----------|----------|
| Server vratio 200 OK | `onResponse` — `response.isSuccessful()` = true |
| Server vratio 404 / 500 | `onResponse` — `response.isSuccessful()` = false |
| Nema interneta | `onFailure` |
| Timeout | `onFailure` |
| DNS greška | `onFailure` |

---

## 8. Kompletni primeri

### GET jedan objekat

```java
Call<Post> call = apiService.getPostById(1);
call.enqueue(new Callback<Post>() {
    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        if (response.isSuccessful() && response.body() != null) {
            Post post = response.body();
            textView.setText(post.getTitle() + "\n" + post.getBody());
        }
    }
    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        textView.setText("Greška: " + t.getMessage());
    }
});
```

### GET lista → broj u Toast-u

```java
Call<List<ApiUser>> call = apiService.getSveKorisnike();
call.enqueue(new Callback<List<ApiUser>>() {
    @Override
    public void onResponse(Call<List<ApiUser>> call, Response<List<ApiUser>> response) {
        if (response.isSuccessful() && response.body() != null) {
            Toast.makeText(this, "Korisnika: " + response.body().size(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFailure(Call<List<ApiUser>> call, Throwable t) { }
});
```

---

## 9. Šablon za kolokvijum

### Minimalni koraci za dodavanje Retrofit-a

**1. build.gradle:**
```groovy
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```

**2. Model klasa** (polja = JSON ključevi, `@SerializedName` ako se razlikuju):
```java
public class MojModel {
    @SerializedName("json_kljuc") private String mojAtribut;
    public String getMojAtribut() { return mojAtribut; }
}
```

**3. ApiService interfejs:**
```java
public interface ApiService {
    @GET("endpoint")
    Call<List<MojModel>> getSve();

    @GET("endpoint/{id}")
    Call<MojModel> getJedan(@Path("id") int id);
}
```

**4. RetrofitClient** (kopiraj, promeni samo BASE_URL):
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://TVOJ-SERVER.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
ApiService apiService = retrofit.create(ApiService.class);
```

**5. Slanje zahteva** (uvek isti obrazac):
```java
apiService.getJedan(1).enqueue(new Callback<MojModel>() {
    @Override
    public void onResponse(Call<MojModel> call, Response<MojModel> response) {
        if (response.isSuccessful() && response.body() != null) {
            MojModel obj = response.body();
            textView.setText(obj.getMojAtribut());
        }
    }
    @Override
    public void onFailure(Call<MojModel> call, Throwable t) {
        Toast.makeText(ctx, "Greška!", Toast.LENGTH_SHORT).show();
    }
});
```

---

## API endpointi (beeceptor mock server)

| Metoda | Endpoint | Vraća |
|--------|----------|-------|
| GET | `/posts` | Lista svih postova |
| GET | `/posts/{id}` | Jedan post |
| GET | `/comments` | Lista svih komentara |
| GET | `/comments/{id}` | Jedan komentar |
| GET | `/users` | Lista svih korisnika |
| GET | `/companies` | Lista kompanija |

**Base URL:** `https://dummy-json.mock.beeceptor.com/`

---

## Kreirani fajlovi u Vezbi 7

| Fajl | Opis |
|------|------|
| `model/Post.java` | Model klasa za post |
| `model/Comment.java` | Model klasa za komentar |
| `model/ApiUser.java` | Model klasa za korisnika sa API-ja |
| `network/ApiService.java` | Retrofit interfejs sa svim endpointima |
| `network/RetrofitClient.java` | Singleton konfiguracija Retrofit-a |
| `RetrofitActivity.java` | Ekran koji šalje GET zahteve i prikazuje rezultate |
| `activity_retrofit_screen.xml` | Layout sa 2 TextView-a i 3 dugmeta |
