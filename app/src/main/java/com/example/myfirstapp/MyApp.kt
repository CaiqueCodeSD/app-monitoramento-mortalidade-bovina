package com.example.myfirstapp

import android.app.Application
import com.example.myfirstapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            workManagerFactory()
            modules(appModule)
        }
    }
}