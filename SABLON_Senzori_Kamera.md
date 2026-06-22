# ŠABLON — Senzori i Kamera

> `TODO` → zameni sa svojim podacima. Ostalo kopiraš bukvalno.

---

## TODO lista

- [ ] `TODO_IME_PAKETA` → tvoj paket
- [ ] `TODO_TIP_SENZORA` → npr. `Sensor.TYPE_ACCELEROMETER`, `Sensor.TYPE_GYROSCOPE`
- [ ] `TODO_textView` → ID TextView-a za prikaz podataka senzora
- [ ] `TODO_imageView` → ID ImageView-a za prikaz slike

---

## KORAK 1 — AndroidManifest.xml

```xml
<!-- Dozvola za kameru -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />

<!-- Unutar <application> — FileProvider za kameru -->
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

## KORAK 2 — res/xml/file_paths.xml (napravi ovaj fajl)

```xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="moje_slike" path="Pictures/" />
</paths>
```

---

## KORAK 3 — Senzori (Activity sa implements SensorEventListener)

```java
public class TODO_Activity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor        senzor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.TODO_layout);

        // 1. Uzmi SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 2. Dobavi senzor (može biti null!)
        senzor = sensorManager.getDefaultSensor(TODO_TIP_SENZORA);
        if (senzor == null) {
            Toast.makeText(this, "Senzor nije dostupan!", Toast.LENGTH_LONG).show();
        }
    }

    // 3. Registruj u onResume
    @Override
    protected void onResume() {
        super.onResume();
        if (senzor != null) {
            sensorManager.registerListener(this, senzor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // 4. OBAVEZNO odjavi u onPause!
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // 5. Obrada podataka
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == TODO_TIP_SENZORA) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            TODO_textView.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
        }
    }

    // 6. Obavezno override
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
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

## KORAK 5 — Kamera (Activity)

```java
public class TODO_KameraActivity extends AppCompatActivity {

    private Uri     fotografijUri;
    private ImageView TODO_imageView;

    // Launcher — definiši KAO FIELD, van onCreate()!
    private final ActivityResultLauncher<Intent> kameraLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    TODO_imageView.setImageURI(null);
                    TODO_imageView.setImageURI(fotografijUri); // prikaži sliku
                }
            });

    private final ActivityResultLauncher<Intent> galerijaLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    TODO_imageView.setImageURI(result.getData().getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.TODO_kamera_layout);
        TODO_imageView = findViewById(R.id.TODO_imageView);

        // Dugmad
        dugmeSnimi.setOnClickListener(v -> otvoriKameru());
        dugmeGalerija.setOnClickListener(v -> otvoriGaleriju());
        dugmeBrisi.setOnClickListener(v -> {
            TODO_imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        });
    }

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

    private void otvoriGaleriju() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galerijaLauncher.launch(intent);
    }
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
