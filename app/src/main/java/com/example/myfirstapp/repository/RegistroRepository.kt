package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.RetrofitClient
import retrofit2.HttpException

class RegistroRepository {

    suspend fun buscarRegistros(): List<Registro> {

        val response = RetrofitClient.api.getRegistros()

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw HttpException(response)
        }
    }
}