package com.example.concesionariocoches.model.marca

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "marca")
data class MarcaEntity(
    @PrimaryKey(autoGenerate = false) // Usamos el ID del JSON (1, 2, 3...)
    @ColumnInfo(name = "marcaId")     // Nombre de la columna en la BD
    val marcaId: Long,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "pais")
    val pais: String
)