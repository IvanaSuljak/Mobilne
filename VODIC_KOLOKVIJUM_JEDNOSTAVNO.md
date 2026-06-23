# VODIČ ZA KOLOKVIJUM — sve na jednom mestu

> **Otvori OVAJ fajl pred kolokvijum.**  
> Detaljni kod: ostali `SABLON_*.md` fajlovi. Ovde je **šta ide gde** i **šta znači šta**.

---

## 3 pravila — zapamti ovo

1. **Jedna klasa:** skoro uvek samo `MainActivity`. Ne praviš `MapsActivity`, `RetrofitActivity`...
2. **Kod iz šablona** = field-ovi + metode u MainActivity. **NE** kopiraj `public class TODO_Activity { ... }` unutra.
3. **Gde šta ide:**
   - **XML** = šta vidiš na ekranu
   - **Manifest** = dozvole
   - **Gradle (Module :app)** = biblioteke + Sync Now
   - **MainActivity** = sve ostalo (klik, GPS, kamera, API, baza)

---

## Šta je `TODO_...` i `TODO_Activity`?

U šablonima `TODO` = **zameni svojim podatkom**.

| Placeholder | Zameni sa | Primer |
|-------------|-----------|--------|
| `TODO_IME_PAKETA` | tvoj paket | `com.example.kolokvijum2` |
| `TODO_Activity` | ime **nove** Activity (vežbe sa više ekrana) | `MapsActivity` |
| `TODO_textView` | ID iz layouta | `lokacijaTextView` |
| `TODO_BASE_URL` | adresa API-ja | `https://app.beeceptor.com/mock-server/dummy-json/` |
| `TODO_endpoint` | putanja API-ja | `posts` |
| `TODO_TIP_SENZORA` | tip senzora | `Sensor.TYPE_GYROSCOPE` |

### `TODO_Activity` — posebno objašnjenje

| Pitanje | Odgovor |
|---------|---------|
| Šta je? | Placeholder za **ime Java klase** kad praviš **novi ekran** |
| Gde ide? | `public class MapsActivity` + `<activity android:name=".MapsActivity"/>` u Manifest-u |
| Kolokvijum 2? | **NE koristiš.** Sve je u `MainActivity`. Ignoriši `<activity .TODO_Activity>` u Retrofit šablonu. |
| Retrofit KORAK 6 | Treba ti samo `INTERNET` dozvola — **ne dodaj** novu Activity |

---

## Mapa fajlova — šta MORA biti u kom fajlu

| Fajl | Gde u Android Studiju | Šta mora da sadrži (Kolokvijum 2) |
|------|------------------------|-----------------------------------|
| `activity_main.xml` | `res/layout/` | TextView, ImageButton, ImageView, Switch, Button (vertikalno po defaultu; vidi `SABLON_Layout_XML.md` ako nije „ispod“) |
| `build.gradle (Module :app)` | Gradle Scripts | `location` + `retrofit` + `gson` + `logging` → **Sync** |
| `AndroidManifest.xml` | `app/src/main/` | Dozvole PRE `<application>`; FileProvider UNUTAR `<application>` |
| `file_paths.xml` | `res/xml/` | `<paths>` za kameru |
| `MainActivity.java` | glavni paket | **SVE** — vidi tabelu ispod |
| `Post.java` | glavni paket | 6 polja + `@SerializedName("comment_count")` + prazan konstruktor |
| `DatabaseHelper.java` | glavni paket | tabela `postovi`, `dodajPost`, `getPrviPost`, `obrisiPrviPost` |
| `ApiService.java` | paket `network` | **Interface** — `@GET("posts") Call<List<Post>>` |
| `RetrofitClient.java` | paket `network` | `BASE_URL` sa `/` na kraju + singleton |

---

## Kolokvijum 2 — zadatak po zadatku (gde pišeš)

| # | Zadatak | Fajl(ovi) | Šta tačno |
|---|---------|-----------|-----------|
| 1 | Novi projekat | Android Studio | Empty Views Activity, `com.example.kolokvijum2` |
| 2 | Layout | `activity_main.xml` + MainActivity | 5 elemenata; ID: `lokacijaTextView`, `kameraImageButton`, `slikaImageView`, `postSwitch`, `obrisiButton` |
| 3 | GPS | Gradle + Manifest + MainActivity | lat/lng u `lokacijaTextView` |
| 4 | Kamera + žiroskop | Manifest + file_paths + MainActivity | slika u ImageView; Toast X,Y,Z **posle** fotografije |
| 5 | Model + Retrofit + baza | Post, DatabaseHelper, network, Gradle, Manifest | init `apiService` + `dbHelper` u onCreate |
| 6 | Switch ON | MainActivity | 1. put: fetch 10 postova → baza; 2. put: Toast sa `title` prvog u bazi |
| 7 | Dugme obriši | MainActivity + DatabaseHelper | obriši prvi post; prazno → **notifikacija** "Nema više postova!" |
| 8 | Akcelerometar | MainActivity | tekst na **dugmetu** `obrisiButton` (NE žiroskop!) |
| 9 | Switch OFF | MainActivity + Manifest | sačuvaj TextView u prefs `"tekst"`; prikaži prvi kontakt |

---

## MainActivity — checklist (sve u jednoj klasi)

```java
public class MainActivity extends AppCompatActivity implements SensorEventListener {
```

### Fields (vrh klase)

| Field | Zadatak |
|-------|---------|
| `lokacijaTextView`, `fusedLocationClient`, `REQUEST_LOCATION` | 3 |
| `kameraImageButton`, `slikaImageView`, `fotografijUri`, `kameraLauncher` | 4 |
| `sensorManager`, `ziroskop`, `ziroskopVrednosti[]` | 4 (Toast) |
| `sensorManager`, `akcelerometar` | 8 (dugme) |
| `postSwitch`, `obrisiButton` | 6, 7, 8 |
| `apiService`, `dbHelper`, `vecFetchovano` | 5, 6 |

### onCreate — redosled

1. `setContentView` + svi `findViewById`
2. GPS init + `dohvatiLokaciju()`
3. Kamera click listener
4. `apiService = RetrofitClient.getInstance().getApiService()`
5. `dbHelper = DatabaseHelper.getInstance(this)`
6. Senzori init (žiroskop + akcelerometar)
7. `postSwitch.setOnCheckedChangeListener(...)`
8. `obrisiButton.setOnClickListener(...)`

### Metode koje moraš imati

| Metoda | Zadatak |
|--------|---------|
| `dohvatiLokaciju`, `imaDozvolu`, `zatraziDozvolu` | 3 |
| `onRequestPermissionsResult` | 3 (+ kontakti za 9) |
| `otvoriKameru` | 4 |
| `onResume`, `onPause` | 4, 8 |
| `onSensorChanged` | žiroskop → čuva vrednosti; akcelerometar → tekst dugmeta |
| `getPrviKontakt` (ili iz SQL šablona K6) | 9 |

### Senzori — NE mešaj!

| Senzor | Za šta | Gde |
|--------|--------|-----|
| **Žiroskop** | Toast X,Y,Z posle slike | `kameraLauncher` callback |
| **Akcelerometar** | Tekst na dugmetu u realnom vremenu | `onSensorChanged` → `obrisiButton.setText(...)` |

---

## Gradle — šta dodati (Module :app)

Unutar `dependencies { }` → **Sync Now**:

```groovy
implementation 'com.google.android.gms:play-services-location:21.2.0'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
```

---

## Manifest — dozvole (PRE `<application>`)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

**UNUTAR** `<application>` (posle `<activity MainActivity>`):

```xml
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

---

## Post.java — mora imati

```java
private int id, userId, commentCount;
private String title, body, link;
@SerializedName("comment_count")  // samo ovo polje
public Post() { }  // obavezno za Gson
// getteri za sva polja
```

---

## ApiService.java — mora imati (Interface!)

```java
package com.example.kolokvijum2.network;

public interface ApiService {
    @GET("posts")
    Call<List<Post>> getSviPostovi();
}
```

---

## RetrofitClient.java — mora imati

```java
private static final String BASE_URL =
    "https://app.beeceptor.com/mock-server/dummy-json/";  // / na kraju!
// singleton getInstance() + getApiService()
```

---

## DatabaseHelper — mora imati metode

| Metoda | Radi |
|--------|------|
| `dodajPost(Post p)` | upiši jedan post |
| `getPrviPost()` | prvi red u tabeli (NE `WHERE id=1`) |
| `obrisiPrviPost()` | obriši prvi; vrati `false` ako nema ništa |

---

## Kod koji fali u MainActivity (kopiraj kad dođeš do toga)

### Zadatak 6 — Switch ON

```java
private boolean vecFetchovano = false;

postSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
    if (isChecked) {
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
                Toast.makeText(this, prvi.getTitle(), Toast.LENGTH_LONG).show();
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
```

Importi za gornje:
```java
import com.example.kolokvijum2.Post;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
```

### Zadatak 7 — dugme obriši + notifikacija

```java
obrisiButton.setOnClickListener(v -> {
    if (!dbHelper.obrisiPrviPost()) {
        prikaziNotifikaciju("Nema više postova!");
    }
});

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
```

Importi:
```java
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
```

### Zadatak 8 — akcelerometar na dugmetu

```java
// field:
private Sensor akcelerometar;

// onCreate:
akcelerometar = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

// onResume — oba senzora:
if (ziroskop != null)
    sensorManager.registerListener(this, ziroskop, SensorManager.SENSOR_DELAY_NORMAL);
if (akcelerometar != null)
    sensorManager.registerListener(this, akcelerometar, SensorManager.SENSOR_DELAY_NORMAL);

// onSensorChanged:
if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
    ziroskopVrednosti = event.values.clone();
}
if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    obrisiButton.setText(String.format(Locale.getDefault(),
            "X:%.1f Y:%.1f Z:%.1f",
            event.values[0], event.values[1], event.values[2]));
}
```

### Zadatak 9 — prvi kontakt (metoda u MainActivity)

```java
private String getPrviKontakt() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS}, 400);
        return "Nema dozvole";
    }
    android.database.Cursor c = getContentResolver().query(
            android.provider.ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null);
    if (c != null && c.moveToFirst()) {
        String ime = c.getString(c.getColumnIndexOrThrow(
                android.provider.ContactsContract.Contacts.DISPLAY_NAME));
        c.close();
        return ime;
    }
    if (c != null) c.close();
    return "Nema kontakata";
}
```

---

## Retrofit u 4 rečenice

1. **Post.java** = oblik podataka sa servera.  
2. **ApiService** = koji URL zoveš (`@GET("posts")`).  
3. **RetrofitClient** = adresa servera (`BASE_URL`).  
4. **MainActivity** = `apiService.getSviPostovi().enqueue(...)` → u `onResponse` radiš Toast / bazu.

Ne moraš razumeti Retrofit duboko — kopiraš obrazac.

---

## UNIVERZALNO vs KONKRETNO (kratko)

| Kopiraj bukvalno | Menjaj po zadatku |
|------------------|-------------------|
| Gradle retrofit linije | ID-evi u layoutu |
| Manifest INTERNET | BASE_URL |
| RetrofitClient singleton | polja u Post.java |
| enqueue + Callback obrazac | šta radiš u onResponse |
| GPS dozvole obrazac | koji senzor (gyro vs accel) |
| FileProvider + file_paths | — |

Detaljno: `SABLON_UNIVERZALNO_VS_KONKRETNO.md`

---

## Česte greške

| ❌ | ✅ |
|----|-----|
| Ceo KORAK 3 Senzori odjednom | Delovi 3a–3e, dodaj u postojeći MainActivity |
| Dva `onCreate()` | Jedan |
| `public class TODO_Activity` unutar MainActivity | Metode direktno u MainActivity |
| Gradle u root fajlu | `build.gradle (Module :app)` |
| Žiroskop na dugmetu | Akcelerometar na dugmetu |
| `getPostById(1)` za prvi u bazi | `getPrviPost()` iz SQLite |
| Nova Activity za Retrofit | Sve u MainActivity |
| Zaboravljen Sync Now | Posle Gradle izmene |

---

## Redosled rada (ne skači)

```
1. Projekt + layout
2. Gradle + Sync
3. Manifest + file_paths.xml
4. MainActivity: GPS → kamera → senzori
5. Post + DatabaseHelper + ApiService + RetrofitClient
6. Switch (6 + 9)
7. Dugme obriši (7)
8. Akcelerometar (8)
9. Run + testiraj
```

---

## Ostali fajlovi (kad treba detaljan kod)

| Fajl | Za šta |
|------|--------|
| `SABLON_MASTER_VODIC.md` | opšti redosled bilo kog zadatka |
| `SABLON_KOLOKVIJUM_2.md` | pun checklist kolokvijuma |
| `SABLON_Retrofit_HTTP_Zahtevi.md` | Retrofit detalji |
| `SABLON_Senzori_Kamera.md` | kamera + senzori |
| `SABLON_Lokacija_GoogleMaps.md` | GPS |
| `SABLON_SQLite_...md` | baza + prefs + kontakti |
| `SABLON_UNIVERZALNO_VS_KONKRETNO.md` | šta kopiraš vs menjaš |

---

## Tvoj trenutni status (Kolokvijum 2)

| Zadatak | Status |
|---------|--------|
| 1–2 Layout | ✅ |
| 3 GPS | ✅ |
| 4 Kamera + žiroskop | ✅ |
| 5 Post, DatabaseHelper, Retrofit fajlovi | proveri da postoje |
| 6 Switch ON | ❌ dodaj kod iznad |
| 7 Obriši + notifikacija | ❌ |
| 8 Akcelerometar | ❌ |
| 9 Switch OFF | ❌ (deo je u Switch listeneru) |

---

**Srećno na kolokvijumu.** Otvori ovaj fajl, prati redosled, jedan korak po korak. Ne moraš sve odjednom.
