package model.middle

import androidx.room.Embedded
import androidx.room.Relation
import model.coche.CocheEntity
import model.marca.MarcaEntity
import model.motor.MotorEntity

data class CocheCompleto(
    @Embedded val coche: CocheEntity,
    @Relation(parentColumn = "marcaId", entityColumn = "marcaId")
    val marca: MarcaEntity,
    @Relation(parentColumn = "motorId", entityColumn = "motorId")
    val motor: MotorEntity
)