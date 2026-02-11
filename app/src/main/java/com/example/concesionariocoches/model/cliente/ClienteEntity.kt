package com.example.concesionariocoches.model.cliente

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "cliente")
data class ClienteEntity(
    // Ponemos autoGenerate = false porque tu JSON ya trae IDs fijos (101, 102...)
    // Si fuera una app nueva desde cero, pondrías true.
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "clienteId")
    val id: Long,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "telefono")
    val telefono: String
)