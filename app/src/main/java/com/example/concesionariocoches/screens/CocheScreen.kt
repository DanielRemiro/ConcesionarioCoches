package com.example.concesionariocoches.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.viewmodel.CocheViewModel
import com.example.concesionariocoches.screens.functions.*

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
                title = { Text("Gestión de Coches") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sincronizar con API",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Coche")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (coches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay coches. Pulsa el botón de arriba para sincronizar.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
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

