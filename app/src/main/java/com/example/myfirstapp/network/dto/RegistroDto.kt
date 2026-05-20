package com.example.myfirstapp.network.dto

data class RegistroDto(
    val id: Int,
    val data: String,
    val causa: String,
    val observacao: String,
    val fotoUri: String?,
    val latitude: Double?,
    val longitude: Double?
)