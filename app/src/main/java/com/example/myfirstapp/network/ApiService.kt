package com.example.myfirstapp.network

import com.example.myfirstapp.network.dto.RegistroDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/registros")
    suspend fun getRegistros(): Response<List<RegistroDto>>

    @POST("/registros")
    suspend fun salvarRegistro(
        @Body registro: RegistroDto
    ): Response<RegistroDto>

}