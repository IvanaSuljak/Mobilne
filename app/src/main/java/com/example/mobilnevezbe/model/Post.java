package com.example.mobilnevezbe.model;

import com.google.gson.annotations.SerializedName;

/**
 * VEZBA 7: Model klasa za POST.
 *
 * @SerializedName("json_kljuc") - govori Gson-u koji JSON ključ
 * da mapira na koje Java polje.
 * Ako je ime polja isto kao JSON ključ, anotacija nije obavezna.
 *
 * JSON struktura (sa API-ja):
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "Introduction to Artificial Intelligence",
 *   "body": "Learn the basics...",
 *   "link": "https://example.com/article1",
 *   "comment_count": 8
 * }
 */
public class Post {

    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("link")
    private String link;

    @SerializedName("comment_count")
    private int commentCount;

    // Getters
    public int    getId()           { return id; }
    public int    getUserId()       { return userId; }
    public String getTitle()        { return title; }
    public String getBody()         { return body; }
    public String getLink()         { return link; }
    public int    getCommentCount() { return commentCount; }

    @Override
    public String toString() {
        return "Post #" + id + "\n" +
               "Naslov: " + title + "\n" +
               "Sadržaj: " + body + "\n" +
               "Komentara: " + commentCount;
    }
}
