# ŠABLON — Android Layout XML

> Svuda gde vidiš `TODO` → zameni sa svojim ID-evima i tekstovima.
> Sve ostalo kopiraš bukvalno.

---

## Tablica — element → XML tag

| Šta zadatak kaže | XML tag | Posebno |
|---|---|---|
| TextView | `<TextView` | `android:text` |
| Button | `<Button` | `android:text` |
| ImageButton | `<ImageButton` | `android:src` (ikona) |
| ImageView | `<ImageView` | `layout_height="200dp"` (konkretna visina!) |
| Switch | `<Switch` | `android:text` (labela pored) |
| EditText | `<EditText` | `android:hint`, `android:inputType` |
| CheckBox | `<CheckBox` | `android:text` |
| RadioGroup + RadioButton | `<RadioGroup>` + `<RadioButton` | RadioButton-i idu unutar RadioGroup |
| ListView | `<ListView` | ne treba `android:text` |
| ScrollView | `<ScrollView` | može imati samo JEDNO direktno dijete |
| Spinner | `<Spinner` | popunjava se iz koda, ne XML-om |

---

## "Jedan ispod drugog" → LinearLayout vertical

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- OVDE STAVLJAŠ ELEMENTE JEDAN ISPOD DRUGOG -->

</LinearLayout>
```

---

## Blokovi — kopijaš onaj koji treba, zameniš TODO

### TextView
```xml
<TextView
    android:id="@+id/TODO_textView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="TODO_pocetni_tekst"
    android:textSize="16sp"
    android:layout_marginBottom="12dp"/>
```

### Button
```xml
<Button
    android:id="@+id/TODO_button"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="TODO_tekst_dugmeta"
    android:layout_marginBottom="12dp"/>
```

### ImageButton
```xml
<ImageButton
    android:id="@+id/TODO_imageButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@android:drawable/ic_menu_camera"
    android:contentDescription="TODO_opis"
    android:layout_marginBottom="12dp"/>
```

### ImageView
```xml
<ImageView
    android:id="@+id/TODO_imageView"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:scaleType="centerCrop"
    android:background="#EEEEEE"
    android:contentDescription="TODO_opis"
    android:layout_marginBottom="12dp"/>
```

### Switch
```xml
<Switch
    android:id="@+id/TODO_switch"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="TODO_labela"
    android:layout_marginBottom="12dp"/>
```

### EditText
```xml
<EditText
    android:id="@+id/TODO_editText"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="TODO_placeholder_tekst"
    android:inputType="text"
    android:layout_marginBottom="12dp"/>
```
> `inputType` opcije: `text`, `textPassword`, `textEmailAddress`, `phone`, `number`

### CheckBox
```xml
<CheckBox
    android:id="@+id/TODO_checkBox"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="TODO_labela"
    android:layout_marginBottom="12dp"/>
```

### Spinner
```xml
<Spinner
    android:id="@+id/TODO_spinner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="12dp"/>
```

### ListView
```xml
<ListView
    android:id="@+id/TODO_listView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"/>
```
> `layout_weight="1"` + `layout_height="0dp"` = zauzima sav slobodni prostor

### RadioGroup sa RadioButton-ima
```xml
<RadioGroup
    android:id="@+id/TODO_radioGroup"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:layout_marginBottom="12dp">

    <RadioButton
        android:id="@+id/TODO_radio1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="TODO_opcija1"/>

    <RadioButton
        android:id="@+id/TODO_radio2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="TODO_opcija2"/>

</RadioGroup>
```

### ScrollView (kada ima mnogo elemenata)
```xml
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- ELEMENTI OVDE -->

    </LinearLayout>

</ScrollView>
```

---

## Gotov layout sa svih 5 elemenata (primer za kolokvijum)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/TODO_textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Čekanje..."
        android:textSize="16sp"
        android:layout_marginBottom="12dp"/>

    <ImageButton
        android:id="@+id/TODO_imageButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@android:drawable/ic_menu_camera"
        android:contentDescription="Kamera"
        android:layout_marginBottom="12dp"/>

    <ImageView
        android:id="@+id/TODO_imageView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:background="#EEEEEE"
        android:layout_marginBottom="12dp"/>

    <Switch
        android:id="@+id/TODO_switch"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="TODO_labela"
        android:layout_marginBottom="12dp"/>

    <Button
        android:id="@+id/TODO_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="TODO_tekst"/>

</LinearLayout>
```

---

## Atributi koji se UVEK ponavljaju

| Atribut | Vrednost | Kada |
|---|---|---|
| `layout_width` | `match_parent` | skoro uvek (zauzmi celu širinu) |
| `layout_width` | `wrap_content` | ImageButton, Switch, CheckBox |
| `layout_height` | `wrap_content` | skoro uvek |
| `layout_height` | `200dp` | ImageView (mora konkretna visina!) |
| `layout_height` | `0dp` + `layout_weight="1"` | ListView (da zauzme ostatak ekrana) |
| `layout_marginBottom` | `12dp` | razmak između elemenata |
| `padding` | `16dp` | na korenskom LinearLayout-u |

---

## Česta inputType vrednosti za EditText

| `inputType=` | Za šta |
|---|---|
| `text` | obično tekstualno polje |
| `textPassword` | lozinka (tačkice) |
| `textEmailAddress` | email (prikazuje @ na tastaturi) |
| `phone` | broj telefona |
| `number` | samo brojevi |
| `numberDecimal` | decimalni broj |
