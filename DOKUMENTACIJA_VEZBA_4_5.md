# DOKUMENTACIJA IMPLEMENTACIJE - VEZBA 4, 5

## UVOD

Ovaj dokument opisuje implementaciju VEZBA 4 i VEZBA 5 Android mobilne aplikacije. Ove vežbe uvode napredne koncepte kao što su Toolbar navigacija, Singleton pattern, servisi i notifikacije.

---

# VEZBA 4 - NAPREDNA NAVIGACIJA I UPRAVLJANJE KORISNICIMA

## Čemu služe VEZBA 4 zadaci?

VEZBA 4 uvodi napredne koncepte Android razvoja:
- **Toolbar navigacija** - konzistentna navigacija kroz aplikaciju
- **Singleton pattern** - efikasno upravljanje podacima
- **Sortiranje i filtriranje** - napredne UI funkcionalnosti
- **Mock podaci** - testiranje sa simuliranim podacima
- **Validacija unosa** - provera duplikata i login kredencijala

## 1. USER MODEL - USER.JAVA

### Šta je User klasa?
```java
/**
 * VEZBA 4: Klasa za modelovanje korisnika
 * Sadrži sve informacije o korisniku aplikacije
 */
public class User {
    private String name;
    private String email;
    private String phone;
    private String password;
    private long registrationDate;
    
    // VEZBA 4: Konstruktor za kreiranje novog korisnika
    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.registrationDate = System.currentTimeMillis();
    }
    
    // VEZBA 4: Provera login podataka
    public boolean checkLogin(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
    
    // VEZBA 4: Formatiran prikaz
    public String getDisplayName() {
        return name + " (" + email + ")";
    }
}
```

### Zašto User model?
- **Objektno-orijentisani pristup** - svi podaci o korisniku na jednom mestu
- **Enkapsulacija** - podaci su zaštićeni i kontrolisani
- **Validacija** - metoda `checkLogin()` za proveru kredencijala
- **Prikaz** - `getDisplayName()` za prikaz u listi

## 2. USERMANAGER - SINGLETON PATTERN

### Šta je UserManager?
```java
/**
 * VEZBA 4: Singleton klasa za upravljanje korisnicima
 * Čuva sve korisnike u memoriji i pruža metode za manipulaciju
 */
public class UserManager {
    private static UserManager instance;
    private List<User> users;
    
    // VEZBA 4: Privatni konstruktor za Singleton
    private UserManager() {
        users = new ArrayList<>();
        addMockUsers(); // VEZBA 4: Dodaj test korisnike
    }
    
    // VEZBA 4: Singleton instanca
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    // VEZBA 4: Dodavanje korisnika sa proverom duplikata
    public boolean addUser(User user) {
        if (getUserByEmail(user.getEmail()) != null) {
            return false; // Email već postoji
        }
        users.add(user);
        return true;
    }
    
    // VEZBA 4: Login provera
    public User loginUser(String email, String password) {
        for (User user : users) {
            if (user.checkLogin(email, password)) {
                return user;
            }
        }
        return null;
    }
}
```

### Zašto Singleton pattern?
- **Jedinstvena instanca** - samo jedan UserManager u celoj aplikaciji
- **Deljeni podaci** - svi screen-ovi vide iste korisnike
- **Memorijska efikasnost** - ne dupliraju se podaci
- **Thread safety** - jedinstven pristup podacima

## 3. SORTIRANJE I FILTRIRANJE

### Sortiranje opcije:
```java
// VEZBA 4: Sortiranje po imenu
public List<User> sortUsersByName() {
    List<User> sortedUsers = new ArrayList<>(users);
    Collections.sort(sortedUsers, new Comparator<User>() {
        @Override
        public int compare(User u1, User u2) {
            return u1.getName().compareToIgnoreCase(u2.getName());
        }
    });
    return sortedUsers;
}

// VEZBA 4: Sortiranje po email-u
public List<User> sortUsersByEmail() {
    List<User> sortedUsers = new ArrayList<>(users);
    Collections.sort(sortedUsers, new Comparator<User>() {
        @Override
        public int compare(User u1, User u2) {
            return u1.getEmail().compareToIgnoreCase(u2.getEmail());
        }
    });
    return sortedUsers;
}
```

### Filtriranje opcije:
```java
// VEZBA 4: Filtriranje po imenu sa Stream API
public List<User> filterUsersByName(String searchTerm) {
    return users.stream()
            .filter(user -> user.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
}

// VEZBA 4: Filtriranje po email-u
public List<User> filterUsersByEmail(String searchTerm) {
    return users.stream()
            .filter(user -> user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
}

// VEZBA 4: Filtriranje po telefonu
public List<User> filterUsersByPhone(String searchTerm) {
    return users.stream()
            .filter(user -> user.getPhone().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
}
```

### Zašto Stream API?
- **Funkcionalni pristup** - čišći i čitljiviji kod
- **Lančano filtriranje** - mogućnost kombinovanja filtera
- **Performanse** - efikasno procesiranje kolekcija

## 4. USERSCREEN - UI ZA UPRAVLJANJE KORISNICIMA

### Komponente UI-a:
```xml
<!-- VEZBA 4: Toolbar za navigaciju -->
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    app:title="Korisnici" />

<!-- VEZBA 4: Spinner za sortiranje -->
<Spinner
    android:id="@+id/sortSpinner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<!-- VEZBA 4: Polje za filtriranje -->
<EditText
    android:id="@+id/filterEditText"
    android:hint="Filtriraj korisnike..." />

<!-- VEZBA 4: Lista korisnika -->
<ListView
    android:id="@+id/usersListView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1" />
```

### Funkcionalnosti:
- **Sortiranje** - po imenu, email-u, datumu registracije
- **Filtriranje** - pretraga po bilo kom polju
- **Brojanje** - prikaz ukupnog broja korisnika
- **Menu opcije** - osveži listu, obriši sve

## 5. TOOLBAR NAVIGACIJA

### Implementacija u svakom screen-u:
```java
// VEZBA 4: Postavljanje toolbar-a
private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
        getSupportActionBar().setTitle("Naslov ekrana");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

// VEZBA 4: Menu item click handler
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    
    if (id == android.R.id.home) {
        finish(); // VEZBA 4: Nazad na prethodni ekran
        return true;
    } else if (id == R.id.action_users) {
        // VEZBA 4: Navigacija na UserScreen
        Intent intent = new Intent(HomeScreenActivity.this, UserScreenActivity.class);
        startActivity(intent);
        return true;
    }
    
    return super.onOptionsItemSelected(item);
}
```

### Menu integracija:
```xml
<!-- VEZBA 4: Menu za HomeScreen -->
<item android:id="@+id/action_users"
      android:icon="@android:drawable/ic_menu_people"
      android:title="Korisnici"
      app:showAsAction="ifRoom" />
```

## 6. REGISTRACIJA SA USERMANAGER

### Promene u RegisterScreen:
```java
// VEZBA 4: Kreiranje korisnika i čuvanje
User newUser = new User(name, email, phone, password);

// VEZBA 4: Provera duplikata
if (!userManager.addUser(newUser)) {
    Toast.makeText(this, getString(R.string.email_exists), Toast.LENGTH_SHORT).show();
    return;
}

// VEZBA 4: Uspešna registracija
Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show();

// Prelazak na HomeScreen sa podacima
Intent intent = new Intent(RegisterScreenActivity.this, HomeScreenActivity.class);
intent.putExtra("USER_NAME", name);
intent.putExtra("USER_EMAIL", email);
intent.putExtra("USER_PHONE", phone);
startActivity(intent);
finish();
```

### Validacija:
- **Provera praznih polja** - sva polja moraju biti popunjena
- **Poklapanje lozinki** - password i confirm password
- **Email jedinstvenost** - provera da li email već postoji

## 7. LOGIN SA USERMANAGER

### Promene u LoginScreen:
```java
// VEZBA 4: Provera login podataka
User loggedInUser = userManager.loginUser(email, password);

if (loggedInUser != null) {
    // Uspešan login - prenos podataka
    Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
    intent.putExtra("USER_NAME", loggedInUser.getName());
    intent.putExtra("USER_EMAIL", loggedInUser.getEmail());
    intent.putExtra("USER_PHONE", loggedInUser.getPhone());
    startActivity(intent);
    finish();
} else {
    // Neuspešan login
    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
}
```

## 8. MOCK PODACI

### Test korisnici:
```java
// VEZBA 4: Dodavanje test korisnika
private void addMockUsers() {
    users.add(new User("Marko Marković", "marko@example.com", "064123456", "marko123"));
    users.add(new User("Jelena Jovanović", "jelena@example.com", "065987654", "jelena123"));
    users.add(new User("Petar Petrović", "petar@example.com", "063456789", "petar123"));
    users.add(new User("Ana Anić", "ana@example.com", "062111222", "ana123"));
    users.add(new User("Milan Milenković", "milan@example.com", "064333444", "milan123"));
}
```

### Zašto mock podaci?
- **Testiranje** - odmah vidite kako aplikacija radi
- **Demonstracija** - pokazivanje funkcionalnosti
- **Debug** - lakše testiranje sortiranja i filtriranja

---

# VEZBA 5 - SERVISI I PRIJEMNICI PORUKA

## Čemu služe VEZBA 5 zadaci?

VEZBA 5 uvodi napredne koncepte Android razvoja:
- **Notifikacije** - kanali, akcije i stilovi
- **Servisi** - pozadinski procesi
- **Broadcast Receiver** - komunikacija između komponenti
- **Network monitoring** - provera konekcije
- **Background tasks** - periodične operacije

## 1. NOTIFICATIONHELPER - UPRAVLJANJE NOTIFIKACIJAMA

### Šta je NotificationHelper?
```java
/**
 * VEZBA 5: Helper klasa za notifikacije
 * Kreira kanale, notifikacije i upravlja njihovim prikazom
 */
public class NotificationHelper {
    
    // VEZBA 5: Kanal ID-i za različite tipove notifikacija
    public static final String CHANNEL_USER_REGISTRATION = "user_registration_channel";
    public static final String CHANNEL_INTERNET_STATUS = "internet_status_channel";
    
    // VEZBA 5: Notifikacija ID-i
    public static final int NOTIFICATION_USER_REGISTERED = 1001;
    public static final int NOTIFICATION_INTERNET_OFFLINE = 1002;
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        
        // VEZBA 5: Kreiraj kanale pri prvoj instanci
        createNotificationChannels();
    }
}
```

### Zašto NotificationHelper?
- **Centralizovano upravljanje** - sve notifikacije na jednom mestu
- **Kanalizacija** - Android 8+ zahteva kanale
- **Konzistentnost** - isti stil za sve notifikacije
- **Lakše održavanje** - promene na jednom mestu

## 2. NOTIFIKACIJA O NOVOM KORISNIKU

### Kreiranje notifikacije:
```java
// VEZBA 5: Notifikacija o novom korisniku
public void showUserRegisteredNotification(String userName) {
    // VEZBA 5: Intent za akciju "Prikaži"
    Intent showIntent = new Intent(context, HomeScreenActivity.class);
    showIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    showIntent.putExtra("FROM_NOTIFICATION", true);
    showIntent.putExtra("USER_NAME", userName);
    
    PendingIntent showPendingIntent = PendingIntent.getActivity(
        context, 0, showIntent, 
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    
    // VEZBA 5: Kreiranje notifikacije
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_USER_REGISTRATION)
        .setSmallIcon(android.R.drawable.ic_dialog_email) // VEZBA 5: Ikona za notifikaciju
        .setContentTitle("Novi korisnik registrovan!")
        .setContentText("Korisnik " + userName + " se uspešno registrovao")
        .setStyle(new NotificationCompat.BigTextStyle()
            .bigText("Novi korisnik " + userName + " se upravo registrovao u aplikaciji. Kliknite da vidite detalje."))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(false) // VEZBA 5: Ne uklanjaj automatski
        .addAction(android.R.drawable.ic_menu_view, "Prikaži", showPendingIntent) // VEZBA 5: Akcija dugme
        .setContentIntent(showPendingIntent);
    
    // VEZBA 5: Prikaz notifikacije
    notificationManager.notify(NOTIFICATION_USER_REGISTERED, builder.build());
}
```

### Funkcionalnosti notifikacije:
- **Mali ikona** - `ic_dialog_email`
- **Naslov i tekst** - dinamički sa imenom korisnika
- **BigTextStyle** - prošireni tekst
- **Akcija dugme** - "Prikaži" sa PendingIntent
- **Prioritet** - HIGH za važne notifikacije
- **AutoCancel false** - ne uklanja automatski

## 3. INTERNETCHECKSERVICE - SERVIS ZA PROVERU KONEKCIJE

### Šta je InternetCheckService?
```java
/**
 * VEZBA 5: Servis za proveru internet konekcije svakog minuta
 * Radi u pozadini i proverava status konekcije
 */
public class InternetCheckService extends Service {
    
    private static final String TAG = "InternetCheckService";
    private static final int CHECK_INTERVAL = 60000; // VEZBA 5: Provera svakog minuta (60 sekundi)
    
    private Handler handler;
    private Runnable checkRunnable;
    private boolean isRunning = false;
    private boolean wasConnected = true; // VEZBA 5: Pamti prethodno stanje
}
```

### Zašto servis?
- **Pozadinski rad** - radi i kada aplikacija nije u fokusu
- **Periodična provera** - svakog minuta
- **Efikasnost** - Handler sa postDelayed
- **Lifecycle** - START_STICKY za automatski restart

### Ključna logika:
```java
// VEZBA 5: Pokretanje periodične provere
private void startPeriodicCheck() {
    checkRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                checkInternetConnection();
                
                // VEZBA 5: Zakazuj sledeću proveru
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }
    };
    
    // VEZBA 5: Prva provera odmah
    handler.post(checkRunnable);
}
```

### Provera konekcije:
```java
// VEZBA 5: Provera da li ima internet konekcije
private boolean isInternetConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    if (cm != null) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    return false;
}
```

## 4. BROADCAST RECEIVER - KOMUNIKACIJA

### Šta je InternetStatusReceiver?
```java
/**
 * VEZBA 5: Broadcast Receiver za praćenje statusa internet konekcije
 * Prima broadcast-ove od InternetCheckService i prikazuje notifikacije
 */
public class InternetStatusReceiver extends BroadcastReceiver {
    
    private static final String TAG = "InternetStatusReceiver";
    private NotificationHelper notificationHelper;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("com.example.mobilnevezbe.INTERNET_STATUS")) {
                handleInternetStatusChange(context, intent);
            }
        }
    }
}
```

### Zašto Broadcast Receiver?
- **Dekoupling** - servis i UI su odvojeni
- **Flexibilnost** - više komponenti može slušati
- **Efikasnost** - samo jedna instanca za sve
- **Reusability** - može se koristiti u drugim delovima aplikacije

### Obrada broadcast-a:
```java
// VEZBA 5: Obrada promene statusa internet konekcije
private void handleInternetStatusChange(Context context, Intent intent) {
    boolean isConnected = intent.getBooleanExtra("is_connected", false);
    long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
    
    // VEZBA 5: Inicijalizuj NotificationHelper ako je potrebno
    if (notificationHelper == null) {
        notificationHelper = new NotificationHelper(context);
    }
    
    if (isConnected) {
        // VEZBA 5: Internet je vraćen - ukloni notifikaciju
        notificationHelper.hideInternetOfflineNotification();
    } else {
        // VEZBA 5: Internet je izgubljen - prikaži notifikaciju
        notificationHelper.showInternetOfflineNotification();
    }
}
```

## 5. NOTIFIKACIJA O INTERNET STATUSU

### Kreiranje offline notifikacije:
```java
// VEZBA 5: Notifikacija o offline statusu
public void showInternetOfflineNotification() {
    // VEZBA 5: Intent za akciju "Podešavanja"
    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    
    PendingIntent settingsPendingIntent = PendingIntent.getActivity(
        context, 1, settingsIntent, 
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    
    // VEZBA 5: Kreiranje notifikacije
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_INTERNET_STATUS)
        .setSmallIcon(android.R.drawable.stat_notify_error) // VEZBA 5: Ikona za grešku
        .setContentTitle("Nema internet konekcije")
        .setContentText("Proverite internet konekciju i pokušajte ponovo")
        .setStyle(new NotificationCompat.BigTextStyle()
            .bigText("Aplikacija nije povezana na internet. Kliknite da otvorite podešavanja i uključite Wi-Fi ili mobilne podatke."))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(false) // VEZBA 5: Ne uklanjaj automatski
        .addAction(android.R.drawable.ic_menu_preferences, "Podešavanja", settingsPendingIntent) // VEZBA 5: Akcija dugme
        .setOngoing(true); // VEZBA 5: Ongoing notifikacija
    
    // VEZBA 5: Prikaz notifikacije
    notificationManager.notify(NOTIFICATION_INTERNET_OFFLINE, builder.build());
}
```

### Funkcionalnosti:
- **Error ikona** - `stat_notify_error`
- **Akcija dugme** - "Podešavanja" otvara Wi-Fi podešavanja
- **Ongoing notifikacija** - ne može se obrisati
- **AutoCancel false** - ostaje dok se konekcija ne vrati

## 6. INTEGRACIJA U APLIKACIJU

### RegisterScreenActivity - notifikacija pri registraciji:
```java
// VEZBA 5: Pošalji notifikaciju o novom korisniku
if (notificationHelper.areNotificationsEnabled()) {
    notificationHelper.showUserRegisteredNotification(name);
    Log.d(TAG, "Notification sent for new user: " + name);
} else {
    Log.w(TAG, "Notifications are not enabled");
}
```

### HomeScreenActivity - pokretanje servisa:
```java
// VEZBA 5: Pokreni servis za proveru interneta
InternetCheckService.startService(this);

// VEZBA 5: Registruj receiver za internet status
InternetStatusReceiver.register(this);

// VEZBA 5: Zaustavljanje servisa i receiver-a
@Override
protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy called");
    
    // VEZBA 5: Zaustavi servis
    InternetCheckService.stopService(this);
    
    // VEZBA 5: Unregistruj receiver
    InternetStatusReceiver.unregister(this);
}
```

## 7. ANDROIDMANIFEST - PERMISIJE I KOMPONENTE

### Potrebne permisije:
```xml
<!-- VEZBA 5: Network state permission za proveru konekcije -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- VEZBA 5: Notification permission za Android 13+ -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- VEZBA 5: Wake lock permission za servis u pozadini -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Registracija komponenti:
```xml
<!-- VEZBA 5: Internet Check Service -->
<service
    android:name=".InternetCheckService"
    android:enabled="true"
    android:exported="false" />

<!-- VEZBA 5: Internet Status Receiver -->
<receiver
    android:name=".InternetStatusReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="com.example.mobilnevezbe.INTERNET_STATUS" />
    </intent-filter>
</receiver>
```

## 8. ISTOVREMENI PRIKAZ NOTIFIKACIJA

### Različiti ID-i:
```java
// VEZBA 5: Notifikacija ID-i
public static final int NOTIFICATION_USER_REGISTERED = 1001;
public static final int NOTIFICATION_INTERNET_OFFLINE = 1002;
```

### AutoCancel kontrola:
```java
// VEZBA 5: Ne uklanjaj automatski - obe notifikacije ostaju
.setAutoCancel(false)
.setOngoing(true) // Samo za internet notifikaciju
```

### Različiti kanali:
```java
// VEZBA 5: Različiti kanali za različite tipove
public static final String CHANNEL_USER_REGISTRATION = "user_registration_channel";
public static final String CHANNEL_INTERNET_STATUS = "internet_status_channel";
```

## 9. FONT, IKONICA I STILOVI

### Font i stilovi:
```java
// VEZBA 5: BigTextStyle za prošireni tekst
.setStyle(new NotificationCompat.BigTextStyle()
    .bigText("Novi korisnik " + userName + " se upravo registrovao u aplikaciji. Kliknite da vidite detalje."))
```

### Ikone:
```java
// VEZBA 5: Različite ikone za različite tipove
.setSmallIcon(android.R.drawable.ic_dialog_email) // Za registraciju
.setSmallIcon(android.R.drawable.stat_notify_error) // Za internet grešku
```

### Akcije:
```java
// VEZBA 5: Akcija dugme sa ikonom
.addAction(android.R.drawable.ic_menu_view, "Prikaži", showPendingIntent)
.addAction(android.R.drawable.ic_menu_preferences, "Podešavanja", settingsPendingIntent)
```

## 10. BEST PRACTICES

### Servis lifecycle:
- **START_STICKY** - automatski restart
- **Handler cleanup** - removeCallbacks u onDestroy
- **Memory leak prevention** - proper cleanup

### Notifikacije:
- **Channel creation** - Android 8+ zahteva
- **Permission check** - areNotificationsEnabled()
- **PendingIntent flags** - FLAG_IMMUTABLE za sigurnost

### Broadcast komunikacija:
- **Custom actions** - jedinstvene action string-ove
- **Data passing** - putExtra za podatke
- **Error handling** - null checks

---

## ZAKLJUČAK VEZBA 4-5

Implementirali smo napredne Android funkcionalnosti sa:

### VEZBA 4:
- ✅ Toolbar navigaciju u svim screen-ovima
- ✅ Singleton UserManager za upravljanje korisnicima
- ✅ UserScreen sa sortiranjem i filtriranjem
- ✅ Registraciju sa proverom duplikata
- ✅ Login sa autentifikacijom
- ✅ Mock podatke za testiranje
- ✅ Profesionalan UI sa menu opcijama
- ✅ String resurse za internacionalizaciju

### VEZBA 5:
- ✅ Notification kanale sa custom stilovima
- ✅ Notifikacije o novim korisnicima sa akcijama
- ✅ Servis za proveru interneta svakog minuta
- ✅ Broadcast Receiver za komunikaciju
- ✅ Notifikacije o internet statusu sa podešavanja
- ✅ Istovremen prikaz obe notifikacije
- ✅ Font, ikonice i akcije za notifikacije
- ✅ Background task management
- ✅ Android 13+ permission support

Sve komponente su povezane i funkcionišu kao jedinstvena napredna Android aplikacija sa kompletnim backend sistemom i profesionalnim UI-om.
