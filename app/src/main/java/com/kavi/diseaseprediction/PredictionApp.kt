package com.kavi.diseaseprediction

import android.app.Application
import com.kavi.diseaseprediction.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PredictionApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PredictionApp)
            androidLogger()

            modules(appModule)
        }
    }
}