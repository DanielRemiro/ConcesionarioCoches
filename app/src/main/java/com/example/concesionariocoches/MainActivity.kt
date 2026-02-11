package com.example.concesionariocoches

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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

        // 1. Inicialización de la base de datos y API
        val database = AppDatabase.getDatabase(this)
        val api = RetrofitClient.instance

        // 2. Creación del repositorio
        val repository = CocheRepository(api, database.concesionarioDao())

        // 3. Instanciación del ViewModel usando el Factory
        // Usamos 'viewModels' con el factory para que el ViewModel sobreviva a cambios de configuración
        val viewModel: CocheViewModel by viewModels {
            CocheViewModel.Factory(repository)
        }

        setContent {
            ConcesionarioCochesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. Inyección del ViewModel en la pantalla principal
                    CocheScreen(viewModel = viewModel)
                }
            }
        }
    }
}