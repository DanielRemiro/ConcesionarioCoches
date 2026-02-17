package com.example.concesionariocoches.screens.functions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity
import com.example.concesionariocoches.model.middle.CocheCompleto

@Composable
fun EditarCoche(
    cocheCompleto: CocheCompleto,
    marcasDisponibles: List<MarcaEntity>,
    clientesDisponibles: List<ClienteEntity>,
    onDismiss: () -> Unit,
    onConfirm: (CocheDto, MatriculaEntity) -> Unit
) {
    var modelo by remember { mutableStateOf(cocheCompleto.coche.modelo) }
    var precio by remember { mutableStateOf(cocheCompleto.coche.precio.toString()) }
    var marcaSeleccionadaId by remember { mutableStateOf(cocheCompleto.coche.marcaId) }
    var clientesSeleccionadosIds by remember {
        mutableStateOf(cocheCompleto.clientesInteresados.map { it.id }.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Vehículo") },
        text = {
            // EL COLUMN DEBE TENER SCROLL PARA NO SUPERPONERSE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (€)") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Text("Seleccionar Marca", style = MaterialTheme.typography.titleSmall)
                marcasDisponibles.forEach { marca ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (marca.marcaId == marcaSeleccionadaId),
                            onClick = { marcaSeleccionadaId = marca.marcaId }
                        )
                        Text(marca.nombre)
                    }
                }

                HorizontalDivider()

                Text("Clientes Interesados", style = MaterialTheme.typography.titleSmall)
                clientesDisponibles.forEach { cliente ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = clientesSeleccionadosIds.contains(cliente.id),
                            onCheckedChange = { isChecked ->
                                clientesSeleccionadosIds = if (isChecked) {
                                    clientesSeleccionadosIds + cliente.id
                                } else {
                                    clientesSeleccionadosIds - cliente.id
                                }
                            }
                        )
                        Text(cliente.nombre)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val dto = CocheDto(
                    id = cocheCompleto.coche.cocheId,
                    modelo = modelo,
                    precio = precio.toDoubleOrNull() ?: cocheCompleto.coche.precio,
                    marcaId = marcaSeleccionadaId,
                    matriculaId = cocheCompleto.coche.matriculaId,
                    clientesIds = clientesSeleccionadosIds.toList()
                )
                onConfirm(dto, cocheCompleto.matricula)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}