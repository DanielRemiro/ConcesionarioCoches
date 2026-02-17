package com.example.concesionariocoches.screens.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.concesionariocoches.api.dto.CocheDto

@Composable
fun AnadirCoche(onDismiss: () -> Unit, onConfirm: (CocheDto) -> Unit) {
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