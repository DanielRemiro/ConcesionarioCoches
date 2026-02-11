package com.example.concesionariocoches.api.dto

import com.google.gson.annotations.SerializedName

data class CocheDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("marcaId")
    val marcaId: Long,

    @SerializedName("motorId")
    val motorId: Long,

    @SerializedName("clientesIds")
    val clientesIds: List<Long> = emptyList(),

    @SerializedName("marca")
    val marca: MarcaDto? = null,

    @SerializedName("motor")
    val motor: MotorDto? = null
)