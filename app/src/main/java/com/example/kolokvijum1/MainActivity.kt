package com.example.kolokvijum1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.kolokvijum1.data.Database
import com.example.kolokvijum1.data.UserRepository

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: Database
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("KolokvijumPrefs", Context.MODE_PRIVATE)
        database = Database(this)
        userRepository = UserRepository(database)
        
        // Inicijalno upisivanje u SharedPreferences
        if (!sharedPreferences.contains("inicijalno")) {
            sharedPreferences.edit()
                .putString("inicijalno", getString(R.string.zdravo))
                .apply()
        }

        // Postavljanje fragmenata
        if (savedInstanceState == null) {
            val firstFragment = FirstFragment()
            val secondFragment = SecondFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_top, firstFragment)
                .replace(R.id.fragment_container_bottom, secondFragment)
                .commit()
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
    
    fun getUserRepository(): UserRepository {
        return userRepository
    }
    
    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }
}
