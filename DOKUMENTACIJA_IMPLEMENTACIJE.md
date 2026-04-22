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

---

# VEZBA 3 - NOVE FUNKCIONALNOSTI

## \u010cemu slu\u017ee VEZBA 3 zadaci?

VEZBA 3 uvodi naprednije koncepte Android razvoja:
- **Relativni raspored** - kori\u0161\u0107enje ConstraintLayout za kompleksnije layout-e
- **InputType optimizacija** - prilago\u0111avanje tastature za tip unosa
- **Prenos podataka** - slanje podataka izme\u0111u aktivnosti
- **String resursi** - internacionalizacija i odr\u017eavanje koda

## 1. REGISTERSCREEN - DODAT POLJE ZA TELEFON

### \u0160ta je dodato?
```xml
<!-- VEZBA 3: Dodato polje za broj telefona -->
<EditText
    android:id="@+id/phoneEditText"
    android:hint="@string/register_phone_hint"
    android:inputType="phone" />
```

### Za\u0161to `inputType="phone"`?
- **Optimizovana tastatura** - prikazuje brojeve i simbole za telefon
- **Automatsko formatiranje** - mo\u017ee da formatira broj telefona
- **Bolje korisni\u010dko iskustvo** - korisnik odmah vidi da treba da unese broj

### Java promene:
```java
// VEZBA 3: Dodato polje za broj telefona
private EditText phoneEditText;

// U initViews():
phoneEditText = findViewById(R.id.phoneEditText);

// U validaciji:
String phone = phoneEditText.getText().toString().trim();
if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
    // Provera svih polja uklju\u010duju\u0107i telefon
}
```

## 2. LOGINSCREEN - PRILAGO\u0110EN INPUTTYPE

### \u0160ta je promenjeno?
```xml
<EditText
    android:inputType="textEmailAddress" />  <!-- VEZBA 3: Email tastatura -->
    
<EditText
    android:inputType="textPassword" />     <!-- VEZBA 3: Sakriva tekst -->
```

### InputType opcije:
- `textEmailAddress` - prikazuje @ tastaturu, optimizovano za email
- `textPassword` - sakriva unos (****), automatski prelazi na slede\u0107e polje
- `textPersonName` - optimizovano za imena (prvo slovo veliko)
- `phone` - numeri\u010dka tastatura sa formatiranjem

## 3. HOMESCREEN - CONSTRAINTLAYOUT RASPORED

### \u0160ta je novo?
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

<!-- VEZBA 3: Settings i Logout dugmi\u0107i -->
<Button android:id="@+id/settingsButton" />
<Button android:id="@+id/logoutButton" />
```

### ConstraintLayout lanac:
- **Vertikalni lanac** - svi elementi su povezani
- **Responsive dizajn** - prilago\u0111ava se razli\u010ditim ekranima
- **Pozicioniranje** - svaki element je vezan za prethodni i slede\u0107i

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
    welcomeTextView.setText("Dobrodo\u0161li, " + userName + "!");
    emailValueTextView.setText(userEmail);
    phoneValueTextView.setText(userPhone);
}
```

### Za\u0161to `Intent.putExtra()`?
- **Prenos podataka** - \u010duva stanje izme\u0111u aktivnosti
- **Tipovi podataka** - mo\u017ee da prenese String, int, boolean, objekte
- **Klju\u010d-vrednost** - svaki podatak ima jedinstveni klju\u010d

## 5. STRING RESURSI - INTERNACIONALIZACIJA

### \u0160ta je strings.xml?
```xml
<!-- VEZBA 3 - String resursi za sve tekstove -->
<string name="register_title">Registracija</string>
<string name="register_email_hint">Email</string>
<string name="register_phone_hint">Broj telefona</string>
<string name="home_welcome">Dobrodo\u0161li na po\u010detnu stranicu!</string>
```

### Kori\u0161\u0107enje u XML:
```xml
<TextView android:text="@string/register_title" />
<EditText android:hint="@string/register_email_hint" />
```

### Kori\u0161\u0107enje u Java:
```java
welcomeTextView.setText(getString(R.string.home_welcome));
Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show();
```

### Prednosti string resursa:
1. **Odr\u017eavanje** - svi tekstovi na jednom mestu
2. **Internacionalizacija** - lako prevo\u0111enje na druge jezike
3. **Konsistentnost** - isti tekst se koristi na vi\u0161e mesta
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
- **Prenos podataka** - korisni\u010dki podaci se prenose na HomeScreen
- **Personalizacija** - HomeScreen prikazuje ime i podatke korisnika

---

**VEZBA 3 je kompletna!** Aplikacija sada ima:
- \u2713 Relativni raspored sa ConstraintLayout
- \u2713 Optimizovane inputType za sve polja
- \u2713 Prenos podataka izme\u0111u aktivnosti
- \u2713 Sve tekstove u string resursima
- \u2713 Personalizovani HomeScreen
- \u2713 Profesionalan UI sa bojama i stilovima