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

/**
 * PANTALLA PRINCIPAL: Coordina la visualización de datos y la interacción del usuario.
 * @param viewModel El cerebro de la pantalla que provee los datos y procesa las acciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocheScreen(viewModel: CocheViewModel) {
    /** * OBSERVACIÓN DE ESTADOS (Patrón Observer):
     * Convertimos los flujos del ViewModel en estados de Compose.
     * Si los datos cambian en la base de datos, esta pantalla se "entera" y se redibuja sola.
     */
    val coches by viewModel.cochesState.collectAsState()
    val marcas by viewModel.marcasState.collectAsState()
    val clientes by viewModel.clientesState.collectAsState()

    // Estados para controlar la visibilidad de los diálogos (Modales)
    var showAddDialog by remember { mutableStateOf(false) }
    var cocheAEditar by remember { mutableStateOf<CocheCompleto?>(null) }

    /** * ESTRUCTURA SCAFFOLD:
     * Proporciona el diseño estándar de Android (Barra superior, Botón flotante y Contenido).
     */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Coches") },
                actions = {
                    // Acción de refresco: Sincroniza los datos locales con la API externa
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sincronizar",
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
            // FAB: Botón flotante para abrir el formulario de creación
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
            /** * LÓGICA DE CONTENIDO VACÍO:
             * Si no hay datos, mostramos un mensaje de ayuda al usuario.
             */
            if (coches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay coches. Pulsa el botón de arriba para sincronizar.")
                }
            } else {
                /** * LISTADO EFICIENTE (LazyColumn):
                 * Solo dibuja los elementos que son visibles en pantalla,
                 * optimizando el uso de memoria y batería.
                 */
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que el FAB no tape el último item
                ) {
                    items(coches) { cocheCompleto ->
                        CocheItem(
                            cocheCompleto = cocheCompleto,
                            // Conectamos las acciones del Item directamente con el ViewModel
                            onDelete = { viewModel.eliminarCoche(cocheCompleto) },
                            onUpdate = { cocheAEditar = cocheCompleto }
                        )
                    }
                }
            }
        }

        /** * GESTIÓN DE DIÁLOGOS (UI Condicional):
         * Estos componentes solo se instancian si el estado correspondiente es verdadero.
         */

        // 1. Diálogo para Añadir
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

        // 2. Diálogo para Editar (se activa al seleccionar un coche específico)
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

