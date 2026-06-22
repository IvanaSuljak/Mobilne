package com.example.mobilnevezbe;

/**
 * VEZBA 4/6: Model klasa korisnika.
 * VEZBA 6: Dodate uloga i id polja za podršku SQLite baze i SharedPreferences.
 */
public class User {

    private int    id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String uloga;            // "vozac" | "putnik" | "administrator"
    private long   registrationDate;

    // Konstruktor za NOVU registraciju (bez ID-a, default uloga = putnik)
    public User(String name, String email, String phone, String password) {
        this.name             = name;
        this.email            = email;
        this.phone            = phone;
        this.password         = password;
        this.uloga            = "putnik";
        this.registrationDate = System.currentTimeMillis();
    }

    // Konstruktor za NOVU registraciju SA izabranom ulogom
    public User(String name, String email, String phone, String password, String uloga) {
        this.name             = name;
        this.email            = email;
        this.phone            = phone;
        this.password         = password;
        this.uloga            = uloga;
        this.registrationDate = System.currentTimeMillis();
    }

    // Konstruktor za ČITANJE IZ BAZE (sa svim poljima uključujući ID)
    public User(int id, String name, String email, String phone, String password, String uloga) {
        this.id               = id;
        this.name             = name;
        this.email            = email;
        this.phone            = phone;
        this.password         = password;
        this.uloga            = uloga;
        this.registrationDate = System.currentTimeMillis();
    }

    // Getters
    public int    getId()               { return id; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getPhone()            { return phone; }
    public String getPassword()         { return password; }
    public String getUloga()            { return uloga; }
    public long   getRegistrationDate() { return registrationDate; }

    // Setters
    public void setId(int id)                         { this.id = id; }
    public void setName(String name)                  { this.name = name; }
    public void setEmail(String email)                { this.email = email; }
    public void setPhone(String phone)                { this.phone = phone; }
    public void setPassword(String password)          { this.password = password; }
    public void setUloga(String uloga)                { this.uloga = uloga; }
    public void setRegistrationDate(long date)        { this.registrationDate = date; }

    // Provjera login podataka (čuva se za backwards compatibility)
    public boolean checkLogin(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public String getDisplayName() {
        return name + " (" + email + ")";
    }

    @Override
    public String toString() {
        return name + " [" + uloga + "] - " + email;
    }
}
