package com.example.myfirstapp.repository

import android.util.Log
import com.example.myfirstapp.data.local.RegistroDao
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.network.ApiService
import com.example.myfirstapp.repository.mapper.toDomain
import com.example.myfirstapp.repository.mapper.toDto
import com.example.myfirstapp.repository.mapper.toEntity
import com.example.myfirstapp.worker.SyncScheduler


class RegistroRepository(
    private val registroDao: RegistroDao,
    private val apiService: ApiService,
    private val syncScheduler: SyncScheduler
) : RegistroRepositoryInterface {

    override suspend fun buscarRegistros(): List<Registro> {

        val registros = registroDao.buscarTodos()

        return registros.map {
            it.toDomain()
        }
    }

    override suspend fun salvarRegistro(registro: Registro) {

        registroDao.inserir(
            registro.toEntity()
        )

        syncScheduler.agendarSincronizacao()
    }

    override suspend fun sincronizarPendentes() {

        val pendentes = registroDao.buscarNaoSincronizados()

        Log.d(
            "SyncRepository",
            "Registros pendentes encontrados: ${pendentes.size}"
        )

        pendentes.forEach { registro ->

            Log.d(
                "SyncRepository",
                "Tentando sincronizar registro: ${registro.id}"
            )

            try {

                val response = apiService.salvarRegistro(
                    registro.toDomain().toDto()
                )

                Log.d(
                    "SyncRepository",
                    "Resposta API: ${response.code()} - sucesso=${response.isSuccessful}"
                )

                if (response.isSuccessful) {

                    registroDao.marcarComoSincronizado(
                        registro.id
                    )

                    Log.d(
                        "SyncRepository",
                        "Registro ${registro.id} marcado como sincronizado"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "SyncRepository",
                    "Falha ao sincronizar registro ${registro.id}",
                    e
                )
            }
        }
    }

}