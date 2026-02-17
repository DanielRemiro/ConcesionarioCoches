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
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.viewmodel.CocheViewModel

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
            AddCocheDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nuevo ->
                    viewModel.agregarCoche(nuevo)
                    showAddDialog = false
                }
            )
        }

        // Diálogo para Editar (Nuevo)
        cocheAEditar?.let { coche ->
            EditCocheDialog(
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


@Composable
fun AddCocheDialog(onDismiss: () -> Unit, onConfirm: (CocheDto) -> Unit) {
    // Estados para los campos del formulario
    var modelo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var marcaId by remember { mutableStateOf("") }
    var matriculaId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Coche") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo (ej: 911 Carrera)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (€)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = marcaId,
                    onValueChange = { marcaId = it },
                    label = { Text("ID de Marca (Existente)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = matriculaId,
                    onValueChange = { matriculaId = it },
                    label = { Text("ID de Matrícula (Existente)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Nota: Asegúrate de que los IDs de marca y matrícula existan en la base de datos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validamos mínimamente que los campos no estén vacíos
                    if (modelo.isNotBlank() && precio.isNotBlank()) {
                        val nuevoCoche = CocheDto(
                            id = (100..9999).random().toLong(), // ID temporal o generado por API
                            modelo = modelo,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            marcaId = marcaId.toLongOrNull() ?: 1L,
                            matriculaId = matriculaId.toLongOrNull() ?: 1L,
                            clientesIds = emptyList() // Inicialmente sin clientes
                        )
                        onConfirm(nuevoCoche)
                    }
                }
            ) {
                Text("Crear Coche")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
@Composable
fun EditCocheDialog(
    cocheCompleto: CocheCompleto,
    onDismiss: () -> Unit,
    onConfirm: (CocheDto) -> Unit
) {
    var modelo by remember { mutableStateOf(cocheCompleto.coche.modelo) }
    var precio by remember { mutableStateOf(cocheCompleto.coche.precio.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Coche") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") }
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (€)") }
                )
                Text(
                    text = "Marca: ${cocheCompleto.marca.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val dto = CocheDto(
                    id = cocheCompleto.coche.cocheId, // Mantenemos el ID original
                    modelo = modelo,
                    precio = precio.toDoubleOrNull() ?: cocheCompleto.coche.precio,
                    marcaId = cocheCompleto.coche.marcaId,
                    matriculaId = cocheCompleto.coche.matriculaId,
                    clientesIds = cocheCompleto.clientesInteresados.map { it.id }
                )
                onConfirm(dto)
            }) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}