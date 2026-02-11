package com.example.concesionariocoches.api

import com.example.concesionariocoches.api.dto.CocheDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ConcesionarioApi {
    // MODIFICADO: Añadimos _expand para evitar error de Foreign Key en Room
    @GET("coche?_expand=marca&_expand=matricula")
    suspend fun getCoches(): List<CocheDto>

    @POST("coche")
    suspend fun crearCoche(@Body coche: CocheDto): CocheDto

    @PUT("coche/{id}")
    suspend fun actualizarCoche(@Path("id") id: Long, @Body coche: CocheDto): CocheDto

    @DELETE("coche/{id}")
    suspend fun eliminarCoche(@Path("id") id: Long)
}