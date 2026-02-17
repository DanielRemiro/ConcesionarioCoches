package com.example.concesionariocoches.model.matricula

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "matricula",

    indices = [Index(value = ["numero"], unique = true)]
)
data class MatriculaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "matriculaId")
    val matriculaId: Long,

    @ColumnInfo(name = "numero")
    val numero: String,

    @ColumnInfo(name = "fecha_matriculacion")
    val fechaMatriculacion: String
)