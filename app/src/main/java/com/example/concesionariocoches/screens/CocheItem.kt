package com.example.concesionariocoches.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.model.middle.CocheCompleto

/**
 * COMPONENTE DE LISTA: Define la estructura visual de un solo coche.
 * @param cocheCompleto Objeto que contiene toda la información relacional.
 * @param onDelete Callback que se ejecuta al pulsar el botón de borrar.
 * @param onUpdate Callback que se ejecuta al pulsar el botón de editar.
 */
@Composable
fun CocheItem(cocheCompleto: CocheCompleto, onDelete: () -> Unit, onUpdate: () -> Unit) {
    // Contenedor principal con elevación y margen para dar aspecto de tarjeta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Columna interna para organizar la información verticalmente
        Column(modifier = Modifier.padding(16.dp)) {

            // TÍTULO: Combinamos la Marca y el Modelo para identificar el vehículo
            Text(
                text = "${cocheCompleto.marca.nombre} ${cocheCompleto.coche.modelo}",
                style = MaterialTheme.typography.titleLarge
            )

            // DATOS TÉCNICOS: Visualización de la matrícula vinculada (Relación 1:1)
            Text(
                text = "Matrícula: ${cocheCompleto.matricula.numero}",
                style = MaterialTheme.typography.bodyMedium
            )

            // PRECIO: Destacado con el color primario del tema para llamar la atención
            Text(
                text = "Precio: ${cocheCompleto.coche.precio} €",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            /** * LÓGICA CONDICIONAL (Relación N:M):
             * Si existen clientes interesados en el coche, los mostramos concatenados.
             * Si no hay ninguno, el espacio no se reserva, optimizando la UI.
             */
            if (cocheCompleto.clientesInteresados.isNotEmpty()) {
                Text(
                    text = "Interesados: ${cocheCompleto.clientesInteresados.joinToString { it.nombre }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            /** * BOTONES DE ACCIÓN:
             * Fila alineada al final que contiene las opciones de gestión.
             */
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                // Botón Editar: Llama a la función 'onUpdate' pasada por el padre
                IconButton(onClick = onUpdate) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Editar",
                        tint = Color(0xFFFBC02D) // Color amarillo/ocre
                    )
                }

                // Botón Borrar: Llama a la función 'onDelete'
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}