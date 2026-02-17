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

/**
 * COMPONENTE DE EDICIÓN: Recibe el objeto 'CocheCompleto' con los datos actuales
 * y las listas de opciones disponibles (marcas y clientes).
 */
@Composable
fun EditarCoche(
    cocheCompleto: CocheCompleto,
    marcasDisponibles: List<MarcaEntity>,
    clientesDisponibles: List<ClienteEntity>,
    onDismiss: () -> Unit,
    onConfirm: (CocheDto, MatriculaEntity) -> Unit
) {
    /** * INICIALIZACIÓN CON DATOS EXISTENTES:
     * A diferencia de 'AnadirCoche', aquí los estados se inicializan con los
     * valores que ya tiene el coche en la base de datos.
     */
    var modelo by remember { mutableStateOf(cocheCompleto.coche.modelo) }
    var precio by remember { mutableStateOf(cocheCompleto.coche.precio.toString()) }
    var marcaSeleccionadaId by remember { mutableStateOf(cocheCompleto.coche.marcaId) }

    // Convertimos la lista de clientes interesados en un Set de IDs para facilitar la gestión
    var clientesSeleccionadosIds by remember {
        mutableStateOf(cocheCompleto.clientesInteresados.map { it.id }.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Vehículo") },
        text = {
            // Contenedor con scroll para asegurar que todos los campos sean accesibles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campos de texto para modificar modelo y precio
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

                /** * CAMBIO DE MARCA (Relación 1:N):
                 * Permite reasignar el coche a una marca diferente de la lista disponible.
                 */
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

                /** * GESTIÓN DE INTERESADOS (Relación N:M):
                 * El usuario puede añadir o quitar clientes interesados de forma dinámica.
                 */
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
            /** * GUARDADO DE CAMBIOS:
             * Al pulsar "Guardar", se construye un nuevo 'CocheDto' con la misma ID
             * original pero con los datos modificados por el usuario.
             */
            Button(onClick = {
                val dto = CocheDto(
                    id = cocheCompleto.coche.cocheId, // Mantenemos la ID original
                    modelo = modelo,
                    precio = precio.toDoubleOrNull() ?: cocheCompleto.coche.precio,
                    marcaId = marcaSeleccionadaId,
                    matriculaId = cocheCompleto.coche.matriculaId,
                    clientesIds = clientesSeleccionadosIds.toList()
                )
                onConfirm(dto, cocheCompleto.matricula) // Ejecuta la actualización en el Repositorio
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}