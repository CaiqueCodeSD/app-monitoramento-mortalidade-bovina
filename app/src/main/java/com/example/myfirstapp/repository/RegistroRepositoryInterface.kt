package com.example.myfirstapp.repository

import com.example.myfirstapp.model.Registro

interface RegistroRepositoryInterface {
    suspend fun buscarRegistros(): List<Registro>
}