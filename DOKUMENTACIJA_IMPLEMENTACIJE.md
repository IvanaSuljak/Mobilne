# DOKUMENTACIJA IMPLEMENTACIJE ANDROID APLIKACIJE

## UVOD
Ovaj dokument opisuje detaljnu implementaciju Android aplikacije sa tri ekrana: SplashScreen, LoginScreen i HomeScreen. Aplikacija prati standardni Android lifecycle i implementira osnovne funkcionalnosti kao sto su login, navigacija i koriscenje mikrofona.

## PROJEKATNA STRUKTURA

### Glavni direktorijum: `c:\Users\ivana\Desktop\mobilne vezbe`

**Vazne putanje:**
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

**Kljucni kod:**
```java
// Handler za odgodu prelaska
new Handler().postDelayed(() -> {
    Intent intent = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
    startActivity(intent);
    finish(); // Zatvara splash screen da se ne moze vratiti nazad
}, 5000); // 5000 milisekundi = 5 sekundi
```

**Zasto Handler?**
- `Handler.postDelayed()` omogucava da se kod izvrsi nakon odredjenog vremena
- Koristimo ga da prikazemo splash screen tacno 5 sekundi
- `finish()` je vazan da korisnik ne moze da se vrati na splash screen sa back dugmeta

**Lifecycle metode:**
Svaka metoda loguje svoje izvrsavanje:
- `onCreate()` - kreira aktivnost
- `onStart()` - aktivnost postaje vidljiva
- `onResume()` - aktivnost je u fokusu
- `onPause()`, `onStop()`, `onDestroy()` - ciscenje resursa

### Layout: `activity_splash_screen.xml`

**Struktura:**
- `ConstraintLayout` kao glavni kontejner
- `TextView` za poruku "Dobrodosli!"
- `ImageView` za logo (opciono)

## 2. LOGIN SCREEN IMPLEMENTACIJA

### Fajl: `LoginScreenActivity.java`

**Logika rada:**
- Prikazuje polja za email i password
- Dva dugmeta: Login i Register
- Login dugme vodi na HomeScreen
- Register dugme vodi na RegisterScreen
- Loguje sve lifecycle metode

**Kljucni kod:**
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

**Zasto `finish()` nakon startActivity?**
- Da korisnik ne moze da se vrati na login screen sa back dugmeta
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
- Nakon registracije vraca na LoginScreen
- Loguje sve lifecycle metode

**Kljucni kod:**
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
            return; // Prekida dalje izvrsavanje
        }
        
        // 2. Provera da li se password-i poklapaju
        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "Passwords do not match");
            return; // Prekida dalje izvrsavanje
        }
        
        // 3. Ako sve prodje, vrati se na LoginScreen
        finish(); // Zatvara RegisterScreen i vraca se na prethodni (LoginScreen)
    });
}
```

**Zasto `finish()` umesto `startActivity`?**
- `finish()` zatvara trenutnu aktivnost i vraca se na prethodnu
- Korisnik se vraca na LoginScreen nakon uspesne registracije
- Ne kreiramo novi LoginScreen, vec se vracamo na postojeci
- Cuva se back stack - korisnik moze da se vrati nazad

**Validacija - zasto je vazna?**
1. **Pracena polja** - spracava slanje praznih podataka
2. **Poklapanje lozinki** - osigurava da korisnik nije pogresio unos
3. **Trim()** - uklanja suvisne razmake sa pocetka i kraja

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

**InputType objasnjenje:**
- `textPersonName` - optimizovano tastaturo za imena
- `textEmailAddress` - prikazuje @ tastaturu
- `textPassword` - sakriva tekst (prikazuje ****)

**ConstraintLayout lanac (chain):**
- Svi elementi su u vertikalnom lancu (`app:layout_constraintVertical_chainStyle="packed"`)
- Svaki element je vezan za prethodni i sledeci
- Omogucava responsive dizajn - prilagodjava se razlicitim ekranima

**Styling:**
- `android:padding="16dp"` - unutrasnji razmak
- `app:layout_constraintWidth_max="400dp"` - maksimalna sirina
- `android:layout_marginBottom="16dp"` - razmak izmedju elemenata

## 4. HOME SCREEN IMPLEMENTACIJA

### Fajl: `HomeScreen.java`

**Logika rada:**
- Prikazuje welcome poruku
- Mikrofon dugme koje trazi permisiju
- Logout dugme za izlaz iz aplikacije
- Loguje sve lifecycle metode

**Kljucni kod:**
```java
// Provera permisije za mikrofon
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
    // Trazi permisiju ako nije data
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.RECORD_AUDIO},
            MICROPHONE_PERMISSION_REQUEST);
} else {
    // Koristi mikrofon ako je permisija data
    useMicrophone();
}
```

**Permisije - zasto su vazne?**
- Android 6.0+ zahteva runtime permisije za opasne dozvole
- `RECORD_AUDIO` je "dangerous permission" - mora traziti u runtime
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

## 5. LAYOUT FAJLOVI - VAZNA NAPOMENA

### Context atribut
Svaki layout fajl ima `tools:context` atribut:
```xml
tools:context=".HomeScreen"
```

**Sta radi?**
- Povezuje XML layout sa odgovarajucom Java klasom
- Omogucava Android Studiju da prikaze preview
- Mora biti tacno ime klase (bez .java ekstenzije)

**Problem koji smo resili:**
- Layout je imao `tools:context=".HomeScreenActivity"`
- Java klasa se zvala `HomeScreen`
- Ispravili smo da bude `tools:context=".HomeScreen"`

## 6. IMPORTI - STA I ZASTO

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

**Zasto svaki import?**
- `Intent` - navigacija izmedju aktivnosti
- `Bundle` - cuva stanje aktivnosti
- `Log` - debug informacije
- `View` - osnova za sve UI elemente
- Widget klase - pristup UI komponentama
- `Toast` - kratke poruke korisniku
- `AppCompatActivity` - moderna base klasa
- `ActivityCompat`/`ContextCompat` - kompatibilnost za permisije
- `Manifest` - konstante za permisije
- `PackageManager` - status permisija
- `@NonNull` - sigurnost koda

## 7. ANDROID LIFECYCLE

### Metode i njihovo znacenje:
1. **`onCreate()`** - Kreiranje aktivnosti, postavljanje layouta
2. **`onStart()`** - Aktivnost postaje vidljiva
3. **`onResume()`** - Aktivnost je u fokusu, korisnik moze interakciju
4. **`onPause()`** - Aktivnost gubi fokus
5. **`onStop()`** - Aktivnost vise nije vidljiva
6. **`onDestroy()`** - Aktivnost se unistava

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

## 8. MANIFEST FAJL

**AndroidManifest.xml** mora sadrzati sve aktivnosti:
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

**Sta znaci `android:exported="true"`?**
- Dozvoljava drugim aplikacijama da pokrenu ovu aktivnost
- Obavezno za LAUNCHER aktivnost

## 9. TESTIRANJE I DEBUG

### Kako testirati:
1. **Pokrenuti aplikaciju** - zelena strelica u Android Studiju
2. **Proveriti Logcat** - videti sve logove
3. **Testirati navigaciju** - proveriti sve prelaze
4. **Testirati permisije** - odbiti i dozvoliti mikrofon

### Logcat filter:
- Tag: `SplashScreen`, `LoginScreen`, `HomeScreen`
- Vidice sve lifecycle logove

## 10. MOGUCE PROBLEMI I RESENJA

### Problem: "Cannot resolve symbol"
- **Uzrok:** Pogresan naziv klase u `tools:context`
- **Resenje:** Proveriti da se naziv u XML-u poklapa sa Java klasom

### Problem: "Resource not found"
- **Uzrok:** Pogresan ID u `findViewById()`
- **Resenje:** Proveriti da ID postoji u layout fajlu

### Problem: Aplikacija se ne startuje
- **Uzrok:** Nedostaje aktivnost u manifestu
- **Resenje:** Dodati sve aktivnosti u AndroidManifest.xml

## 11. NAJBOLJE PRAKSE

### Sta smo primenili:
1. **Konstante za tagove** - `private static final String TAG`
2. **Metode za inicijalizaciju** - `initViews()`, `setupClickListeners()`
3. **Provera inputa** - provera da li su polja prazna
4. **Lifecycle logovanje** - za debug
5. **Provera permisija** - runtime permission handling

### Zasto je ovo vazno:
- **Odrzivost koda** - lakse za razumevanje i modifikaciju
- **Debug** - lakse pronalazenje gresaka
- **Korisnicko iskustvo** - pravilna navigacija i poruke
- **Sigurnost** - pravilno rukovanje permisijama

## ZAKLJUCAK

Implementirali smo potpuno funkcionalnu Android aplikaciju sa:
- \u2713 Tri ekrana sa navigacijom
- \u2713 Splash screen sa vremenskom odgodom
- \u2713 Login forma sa validacijom
- \u2713 Home screen sa mikrofon funkcionalnoscu
- \u2713 Runtime permisije
- \u2713 Lifecycle logovanje
- \u2713 Error handling

Svi komponenti su povezani i funkcionisu kao jedinstvena aplikacija. Kod prati Android best practice i spreman je za produkciju.

Permisije u Androidu su dozvole koje korisnik daje aplikaciji da pristupi odredjenim resursima ili funkcijama telefona.
Kako radi u nasoj aplikaciji
// 1. Provera da li imamo dozvolu
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
    
    // 2. Ako nemamo, trazimo od korisnika
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.RECORD_AUDIO],
            MICROPHONE_PERMISSION_REQUEST);
} else {
    // 3. Ako imamo, koristimo mikrofon
    useMicrophone();
}

---

# VEZBA 3 - NOVE FUNKCIONALNOSTI

## Cemu sluze VEZBA 3 zadaci?

VEZBA 3 uvodi naprednije koncepte Android razvoja:
- **Relativni raspored** - koriscenje ConstraintLayout za kompleksnije layout-e
- **InputType optimizacija** - prilagodjavanje tastature za tip unosa
- **Prenos podataka** - slanje podataka izmedju aktivnosti
- **String resursi** - internacionalizacija i odrzavanje koda

## 1. REGISTERSCREEN - DODAT POLJE ZA TELEFON

### Sta je dodato?
```xml
<!-- VEZBA 3: Dodato polje za broj telefona -->
<EditText
    android:id="@+id/phoneEditText"
    android:hint="@string/register_phone_hint"
    android:inputType="phone" />
```

### Zasto `inputType="phone"`?
- **Optimizovana tastatura** - prikazuje brojeve i simbole za telefon
- **Automatsko formatiranje** - moze da formatira broj telefona
- **Bolje korisnicko iskustvo** - korisnik odmah vidi da treba da unese broj

### Java promene:
```java
// VEZBA 3: Dodato polje za broj telefona
private EditText phoneEditText;

// U initViews():
phoneEditText = findViewById(R.id.phoneEditText);

// U validaciji:
String phone = phoneEditText.getText().toString().trim();
if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
    // Provera svih polja ukljucujuci telefon
}
```

## 2. LOGINSCREEN - PRILAGODJEN INPUTTYPE

### Sta je promenjeno?
```xml
<EditText
    android:inputType="textEmailAddress" />  <!-- VEZBA 3: Email tastatura -->
    
<EditText
    android:inputType="textPassword" />     <!-- VEZBA 3: Sakriva tekst -->
```

### InputType opcije:
- `textEmailAddress` - prikazuje @ tastaturu, optimizovano za email
- `textPassword` - sakriva unos (****), automatski prelazi na sledece polje
- `textPersonName` - optimizovano za imena (prvo slovo veliko)
- `phone` - numericka tastatura sa formatiranjem

## 3. HOMESCREEN - CONSTRAINTLAYOUT RASPORED

### Sta je novo?
Kompletno redizajniran layout sa **ConstraintLayout**:

```xml
<!-- VEZBA 3: Welcome poruka na vrhu -->
<TextView
    android:id="@+id/welcomeTextView"
    android:background="@android:color/holo_blue_light"
    android:textColor="@android:color/white" />

<!-- VEZBA 3: Sekcija za informacije o profilu -->
<TextView
    android:id="@+id/profileInfoTextView"
    android:background="@android:color/holo_orange_light" />

<!-- VEZBA 3: Email i Phone prikaz -->
<TextView android:id="@+id/emailValueTextView" />
<TextView android:id="@+id/phoneValueTextView" />

<!-- VEZBA 3: Settings i Logout dugmici -->
<Button android:id="@+id/settingsButton" />
<Button android:id="@+id/logoutButton" />
```

### ConstraintLayout lanac:
- **Vertikalni lanac** - svi elementi su povezani
- **Responsive dizajn** - prilagodjava se razlicitim ekranima
- **Pozicioniranje** - svaki element je vezan za prethodni i sledeci

## 4. PRENOS PODATAKA IZ REGISTERSCREEN U HOMESCREEN

### Kako radi?
```java
// RegisterScreen - slanje podataka
Intent intent = new Intent(RegisterScreenActivity.this, HomeScreenActivity.class);
intent.putExtra("USER_NAME", name);
intent.putExtra("USER_EMAIL", email);
intent.putExtra("USER_PHONE", phone);
startActivity(intent);

// HomeScreen - prijem podataka
private void receiveAndDisplayUserData() {
    Intent intent = getIntent();
    String userName = intent.getStringExtra("USER_NAME");
    String userEmail = intent.getStringExtra("USER_EMAIL");
    String userPhone = intent.getStringExtra("USER_PHONE");
    
    // Prikaz podataka
    welcomeTextView.setText("Dobrodosli, " + userName + "!");
    emailValueTextView.setText(userEmail);
    phoneValueTextView.setText(userPhone);
}
```

### Zasto `Intent.putExtra()`?
- **Prenos podataka** - cuva stanje izmedju aktivnosti
- **Tipovi podataka** - moze da prenese String, int, boolean, objekte
- **Kljuc-vrednost** - svaki podatak ima jedinstveni kljuc

## 5. STRING RESURSI - INTERNACIONALIZACIJA

### Sta je strings.xml?
```xml
<!-- VEZBA 3 - String resursi za sve tekstove -->
<string name="register_title">Registracija</string>
<string name="register_email_hint">Email</string>
<string name="register_phone_hint">Broj telefona</string>
<string name="home_welcome">Dobrodosli na pocetnu stranicu!</string>
```

### Koriscenje u XML:
```xml
<TextView android:text="@string/register_title" />
<EditText android:hint="@string/register_email_hint" />
```

### Koriscenje u Java:
```java
welcomeTextView.setText(getString(R.string.home_welcome));
Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show();
```

### Prednosti string resursa:
1. **Odrzavanje** - svi tekstovi na jednom mestu
2. **Internacionalizacija** - lako prevodjenje na druge jezike
3. **Konsistentnost** - isti tekst se koristi na vise mesta
4. **Tip bezbednosti** - kompajler proverava postojanje resursa

## 6. NAVIGACIJA TOKOM APLIKACIJE

### VEZBA 3 tok:
```
SplashScreen (5s) 
    \u2192 LoginScreen 
        \u2192 RegisterScreen 
            \u2192 HomeScreen (sa podacima)
```

### Promene u navigaciji:
- **RegisterScreen \u2192 HomeScreen** - umesto povratka na LoginScreen
- **Prenos podataka** - korisnicki podaci se prenose na HomeScreen
- **Personalizacija** - HomeScreen prikazuje ime i podatke korisnika

---

**VEZBA 3 je kompletna!** Aplikacija sada ima:
- \u2713 Relativni raspored sa ConstraintLayout
- \u2713 Optimizovane inputType za sve polja
- \u2713 Prenos podataka izmedju aktivnosti
- \u2713 Sve tekstove u string resursima
- \u2713 Personalizovani HomeScreen
- \u2713 Profesionalan UI sa bojama i stilovima
