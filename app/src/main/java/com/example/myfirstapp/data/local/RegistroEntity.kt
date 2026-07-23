package com.example.myfirstapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey
    val id: Int,
    val data: String,
    val causa: String,
    val observacao: String,
    val fotoUri: String?,
    val latitude: Double?,
    val longitude: Double?
)