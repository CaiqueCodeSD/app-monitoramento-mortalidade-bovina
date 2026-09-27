package com.example.myfirstapp.di

import androidx.room.Room
import com.example.myfirstapp.data.local.AppDatabase
import com.example.myfirstapp.data.local.MIGRATION_1_2
import com.example.myfirstapp.network.RetrofitClient
import com.example.myfirstapp.repository.RegistroRepository
import com.example.myfirstapp.repository.RegistroRepositoryInterface
import com.example.myfirstapp.viewmodel.RegistroViewModel
import com.example.myfirstapp.worker.SyncScheduler
import com.example.myfirstapp.worker.SyncWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // API
    single { RetrofitClient.api }

    // BANCO
    single {
        Room.databaseBuilder(
            get(), AppDatabase::class.java, "mortalidade.db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    // DAO
    single {
        get<AppDatabase>().registroDao()
    }

    // Scheduler
    single {
        SyncScheduler(get())
    }

    // Repository
    single<RegistroRepositoryInterface> {
        RegistroRepository(
            get(), // RegistroDao
            get(), // ApiService
            get()  // SyncScheduler
        )
    }

    // ViewModel
    viewModelOf(::RegistroViewModel)
    workerOf(::SyncWorker)
}