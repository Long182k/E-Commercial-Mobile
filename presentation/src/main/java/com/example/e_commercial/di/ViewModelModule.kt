package com.example.e_commercial.di

import com.example.e_commercial.ui.feature.home.HomeViewModel
import com.example.e_commercial.ui.feature.product_details.ProductDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{
        HomeViewModel(get(), get())
    }
    viewModel {
        ProductDetailsViewModel()
    }
}