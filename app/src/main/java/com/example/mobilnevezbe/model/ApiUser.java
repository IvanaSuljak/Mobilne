package com.example.mobilnevezbe.model;

import com.google.gson.annotations.SerializedName;

/**
 * VEZBA 7: Model klasa za KORISNIKA sa API-ja.
 * Naziv ApiUser (a ne User) da ne bi došlo do konflikta
 * sa postojećom User.java klasom koja se koristi za lokalnu bazu.
 *
 * JSON struktura (sa API-ja):
 * {
 *   "id": 1,
 *   "name": "Emily Johnson",
 *   "company": "ABC Corporation",
 *   "username": "emily_johnson",
 *   "email": "emily.johnson@abccorporation.com",
 *   "address": "123 Main St",
 *   "zip": "12345",
 *   "state": "California",
 *   "country": "USA",
 *   "phone": "+1-555-123-4567"
 * }
 */
public class ApiUser {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("company")
    private String company;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("state")
    private String state;

    @SerializedName("country")
    private String country;

    @SerializedName("phone")
    private String phone;

    // Getters
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getCompany()  { return company; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getAddress()  { return address; }
    public String getState()    { return state; }
    public String getCountry()  { return country; }
    public String getPhone()    { return phone; }

    @Override
    public String toString() {
        return name + " (@" + username + ") - " + company;
    }
}
