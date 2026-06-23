# PRIprema Kolokvijum 2 — kompletno rešenje (svaka klasa objašnjena)

> **Za učenje:** ovaj fajl = ceo urađen projekat + **zašto** postoji svaki deo.  
> **Za brzi pregled:** `VODIC_KOLOKVIJUM_JEDNOSTAVNO.md`  
> **Paket:** `com.example.kolokvijum2`

---

## Pregled — koji fajl služi čemu

| Fajl | Zbog kojih zadataka | U jednoj rečenici |
|------|---------------------|-------------------|
| `activity_main.xml` | 2 | 5 UI elementa na ekranu (raspored: vidi `SABLON_Layout_XML.md`) |
| `build.gradle (Module :app)` | 3, 5 | GPS + Retrofit biblioteke |
| `AndroidManifest.xml` | 3, 4, 5, 7, 9 | Dozvole + FileProvider + MainActivity |
| `file_paths.xml` | 4 | Kamera — gde se čuva slika |
| `Post.java` | 5, 6 | Oblik podataka sa API-ja (JSON) |
| `DatabaseHelper.java` | 5, 6, 7 | SQLite — čuvanje/brisanje postova |
| `ApiService.java` | 5, 6 | Koji URL zove Retrofit |
| `RetrofitClient.java` | 5, 6 | Adresa servera (BASE_URL) |
| `MainActivity.java` | 2–9 | **SVE** — GPS, kamera, senzori, Switch, dugme |

---

## Mapa: deo koda → zadatak

| Zadatak | Šta traži | Gde u kodu |
|---------|-----------|------------|
| **1** | Novi projekat | Android Studio |
| **2** | Layout + findViewById | `activity_main.xml`, MainActivity fields |
| **3** | GPS lat/lng u TextView | Gradle location, Manifest GPS, `dohvatiLokaciju()` |
| **4** | Kamera → ImageView + Toast žiroskop | Manifest CAMERA, FileProvider, `kameraLauncher`, `TYPE_GYROSCOPE` |
| **5** | Model + Retrofit + SQLite | Post, DatabaseHelper, network, Gradle retrofit |
| **6** | Switch ON | `postSwitch` listener + `vecFetchovano` + Retrofit + baza |
| **7** | Dugme obriši prvi | `obrisiButton` + `obrisiPrviPost()` + notifikacija |
| **8** | Tekst dugmeta = akcelerometar | `TYPE_ACCELEROMETER` u `onSensorChanged` |
| **9** | Switch OFF | SharedPreferences `"tekst"` + prvi kontakt |

---

## 1. `res/layout/activity_main.xml` — ZADATAK 2

**Zašto:** zadatak traži TextView, ImageButton, ImageView, Switch, Button — jedno ispod drugog (ili drugi raspored iz zadatka).

> Ako zadatak **ne** kaže „jedno ispod drugog“ → vidi `SABLON_Layout_XML.md` → sekcija **„Ako NE piše jedno ispod drugog“**. Menja se samo XML; ID-evi i Java ostaju isti.

| Element | ID | Koristi se u zadatku |
|---------|-----|---------------------|
| TextView | `lokacijaTextView` | 3 (GPS), 9 (prefs/kontakt) |
| ImageButton | `kameraImageButton` | 4 (otvara kameru) |
| ImageView | `slikaImageView` | 4 (prikaz slike) |
| Switch | `postSwitch` | 6 (ON), 9 (OFF) |
| Button | `obrisiButton` | 7 (briše), 8 (tekst senzora) |

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- ZADATAK 3, 9 — prikaz lokacije / kontakta -->
    <TextView
        android:id="@+id/lokacijaTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Lokacija"
        android:textSize="16sp"
        android:layout_marginBottom="12dp"/>

    <!-- ZADATAK 4 — dugme za kameru -->
    <ImageButton
        android:id="@+id/kameraImageButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@android:drawable/ic_menu_camera"
        android:contentDescription="Kamera"
        android:layout_marginBottom="12dp"/>

    <!-- ZADATAK 4 — prikaz fotografije -->
    <ImageView
        android:id="@+id/slikaImageView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:background="#EEEEEE"
        android:layout_marginBottom="12dp"/>

    <!-- ZADATAK 6, 9 — Switch logika -->
    <Switch
        android:id="@+id/postSwitch"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Ucitaj postove"
        android:layout_marginBottom="12dp"/>

    <!-- ZADATAK 7, 8 — brisanje + akcelerometar na tekstu -->
    <Button
        android:id="@+id/obrisiButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Obrisi prvi post"
        android:layout_marginBottom="12dp"/>

</LinearLayout>
```

---

## 2. `build.gradle (Module :app)` — ZADACI 3 i 5

**Zašto:** GPS biblioteka (zadatak 3) + Retrofit (zadatak 5). Unutar `dependencies { }` → **Sync Now**.

```groovy
dependencies {
    // ... postojeće (appcompat, material...) ...

    // ZADATAK 3 — GPS
    implementation 'com.google.android.gms:play-services-location:21.2.0'

    // ZADATAK 5 — Retrofit / HTTP
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

---

## 3. `AndroidManifest.xml` — ZADACI 3, 4, 5, 7, 9

**Zašto:** svaka funkcija traži dozvolu. **Redosled:** dozvole PRE `<application>`, FileProvider UNUTAR `<application>`.

> **NE dodaj** `<activity android:name=".TODO_Activity"/>` — MainActivity već postoji.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- ZADATAK 5 — Retrofit -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- ZADATAK 3 — GPS -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <!-- ZADATAK 4 — kamera -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" android:required="false" />

    <!-- ZADATAK 9 — kontakti -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <!-- ZADATAK 7 — notifikacija (Android 13+) -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.Kolokvijum2">

        <!-- ZADATAK 1 — glavni ekran (već postoji kad praviš projekat) -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- ZADATAK 4 — FileProvider (kamera na Android 7+) -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>
</manifest>
```

---

## 4. `res/xml/file_paths.xml` — ZADATAK 4 (kamera)

**Zašto:** FileProvider mora znati gde sme da piše sliku. **Ne ide u Manifest** — poseban fajl.

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="moje_slike" path="Pictures/" />
</paths>
```

---

## 5. `Post.java` — ZADATAK 5 (model sa API-ja)

**Zašto:** Retrofit prima JSON sa servera. Gson mapira JSON → ovaj Java objekat.

**Polje `comment_count`** u JSON-u ima `_` → u Javi `commentCount` + `@SerializedName`.

```java
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;

public class Post {

    private int id;
    private int userId;
    private String title;   // ZADATAK 6 — Toast prikazuje title
    private String body;
    private String link;

    @SerializedName("comment_count")
    private int commentCount;

    public Post() { }  // obavezno za Gson (Retrofit)

    // Konstruktor za čitanje iz SQLite (DatabaseHelper)
    public Post(int id, int userId, String title, String body, String link, int commentCount) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.link = link;
        this.commentCount = commentCount;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getLink() { return link; }
    public int getCommentCount() { return commentCount; }
}
```

---

## 6. `DatabaseHelper.java` — ZADACI 5, 6, 7

**Zašto:** zadatak traži SQLite. Postovi sa interneta se **čuvaju ovde** (zadatak 6), **brišu** dugmetom (zadatak 7).

| Metoda | Zadatak | Radi |
|--------|---------|------|
| `dodajPost(Post)` | 6 | Upiši post iz API-ja |
| `getPrviPost()` | 6 | Prvi red u tabeli (NE `WHERE id=1`!) |
| `obrisiPrviPost()` | 7 | Obriši prvi; `false` = prazna baza |

```java
package com.example.kolokvijum2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kolokvijum2.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "postovi";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "title TEXT, " +
                "body TEXT, " +
                "link TEXT, " +
                "comment_count INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /** ZADATAK 6 — upiši post iz Retrofit odgovora */
    public long dodajPost(Post post) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("userId", post.getUserId());
        v.put("title", post.getTitle());
        v.put("body", post.getBody());
        v.put("link", post.getLink());
        v.put("comment_count", post.getCommentCount());
        return db.insert(TABLE, null, v);
    }

    /** ZADATAK 6 — prvi post u bazi (najmanji lokalni id) */
    public Post getPrviPost() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, null, null, null, null, "id ASC", "1");
        Post post = null;
        if (c.moveToFirst()) {
            post = new Post(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getInt(c.getColumnIndexOrThrow("userId")),
                    c.getString(c.getColumnIndexOrThrow("title")),
                    c.getString(c.getColumnIndexOrThrow("body")),
                    c.getString(c.getColumnIndexOrThrow("link")),
                    c.getInt(c.getColumnIndexOrThrow("comment_count"))
            );
        }
        c.close();
        return post;
    }

    /** ZADATAK 7 — obriši prvi; false = nema više postova */
    public boolean obrisiPrviPost() {
        Post prvi = getPrviPost();
        if (prvi == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, "id=?", new String[]{String.valueOf(prvi.getId())});
        return true;
    }
}
```

---

## 7. `network/ApiService.java` — ZADATAK 5, 6

**Zašto:** definiše **koji HTTP GET** šalješ. **Interface**, ne Class.

**Endpoint iz zadatka:** `https://...dummy-json/` + `posts`

```java
package com.example.kolokvijum2.network;

import com.example.kolokvijum2.Post;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // ZADATAK 6 — dohvati sve postove (lista)
    @GET("posts")
    Call<List<Post>> getSviPostovi();
}
```

---

## 8. `network/RetrofitClient.java` — ZADATAK 5, 6

**Zašto:** povezuje se na server. Menjaš **samo BASE_URL** (mora `/` na kraju).

```java
package com.example.kolokvijum2.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ZADATAK 5 — adresa iz kolokvijuma
    private static final String BASE_URL =
            "https://app.beeceptor.com/mock-server/dummy-json/";

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

---

## 9. `MainActivity.java` — ZADACI 2–9 (sve u jednoj klasi)

### Struktura fajla

```
MainActivity
├── FIELDS po zadatku (GPS, kamera, senzori, Retrofit, Switch...)
├── kameraLauncher (ZADATAK 4) — VAN onCreate
├── onCreate() — init svega + listeneri
├── GPS metode (ZADATAK 3)
├── Kamera metoda (ZADATAK 4)
├── Switch + kontakt + notifikacija (ZADACI 6, 7, 9)
├── onResume / onPause / onSensorChanged (ZADACI 4, 8)
└── onRequestPermissionsResult (ZADACI 3, 9)
```

### Kompletan kod

```java
package com.example.kolokvijum2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.kolokvijum2.network.ApiService;
import com.example.kolokvijum2.network.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_LOCATION = 300;
    private static final int REQUEST_CONTACTS = 400;

    // ─── ZADATAK 2 — UI ───
    private TextView lokacijaTextView;
    private ImageButton kameraImageButton;
    private ImageView slikaImageView;
    private Switch postSwitch;
    private Button obrisiButton;

    // ─── ZADATAK 3 — GPS ───
    private FusedLocationProviderClient fusedLocationClient;

    // ─── ZADATAK 4 — kamera + žiroskop ───
    private Uri fotografijUri;
    private SensorManager sensorManager;
    private Sensor ziroskop;
    private float[] ziroskopVrednosti = new float[3];

    // ─── ZADATAK 8 — akcelerometar na dugmetu ───
    private Sensor akcelerometar;

    // ─── ZADATAK 5, 6 — Retrofit + SQLite ───
    private ApiService apiService;
    private DatabaseHelper dbHelper;
    private boolean vecFetchovano = false;  // ZADATAK 6 — prvi put vs drugi put

    // ─── ZADATAK 4 — launcher VAN onCreate ───
    private final ActivityResultLauncher<Intent> kameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            slikaImageView.setImageURI(null);
                            slikaImageView.setImageURI(fotografijUri);
                            // ZADATAK 4 — Toast sa žiroskopom posle slike
                            Toast.makeText(this,
                                    "X: " + ziroskopVrednosti[0] +
                                            " Y: " + ziroskopVrednosti[1] +
                                            " Z: " + ziroskopVrednosti[2],
                                    Toast.LENGTH_LONG).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ZADATAK 2 — findViewById
        lokacijaTextView  = findViewById(R.id.lokacijaTextView);
        kameraImageButton = findViewById(R.id.kameraImageButton);
        slikaImageView    = findViewById(R.id.slikaImageView);
        postSwitch        = findViewById(R.id.postSwitch);
        obrisiButton      = findViewById(R.id.obrisiButton);

        // ZADATAK 3 — GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dohvatiLokaciju();

        // ZADATAK 4 — kamera
        kameraImageButton.setOnClickListener(v -> otvoriKameru());

        // ZADATAK 5 — Retrofit + baza
        apiService = RetrofitClient.getInstance().getApiService();
        dbHelper = DatabaseHelper.getInstance(this);

        // ZADATAK 4 + 8 — senzori
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ziroskop = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        akcelerometar = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // ZADATAK 6 + 9 — Switch
        postSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                // ZADATAK 6 — Switch ON
                if (!vecFetchovano) {
                    apiService.getSviPostovi().enqueue(new Callback<List<Post>>() {
                        @Override
                        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Post> svi = response.body();
                                for (int i = 0; i < Math.min(10, svi.size()); i++) {
                                    dbHelper.dodajPost(svi.get(i));
                                }
                                vecFetchovano = true;
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Post>> call, Throwable t) { }
                    });
                } else {
                    Post prvi = dbHelper.getPrviPost();
                    if (prvi != null) {
                        Toast.makeText(MainActivity.this, prvi.getTitle(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                // ZADATAK 9 — Switch OFF
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("tekst", lokacijaTextView.getText().toString())
                        .apply();
                lokacijaTextView.setText(getPrviKontakt());
            }
        });

        // ZADATAK 7 — obriši prvi post
        obrisiButton.setOnClickListener(v -> {
            if (!dbHelper.obrisiPrviPost()) {
                prikaziNotifikaciju("Nema više postova!");
            }
        });
    }

    // ═══════════════════════════════════════
    // ZADATAK 3 — GPS
    // ═══════════════════════════════════════
    private void dohvatiLokaciju() {
        if (!imaDozvoluLokacije()) {
            zatraziDozvoluLokacije();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, lokacija -> {
                    if (lokacija != null) {
                        lokacijaTextView.setText("Lat: " + lokacija.getLatitude()
                                + "\nLng: " + lokacija.getLongitude());
                    } else {
                        Toast.makeText(this, "Uključi GPS!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean imaDozvoluLokacije() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void zatraziDozvoluLokacije() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQUEST_LOCATION);
    }

    // ═══════════════════════════════════════
    // ZADATAK 4 — kamera
    // ═══════════════════════════════════════
    private void otvoriKameru() {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File fajl = File.createTempFile("FOTO_" + ts, ".jpg", dir);

            fotografijUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", fajl);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotografijUri);
            kameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Greška!", Toast.LENGTH_SHORT).show();
        }
    }

    // ═══════════════════════════════════════
    // ZADATAK 7 — notifikacija kad nema postova
    // ═══════════════════════════════════════
    private void prikaziNotifikaciju(String poruka) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String kanalId = "postovi";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(
                    kanalId, "Postovi", NotificationManager.IMPORTANCE_DEFAULT));
        }
        Notification n = new androidx.core.app.NotificationCompat.Builder(this, kanalId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(poruka)
                .build();
        nm.notify(1, n);
    }

    // ═══════════════════════════════════════
    // ZADATAK 9 — prvi kontakt
    // ═══════════════════════════════════════
    private String getPrviKontakt() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
            return "Nema dozvole za kontakte";
        }
        android.database.Cursor c = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String ime = c.getString(c.getColumnIndexOrThrow(
                    ContactsContract.Contacts.DISPLAY_NAME));
            c.close();
            return ime;
        }
        if (c != null) c.close();
        return "Nema kontakata";
    }

    // ═══════════════════════════════════════
    // ZADACI 4, 8 — senzori (žiroskop + akcelerometar)
    // ═══════════════════════════════════════
    @Override
    protected void onResume() {
        super.onResume();
        if (ziroskop != null)
            sensorManager.registerListener(this, ziroskop, SensorManager.SENSOR_DELAY_NORMAL);
        if (akcelerometar != null)
            sensorManager.registerListener(this, akcelerometar, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // ZADATAK 4 — čuvaj žiroskop za Toast posle fotografije
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            ziroskopVrednosti = event.values.clone();
        }
        // ZADATAK 8 — akcelerometar menja tekst dugmeta
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            obrisiButton.setText(String.format(Locale.getDefault(),
                    "X:%.1f Y:%.1f Z:%.1f",
                    event.values[0], event.values[1], event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dohvatiLokaciju();
            } else {
                Toast.makeText(this, "Dozvola za lokaciju odbijena!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
```

---

## Brza tabela — „gde tražim u MainActivity?“

| Tražim... | Traži u MainActivity |
|-----------|----------------------|
| GPS | `dohvatiLokaciju`, `fusedLocationClient`, `lokacijaTextView` |
| Kamera | `kameraLauncher`, `otvoriKameru`, `slikaImageView` |
| Žiroskop Toast | `ziroskopVrednosti`, unutar `kameraLauncher` |
| Retrofit fetch | `apiService.getSviPostovi().enqueue` u Switch listeneru |
| SQLite upis | `dbHelper.dodajPost` |
| Switch prvi/drugi put | `vecFetchovano` |
| Obriši post | `obrisiButton` → `dbHelper.obrisiPrviPost()` |
| Notifikacija | `prikaziNotifikaciju` |
| Akcelerometar | `TYPE_ACCELEROMETER` u `onSensorChanged` → `obrisiButton` |
| SharedPreferences | Switch OFF → `"tekst"` |
| Kontakt | `getPrviKontakt()` |

---

## Kako testirati redom

| Korak | Akcija | Očekivano |
|-------|--------|-----------|
| 1 | Pokreni app | TextView prikaže lat/lng (dozvoli GPS) |
| 2 | Klik na kameru | Slika u ImageView + Toast X,Y,Z |
| 3 | Switch ON (1. put) | Retrofit fetch → 10 postova u bazu |
| 4 | Switch OFF → ON (2. put) | Toast sa `title` prvog posta u bazi |
| 5 | Klik Obrisi | Briše post; kad prazno → notifikacija |
| 6 | Pomeraj telefon | Tekst dugmeta se menja (akcelerometar) |
| 7 | Switch OFF | TextView = ime prvog kontakta |

---

## Povezani fajlovi

| Fajl | Uloga |
|------|-------|
| `VODIC_KOLOKVIJUM_JEDNOSTAVNO.md` | kratak vodič |
| `SABLON_KOLOKVIJUM_2.md` | checklist |
| `SABLON_Retrofit_HTTP_Zahtevi.md` | Retrofit detalji |
| `SABLON_Senzori_Kamera.md` | kamera + senzori |
| `SABLON_Lokacija_GoogleMaps.md` | GPS |
| `SABLON_SQLite_...md` | baza + prefs + kontakti |
