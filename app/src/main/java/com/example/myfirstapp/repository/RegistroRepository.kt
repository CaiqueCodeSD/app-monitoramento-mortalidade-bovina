package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.ApiService
import retrofit2.HttpException

class RegistroRepository(
    private val apiService: ApiService
) : RegistroRepositoryInterface {

    override suspend fun buscarRegistros(): List<Registro> {
        val response = apiService.getRegistros()

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw HttpException(response)
        }
    }
}