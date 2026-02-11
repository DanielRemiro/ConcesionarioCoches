package model.coche

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import model.marca.MarcaEntity
import model.motor.MotorEntity

@Entity(
    tableName = "coche",
    foreignKeys = [
        ForeignKey(
            entity = MarcaEntity::class,
            parentColumns = ["marcaId"],
            childColumns = ["marcaId"],
            onDelete = ForeignKey.CASCADE // Si borras Toyota, se borran sus Corollas (Correcto)
        ),
        ForeignKey(
            entity = MotorEntity::class,
            parentColumns = ["motorId"],
            childColumns = ["motorId"],
            onDelete = ForeignKey.CASCADE // Si borras el Motor, se borra el Coche (Correcto)
        )
    ],
    indices = [Index("marcaId"), Index("motorId")]
)
data class CocheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modelo: String,
    val precio: Double,
    val descripcion: String,
    val marcaId: Long, // FK 1:N
    val motorId: Long  // FK 1:1
)