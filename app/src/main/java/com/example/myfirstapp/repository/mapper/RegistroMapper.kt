package com.example.myfirstapp.repository.mapper

import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.dto.RegistroDto

fun RegistroDto.toDomain(): Registro {
    return Registro(
        id = id,
        data = data,
        causa = causa,
        observacao = observacao,
        fotoUri = fotoUri,
        latitude = latitude,
        longitude = longitude
    )
}

fun Registro.toDto(): RegistroDto {
    return RegistroDto(
        id = id,
        data = data,
        causa = causa,
        observacao = observacao,
        fotoUri = fotoUri,
        latitude = latitude,
        longitude = longitude
    )
}