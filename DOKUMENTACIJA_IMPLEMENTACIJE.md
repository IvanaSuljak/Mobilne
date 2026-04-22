# DOKUMENTACIJA IMPLEMENTACIJE ANDROID APLIKACIJE

## UVOD
Ovaj dokument opisuje detaljnu implementaciju Android aplikacije sa tri ekrana: SplashScreen, LoginScreen i HomeScreen. Aplikacija prati standardni Android lifecycle i implementira osnovne funkcionalnosti kao što su login, navigacija i korišćenje mikrofona.

## PROJEKATNA STRUKTURA

### Glavni direktorijum: `c:\Users\ivana\Desktop\mobilne vezbe`

**Važne putanje:**
- `app/src/main/java/com/example/mobilnevezbe/` - Java klase
- `app/src/main/res/layout/` - XML layout fajlovi
- `app/src/main/res/values/strings.xml` - String resursi
- `AndroidManifest.xml` - Manifest fajl

## 1. SPLASH SCREEN IMPLEMENTACIJA

### Fajl: `SplashScreenActivity.java`

**Logika rada:**
- Splash screen se prikazuje 5 sekundi
- Nakon 5 sekundi automatski prelazi na LoginScreen
- Loguje sve lifecycle metode

**Ključni kod:**
```java
// Handler za odgodu prelaska
new Handler().postDelayed(() -> {
    Intent intent = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
    startActivity(intent);
    finish(); // Zatvara splash screen da se ne može vratiti nazad
}, 5000); // 5000 milisekundi = 5 sekundi
```

**Zašto Handler?**
- `Handler.postDelayed()` omogućava da se kod izvrši nakon određenog vremena
- Koristimo ga da prikažemo splash screen tačno 5 sekundi
- `finish()` je važan da korisnik ne može da se vrati na splash screen sa back dugmeta

**Lifecycle metode:**
Svaka metoda loguje svoje izvršavanje:
- `onCreate()` - kreira aktivnost
- `onStart()` - aktivnost postaje vidljiva
- `onResume()` - aktivnost je u fokusu
- `onPause()`, `onStop()`, `onDestroy()` - čišćenje resursa

### Layout: `activity_splash_screen.xml`

**Struktura:**
- `ConstraintLayout` kao glavni kontejner
- `TextView` za poruku "Dobrodošli!"
- `ImageView` za logo (opciono)

## 2. LOGIN SCREEN IMPLEMENTACIJA

### Fajl: `LoginScreenActivity.java`

**Logika rada:**
- Prikazuje polja za email i password
- Dva dugmeta: Login i Register
- Login dugme vodi na HomeScreen
- Register dugme vodi na RegisterScreen
- Loguje sve lifecycle metode

**Ključni kod:**
```java
// Inicijalizacija view-ova
private void initViews() {
    emailEditText = findViewById(R.id.emailEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    loginButton = findViewById(R.id.loginButton);
    registerButton = findViewById(R.id.registerButton);
}

// Postavljanje click listener-a
private void setupClickListeners() {
    loginButton.setOnClickListener(v -> {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        
        // Provera da li su polja popunjena
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Molimo popunite sva polja", Toast.LENGTH_SHORT).show();
        } else {
            // Prelazak na HomeScreen
            Intent intent = new Intent(LoginScreenActivity.this, HomeScreen.class);
            startActivity(intent);
            finish(); // Zatvara login screen
        }
    });
}
```

**Zašto `finish()` nakon startActivity?**
- Da korisnik ne može da se vrati na login screen sa back dugmeta
- Pravi "forward navigation" - korisnik ide samo napred kroz aplikaciju

### Layout: `activity_login_screen.xml`

**Struktura:**
- `ConstraintLayout` za pozicioniranje
- `EditText` za email unos (`android:inputType="textEmailAddress"`)
- `EditText` za password (`android:inputType="textPassword"`)
- `Button` za login i register akcije

## 3. REGISTER SCREEN IMPLEMENTACIJA

### Fajl: `RegisterScreenActivity.java`

**Logika rada:**
- Prikazuje formu za registraciju korisnika
- Polja: ime, email, password, confirm password
- Validacija unosa pre registracije
- Nakon registracije vra na LoginScreen
- Loguje sve lifecycle metode

**Kljuèni kod:**
```java
// Deklaracija svih polja za registraciju
private EditText nameEditText;
private EditText emailEditText;
private EditText passwordEditText;
private EditText confirmPasswordEditText;
private Button registerButton;

// Inicijalizacija view-ova
private void initViews() {
    nameEditText = findViewById(R.id.nameEditText);
    emailEditText = findViewById(R.id.emailEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
    registerButton = findViewById(R.id.registerButton);
}
```

**Validacija registracije:**
```java
private void setupClickListeners() {
    registerButton.setOnClickListener(v -> {
        // Uzimanje teksta iz svih polja
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // 1. Provera da li su sva polja popunjena
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "Please fill all fields");
            return; // Prekida dalje izvravanje
        }
        
        // 2. Provera da li se password-i poklapaju
        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "Passwords do not match");
            return; // Prekida dalje izvravanje
        }
        
        // 3. Ako sve proðe, vrati se na LoginScreen
        finish(); // Zatvara RegisterScreen i vraa se na prethodni (LoginScreen)
    });
}
```

**Za¹to `finish()` umesto `startActivity`?**
- `finish()` zatvara trenutnu aktivnost i vraæa se na prethodnu
- Korisnik se vraæa na LoginScreen nakon uspe¹ne registracije
- Ne kreiramo novi LoginScreen, veæ se vraæamo na postojeæi
- Èuva se back stack - korisnik moæe da se vrati nazad

**Validacija - za¹to je vaæna?**
1. **Praæena polja** - spreèava slanje praznih podataka
2. **Poklapanje lozinki** - osigurava da korisnik nije pogre¹io unos
3. **Trim()** - uklanja suvi¹ne razmake sa poèetka i kraja

### Layout: `activity_register_screen.xml`

**Struktura:**
- `ConstraintLayout` za vertikalno centriranje
- `TextView` za naslov "Register"
- 4 `EditText` polja za unos podataka
- `Button` za registraciju

**Detalji polja:**
```xml
<!-- Polje za ime -->
<EditText
    android:id="@+id/nameEditText"
    android:hint="Full Name"
    android:inputType="textPersonName" />

<!-- Polje za email -->
<EditText
    android:id="@+id/emailEditText"
    android:hint="Email"
    android:inputType="textEmailAddress" />

<!-- Polje za lozinku -->
<EditText
    android:id="@+id/passwordEditText"
    android:hint="Password"
    android:inputType="textPassword" />

<!-- Polje za potvrdu lozinke -->
<EditText
    android:id="@+id/confirmPasswordEditText"
    android:hint="Confirm Password"
    android:inputType="textPassword" />
```

**InputType obja¹njenje:**
- `textPersonName` - optimizovano tastaturo za imena
- `textEmailAddress` - prikazuje @ tastaturu
- `textPassword` - sakriva tekst (prikazuje ****)

**ConstraintLayout lanac (chain):**
- Svi elementi su u vertikalnom lancu (`app:layout_constraintVertical_chainStyle="packed"`)
- Svaki element je vezan za prethodni i sledeæi
- Omoguæava responsive dizajn - prilagoðava se razlièitim ekranima

**Styling:**
- `android:padding="16dp"` - unutra¹nji razmak
- `app:layout_constraintWidth_max="400dp"` - maksimalna ¹irina
- `android:layout_marginBottom="16dp"` - razmak izmeðu elemenata

## 4. HOME SCREEN IMPLEMENTACIJA

### Fajl: `HomeScreen.java`

**Logika rada:**
- Prikazuje welcome poruku
- Mikrofon dugme koje traži permisiju
- Logout dugme za izlaz iz aplikacije
- Loguje sve lifecycle metode

**Ključni kod:**
```java
// Provera permisije za mikrofon
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
    // Traži permisiju ako nije data
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.RECORD_AUDIO},
            MICROPHONE_PERMISSION_REQUEST);
} else {
    // Koristi mikrofon ako je permisija data
    useMicrophone();
}
```

**Permisije - zašto su važne?**
- Android 6.0+ zahteva runtime permisije za opasne dozvole
- `RECORD_AUDIO` je "dangerous permission" - mora tražiti u runtime
- `onRequestPermissionsResult()` hvata odgovor korisnika

**Permission Request kod:**
```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
    if (requestCode == MICROPHONE_PERMISSION_REQUEST) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useMicrophone(); // Dozvola data
        } else {
            Toast.makeText(this, "Permisija za mikrofon je odbijena", Toast.LENGTH_SHORT).show();
        }
    }
}
```

### Layout: `activity_home_screen.xml`

**Struktura:**
- `TextView` za welcome poruku
- `Button` za mikrofon sa ikonom (`android:drawableStart="@android:drawable/ic_btn_speak_now"`)
- `Button` za logout

## 4. LAYOUT FAJLOVI - VAŽNA NAPOMENA

### Context atribut
Svaki layout fajl ima `tools:context` atribut:
```xml
tools:context=".HomeScreen"
```

**Šta radi?**
- Povezuje XML layout sa odgovarajućom Java klasom
- Omogućava Android Studiju da prikaže preview
- Mora biti tačno ime klase (bez .java ekstenzije)

**Problem koji smo rešili:**
- Layout je imao `tools:context=".HomeScreenActivity"`
- Java klasa se zvala `HomeScreen`
- Ispravili smo da bude `tools:context=".HomeScreen"`

## 5. IMPORTI - ŠTA I ZAŠVO

### Obavezni importi za svaku aktivnost:
```java
import android.content.Intent;           // Za startActivity()
import android.os.Bundle;               // Za onCreate() parametar
import android.util.Log;                // Za logovanje
import android.view.View;               // Za click listener-e
import android.widget.Button;           // Za Button widget
import android.widget.EditText;         // Za EditText widget
import android.widget.TextView;         // Za TextView widget
import android.widget.Toast;            // Za Toast poruke
import androidx.appcompat.app.AppCompatActivity; // Base klasa
import androidx.core.app.ActivityCompat;         // Za permisije
import androidx.core.content.ContextCompat;      // Za proveru permisija
import android.Manifest;                // Konstante permisija
import android.content.pm.PackageManager; // Za proveru statusa permisija
import android.annotation.NonNull;       // Za onRequestPermissionsResult
```

**Zašto svaki import?**
- `Intent` - navigacija između aktivnosti
- `Bundle` - čuva stanje aktivnosti
- `Log` - debug informacije
- `View` - osnova za sve UI elemente
- Widget klase - pristup UI komponentama
- `Toast` - kratke poruke korisniku
- `AppCompatActivity` - moderna base klasa
- `ActivityCompat`/`ContextCompat` - kompatibilnost za permisije
- `Manifest` - konstante za permisije
- `PackageManager` - status permisija
- `@NonNull` - sigurnost koda

## 6. ANDROID LIFECYCLE

### Metode i njihovo značenje:
1. **`onCreate()`** - Kreiranje aktivnosti, postavljanje layouta
2. **`onStart()`** - Aktivnost postaje vidljiva
3. **`onResume()`** - Aktivnost je u fokusu, korisnik može interakciju
4. **`onPause()`** - Aktivnost gubi fokus
5. **`onStop()`** - Aktivnost više nije vidljiva
6. **`onDestroy()`** - Aktivnost se uništava

**Logovanje:**
```java
private static final String TAG = "HomeScreen";

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home_screen);
    Log.d(TAG, "onCreate called");
}
```

## 7. MANIFEST FAJL

**AndroidManifest.xml** mora sadržati sve aktivnosti:
```xml
<activity android:name=".SplashScreenActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity android:name=".LoginScreenActivity" />
<activity android:name=".HomeScreen" />
```

**Šta znači `android:exported="true"`?**
- Dozvoljava drugim aplikacijama da pokrenu ovu aktivnost
- Obavezno za LAUNCHER aktivnost

## 8. TESTIRANJE I DEBUG

### Kako testirati:
1. **Pokrenuti aplikaciju** - zelena strelica u Android Studiju
2. **Proveriti Logcat** - videti sve logove
3. **Testirati navigaciju** - proveriti sve prelaze
4. **Testirati permisije** - odbiti i dozvoliti mikrofon

### Logcat filter:
- Tag: `SplashScreen`, `LoginScreen`, `HomeScreen`
- Vidiće sve lifecycle logove

## 9. MOGUĆI PROBLEMI I REŠENJA

### Problem: "Cannot resolve symbol"
- **Uzrok:** Pogrešan naziv klase u `tools:context`
- **Rešenje:** Proveriti da se naziv u XML-u poklapa sa Java klasom

### Problem: "Resource not found"
- **Uzrok:** Pogrešan ID u `findViewById()`
- **Rešenje:** Proveriti da ID postoji u layout fajlu

### Problem: Aplikacija se ne startuje
- **Uzrok:** Nedostaje aktivnost u manifestu
- **Rešenje:** Dodati sve aktivnosti u AndroidManifest.xml

## 10. NAJBOLJE PRAKSE

### Šta smo primenili:
1. **Konstante za tagove** - `private static final String TAG`
2. **Metode za inicijalizaciju** - `initViews()`, `setupClickListeners()`
3. **Provera inputa** - provera da li su polja prazna
4. **Lifecycle logovanje** - za debug
5. **Provera permisija** - runtime permission handling

### Zašto je ovo važno:
- **Održivost koda** - lakše za razumevanje i modifikaciju
- **Debug** - lakše pronalaženje grešaka
- **Korisničko iskustvo** - pravilna navigacija i poruke
- **Sigurnost** - pravilno rukovanje permisijama

## ZAKLJUČAK

Implementirali smo potpuno funkcionalnu Android aplikaciju sa:
- ✅ Tri ekrana sa navigacijom
- ✅ Splash screen sa vremenskom odgodom
- ✅ Login forma sa validacijom
- ✅ Home screen sa mikrofon funkcionalnošću
- ✅ Runtime permisije
- ✅ Lifecycle logovanje
- ✅ Error handling

Svi komponenti su povezani i funkcionišu kao jedinstvena aplikacija. Kod prati Android best practice i spreman je za produkciju.

Permisije u Androidu su dozvole koje korisnik daje aplikaciji da pristupi određenim resursima ili funkcijama telefona.
Kako radi u nasoj aplikaciji
// 1. Provera da li imamo dozvolu
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
    
    // 2. Ako nemamo, tražimo od korisnika
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.RECORD_AUDIO},
            MICROPHONE_PERMISSION_REQUEST);
} else {
    // 3. Ako imamo, koristimo mikrofon
    useMicrophone();
}