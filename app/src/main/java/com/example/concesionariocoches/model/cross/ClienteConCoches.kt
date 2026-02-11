package com.example.concesionariocoches.model.cross

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity

data class ClienteConCoches(
    @Embedded val cliente: ClienteEntity,

    @Relation(
        parentColumn = "clienteId",
        entityColumn = "cocheId",
        associateBy = Junction(ClienteCocheCrossRef::class) // Aquí ocurre la magia
    )
    val cochesInteres: List<CocheEntity> // Esto es lo que en tu JSON era "cochesInteresIds"
)