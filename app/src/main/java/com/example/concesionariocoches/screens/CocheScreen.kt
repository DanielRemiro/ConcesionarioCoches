package com.example.concesionariocoches.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.viewmodel.CocheViewModel
import com.example.concesionariocoches.screens.functions.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocheScreen(viewModel: CocheViewModel) {
    val coches by viewModel.cochesState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    // Estado para el coche que se va a editar
    var cocheAEditar by remember { mutableStateOf<CocheCompleto?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Coches") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Coche")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (coches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay coches disponibles.")
                }
            } else {
                LazyColumn {
                    items(coches) { cocheCompleto ->
                        CocheItem(
                            cocheCompleto = cocheCompleto,
                            onDelete = { viewModel.eliminarCoche(cocheCompleto.coche) },
                            // Al pulsar editar, guardamos el coche en el estado
                            onUpdate = { cocheAEditar = cocheCompleto }
                        )
                    }
                }
            }
        }

        // Diálogo para Añadir
        if (showAddDialog) {
            AnadirCoche(
                onDismiss = { showAddDialog = false },
                onConfirm = { nuevo ->
                    viewModel.agregarCoche(nuevo)
                    showAddDialog = false
                }
            )
        }

        // Diálogo para Editar (Nuevo)
        cocheAEditar?.let { coche ->
            EditarCoche (
                cocheCompleto = coche,
                onDismiss = { cocheAEditar = null },
                onConfirm = { cocheActualizado ->
                    viewModel.actualizarCoche(cocheActualizado)
                    cocheAEditar = null
                }
            )
        }
    }
}

@Composable
fun CocheItem(cocheCompleto: CocheCompleto, onDelete: () -> Unit, onUpdate: () -> Unit) {
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
            Text(text = "Matrícula: ${cocheCompleto.matricula.numero}", style = MaterialTheme.typography.bodyMedium)

            Text(
                text = "Precio: ${cocheCompleto.coche.precio} €",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            if (cocheCompleto.clientesInteresados.isNotEmpty()) {
                Text(
                    text = "Interesados: ${cocheCompleto.clientesInteresados.joinToString { it.nombre }}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                // Botón de Actualizar (Cambiado icono y color)
                IconButton(onClick = onUpdate) {
                    Icon(Icons.Default.Create, contentDescription = "Editar", tint = Color(0xFFFBC02D))
                }
                // Botón de Borrar
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                }
            }
        }
    }
}