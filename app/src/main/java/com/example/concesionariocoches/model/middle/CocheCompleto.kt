package com.example.concesionariocoches.model.middle

import androidx.room.Embedded
import androidx.room.Relation
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.motor.MotorEntity

data class CocheCompleto(
    @Embedded val coche: CocheEntity,
    @Relation(parentColumn = "marcaId", entityColumn = "marcaId")
    val marca: MarcaEntity,
    @Relation(parentColumn = "motorId", entityColumn = "motorId")
    val motor: MotorEntity
)