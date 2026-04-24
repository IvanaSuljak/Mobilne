package com.example.kolokvijum1.data

import com.squareup.sqldelight.coroutines.asFlow
import com.squareup.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val database: Database) {
    
    suspend fun insertUser(name: String) {
        withContext(Dispatchers.IO) {
            database.database.korisnikQueries.insertUser(name)
        }
    }
    
    suspend fun getLastUser(): Korisnik? {
        return withContext(Dispatchers.IO) {
            database.database.korisnikQueries.selectLast().executeAsOneOrNull()
        }
    }
    
    suspend fun getAllUsers(): List<Korisnik> {
        return withContext(Dispatchers.IO) {
            database.database.korisnikQueries.selectAll().executeAsList()
        }
    }
}
