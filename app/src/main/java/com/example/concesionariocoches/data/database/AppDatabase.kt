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
import com.example.concesionariocoches.model.matricula.MatriculaEntity

@Database(
    entities = [
        CocheEntity::class,
        MarcaEntity::class,
        MatriculaEntity::class,
        ClienteEntity::class,
        CocheClienteCrossRef::class
    ],
    version = 2,
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
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}