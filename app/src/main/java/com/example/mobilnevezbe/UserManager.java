package com.example.mobilnevezbe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VEZBA 4: Singleton klasa za upravljanje korisnicima
 * Čuva sve korisnike u memoriji i pruža metode za manipulaciju
 */
public class UserManager {
    private static UserManager instance;
    private List<User> users;
    
    // VEZBA 4: Privatni konstruktor za singleton pattern
    private UserManager() {
        users = new ArrayList<>();
        // VEZBA 4: Dodajemo nekoliko test korisnika za demonstraciju
        addMockUsers();
    }
    
    // VEZBA 4: Metoda za dobijanje instance
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    // VEZBA 4: Metoda za dodavanje novog korisnika
    public boolean addUser(User user) {
        // VEZBA 4: Provera da li email već postoji
        if (getUserByEmail(user.getEmail()) != null) {
            return false; // Email već postoji
        }
        users.add(user);
        return true;
    }
    
    // VEZBA 4: Metoda za proveru login podataka
    public User loginUser(String email, String password) {
        for (User user : users) {
            if (user.checkLogin(email, password)) {
                return user;
            }
        }
        return null; // Korisnik nije pronađen
    }
    
    // VEZBA 4: Metoda za dobijanje korisnika po email-u
    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    // VEZBA 4: Metoda za dobijanje svih korisnika
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    // VEZBA 4: Metoda za sortiranje korisnika po imenu
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
    
    // VEZBA 4: Metoda za sortiranje korisnika po email-u
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
    
    // VEZBA 4: Metoda za sortiranje korisnika po datumu registracije
    public List<User> sortUsersByRegistrationDate() {
        List<User> sortedUsers = new ArrayList<>(users);
        Collections.sort(sortedUsers, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Long.compare(u1.getRegistrationDate(), u2.getRegistrationDate());
            }
        });
        return sortedUsers;
    }
    
    // VEZBA 4: Metoda za filtriranje korisnika po imenu
    public List<User> filterUsersByName(String searchTerm) {
        return users.stream()
                .filter(user -> user.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // VEZBA 4: Metoda za filtriranje korisnika po email-u
    public List<User> filterUsersByEmail(String searchTerm) {
        return users.stream()
                .filter(user -> user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // VEZBA 4: Metoda za filtriranje korisnika po telefonu
    public List<User> filterUsersByPhone(String searchTerm) {
        return users.stream()
                .filter(user -> user.getPhone().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    // VEZBA 4: Metoda za dobijanje broja korisnika
    public int getUserCount() {
        return users.size();
    }
    
    // VEZBA 4: Metoda za brisanje svih korisnika
    public void clearAllUsers() {
        users.clear();
    }
    
    // VEZBA 4: Metoda za dodavanje mock korisnika
    private void addMockUsers() {
        users.add(new User("Marko Marković", "marko@example.com", "064123456", "marko123"));
        users.add(new User("Jelena Jovanović", "jelena@example.com", "065987654", "jelena123"));
        users.add(new User("Petar Petrović", "petar@example.com", "063456789", "petar123"));
        users.add(new User("Ana Anić", "ana@example.com", "062111222", "ana123"));
        users.add(new User("Miloš Milošević", "milos@example.com", "061333444", "milos123"));
    }
}
