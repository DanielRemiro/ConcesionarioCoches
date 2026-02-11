package com.example.concesionariocoches.api.dto


import com.google.gson.annotations.SerializedName

data class MarcaDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("pais")
    val pais: String
)