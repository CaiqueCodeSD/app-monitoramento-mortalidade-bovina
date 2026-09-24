package com.example.myfirstapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.example.myfirstapp.repository.RegistroRepositoryInterface

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: RegistroRepositoryInterface
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {

            Log.d("SyncWorker", "Iniciando sincronização")

            repository.sincronizarPendentes()

            Log.d("SyncWorker", "Sincronização concluída com sucesso")

            Result.success()

        } catch (e: Exception) {

            Log.e("SyncWorker", "Erro durante sincronização", e)

            Result.retry()

        }
    }
}