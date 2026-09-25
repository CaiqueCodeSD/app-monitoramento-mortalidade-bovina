package com.example.myfirstapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros")
    suspend fun buscarTodos(): List<RegistroEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: RegistroEntity)

    @Query("SELECT * FROM registros WHERE sincronizado = 0")
    suspend fun buscarNaoSincronizados(): List<RegistroEntity>

    @Query(
        """
    UPDATE registros
    SET sincronizado = 1
    WHERE id = :id
"""
    )
    suspend fun marcarComoSincronizado(id: Int): Int
}