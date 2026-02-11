package com.example.concesionariocoches.api.dto

import com.google.gson.annotations.SerializedName

data class ClienteDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String,

    @SerializedName("cochesInteresIds") // Array de enteros [1, 5, 20...]
    val cochesInteresIds: List<Long>? = emptyList()
)