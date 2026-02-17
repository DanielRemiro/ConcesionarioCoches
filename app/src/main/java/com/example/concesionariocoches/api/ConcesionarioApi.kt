package com.example.concesionariocoches.api

import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.api.dto.ClienteDto // Asegúrate de importar esto
import com.example.concesionariocoches.api.dto.MarcaDto
import com.example.concesionariocoches.api.dto.MatriculaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ConcesionarioApi {

    @POST("coche")
    suspend fun crearCoche(@Body coche: CocheDto): CocheDto

    @PUT("coche/{id}")
    suspend fun actualizarCoche(@Path("id") id: Long, @Body coche: CocheDto): CocheDto

    @DELETE("coche/{id}")
    suspend fun eliminarCoche(@Path("id") id: Long)

    @GET("cliente")
    suspend fun getClientes(): List<ClienteDto>
    @GET("marca")
    suspend fun getMarcas(): List<MarcaDto>
    @GET("matricula")
    suspend fun getMatriculas(): List<MatriculaDto>
    @GET("coche")
    suspend fun getCoches(): List<CocheDto>
}