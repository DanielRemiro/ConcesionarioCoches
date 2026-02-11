package com.example.concesionariocoches

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.concesionariocoches.api.RetrofitClient
import com.example.concesionariocoches.data.database.AppDatabase
import com.example.concesionariocoches.repository.CocheRepository
import com.example.concesionariocoches.screens.CocheScreen
import com.example.concesionariocoches.ui.theme.ConcesionarioCochesTheme
import com.example.concesionariocoches.viewmodel.CocheViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de dependencias (Manual DI)
        val database = AppDatabase.getDatabase(this)
        val repository = CocheRepository(RetrofitClient.instance, database.concesionarioDao())
        val viewModel = CocheViewModel.Factory(repository).create(CocheViewModel::class.java)

        setContent {
            ConcesionarioCochesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CocheScreen(viewModel = viewModel)
                }
            }
        }
    }
}