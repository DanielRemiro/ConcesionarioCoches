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
            parentColumns = ["cocheId"],
            childColumns = ["cocheId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["clienteId"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
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