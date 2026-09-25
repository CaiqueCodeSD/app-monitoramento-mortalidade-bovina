package com.example.myfirstapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class SyncScheduler(
    private val context: Context
) {

    fun agendarSincronizacao() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                "sync_registros",
                ExistingWorkPolicy.KEEP,
                request
            )
    }
}