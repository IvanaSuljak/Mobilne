package com.example.kolokvijum1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kolokvijum1.data.AppDatabase
import com.example.kolokvijum1.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirstFragment : Fragment() {
    private lateinit var btnProveri: Button
    private lateinit var btnIspisi: Button
    private lateinit var database: AppDatabase
    private var isSecondButtonEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AppDatabase.getDatabase(requireContext())
        
        btnProveri = view.findViewById(R.id.btn_proveri)
        btnIspisi = view.findViewById(R.id.btn_ispisi)
        
        // Inicijalno onemogući drugo dugme
        btnIspisi.isEnabled = false
        
        btnProveri.setOnClickListener {
            // Promeni stanje drugog dugmeta
            isSecondButtonEnabled = !isSecondButtonEnabled
            btnIspisi.isEnabled = isSecondButtonEnabled
        }
        
        btnIspisi.setOnClickListener {
            showUserOrSharedPreferencesToast()
        }
    }
    
    private fun showUserOrSharedPreferencesToast() {
        lifecycleScope.launch {
            val lastUser = withContext(Dispatchers.IO) {
                database.userDao().getLastUser()
            }
            
            val sharedPreferences = (requireActivity() as MainActivity).getSharedPreferences()
            val initialContent = sharedPreferences.getString("inicijalno", getString(R.string.zdravo))
            
            val message = if (lastUser != null) {
                lastUser.ime
            } else {
                initialContent
            }
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            
            // Ako je sadržaj iz SharedPreferences ime entiteta, zameni sa "Zdravo!"
            if (lastUser == null && initialContent != getString(R.string.zdravo)) {
                sharedPreferences.edit()
                    .putString("inicijalno", getString(R.string.zdravo))
                    .apply()
            }
        }
    }
}
