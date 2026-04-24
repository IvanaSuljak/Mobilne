package com.example.kolokvijum1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM korisnici ORDER BY id DESC LIMIT 1")
    suspend fun getLastUser(): User?
}
