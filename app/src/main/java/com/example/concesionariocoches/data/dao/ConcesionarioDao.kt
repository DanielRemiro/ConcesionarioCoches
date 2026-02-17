package com.example.concesionariocoches.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity // Cambiado Motor por Matricula
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef
import com.example.concesionariocoches.model.middle.CocheCompleto

@Dao
interface ConcesionarioDao {

    @Transaction
    @Query("SELECT * FROM coche")
    fun getCochesCompletos(): Flow<List<CocheCompleto>>

    @Query("SELECT * FROM coche WHERE cocheId = :id")
    suspend fun getCocheById(id: Long): CocheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarca(marca: MarcaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatricula(matricula: MatriculaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoche(coche: CocheEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocheClienteRef(ref: CocheClienteCrossRef)

    @Update
    suspend fun updateCoche(coche: CocheEntity)

    @Delete
    suspend fun deleteCoche(coche: CocheEntity)
    @Query("DELETE FROM coche")
    suspend fun deleteAllCoches()
    @Delete
    suspend fun deleteMatricula(matricula: MatriculaEntity)

    @Query("DELETE FROM coche_cliente_cross_ref WHERE cocheId = :cocheId")
    suspend fun deleteCocheClienteRefs(cocheId: Long)
    @Query("SELECT * FROM cliente")
    fun getAllClientes(): Flow<List<ClienteEntity>>
    @Query("SELECT * FROM marca")
    fun getAllMarcas(): Flow<List<MarcaEntity>>
}