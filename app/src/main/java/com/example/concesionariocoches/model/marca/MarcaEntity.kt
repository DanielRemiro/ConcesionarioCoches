package com.example.concesionariocoches.model.marca

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "marca")
data class MarcaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "marcaId")
    val marcaId: Long,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "pais")
    val pais: String
)