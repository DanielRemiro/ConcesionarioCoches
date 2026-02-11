package model.motor

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motor")
data class MotorEntity(
    @PrimaryKey(autoGenerate = true) val motorId: Long = 0,
    val combustible: String,
    val potencia: String
)