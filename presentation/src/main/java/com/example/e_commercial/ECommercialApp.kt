package com.example.e_commercial

import android.app.Application
import com.example.data.di.dataModule
import com.example.domain.di.domainModule
import com.example.e_commercial.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ECommercialApp: Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ECommercialApp)
            modules(listOf(
                presentationModule,
                dataModule,
                domainModule
            ))
        }
    }
}