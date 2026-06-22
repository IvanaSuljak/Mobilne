package com.example.mobilnevezbe.network;

import com.example.mobilnevezbe.model.ApiUser;
import com.example.mobilnevezbe.model.Comment;
import com.example.mobilnevezbe.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * VEZBA 7: Retrofit interfejs — definicija svih API endpointa.
 *
 * Retrofit automatski generiše implementaciju ovog interfejsa.
 * Svaka metoda = jedan HTTP zahtev.
 *
 * Anotacije:
 *   @GET("putanja")      → HTTP GET zahtev
 *   @Path("param")       → zamenjuje {param} u URL-u
 *   Call<T>              → wrapper za asinhroni odgovor
 *   List<T>              → kada API vraća JSON niz  [...]
 *   T                    → kada API vraća JSON objekat {...}
 */
public interface ApiService {

    // GET /posts  → vraća listu svih postova
    @GET("posts")
    Call<List<Post>> getSvePosts();

    // GET /posts/1  → vraća jedan post po ID-u
    @GET("posts/{id}")
    Call<Post> getPostById(@Path("id") int id);

    // GET /comments  → vraća listu svih komentara
    @GET("comments")
    Call<List<Comment>> getSveKomentare();

    // GET /comments/2  → vraća jedan komentar po ID-u
    @GET("comments/{id}")
    Call<Comment> getKomentarById(@Path("id") int id);

    // GET /users  → vraća listu svih korisnika
    @GET("users")
    Call<List<ApiUser>> getSveKorisnike();
}
