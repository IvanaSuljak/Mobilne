package com.example.mobilnevezbe;

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
        this.registrationDate = System.currentTimeMillis(); // VEZBA 4: Vreme registracije
    }
    
    // VEZBA 4: Getter metode
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public long getRegistrationDate() {
        return registrationDate;
    }
    
    // VEZBA 4: Setter metode
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // VEZBA 4: Metoda za proveru login podataka
    public boolean checkLogin(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
    
    // VEZBA 4: Metoda za prikaz korisnika
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
    
    // VEZBA 4: Metoda za formatiranje prikaza
    public String getDisplayName() {
        return name + " (" + email + ")";
    }
}
