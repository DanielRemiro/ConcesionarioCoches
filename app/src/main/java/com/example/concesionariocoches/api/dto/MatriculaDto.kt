package com.example.concesionariocoches.api.dto

import com.google.gson.annotations.SerializedName

data class MatriculaDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("numero")
    val numero: String,

    @SerializedName("fecha_matriculacion")
    val fechaMatriculacion: String
)