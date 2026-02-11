package com.example.concesionariocoches.api

import com.example.concesionariocoches.api.dto.CocheDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ConcesionarioApi {
    // GET: Obtener todos los coches (con expand para traer relaciones si el server lo soporta, o manual)
    @GET("coche")
    suspend fun getCoches(): List<CocheDto>
    // Nota: CocheDto es una clase de datos simple para parsear el JSON recibido

    // POST: Crear nuevo coche en la nube
    @POST("coche")
    suspend fun crearCoche(@Body coche: CocheDto): CocheDto

    // PUT: Actualizar
    @PUT("coche/{id}")
    suspend fun actualizarCoche(@Path("id") id: Long, @Body coche: CocheDto): CocheDto

    // DELETE: Borrar
    @DELETE("coche/{id}")
    suspend fun eliminarCoche(@Path("id") id: Long)
}