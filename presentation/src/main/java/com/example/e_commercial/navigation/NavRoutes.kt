package com.example.e_commercial.navigation

import com.example.e_commercial.model.UIProductModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
object LoginScreen

@Serializable
object RegisterScreen

@Serializable
object CartScreen

@Serializable
object NotificationScreen

@Serializable
object OrdersScreen

@Serializable
object ProfileScreen

@Serializable
object CartSummaryScreen

@Serializable
data class ProductDetails(val product: UIProductModel)

@Serializable
data class UserAddressRoute(val userAddressWrapper: UserAddressRouteWrapper)