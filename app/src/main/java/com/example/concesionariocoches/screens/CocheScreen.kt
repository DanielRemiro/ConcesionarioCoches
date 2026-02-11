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
            Text(
                text = "${cocheCompleto.marca.nombre} ${cocheCompleto.coche.modelo}",
                style = MaterialTheme.typography.titleLarge
            )
            // CAMBIO: Ahora mostramos Matrícula en lugar de Motor
            Text(text = "Matrícula: ${cocheCompleto.matricula.numero}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${cocheCompleto.matricula.fechaMatriculacion}", style = MaterialTheme.typography.bodySmall)

            Text(
                text = "Precio: ${cocheCompleto.coche.precio} €",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            // Mostrar clientes interesados si existen
            if (cocheCompleto.clientesInteresados.isNotEmpty()) {
                Text(
                    text = "Interesados: ${cocheCompleto.clientesInteresados.joinToString { it.nombre }}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddCocheDialog(onDismiss: () -> Unit, onConfirm: (CocheDto) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Coche") },
        text = { Text("¿Crear un coche de prueba (Porsche 911)?") },
        confirmButton = {
            Button(onClick = {
                // CAMBIO: Ajustado a la nueva estructura del CocheDto
                val nuevo = CocheDto(
                    id = (21..1000).random().toLong(), // ID temporal
                    modelo = "911 Carrera",
                    precio = 145000.0,
                    marcaId = 1,      // ID de Porsche según tu JSON
                    matriculaId = 1,  // Debe ser un ID de matrícula existente o manejarse en backend
                    clientesIds = emptyList()
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