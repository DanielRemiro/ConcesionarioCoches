package com.example.concesionariocoches.model.cross

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity

@Entity(
    tableName = "coche_cliente_cross_ref",
    primaryKeys = ["cocheId", "clienteId"],
    foreignKeys = [
        ForeignKey(
            entity = CocheEntity::class,
            parentColumns = ["cocheId"], // Debe coincidir con el @PrimaryKey de Coche
            childColumns = ["cocheId"],
            onDelete = ForeignKey.CASCADE // Si borras el coche, se borra la relación
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["clienteId"], // Debe coincidir con el @PrimaryKey de Cliente
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE // Si borras el cliente, se borra la relación
        )
    ],
    // Creamos índices para que las búsquedas sean rápidas
    indices = [
        Index(value = ["cocheId"]),
        Index(value = ["clienteId"])
    ]
)
data class CocheClienteCrossRef(
    @ColumnInfo(name = "cocheId")
    val cocheId: Long,

    @ColumnInfo(name = "clienteId")
    val clienteId: Long
)