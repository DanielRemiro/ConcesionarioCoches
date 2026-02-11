package com.example.concesionariocoches.api.dto

import com.google.gson.annotations.SerializedName

data class CocheDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("precio")
    val precio: Double,

    // --- Claves foráneas (Lo que viene en tu JSON plano) ---
    @SerializedName("marcaId")
    val marcaId: Long,

    @SerializedName("matriculaId")
    val matriculaId: Long,

    @SerializedName("clientesIds")
    val clientesIds: List<Long>? = emptyList(),

    // --- Objetos anidados (Opcionales) ---
    // Estos se llenarán solo si tu API usa ?_expand=marca o similar.
    // El Repository los usaba para guardar la marca/matrícula automáticamente.
    @SerializedName("marca")
    val marca: MarcaDto? = null,

    @SerializedName("matricula")
    val matricula: MatriculaDto? = null
)