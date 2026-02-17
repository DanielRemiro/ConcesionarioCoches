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

/**
 * COMPONENTE DE DIÁLOGO: Recibe las listas de marcas y clientes disponibles
 * para que el usuario pueda establecer las relaciones necesarias.
 */
@Composable
fun AnadirCoche(
    marcas: List<MarcaEntity>,
    clientes: List<ClienteEntity>,
    onDismiss: () -> Unit, // Función para cerrar el diálogo
    onConfirm: (CocheDto, MatriculaEntity) -> Unit // Función para enviar los datos
) {
    /** * GESTIÓN DE ESTADO LOCAL:
     * Definimos variables reactivas para cada campo del formulario.
     * 'remember' asegura que los datos no se pierdan al redibujarse la pantalla.
     */
    var modelo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var numMatricula by remember { mutableStateOf("") }
    var fechaMatricula by remember { mutableStateOf("") }

    // Estado para selección única (1:N): ID de la marca seleccionada
    var marcaSeleccionadaId by remember { mutableStateOf(marcas.firstOrNull()?.marcaId ?: 0L) }

    // Estado para selección múltiple (N:M): Conjunto de IDs de clientes
    var clientesSeleccionadosIds by remember { mutableStateOf(setOf<Long>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Coche y Matrícula") },
        text = {
            /** * DISEÑO SCROLLABLE:
             * Usamos 'verticalScroll' para permitir que el formulario sea más largo
             * que la pantalla, evitando que los elementos se corten.
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SECCIÓN 1: Campos de texto para datos básicos
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

                // SECCIÓN 2: Datos de la Matrícula (Relación 1:1)
                Text("Datos de Matrícula", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = numMatricula,
                    onValueChange = { numMatricula = it },
                    label = { Text("Número de Matrícula") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                /** * SECCIÓN 3: SELECCIÓN DE MARCA (RadioButton)
                 * Muestra dinámicamente todas las marcas registradas.
                 * Al ser RadioButtons, solo permitimos una marca por coche.
                 */
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

                /** * SECCIÓN 4: CLIENTES INTERESADOS (Checkbox)
                 * Permite que el usuario marque varios clientes a la vez.
                 * Gestionamos la lógica de añadir o quitar del conjunto de IDs.
                 */
                Text("Clientes Interesados", style = MaterialTheme.typography.titleSmall)
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
            /** * ACCIÓN DE CONFIRMACIÓN:
             * Cuando se pulsa "Crear Todo", generamos IDs aleatorios (simulando el servidor)
             * y empaquetamos los datos en objetos DTO y Entidad para enviarlos al Repositorio.
             */
            Button(onClick = {
                val mId = (1000..9999).random().toLong() // ID temporal para la matrícula
                val matricula = MatriculaEntity(mId, numMatricula, fechaMatricula)

                val coche = CocheDto(
                    id = (1000..9999).random().toLong(),
                    modelo = modelo,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    marcaId = marcaSeleccionadaId,
                    matriculaId = mId,
                    clientesIds = clientesSeleccionadosIds.toList()
                )
                onConfirm(coche, matricula) // Ejecuta el callback definido en el ViewModel
            }) { Text("Crear Todo") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}