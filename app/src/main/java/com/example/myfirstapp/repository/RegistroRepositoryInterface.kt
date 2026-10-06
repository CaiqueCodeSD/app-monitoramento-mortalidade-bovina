package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro
import kotlinx.coroutines.flow.Flow

interface RegistroRepositoryInterface {

    suspend fun buscarRegistros(): List<Registro>

    fun observarRegistros(): Flow<List<Registro>>

    suspend fun salvarRegistro(registro: Registro)

    suspend fun sincronizarPendentes()
}