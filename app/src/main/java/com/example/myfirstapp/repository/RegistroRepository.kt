package com.example.myfirstapp.repository

import com.example.myfirstapp.data.local.RegistroDao
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.repository.mapper.toDomain
import com.example.myfirstapp.repository.mapper.toDto
import com.example.myfirstapp.repository.mapper.toEntity


class RegistroRepository(
    private val registroDao: RegistroDao
) : RegistroRepositoryInterface {

    override suspend fun buscarRegistros(): List<Registro> {

        val registros = registroDao.buscarTodos()

        return registros.map {
            it.toDomain()
        }
    }

    override suspend fun salvarRegistro(registro: Registro) {

        registroDao.inserir(
            registro.toEntity()
        )
    }
}