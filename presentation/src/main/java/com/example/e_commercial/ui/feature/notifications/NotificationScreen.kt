package com.example.e_commercial.ui.feature.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.OrdersData
import com.example.e_commercial.R

@Composable
fun NotificationScreen(navController: NavController, orders: List<OrdersData>) {
    // Sort the orders list by ID in descending order
    val sortedOrders = orders.sortedByDescending { it.id }

    // Maintain a list of visible notifications by order ID
    val visibleNotifications = remember { mutableStateMapOf<Int, Boolean>().apply {
        sortedOrders.forEach { order -> this[order.id] = true }
    }}

    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header or Title
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notification list with animation
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sortedOrders) { order ->
                        val isVisible = visibleNotifications[order.id] ?: true
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn() + expandVertically()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE3F2FD))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Order #${order.id} pay success",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    IconButton(
                                        onClick = {
                                            visibleNotifications[order.id] = false
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_subtract),
                                            contentDescription = "Dismiss Notification",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
