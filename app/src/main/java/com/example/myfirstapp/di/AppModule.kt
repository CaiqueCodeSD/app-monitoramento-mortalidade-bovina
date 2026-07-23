package com.example.myfirstapp.di

import androidx.room.Room
import com.example.myfirstapp.data.local.AppDatabase
import com.example.myfirstapp.network.RetrofitClient
import com.example.myfirstapp.repository.RegistroRepository
import com.example.myfirstapp.repository.RegistroRepositoryInterface
import com.example.myfirstapp.viewmodel.RegistroViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // API
    single { RetrofitClient.api }

    // BANCO
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "mortalidade.db"
        ).build()
    }

    // DAO
    single {
        get<AppDatabase>().registroDao()
    }

    // Repository
    single<RegistroRepositoryInterface> {
        RegistroRepository(get())
    }

    // ViewModel
    viewModelOf(::RegistroViewModel)
}