# Kolokvijum1 - Android Aplikacija

## Opis projekta
Ovaj projekat je Android aplikacija kreirana za potrebe prvog kolokvijuma iz predmeta Mobilne aplikacije. Aplikacija demonstrira rad sa fragmentima, SharedPreferences, Room bazom podataka i Android dozvolama.

## Funkcionalnosti

### 1. Glavna aktivnost (MainActivity)
- Sadrži dva fragmenta:
  - **FirstFragment** (gornja polovina ekrana)
  - **SecondFragment** (donja polovina ekrana)
- Inicijalno upisuje "Zdravo!" u SharedPreferences pod ključem "inicijalno"

### 2. FirstFragment
- **Dugme "Proveri"** (zelene boje)
  - Klikom na ovo dugme omogućava ili onemogućava drugo dugme
- **Dugme "Ispiši"**
  - Inicijalno je onemogućeno
  - Klikom prikazuje Toast poruku sa:
    - Imenom poslednjeg sačuvanog korisnika iz baze (ako postoji)
    - Sadržajem iz SharedPreferences (ako nema korisnika u bazi)
  - Ako je prikazan sadržaj iz SharedPreferences koji nije "Zdravo!", resetuje ga na "Zdravo!"

### 3. SecondFragment
- **EditText polje** za unos imena
- **Dugme "Sačuvaj"**
  - Proverava dozvole za lokaciju
  - Ako dozvole nisu date, traži ih od korisnika
  - Ako korisnik odbije dozvole:
    - Čuva sadržaj u SharedPreferences
    - Prebacuje na SecondActivity
  - Ako su dozvole date:
    - Čuva sadržaj u Room bazu podataka

### 4. Baza podataka (SQLDelight)
- Entitet **Korisnik** sa poljem:
  - `ime` (String)
- Automatski generisan ID
- SQL upiti za ubacivanje i dohvatanje poslednjeg korisnika
- Korišćenje SQLDelight "lite" baze podataka

### 5. SecondActivity
- Prikazuje centriran tekst "Nema dozvole!"
- Aktivira se kada korisnik odbije dozvole za lokaciju

## Tehnologije
- **Kotlin** - Programski jezik
- **Android SDK** - Platforma
- **SQLDelight** - "Lite" SQL baza podataka
- **SharedPreferences** - Jednostavno čuvanje podataka
- **Fragments** - Modularizacija UI-ja
- **Coroutines** - Asinhrono programiranje
- **Material Design** - UI dizajn

## Struktura projekta

```
app/
├── src/main/
│   ├── java/com/example/kolokvijum1/
│   │   ├── MainActivity.kt
│   │   ├── SecondActivity.kt
│   │   ├── FirstFragment.kt
│   │   ├── SecondFragment.kt
│   │   └── data/
│   │       ├── Database.kt
│   │       └── UserRepository.kt
│   ├── sqldelight/com/example/kolokvijum1/data/
│   │   └── Korisnik.sq
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_second.xml
│   │   │   ├── fragment_first.xml
│   │   │   └── fragment_second.xml
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   ├── colors.xml
│   │   │   └── themes.xml
│   │   └── ...
│   └── AndroidManifest.xml
└── build.gradle
```

## Korišćene dozvole
- `ACCESS_FINE_LOCATION` - Provera lokacijskih dozvola

## Kako pokrenuti projekat
1. Otvorite projekat u Android Studiju
2. Povežite Android uređaj ili pokrenite emulator
3. Kliknite na dugme "Run" ili pritisnite `Shift + F10`

## Testiranje
- **Unit testovi**: `app/src/test/`
- **Instrumented testovi**: `app/src/androidTest/`

## Autor
Ivana Suljak
- GitHub: https://github.com/IvanaSuljak
