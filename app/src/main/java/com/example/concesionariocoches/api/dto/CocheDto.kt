package com.example.concesionariocoches.api.dto

import com.google.gson.annotations.SerializedName

data class CocheDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("marcaId")
    val marcaId: Long,

    @SerializedName("matriculaId")
    val matriculaId: Long,

    @SerializedName("clientesIds")
    val clientesIds: List<Long>? = emptyList(),

    @SerializedName("marca")
    val marca: MarcaDto? = null,

    @SerializedName("matricula")
    val matricula: MatriculaDto? = null
)