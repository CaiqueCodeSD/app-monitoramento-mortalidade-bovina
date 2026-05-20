package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.ApiService
import retrofit2.HttpException
import com.example.myfirstapp.repository.mapper.toDomain
import com.example.myfirstapp.repository.mapper.toDto

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

                response.body()?.let { registrosDto ->

                    registrosLocais.addAll(
                        registrosDto.map { dto ->
                            dto.toDomain()
                        }
                    )
                }

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
        apiService.salvarRegistro(
            registro.toDto()
        )

        // Adiciona localmente
        registrosLocais.add(0, registro)
    }
}