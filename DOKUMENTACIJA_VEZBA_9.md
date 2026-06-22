# VEZBA 9 — Senzori i Multimedija — Kompletna dokumentacija

---

## Zadaci

1. Dobaviti senzor `TYPE_ACCELEROMETER` i registrovati obrađivač događaja
2. Shake detekcija → notifikacija "Shake detektovan!"
3. Drugi senzor po izboru (žiroskop) → prikaz u TextView
4. Kamera: dodavanje, izmena i brisanje slike u ImageView-u

---

## Sadržaj

1. [Senzori — osnove](#1-senzori--osnove)
2. [SensorManager i registracija listenera](#2-sensormanager-i-registracija)
3. [TYPE_ACCELEROMETER — zadatak 1](#3-type_accelerometer)
4. [Shake detekcija — zadatak 2](#4-shake-detekcija)
5. [Drugi senzor (žiroskop) — zadatak 3](#5-drugi-senzor)
6. [Kamera — zadatak 4](#6-kamera)
7. [FileProvider — obavezno za kameru](#7-fileprovider)

---

## 1. Senzori — osnove

Android uređaji imaju različite fizičke senzore:

| Konstanta | Tip | Merne jedinice | values[] |
|-----------|-----|----------------|---------|
| `TYPE_ACCELEROMETER` | Ubrzanje | m/s² | [X, Y, Z] |
| `TYPE_GYROSCOPE` | Rotacija | rad/s | [X, Y, Z] |
| `TYPE_MAGNETIC_FIELD` | Magnetno polje | µT | [X, Y, Z] |
| `TYPE_LIGHT` | Osvetljenost | lux | [lux] |
| `TYPE_PROXIMITY` | Blizina | cm (ili 0/1) | [dist] |
| `TYPE_PRESSURE` | Pritisak (barometar) | hPa | [hPa] |
| `TYPE_TEMPERATURE` | Temperatura | °C | [°C] |

---

## 2. SensorManager i registracija

```java
// 1. Uzmi SensorManager — jedina ulazna tačka za senzore
SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

// 2. Dobavi senzor — može biti null ako uređaj nema taj senzor!
Sensor akcelerometar = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

// 3. Klasa mora implements SensorEventListener
public class MojActivity extends AppCompatActivity implements SensorEventListener {

    // 4. Registruj u onResume() — tada počinje slušanje
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, akcelerometar, SensorManager.SENSOR_DELAY_NORMAL);
        //                 listener     senzor              brzina ažuriranja
    }

    // 5. UVEK odjavi u onPause() — inače troši bateriju!
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); // odjavi SVE senzore
    }

    // 6. Obrada podataka — poziva se pri svakom novom očitavanju
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            textView.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
        }
    }

    // 7. Obavezno override (može biti prazno)
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
```

### Brzine ažuriranja (SENSOR_DELAY_*)

| Konstanta | Aprox. brzina | Kada koristiti |
|-----------|---------------|----------------|
| `SENSOR_DELAY_NORMAL` | ~5/s | Normalan prikaz |
| `SENSOR_DELAY_UI` | ~16/s | Animacije UI-ja |
| `SENSOR_DELAY_GAME` | ~50/s | Igrice |
| `SENSOR_DELAY_FASTEST` | Max | Precizna mjerenja |

---

## 3. TYPE_ACCELEROMETER

Mjeri ubrzanje uređaja na tri ose u m/s²:
- `values[0]` = X osa (levo/desno)
- `values[1]` = Y osa (gore/dole)
- `values[2]` = Z osa (napred/nazad)

**Gravitacija:** Kada uređaj miruje, Z osa = ~9.8 m/s² (gravitacija Zemlje).

```java
@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        textView.setText(String.format(
            "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²", x, y, z));
    }
}
```

---

## 4. Shake detekcija

```java
private static final float SHAKE_PRAG   = 12.0f; // m/s², iznad gravitacije
private static final long  SHAKE_GAP_MS = 1000;   // ms između dva shake događaja

private long  poslednjIShakeVreme = 0;
private float posX = 0, posY = 0, posZ = 0;

private void detektujShake(float x, float y, float z) {
    long sada = System.currentTimeMillis();

    // Promena ubrzanja u odnosu na prethodno
    float deltaX = Math.abs(x - posX);
    float deltaY = Math.abs(y - posY);
    float deltaZ = Math.abs(z - posZ);
    posX = x; posY = y; posZ = z;

    // Vektorska magnitude ukupnog ubrzanja
    float ukupno = (float) Math.sqrt(x * x + y * y + z * z);

    // Shake = visoko ubrzanje + brza promena + dovoljan vremenski razmak
    if (ukupno > SHAKE_PRAG
            && (deltaX + deltaY + deltaZ) / 3 > 0.5f
            && sada - poslednjIShakeVreme > SHAKE_GAP_MS) {

        poslednjIShakeVreme = sada;
        // Pošalji notifikaciju!
        notificationHelper.showShakeNotification();
        Toast.makeText(this, "Shake detektovan!", Toast.LENGTH_SHORT).show();
    }
}
```

---

## 5. Drugi senzor — žiroskop

```java
Sensor ziroskop = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
// Registruj isto kao i akcelerometar

@Override
public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        float x = event.values[0]; // rotacija oko X (pitch) u rad/s
        float y = event.values[1]; // rotacija oko Y (roll) u rad/s
        float z = event.values[2]; // rotacija oko Z (yaw) u rad/s

        textView.setText(String.format(
            "Pitch: %.3f rad/s\nRoll: %.3f rad/s\nYaw: %.3f rad/s", x, y, z));
    }
}
```

---

## 6. Kamera

### Tok (Add/Edit/Delete)

```
Korisnik klikne "Dodaj"
    ↓
Provjeri dozvolu CAMERA
    ↓
Napravi prazan fajl (File.createTempFile)
    ↓
Dobij URI putem FileProvider.getUriForFile()
    ↓
Intent(ACTION_IMAGE_CAPTURE) + putExtra(EXTRA_OUTPUT, uri)
    ↓
Camera app se otvara, korisnik slika
    ↓
onActivityResult → RESULT_OK
    ↓
imageView.setImageURI(uri)  ← prikaži sliku
```

### Kod — snimanje fotografije

```java
// Launcher (definiši kao field u klasi, VAN onCreate)
private final ActivityResultLauncher<Intent> kameraLauncher =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                imageView.setImageURI(null); // clear cache
                imageView.setImageURI(fotografijUri); // prikaži
            }
        });

// Otvaranje kamere
private void otvoriKameru() {
    try {
        File fajl = napraviFajlZaSliku();
        fotografijUri = FileProvider.getUriForFile(
            this,
            getPackageName() + ".fileprovider", // mora da se poklapa sa Manifest-om
            fajl
        );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fotografijUri); // gdje da sačuva
        kameraLauncher.launch(intent);
    } catch (IOException e) {
        Log.e(TAG, "Greška: " + e.getMessage());
    }
}

// Kreiranje privremenog fajla
private File napraviFajlZaSliku() throws IOException {
    String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    return File.createTempFile("FOTO_" + ts + "_", ".jpg", dir);
}
```

### Odabir iz galerije

```java
private final ActivityResultLauncher<Intent> galerijaLauncher =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                imageView.setImageURI(uri);
            }
        });

// Otvaranje galerije
private void otvoriGaleriju() {
    Intent intent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    intent.setType("image/*");
    galerijaLauncher.launch(intent);
}
```

### Brisanje slike

```java
private void obrisiSliku() {
    imageView.setImageResource(R.drawable.ic_slika_placeholder);
    fotografijUri = null;
}
```

---

## 7. FileProvider

FileProvider je **obavezno** za deljenje URI-ja fajlova sa drugim aplikacijama (Android 7+).

### res/xml/file_paths.xml

```xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path
        name="moje_slike"
        path="Pictures/" />
</paths>
```

### AndroidManifest.xml (unutar `<application>`)

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

### Dozvola za kameru

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

---

## Kreirani fajlovi

| Fajl | Opis |
|------|------|
| `SenzoriActivity.java` | Akcelerometar + shake + žiroskop |
| `KameraActivity.java` | Kamera + galerija + brisanje |
| `activity_senzori_screen.xml` | Layout sa 3 TextView-a |
| `activity_kamera_screen.xml` | Layout sa ImageView + 3 dugmeta |
| `res/xml/file_paths.xml` | FileProvider konfiguracija |
| `res/drawable/ic_slika_placeholder.xml` | Placeholder ikonica |
| `NotificationHelper.java` | Dodata `showShakeNotification()` metoda |
