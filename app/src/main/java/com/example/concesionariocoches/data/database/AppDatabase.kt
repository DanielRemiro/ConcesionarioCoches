package com.example.concesionariocoches.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.concesionariocoches.data.dao.ConcesionarioDao
import com.example.concesionariocoches.model.cliente.ClienteEntity
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef

import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity // Asegúrate de importar la nueva entidad

@Database(
    entities = [
        CocheEntity::class,
        MarcaEntity::class,
        MatriculaEntity::class, // Hemos cambiado Motor por Matricula
        ClienteEntity::class,
        CocheClienteCrossRef::class
    ],
    version = 2, // Subimos la versión porque ha cambiado el esquema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun concesionarioDao(): ConcesionarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "concesionario_db"
                )
                    // IMPORTANTE: Esto permite borrar la BD antigua y crear la nueva
                    // si cambias las tablas (útil en desarrollo para evitar crashes)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}