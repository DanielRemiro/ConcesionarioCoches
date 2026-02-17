package com.example.concesionariocoches.model.cliente

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "cliente")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "clienteId")
    val id: Long,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "telefono")
    val telefono: String
)