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

// ... (mismos imports)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocheScreen(viewModel: CocheViewModel) {
    val coches by viewModel.cochesState.collectAsState()
    val marcas by viewModel.marcasState.collectAsState()
    val clientes by viewModel.clientesState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var cocheAEditar by remember { mutableStateOf<CocheCompleto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Concesionario") },
                // Esto asegura que la barra superior sea sólida y no transparente
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Coche")
            }
        }
    ) { padding ->
        // USAMOS EL PADDING DEL SCAFFOLD Y FILLMAXSIZE
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (coches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay coches disponibles.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio extra abajo para el FAB
                ) {
                    items(coches) { cocheCompleto ->
                        CocheItem(
                            cocheCompleto = cocheCompleto,
                            onDelete = { viewModel.eliminarCoche(cocheCompleto) },
                            onUpdate = { cocheAEditar = cocheCompleto }
                        )
                    }
                }
            }
        }

        // Diálogos (están fuera del flujo normal, no se superponen)
        if (showAddDialog) {
            AnadirCoche(
                marcas = marcas,
                clientes = clientes,
                onDismiss = { showAddDialog = false },
                onConfirm = { nuevoDto, nuevaMatricula ->
                    viewModel.agregarCoche(nuevoDto, nuevaMatricula)
                    showAddDialog = false
                }
            )
        }

        cocheAEditar?.let { coche ->
            EditarCoche(
                cocheCompleto = coche,
                marcasDisponibles = marcas,
                clientesDisponibles = clientes,
                onDismiss = { cocheAEditar = null },
                onConfirm = { actualizadoDto, matriculaEditada ->
                    viewModel.actualizarCoche(actualizadoDto, matriculaEditada)
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
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onUpdate) {
                    Icon(Icons.Default.Create, contentDescription = "Editar", tint = Color(0xFFFBC02D))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                }
            }
        }
    }
}