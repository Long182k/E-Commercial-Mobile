package com.example.e_commercial.di

import org.koin.dsl.module

val presentationModule = module {
    includes(viewModelModule)
}