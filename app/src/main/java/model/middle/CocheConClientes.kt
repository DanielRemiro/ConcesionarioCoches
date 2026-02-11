package model.middle

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import model.cliente.ClienteEntity
import model.coche.CocheEntity
import model.cross.CocheClienteCrossRef

data class CocheConClientes(
    @Embedded val coche: CocheEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clienteId",
        associateBy = Junction(CocheClienteCrossRef::class)
    )
    val clientes: List<ClienteEntity>
)