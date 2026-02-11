package model.cross

import androidx.room.Entity
import androidx.room.ForeignKey
import model.cliente.ClienteEntity
import model.coche.CocheEntity

@Entity(
    primaryKeys = ["cocheId", "clienteId"],
    foreignKeys = [
        ForeignKey(
            entity = CocheEntity::class,
            parentColumns = ["id"],
            childColumns = ["cocheId"],
            onDelete = ForeignKey.CASCADE // <--- ¡MAGIA! Si borras Coche, se borra la relación
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["clienteId"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CocheClienteCrossRef(
    val cocheId: Long,
    val clienteId: Long
)