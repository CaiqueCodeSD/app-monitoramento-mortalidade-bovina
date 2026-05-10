package com.example.myfirstapp.network

import com.example.myfirstapp.model.Registro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/registros")
    suspend fun getRegistros(): Response<List<Registro>>

    @POST("/registros")
    suspend fun salvarRegistro(
        @Body registro: Registro
    ): Response<Registro>

}