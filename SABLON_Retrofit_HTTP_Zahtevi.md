# ŠABLON — Retrofit (HTTP zahtevi)

> Svuda gde vidiš `TODO` → zameni sa svojim podacima.
> Sve ostalo kopiraš bukvalno.

---

## TODO lista — šta zameniti

- [ ] `TODO_IME_PAKETA` → tvoj paket (npr. `com.example.mojaplikacija`)
- [ ] `TODO_BASE_URL` → adresa servera (npr. `https://dummy-json.mock.beeceptor.com/`)
- [ ] `TODO_Model` → naziv modela (npr. `Post`, `Comment`, `User`)
- [ ] `TODO_endpoint` → putanja bez base URL-a (npr. `posts`, `comments/2`)
- [ ] `TODO_polje` → naziv JSON ključa / Java polja (npr. `title`, `body`)
- [ ] `TODO_textView` → ID TextView-a u layoutu

---

## KORAK 1 — build.gradle (dodati zavisnosti)

```groovy
dependencies {
    // ... postojeće zavisnosti ...

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

---

## KORAK 2 — Model klasa (`model/TODO_Model.java`)

```java
package com.example.TODO_IME_PAKETA.model;

import com.google.gson.annotations.SerializedName;

public class TODO_Model {

    // @SerializedName = koji JSON ključ mapirati na ovo polje
    // Ako je ime polja ISTO kao JSON ključ, anotacija nije obavezna
    @SerializedName("id")
    private int id;

    @SerializedName("TODO_polje1")       // npr. "title", "name", "body"
    private String TODO_atribut1;

    @SerializedName("TODO_polje2")       // npr. "body", "email"
    private String TODO_atribut2;

    // Ako JSON ključ ima underscore (npr. "comment_count"), a Java ne sme:
    @SerializedName("TODO_json_kljuc_sa_underscoreom")
    private int TODO_javaAtribut;

    // Getters (Alt+Insert → Generate → Getter)
    public int    getId()              { return id; }
    public String getTODO_atribut1()   { return TODO_atribut1; }
    public String getTODO_atribut2()   { return TODO_atribut2; }
    public int    getTODO_javaAtribut(){ return TODO_javaAtribut; }
}
```

> Za svaki tip podatka u JSON-u napravi jednu model klasu.
> Npr. ako imaš Post, Comment i User → tri klase.

---

## KORAK 3 — ApiService interfejs (`network/ApiService.java`)

```java
package com.example.TODO_IME_PAKETA.network;

import com.example.TODO_IME_PAKETA.model.TODO_Model;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // GET lista → API vraća JSON niz [...]
    @GET("TODO_endpoint")
    Call<List<TODO_Model>> getSve();

    // GET jedan po ID → API vraća JSON objekat {...}
    @GET("TODO_endpoint/{id}")
    Call<TODO_Model> getJedan(@Path("id") int id);

    // Primeri sa pravim imenima:
    // @GET("posts")              Call<List<Post>>    getSvePosts();
    // @GET("posts/{id}")         Call<Post>          getPostById(@Path("id") int id);
    // @GET("comments/{id}")      Call<Comment>       getKomentarById(@Path("id") int id);
    // @GET("users")              Call<List<ApiUser>> getSveKorisnike();
}
```

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

## KORAK 5 — Slanje zahteva (u Activity-ju)

### Inicijalizacija (jednom, u `onCreate`)

```java
ApiService apiService = RetrofitClient.getInstance().getApiService();
```

---

### GET jedan objekat → prikaži u TextView-u

```java
Call<TODO_Model> call = apiService.getJedan(1); // TODO: promeni ID

call.enqueue(new Callback<TODO_Model>() {

    @Override
    public void onResponse(Call<TODO_Model> call, Response<TODO_Model> response) {
        if (response.isSuccessful() && response.body() != null) {
            TODO_Model obj = response.body();
            TODO_textView.setText(obj.getTODO_atribut1() + "\n" + obj.getTODO_atribut2());
        } else {
            TODO_textView.setText("Greška: HTTP " + response.code());
        }
    }

    @Override
    public void onFailure(Call<TODO_Model> call, Throwable t) {
        TODO_textView.setText("Mrežna greška: " + t.getMessage());
    }
});
```

---

### GET lista → broj u Toast poruci

```java
Call<List<TODO_Model>> call = apiService.getSve();

call.enqueue(new Callback<List<TODO_Model>>() {

    @Override
    public void onResponse(Call<List<TODO_Model>> call, Response<List<TODO_Model>> response) {
        if (response.isSuccessful() && response.body() != null) {
            int broj = response.body().size();
            Toast.makeText(TODO_Activity.this,
                    "Ukupno: " + broj, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<List<TODO_Model>> call, Throwable t) {
        Toast.makeText(TODO_Activity.this, "Greška!", Toast.LENGTH_SHORT).show();
    }
});
```

---

### GET lista → prikaži u TextView-u (petlja)

```java
Call<List<TODO_Model>> call = apiService.getSve();

call.enqueue(new Callback<List<TODO_Model>>() {

    @Override
    public void onResponse(Call<List<TODO_Model>> call, Response<List<TODO_Model>> response) {
        if (response.isSuccessful() && response.body() != null) {
            StringBuilder sb = new StringBuilder();
            for (TODO_Model obj : response.body()) {
                sb.append(obj.getTODO_atribut1()).append("\n");
            }
            TODO_textView.setText(sb.toString());
        }
    }

    @Override
    public void onFailure(Call<List<TODO_Model>> call, Throwable t) { }
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
1. build.gradle  →  dodaj 2-3 zavisnosti
2. Model klasa   →  polja = JSON ključevi, @SerializedName ako se razlikuju
3. ApiService    →  interfejs, @GET + tip povratne vrednosti
4. RetrofitClient →  Singleton, promeni BASE_URL, ostalo isto
5. Activity      →  apiService.metoda().enqueue(new Callback(){...})
6. Manifest      →  <uses-permission INTERNET> + nova <activity>
```
