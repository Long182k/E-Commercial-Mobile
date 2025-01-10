package com.example.e_commercial

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.example.data.di.dataModule
import com.example.domain.di.domainModule
import com.example.e_commercial.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ECommercialApp: Application() {


    override fun onCreate() {
        super.onCreate()
        
        // Initialize Coil with more configuration
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .logger(DebugLogger())
            .build()

        Coil.setImageLoader(imageLoader)

        // Initialize Koin
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