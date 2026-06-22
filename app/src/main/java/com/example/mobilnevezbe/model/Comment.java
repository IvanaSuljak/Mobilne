package com.example.mobilnevezbe.model;

import com.google.gson.annotations.SerializedName;

/**
 * VEZBA 7: Model klasa za KOMENTAR.
 *
 * JSON struktura (sa API-ja):
 * {
 *   "postId": 3,
 *   "id": 1,
 *   "name": "John Smith",
 *   "email": "john.smith@example.com",
 *   "body": "Great blog post! I learned a lot from it."
 * }
 */
public class Comment {

    @SerializedName("id")
    private int id;

    @SerializedName("postId")
    private int postId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("body")
    private String body;

    // Getters
    public int    getId()     { return id; }
    public int    getPostId() { return postId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getBody()   { return body; }

    @Override
    public String toString() {
        return "Komentar #" + id + "\n" +
               "Autor: " + name + "\n" +
               "Email: " + email + "\n" +
               "Tekst: " + body;
    }
}
