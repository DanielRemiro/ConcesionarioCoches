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
import com.example.concesionariocoches.viewmodel.Factory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val api = RetrofitClient.instance

        val repository = CocheRepository(api, database.concesionarioDao())

        val viewModel: CocheViewModel by viewModels {
            Factory(repository)
        }

        setContent {
            ConcesionarioCochesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CocheScreen(viewModel = viewModel)
                }
            }
        }
    }
}