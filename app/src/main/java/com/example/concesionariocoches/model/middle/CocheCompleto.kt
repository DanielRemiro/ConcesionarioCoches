package com.example.concesionariocoches.model.middle


import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity

data class CocheCompleto(
    @Embedded val coche: CocheEntity,

    // Relación 1:1 con Marca
    @Relation(
        parentColumn = "marcaId",
        entityColumn = "marcaId"
    )
    val marca: MarcaEntity,

    // Relación 1:1 con Matrícula
    @Relation(
        parentColumn = "matriculaId",
        entityColumn = "matriculaId"
    )
    val matricula: MatriculaEntity,

    // Relación N:M con Clientes
    @Relation(
        parentColumn = "cocheId",
        entityColumn = "clienteId",
        associateBy = Junction(CocheClienteCrossRef::class)
    )
    val clientesInteresados: List<ClienteEntity>
)