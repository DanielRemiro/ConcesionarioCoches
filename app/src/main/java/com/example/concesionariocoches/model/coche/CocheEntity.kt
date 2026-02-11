package com.example.concesionariocoches.model.coche

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity

@Entity(
    tableName = "coche",
    foreignKeys = [
        ForeignKey(
            entity = MarcaEntity::class,
            parentColumns = ["marcaId"],
            childColumns = ["marcaId"]
        ),
        ForeignKey(entity = MatriculaEntity::class, parentColumns = ["matriculaId"], childColumns = ["matriculaId"])
    ],
    indices = [Index("marcaId"), Index("matriculaId")] // Buena práctica para rendimiento
)
data class CocheEntity(
    @PrimaryKey(autoGenerate = true) val cocheId: Long = 0,
    val modelo: String,
    val precio: Double,
    val marcaId: Long,     // Relación 1:N con Marca
    val matriculaId: Long  // Relación 1:1 con Matrícula
)