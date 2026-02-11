package com.example.concesionariocoches.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Si usas emulador de Android Studio: http://10.0.2.2:3000/
    // Si usas dispositivo físico: Tu IP local http://192.168.1.X:3000/
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: ConcesionarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConcesionarioApi::class.java)
    }
}