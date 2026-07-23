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
}