# VODIČ — kako se ponašati na dan kolokvijuma

> **Otvori ovaj fajl kad dobiješ zadatak.** Ne učiš sve napamet — znaš **gde da gledaš**.

**Povezani fajlovi:**
- Brzi pregled: `VODIC_KOLOKVIJUM_JEDNOSTAVNO.md`
- Celo rešenje (primer): `PRIprema_KOLOKVIJUM_2_KOMPLETNO.md`
- Mapa šablona: `SABLON_MASTER_VODIC.md`

---

## Korak 0 — kad dobiješ zadatak (5 minuta, bez kucanja)

1. **Pročitaj CEo tekst** — ne kreni odmah u Android Studio.
2. **Podvuci reči** u tekstu:

| Ako vidiš... | Znači da treba... |
|--------------|-------------------|
| TextView, Button, Switch, layout | `activity_main.xml` |
| lat/lng, GPS, lokacija | Gradle location + Manifest GPS |
| kamera, slika, ImageView | Manifest CAMERA + FileProvider + kamera kod |
| senzor, žiroskop, akcelerometar | `SensorEventListener` u MainActivity |
| SQLite, baza, obriši, upiši | `DatabaseHelper` |
| Retrofit, HTTP, API, JSON, sajt | `Post` + `network` + Gradle retrofit |
| Switch ON/OFF, prvi put | Switch listener + `vecFetchovano` |
| SharedPreferences, sačuvaj | prefs u Switch OFF |
| kontakti | `READ_CONTACTS` + query |
| notifikacija | `NotificationManager` |

3. Na papir napiši **listu fajlova** koje treba da dirneš (obično 8–10 fajlova).

---

## Korak 1 — redosled rada (uvek ovim redom)

```
1. Novi projekat (Empty Views Activity)
2. activity_main.xml     ← elementi + ID-evi
3. build.gradle + Sync   ← samo ono što zadatak traži
4. AndroidManifest       ← dozvole + FileProvider ako ima kamera
5. file_paths.xml        ← samo ako ima kamera
6. Post, DatabaseHelper  ← samo ako ima baza/API
7. ApiService, RetrofitClient ← samo ako ima Retrofit
8. MainActivity          ← sve ostalo, JEDAN fajl
9. Run + testiraj
```

**Ne preskači Sync** posle Gradle izmene.

---

## Korak 2 — kako radiš MainActivity (najvažnije)

Na kolokvijumu skoro uvek **samo MainActivity**.

```
1. Otvori POSTOJEĆI MainActivity (ne pravi MapsActivity, RetrofitActivity...)
2. Na vrh: fields
3. Launcher za kameru — VAN onCreate
4. Jedan onCreate: findViewById → init → listeneri
5. Metode ispod: GPS, kamera, kontakt...
6. Override: onResume, onPause, onSensorChanged, onRequestPermissionsResult
```

**Ignoriši `TODO_Activity`** u šablonima — to je za vežbe sa više ekrana.

---

## Korak 3 — kad zapneš, pitaj se samo ovo

> **„Da li ovo ide u XML, Gradle, Manifest ili MainActivity?“**

| Problem | Gde gledaš |
|---------|------------|
| Nema elementa na ekranu | XML + findViewById ID |
| App se ne build-uje | Gradle Sync, importi, paket |
| GPS/kamera/internet ne rade | Manifest dozvole |
| Podaci sa sajta | Retrofit fajlovi + INTERNET |
| Logika klika/Switch-a | MainActivity listener |

---

## Korak 4 — koje fajlove otvoriš (sa GitHub-a)

| Situacija | Otvori |
|-----------|--------|
| Ne znaš odakle | `SABLON_MASTER_VODIC.md` |
| Brzo, panika | `VODIC_KOLOKVIJUM_JEDNOSTAVNO.md` |
| Isti tip kao kolokvijum 2 | `PRIprema_KOLOKVIJUM_2_KOMPLETNO.md` |
| Samo layout | `SABLON_Layout_XML.md` |
| Samo Retrofit | `SABLON_Retrofit_HTTP_Zahtevi.md` |
| Layout nije „ispod drugog“ | `SABLON_Layout_XML.md` → sekcija „Ako NE piše jedno ispod drugog“ |
| Šta kopiraš vs menjaš | `SABLON_UNIVERZALNO_VS_KONKRETNO.md` |

**Ne čitaj sve odjednom** — otvori **jedan** fajl za **jedan** deo zadatka.

---

## Korak 5 — testiraj dok radiš (ne na kraju)

| Uradio si... | Test |
|--------------|------|
| Layout | App se pokrene, vidiš elemente |
| GPS | TextView pokaže lat/lng |
| Kamera | Slika + Toast |
| Retrofit | privremeni Toast „Postova: 10“ |
| Switch | ON → fetch; drugi put → Toast title |
| Dugme | briše; prazno → notifikacija |

---

## Korak 6 — šta NE radiš na kolokvijumu

- ❌ Ne lepiš **ceo blok** šablona (npr. ceo KORAK 3 Senzori)
- ❌ Ne praviš **dva `onCreate()`**
- ❌ Ne praviš **novu Activity** ako nije eksplicitno traženo
- ❌ Ne stavljaš **metode unutar `{}` lambde**
- ❌ Ne menjaš **Gradle u root** fajlu — samo `build.gradle (Module :app)`

---

## Ako zadatak NIJE identičan kolokvijumu 2

Isti princip:

1. **UI** → layout (vertical ako ne piše drugačije)
2. **Dozvole** → Manifest
3. **Biblioteke** → Gradle
4. **Podaci** → model + baza + Retrofit (ako treba)
5. **Ponašanje** → MainActivity listeneri

Menjaš **ID-eve, URL, polja modela, tekst Toast-a** — obrazac ostaje.

---

## Jedna rečenica za dan kolokvijuma

**Pročitaj → podvuci reči → otvori odgovarajući šablon → jedan fajl po jedan → sve u MainActivity → testiraj.**

---

## Brza mapa — reč u zadatku → šablon

| Reč u zadatku | Šablon |
|---------------|--------|
| layout, TextView, Button | `SABLON_Layout_XML.md` |
| GPS, lat, lng | `SABLON_Lokacija_GoogleMaps.md` (KORAK 1b) |
| kamera, senzor | `SABLON_Senzori_Kamera.md` |
| baza, SQLite, prefs, kontakti | `SABLON_SQLite_...md` |
| Retrofit, HTTP, API | `SABLON_Retrofit_...md` |
| ceo kolokvijum 2 | `SABLON_KOLOKVIJUM_2.md` |

---

Pred kolokvijum: `git pull` → otvori ovaj fajl pored Android Studija.
