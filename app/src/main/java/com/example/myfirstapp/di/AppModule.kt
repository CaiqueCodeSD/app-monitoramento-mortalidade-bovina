package com.example.myfirstapp.di

import com.example.myfirstapp.network.RetrofitClient
import com.example.myfirstapp.repository.RegistroRepository
import com.example.myfirstapp.repository.RegistroRepositoryInterface
import com.example.myfirstapp.viewmodel.RegistroViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // API
    single { RetrofitClient.api }

    // Repository
    single<RegistroRepositoryInterface> {
        RegistroRepository(get())
    }

    // ViewModel
    viewModelOf(::RegistroViewModel)
}