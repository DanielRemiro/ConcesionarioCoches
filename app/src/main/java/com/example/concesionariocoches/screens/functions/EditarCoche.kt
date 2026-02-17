package com.example.concesionariocoches.screens.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.model.middle.CocheCompleto

@Composable
fun EditarCoche(
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