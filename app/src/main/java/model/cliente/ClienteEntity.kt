package model.cliente

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cliente")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val clienteId: Long = 0,
    val nombre: String,
    val telefono: String
)