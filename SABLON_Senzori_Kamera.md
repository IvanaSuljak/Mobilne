# ŠABLON — Senzori i Kamera

> **MASTER:** Otvori prvo `SABLON_MASTER_VODIC.md` → nađi "kamera" ili "senzor" u tabeli.

> `TODO` → zameni sa svojim podacima. Ostalo kopiraš bukvalno.

> **KOLOKVIJUM:** Kod ide u **MainActivity** — metode i field-ovi, NE nova klasa unutar nje!
> Klasa mora imati `implements SensorEventListener` ako koristiš senzor.

---

## KADA KORISTITI

| Zadatak kaže | Koristi KORAK |
|--------------|---------------|
| kamera, fotografija, slika, ImageView, ImageButton | KORAK 1, 2, 5 |
| senzor, akcelerometar, žiroskop, TYPE_... | KORAK 3 |
| shake, protresi uređaj | KORAK 4 |
| Toast sa senzorom pri nekoj akciji | KORAK 3 + Toast u callback-u (kamera launcher) |

---

## TAČAN REDOSLED — Kamera

| # | Gde | Šta radiš | Kad |
|---|-----|-----------|-----|
| 1 | `AndroidManifest.xml` | `CAMERA` dozvola **pre** `<application>` | Pre Java koda |
| 2 | `AndroidManifest.xml` | `<provider FileProvider>` **unutar** `<application>` | Posle activity taga |
| 3 | `res/xml/file_paths.xml` | Novi fajl sa `<paths>` (KORAK 2) | Posle Manifest-a |
| 4 | Layout XML | `ImageButton` + `ImageView` sa ID-evima | Pre MainActivity |
| 5 | `MainActivity.java` | Field: `fotografijUri`, `slikaImageView`, `kameraLauncher` | Vrh klase; launcher VAN onCreate |
| 6 | `MainActivity.java` | u `onCreate`: findViewById + click listener | Posle setContentView |
| 7 | `MainActivity.java` | Metoda `otvoriKameru()` (KORAK 5) | Na nivou klase |

## TAČAN REDOSLED — Senzori

| # | Gde | Šta | Kad |
|---|-----|-----|-----|
| 1 | `MainActivity.java` | `implements SensorEventListener` na klasi | Deklaracija klase |
| 2 | `MainActivity.java` | Fields: `sensorManager`, `senzor` (KORAK 3) | Vrh klase |
| 3 | `MainActivity.java` | u `onCreate`: getSystemService + getDefaultSensor | Posle findViewById |
| 4 | `MainActivity.java` | `onResume()` → registerListener | Override metoda |
| 5 | `MainActivity.java` | `onPause()` → unregisterListener | Override metoda |
| 6 | `MainActivity.java` | `onSensorChanged()` → event.values[] | Override metoda |

> Gradle dependency **ne treba** za senzore i kameru (osim androidx koji već postoji).

---

## TODO lista

- [ ] `TODO_IME_PAKETA` → tvoj paket
- [ ] `TODO_TIP_SENZORA` → npr. `Sensor.TYPE_ACCELEROMETER`, `Sensor.TYPE_GYROSCOPE`
- [ ] `TODO_textView` → ID TextView-a za prikaz podataka senzora
- [ ] `TODO_imageView` → ID ImageView-a za prikaz slike

---

## KORAK 1 — AndroidManifest.xml

**Struktura Manifest-a (redosled je bitan!):**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ...>

    <!-- 1. DOZVOLE — ovde, PRE <application> -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" android:required="false" />

    <!-- 2. APPLICATION -->
    <application ...>

        <activity android:name=".MainActivity" ... />

        <!-- 3. FileProvider — UNUTAR <application>, ne posle! -->
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

> **NE STAVLJAJ** `<provider>` ili `<uses-permission>` POSLE `</application>` — neće raditi!

---

## KORAK 2 — res/xml/file_paths.xml (napravi ovaj fajl)

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="moje_slike" path="Pictures/" />
</paths>
```

> **Fajl:** `res/xml/file_paths.xml` — desni klik na `res` → New → Android Resource File → Resource type: **xml**

---

## ⚠️ NAJVAŽNIJE — Kako koristiti KORAK 3 i KORAK 5

**NE kopiraj ceo blok KORAK 3 odjednom** — ima svoj `onCreate()` i zatvara klasu sa `}`!

Umesto toga, iz šablona uzmi **samo delove** i ubaci u **postojeći** MainActivity:

| Iz šablona uzmi | Gde u MainActivity |
|-----------------|-------------------|
| `implements SensorEventListener` | deklaracija klase (jednom) |
| `private SensorManager...` fields | vrh klase, pored ostalih field-ova |
| init senzora (2 linije) | **dodaj u postojeći** `onCreate()`, ne novi |
| `onResume`, `onPause`, `onSensorChanged`, `onAccuracyChanged` | metode na nivou klase |
| `kameraLauncher`, `otvoriKameru()` | iz KORAK 5 — isto, u istu klasu |

```
❌ POGREŠNO: nalepi KORAK 3 pa ispod KORAK 5 → dva onCreate, klasa se zatvori prerano
✅ ISPRAVNO: jedan onCreate, jedna klasa, sve metode jedna pored druge
```

---

## KORAK 3 — Senzori (delovi za kopiranje, NE cela klasa!)

### 3a — Deklaracija klase (izmeni postojeću liniju)

```java
public class MainActivity extends AppCompatActivity implements SensorEventListener {
```

### 3b — Fields (dodaj na vrh klase)

```java
private SensorManager sensorManager;
private Sensor ziroskop;  // ili akcelerometar — zavisi od zadatka
```

**Kolokvijum zadatak 4 (Toast pri slici)** — dodaj i:
```java
private float[] ziroskopVrednosti = new float[3];
```

**Zadatak 8 (Button tekst = akcelerometar)** — koristi:
```java
private Sensor akcelerometar;
```

### 3c — Init (dodaj u POSTOJEĆI onCreate, posle findViewById)

```java
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
ziroskop = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
if (ziroskop == null) {
    Toast.makeText(this, "Senzor nije dostupan!", Toast.LENGTH_LONG).show();
}
```

Zameni tip senzora po zadatku:
- žiroskop → `Sensor.TYPE_GYROSCOPE`
- akcelerometar → `Sensor.TYPE_ACCELEROMETER`

### 3d — Override metode (dodaj u klasu, posle onCreate)

```java
@Override
protected void onResume() {
    super.onResume();
    if (ziroskop != null) {
        sensorManager.registerListener(this, ziroskop, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

@Override
protected void onPause() {
    super.onPause();
    if (sensorManager != null) {
        sensorManager.unregisterListener(this);
    }
}

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) { }
```

### 3e — onSensorChanged — IZABERI varijantu

**Varijanta A — prikaži u TextView** (vežbe):
```java
@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        float x = event.values[0], y = event.values[1], z = event.values[2];
        nekiTextView.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
    }
}
```

**Varijanta B — Kolokvijum zadatak 4** (čuvaj vrednosti, Toast u KORAK 5 launcher):
```java
@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        ziroskopVrednosti = event.values.clone();
    }
}
```

**Varijanta C — Kolokvijum zadatak 8** (akcelerometar na Button):
```java
@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        ziroskopVrednosti = event.values.clone();
    }
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        obrisiButton.setText(String.format("X:%.1f Y:%.1f Z:%.1f",
                event.values[0], event.values[1], event.values[2]));
    }
}
```

> Varijanta C: u `onResume` registruj oba senzora (dva `registerListener`).

**Importi za senzore:**
```java
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
```

---

## KORAK 4 — Shake detekcija (dodaj u isti Activity)

```java
private static final float SHAKE_PRAG   = 12.0f;
private static final long  SHAKE_GAP_MS = 1000;
private long  zadnjiShake = 0;
private float posX = 0, posY = 0, posZ = 0;

// Pozovi ovo unutar onSensorChanged() kada tip == TYPE_ACCELEROMETER
private void detektujShake(float x, float y, float z) {
    long sada = System.currentTimeMillis();

    float deltaX = Math.abs(x - posX);
    float deltaY = Math.abs(y - posY);
    float deltaZ = Math.abs(z - posZ);
    posX = x; posY = y; posZ = z;

    float ukupno = (float) Math.sqrt(x * x + y * y + z * z);

    if (ukupno > SHAKE_PRAG
            && (deltaX + deltaY + deltaZ) / 3 > 0.5f
            && sada - zadnjiShake > SHAKE_GAP_MS) {

        zadnjiShake = sada;
        Toast.makeText(this, "Shake detektovan!", Toast.LENGTH_SHORT).show();
        // TODO: notificationHelper.showShakeNotification();
    }
}
```

---

## KORAK 5 — Kamera (ubaci u MainActivity, NE nova klasa!)

> Kopiraj **field-ove, launcher i metode** u MainActivity.
> NE piši `public class TODO_KameraActivity` unutar MainActivity!

```java
// === U MainActivity — FIELDS ===
private Uri fotografijUri;
private ImageView slikaImageView;  // tvoj ID iz layouta

// === LAUNCHER — FIELD na nivou klase, VAN onCreate! ===
private final ActivityResultLauncher<Intent> kameraLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    slikaImageView.setImageURI(null);
                    slikaImageView.setImageURI(fotografijUri);
                    // Kolokvijum zadatak 4 — Toast sa žiroskopom (Varijanta B iz KORAK 3e):
                    Toast.makeText(this,
                            "X: " + ziroskopVrednosti[0] +
                            " Y: " + ziroskopVrednosti[1] +
                            " Z: " + ziroskopVrednosti[2],
                            Toast.LENGTH_LONG).show();
                }
            });

// === u POSTOJEĆI onCreate() — ne pravi novi! ===
kameraImageButton.setOnClickListener(v -> otvoriKameru());

// === METODA na nivou MainActivity ===
private void otvoriKameru() {
        try {
            String ts  = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File   dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File   fajl = File.createTempFile("FOTO_" + ts, ".jpg", dir);

            fotografijUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider", // mora da se poklapa sa Manifest-om
                fajl
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotografijUri);
            kameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Greška!", Toast.LENGTH_SHORT).show();
        }
}
```

> **Importi koje treba dodati u MainActivity za kameru:**
> `Intent`, `Uri`, `ImageView`, `ImageButton`, `MediaStore`, `File`, `Environment`,
> `FileProvider`, `ActivityResultLauncher`, `ActivityResultContracts`,
> `SimpleDateFormat`, `Date`, `Locale`, `IOException`

---

## ČESTE GREŠKE — ne pravi ugnježdene klase!

| ❌ Greška | ✅ Ispravno |
|----------|------------|
| Nalepiš ceo KORAK 3 blok | Uzmi samo delove 3a–3e |
| Dva `onCreate()` | Jedan — dodaj init senzora u postojeći |
| `}` posle senzora pa GPS ispod | Sve unutar jedne klase |
| `TODO_TIP_SENZORA` ostane u kodu | Zameni sa `Sensor.TYPE_GYROSCOPE` |
| `LokacijaTextView` (pogrešan case) | Java je case-sensitive — `lokacijaTextView` |
| Žiroskop u TextView umesto Toast | Zadatak 4 → Varijanta B + Toast u launcher |

```java
// ❌ POGREŠNO — klasa unutar klase
public class MainActivity extends AppCompatActivity {
    public class TODO_KameraActivity extends AppCompatActivity { ... }
}

// ❌ POGREŠNO — dva onCreate
@Override onCreate() { /* senzor */ }
@Override onCreate() { /* gps */ }

// ✅ ISPRAVNO — sve u jednoj klasi
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // svi fields
    // kameraLauncher
    @Override onCreate() { /* findViewById + gps + senzor + kamera */ }
    private void dohvatiLokaciju() { }
    private void otvoriKameru() { }
    @Override onResume() { }
    @Override onSensorChanged() { }
}
```

---

## Tipovi senzora — brza referenca

| Tip | Šta mjeri | values[] |
|-----|-----------|---------|
| `TYPE_ACCELEROMETER` | Ubrzanje (m/s²) | [X, Y, Z] |
| `TYPE_GYROSCOPE` | Rotacija (rad/s) | [X, Y, Z] |
| `TYPE_MAGNETIC_FIELD` | Magnetno polje (µT) | [X, Y, Z] |
| `TYPE_LIGHT` | Osvetljenost (lux) | [lux] |
| `TYPE_PROXIMITY` | Blizina (cm) | [dist] |

## Redosled koji se NE MENJA

```
Senzori:
1. getSystemService(SENSOR_SERVICE) → SensorManager
2. getDefaultSensor(TIP) → Sensor (provjeri null!)
3. implements SensorEventListener
4. onResume() → registerListener()
5. onPause()  → unregisterListener()   ← OBAVEZNO!
6. onSensorChanged() → event.values[]

Kamera:
1. Manifest → CAMERA dozvola + FileProvider + file_paths.xml
2. ActivityResultLauncher (field, van onCreate)
3. File.createTempFile() → FileProvider.getUriForFile() → Intent + launch()
4. onActivityResult → imageView.setImageURI(uri)
```
