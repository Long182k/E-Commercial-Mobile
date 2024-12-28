package com.example.e_commercial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.e_commercial.model.UIProductModel
import com.example.e_commercial.navigation.CartScreen
import com.example.e_commercial.navigation.CartSummaryScreen
import com.example.e_commercial.navigation.HomeScreen
import com.example.e_commercial.navigation.LoginScreen
import com.example.e_commercial.navigation.NotificationScreen
import com.example.e_commercial.navigation.OrdersScreen
import com.example.e_commercial.navigation.ProductDetails
import com.example.e_commercial.navigation.ProfileScreen
import com.example.e_commercial.navigation.RegisterScreen
import com.example.e_commercial.navigation.UserAddressRoute
import com.example.e_commercial.navigation.UserAddressRouteWrapper
import com.example.e_commercial.navigation.productNavType
import com.example.e_commercial.navigation.userAddressNavType
import com.example.e_commercial.ui.feature.account.login.LoginScreen
import com.example.e_commercial.ui.feature.account.register.RegisterScreen
import com.example.e_commercial.ui.feature.cart.CartScreen
import com.example.e_commercial.ui.feature.home.HomeScreen
import com.example.e_commercial.ui.feature.notifications.NotificationScreen
import com.example.e_commercial.ui.feature.notifications.NotificationViewModel
import com.example.e_commercial.ui.feature.orders.OrdersEvent
import com.example.e_commercial.ui.feature.orders.OrdersScreen
import com.example.e_commercial.ui.feature.orders.OrdersViewModel
import com.example.e_commercial.ui.feature.product_details.ProductDetailsScreen
import com.example.e_commercial.ui.feature.profile.ProfileScreen
import com.example.e_commercial.ui.feature.summary.CartSummaryScreen
import com.example.e_commercial.ui.feature.user_address.UserAddressScreen
import com.example.e_commercial.ui.theme.ECommercialTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val ecommercialSession : EcommercialSession by inject()
            ECommercialTheme {
                val shouldShowBottomNav = remember {
                    mutableStateOf(true)
                }
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(visible = shouldShowBottomNav.value, enter = fadeIn()) {
                            BottomNavigationBar(navController)
                        }

                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {

                        NavHost(
                            navController = navController,
                            startDestination = if (ecommercialSession.getUser() != null) {
                                HomeScreen
                            } else {
                                LoginScreen
                            }
                        ) {

                            composable<LoginScreen> {
                                shouldShowBottomNav.value = false
                                LoginScreen(navController)
                            }
                            composable<RegisterScreen> {
                                shouldShowBottomNav.value = false
                                RegisterScreen(navController)
                            }
                            composable<CartScreen> {
                                shouldShowBottomNav.value = true
                                CartScreen(navController)
                            }

                            composable<HomeScreen> {
                                shouldShowBottomNav.value = true
                                HomeScreen(navController)
                            }
                            composable<OrdersScreen> {
                                shouldShowBottomNav.value = true
                                OrdersScreen()
                            }
                            composable<ProfileScreen> {
                                shouldShowBottomNav.value = true
                                ProfileScreen(navController)
                            }
                            composable<CartSummaryScreen> {
                                shouldShowBottomNav.value = false
                                CartSummaryScreen(navController = navController)
                            }
                            composable<ProductDetails>(
                                typeMap = mapOf(typeOf<UIProductModel>() to productNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val productRoute = it.toRoute<ProductDetails>()
                                ProductDetailsScreen(navController, productRoute.product)
                            }
                            composable<UserAddressRoute>(
                                typeMap = mapOf(typeOf<UserAddressRouteWrapper>() to userAddressNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val userAddressRoute = it.toRoute<UserAddressRoute>()
                                UserAddressScreen(
                                    navController = navController,
                                    userAddress = userAddressRoute.userAddressWrapper.userAddress
                                )
                            }
                            composable<NotificationScreen> {
                                shouldShowBottomNav.value = false

                                val notificationViewModel: NotificationViewModel = koinViewModel()

                                NotificationScreen(
                                    navController = navController,
                                    viewModel = notificationViewModel
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        //current route
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val items = listOf(
            BottomNavItems.Home,
            BottomNavItems.Orders,
            BottomNavItems.Profile
        )

        items.forEach { item ->
            val isSelected = currentRoute?.substringBefore("?") == item.route::class.qualifiedName
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = item.title) },
                icon = {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
                    )
                }, colors = NavigationBarItemDefaults.colors().copy(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}

sealed class BottomNavItems(val route: Any, val title: String, val icon: Int) {
    object Home : BottomNavItems(HomeScreen, "Home", icon = R.drawable.ic_home)
    object Orders : BottomNavItems(OrdersScreen, "Orders", icon = R.drawable.ic_orders)
    object Profile : BottomNavItems(ProfileScreen, "Profile", icon = R.drawable.ic_profile_bn)
}