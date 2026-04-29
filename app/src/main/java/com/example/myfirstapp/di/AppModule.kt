package com.example.myfirstapp.di

import com.example.myfirstapp.network.RetrofitClient
import com.example.myfirstapp.repository.RegistroRepository
import com.example.myfirstapp.viewmodel.RegistroViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // API
    single { RetrofitClient.api }

    // Repository
    single { RegistroRepository(get()) }

    // ViewModel
    viewModel { RegistroViewModel(get()) }
}