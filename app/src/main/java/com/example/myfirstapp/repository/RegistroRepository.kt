package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.ApiService
import retrofit2.HttpException

class RegistroRepository(
    private val apiService: ApiService
) : RegistroRepositoryInterface {

    // Lista local
    private val registrosLocais =
        mutableListOf<Registro>()

    override suspend fun buscarRegistros(): List<Registro> {

        if (registrosLocais.isEmpty()) {

            val response = apiService.getRegistros()

            if (response.isSuccessful) {

                registrosLocais.addAll(
                    response.body() ?: emptyList()
                )

            } else {

                throw HttpException(response)
            }
        }

        return registrosLocais
    }

    override suspend fun salvarRegistro(

        registro: Registro

    ) {

        // Post fake
        apiService.salvarRegistro(registro)

        // Adiciona localmente
        registrosLocais.add(0, registro)
    }
}