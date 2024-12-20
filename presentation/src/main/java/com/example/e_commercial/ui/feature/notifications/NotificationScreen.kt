package com.example.e_commercial.ui.feature.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.OrdersData
import com.example.e_commercial.R

@Composable
fun NotificationScreen(
    navController: NavController,
    orders: List<OrdersData>,
    onRefetchNotifications: () -> Unit
) {
    // Trigger refetch whenever this screen is navigated to
    LaunchedEffect(Unit) {
        onRefetchNotifications()
    }

    // Maintain a list of visible notifications by order ID
    val visibleNotifications = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            orders.forEach { order -> this[order.id] = true }
        }
    }

    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Back Button Implementation
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.4f))
                        .clickable {
                            navController.navigateUp()
                        }
                        .align(Alignment.Start)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Navigate back",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }

                // Header or Title
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
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
                    items(orders) { order ->
                        val isVisible = visibleNotifications[order.id] ?: true
                        if (isVisible) {
                            NotificationCard(
                                order = order,
                                onDismiss = { visibleNotifications[order.id] = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(order: OrdersData, onDismiss: () -> Unit) {
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
            IconButton(onClick = { onDismiss() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_subtract),
                    contentDescription = "Dismiss Notification",
                    tint = Color.Gray
                )
            }
        }
    }
}
