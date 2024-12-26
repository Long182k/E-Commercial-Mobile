package com.example.e_commercial.di

import com.example.e_commercial.EcommercialSession
import org.koin.dsl.module

val presentationModule = module {
    includes(viewModelModule)
    single { EcommercialSession(get()) }
}

