package com.example.concesionariocoches.model.middle

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef

data class CocheConClientes(
    @Embedded val coche: CocheEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clienteId",
        associateBy = Junction(CocheClienteCrossRef::class)
    )
    val clientes: List<ClienteEntity>
)