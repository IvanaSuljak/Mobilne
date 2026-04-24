package com.example.kolokvijum1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "korisnici")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ime: String
)
