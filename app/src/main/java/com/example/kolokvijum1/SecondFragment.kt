package com.example.kolokvijum1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kolokvijum1.data.UserRepository
import kotlinx.coroutines.launch

class SecondFragment : Fragment() {
    private lateinit var editTextName: EditText
    private lateinit var btnSave: Button
    private lateinit var userRepository: UserRepository
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userRepository = (requireActivity() as MainActivity).getUserRepository()
        
        editTextName = view.findViewById(R.id.edit_text_name)
        btnSave = view.findViewById(R.id.btn_save)
        
        btnSave.setOnClickListener {
            val name = editTextName.text.toString()
            if (name.isNotEmpty()) {
                checkLocationPermissionAndSave(name)
            }
        }
    }
    
    private fun checkLocationPermissionAndSave(name: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Dozvola je data, sačuvaj u bazu
                saveToDatabase(name)
            }
            else -> {
                // Zatraži dozvolu
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Dozvola je data, sačuvaj u bazu
                    val name = editTextName.text.toString()
                    if (name.isNotEmpty()) {
                        saveToDatabase(name)
                    }
                } else {
                    // Dozvola je odbijena, sačuvaj u SharedPreferences i idi na SecondActivity
                    val name = editTextName.text.toString()
                    if (name.isNotEmpty()) {
                        saveToSharedPreferencesAndNavigate(name)
                    }
                }
            }
        }
    }
    
    private fun saveToDatabase(name: String) {
        lifecycleScope.launch {
            userRepository.insertUser(name)
            // Očisti polje nakon čuvanja
            editTextName.text.clear()
        }
    }
    
    private fun saveToSharedPreferencesAndNavigate(name: String) {
        val sharedPreferences = (requireActivity() as MainActivity).getSharedPreferences()
        sharedPreferences.edit()
            .putString("inicijalno", name)
            .apply()
        
        // Pređi na SecondActivity
        val intent = Intent(requireContext(), SecondActivity::class.java)
        startActivity(intent)
    }
}
