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

/**
 * ACTIVITY PRINCIPAL: Punto de entrada al proceso de la aplicación en Android.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZACIÓN DE DATOS:
        // Obtenemos la instancia única (Singleton) de la base de datos local.
        val database = AppDatabase.getDatabase(this)

        // Obtenemos la instancia del cliente de red para conectar con la API.
        val api = RetrofitClient.instance

        // 2. CREACIÓN DEL REPOSITORIO:
        // Unimos la API y el DAO de la base de datos en un solo mediador.
        val repository = CocheRepository(api, database.concesionarioDao())

        /**
         * 3. INYECCIÓN DEL VIEWMODEL:
         * Utilizamos la 'Factory' personalizada para pasarle el repositorio al ViewModel.
         * 'by viewModels' asegura que el ViewModel sobreviva a cambios de pantalla.
         */
        val viewModel: CocheViewModel by viewModels {
            Factory(repository)
        }

        /**
         * 4. DEFINICIÓN DE LA UI (Jetpack Compose):
         * Aquí pasamos del mundo de la lógica al mundo visual.
         */
        setContent {
            // Aplicamos el tema visual personalizado de la aplicación.
            ConcesionarioCochesTheme {
                // Surface es el contenedor base que respeta el color de fondo del tema.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // LLAMADA A LA PANTALLA PRINCIPAL:
                    // Le entregamos el ViewModel ya configurado con todos sus datos.
                    CocheScreen(viewModel = viewModel)
                }
            }
        }
    }
}