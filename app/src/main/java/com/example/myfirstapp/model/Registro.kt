package com.example.myfirstapp.model

import java.io.Serializable

data class Registro (
    val id: Int,
    val data: String,
    val causa: String,
    val observacao: String,
    val fotoUri: String?,
    val latitude: Double?,
    val longitude: Double?
) : Serializable