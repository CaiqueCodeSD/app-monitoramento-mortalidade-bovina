package com.example.myfirstapp.repository.mapper

import com.example.myfirstapp.data.local.RegistroEntity
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.dto.RegistroDto
import kotlin.Boolean

fun RegistroDto.toDomain(): Registro {
    return Registro(
        id = id,
        data = data,
        causa = causa,
        observacao = observacao,
        fotoUri = fotoUri,
        latitude = latitude,
        longitude = longitude,
        sincronizado = sincronizado
    )
}

fun Registro.toDto() = RegistroDto(
    id = id,
    data = data,
    causa = causa,
    observacao = observacao,
    fotoUri = fotoUri,
    latitude = latitude,
    longitude = longitude,
    sincronizado = sincronizado
)

fun RegistroEntity.toDomain() = Registro(
    id = id,
    data = data,
    causa = causa,
    observacao = observacao,
    fotoUri = fotoUri,
    latitude = latitude,
    longitude = longitude,
    sincronizado = sincronizado
)

fun Registro.toEntity() = RegistroEntity(
    id = id,
    data = data,
    causa = causa,
    observacao = observacao,
    fotoUri = fotoUri,
    latitude = latitude,
    longitude = longitude,
    sincronizado = sincronizado
)