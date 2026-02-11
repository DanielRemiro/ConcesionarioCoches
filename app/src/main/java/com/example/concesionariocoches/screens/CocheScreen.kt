package com.example.concesionariocoches.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.viewmodel.CocheViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocheScreen(viewModel: CocheViewModel) {
    val coches by viewModel.cochesState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Coches") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Coche")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (coches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay coches disponibles. Verifica la API.")
                }
            } else {
                LazyColumn {
                    items(coches) { cocheCompleto ->
                        CocheItem(
                            cocheCompleto = cocheCompleto,
                            onDelete = { viewModel.eliminarCoche(cocheCompleto.coche) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddCocheDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nuevoCocheDto ->
                    viewModel.agregarCoche(nuevoCocheDto)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CocheItem(cocheCompleto: CocheCompleto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${cocheCompleto.marca.nombre} ${cocheCompleto.coche.modelo}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Motor: ${cocheCompleto.motor.combustible} - ${cocheCompleto.motor.potencia}")
            Text(text = "Precio: ${cocheCompleto.coche.precio} €", color = Color.Green)

            IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddCocheDialog(onDismiss: () -> Unit, onConfirm: (CocheDto) -> Unit) {
    // Ejemplo simplificado. En una app real usarías TextField para cada campo.
    // Aquí hardcodeamos valores para simular la creación según los DTOs que tienes.
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Coche") },
        text = { Text("¿Crear un coche de prueba (Ford Fiesta)?") },
        confirmButton = {
            Button(onClick = {
                val nuevo = CocheDto(
                    id = 0, // La API o la BD deberían generar el ID real
                    modelo = "Fiesta",
                    color = "Azul",
                    precio = 15000.0,
                    descripcion = "Nuevo modelo",
                    marcaId = 1, // Asumiendo que existen
                    motorId = 1,
                    // Deberías rellenar marca/motor completos si la API lo requiere para crear las entidades hijas
                )
                onConfirm(nuevo)
            }) {
                Text("Crear")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}