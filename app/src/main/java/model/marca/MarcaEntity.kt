package model.marca

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marca")
data class MarcaEntity(
    @PrimaryKey(autoGenerate = true) val marcaId: Long = 0,
    val nombre: String,
    val pais: String
)