package com.example.concesionariocoches.model.matricula

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "matricula",
    // Esto asegura que no se repitan números de matrícula en la BD
    indices = [Index(value = ["numero"], unique = true)]
)
data class MatriculaEntity(
    @PrimaryKey(autoGenerate = false) // Usamos el ID del JSON
    @ColumnInfo(name = "matriculaId")
    val matriculaId: Long,

    @ColumnInfo(name = "numero")
    val numero: String, // Ejemplo: "1234 KLR"

    // En el JSON viene como "fecha_matriculacion"
    @ColumnInfo(name = "fecha_matriculacion")
    val fechaMatriculacion: String // Guardamos la fecha como String "2023-01-15"
)