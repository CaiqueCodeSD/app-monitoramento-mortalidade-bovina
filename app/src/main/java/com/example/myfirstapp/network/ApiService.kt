package com.example.myfirstapp.network

import com.example.myfirstapp.model.Registro
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/registros")
    suspend fun getRegistros(): Response<List<Registro>>
}