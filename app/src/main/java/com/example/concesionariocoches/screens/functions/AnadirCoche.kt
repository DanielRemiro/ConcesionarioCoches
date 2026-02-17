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

@Composable
fun AnadirCoche(
    marcas: List<MarcaEntity>,
    clientes: List<ClienteEntity>,
    onDismiss: () -> Unit,
    onConfirm: (CocheDto, MatriculaEntity) -> Unit
) {
    var modelo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var numMatricula by remember { mutableStateOf("") }
    var fechaMatricula by remember { mutableStateOf("") }

    // Estado para la marca (seleccionamos la primera por defecto si existe)
    var marcaSeleccionadaId by remember { mutableStateOf(marcas.firstOrNull()?.marcaId ?: 0L) }

    // Estado para los clientes interesados
    var clientesSeleccionadosIds by remember { mutableStateOf(setOf<Long>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Coche y Matrícula") },
        text = {
            // EL COLUMN AHORA TIENE SCROLL PARA EVITAR QUE SE VEA SUPERPUESTO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Datos Generales", style = MaterialTheme.typography.titleSmall)
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

                Text("Datos de Matrícula", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = numMatricula,
                    onValueChange = { numMatricula = it },
                    label = { Text("Número de Matrícula") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fechaMatricula,
                    onValueChange = { fechaMatricula = it },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // SECCIÓN DE MARCAS (1:N)
                Text("Seleccionar Marca", style = MaterialTheme.typography.titleSmall)
                marcas.forEach { marca ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (marca.marcaId == marcaSeleccionadaId),
                            onClick = { marcaSeleccionadaId = marca.marcaId }
                        )
                        Text(text = marca.nombre, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                HorizontalDivider()

                // SECCIÓN DE CLIENTES (N:M)
                Text("Clientes Interesados", style = MaterialTheme.typography.titleSmall)
                if (clientes.isEmpty()) {
                    Text("No hay clientes en la base de datos.", style = MaterialTheme.typography.bodySmall)
                }
                clientes.forEach { cliente ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                        Text(text = cliente.nombre, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val mId = (1000..9999).random().toLong()
                val matricula = MatriculaEntity(mId, numMatricula, fechaMatricula)
                val coche = CocheDto(
                    id = (1000..9999).random().toLong(),
                    modelo = modelo,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    marcaId = marcaSeleccionadaId,
                    matriculaId = mId,
                    clientesIds = clientesSeleccionadosIds.toList()
                )
                onConfirm(coche, matricula)
            }) { Text("Crear Todo") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}