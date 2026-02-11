package com.example.concesionariocoches.data.dao


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.motor.MotorEntity
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef
import com.example.concesionariocoches.model.middle.CocheCompleto

@Dao
interface ConcesionarioDao {

    // --- OPERACIONES DE LECTURA (Relaciones) ---
    @Transaction
    @Query("SELECT * FROM coche")
    fun getCochesCompletos(): Flow<List<CocheCompleto>>

    @Query("SELECT * FROM coche WHERE id = :id")
    suspend fun getCocheById(id: Long): CocheEntity?

    // --- CRUD BÁSICO ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarca(marca: MarcaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotor(motor: MotorEntity): Long

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

    // Borrado en cascada para limpiar datos antiguos al refrescar de la API
    @Query("DELETE FROM coche")
    suspend fun deleteAllCoches()
}