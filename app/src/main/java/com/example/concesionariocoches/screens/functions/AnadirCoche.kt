package com.example.concesionariocoches.screens.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import com.example.concesionariocoches.model.matricula.MatriculaEntity

@Composable
fun AnadirCoche(onDismiss: () -> Unit,
                onConfirm: (CocheDto, MatriculaEntity) -> Unit) {
    var modelo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var numMatricula by remember { mutableStateOf("") }
    var fechaMatricula by remember { mutableStateOf("") }
    var marcaId by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Coche y Matrícula") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })
                Divider()
                Text("Datos de Matrícula", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = numMatricula, onValueChange = { numMatricula = it }, label = { Text("Número de Matrícula") })
                OutlinedTextField(value = fechaMatricula, onValueChange = { fechaMatricula = it }, label = { Text("Fecha (AAAA-MM-DD)") })
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
                    marcaId = marcaId.toLong(),
                    matriculaId = mId,
                    clientesIds = emptyList()
                )
                onConfirm(coche, matricula)
            }) { Text("Crear Todo") }
        }
    )
}