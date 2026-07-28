package com.example.myfirstapp.repository

import android.util.Log
import com.example.myfirstapp.model.Registro

interface RegistroRepositoryInterface {

    suspend fun buscarRegistros(): List<Registro>

    suspend fun salvarRegistro(registro: Registro)

    suspend fun sincronizarPendentes()
}