package com.example.concesionariocoches.model.cross

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity

@Entity(
    primaryKeys = ["cocheId", "clienteId"],
    foreignKeys = [
        ForeignKey(
            entity = CocheEntity::class,
            parentColumns = ["id"],
            childColumns = ["cocheId"],
            onDelete = ForeignKey.CASCADE // <--- ¡MAGIA! Si borras Coche, se borra la relación
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["clienteId"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CocheClienteCrossRef(
    val cocheId: Long,
    val clienteId: Long
)