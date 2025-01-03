package com.example.e_commercial.ui.feature.notifications

import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.domain.model.OrdersData
import com.example.domain.model.OrderProductItem
import com.example.domain.model.AddressDomainModel
import com.example.domain.model.Product
import com.example.e_commercial.R

@Composable
fun NotificationScreen(
    navController: NavController,
    orders: List<OrdersData>,
    onRefetchNotifications: () -> Unit
) {
    LaunchedEffect(Unit) {
        onRefetchNotifications()
    }

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
                // Back Button
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
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

                // Notification List
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order ID
            Text(
                text = "Order #${order.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product List
            order.items.forEach { item ->
                Log.d("NotificationImage", "Product: ${item.productName}, Image: ${item.image}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = item.image,
                            builder = {
                                placeholder(R.drawable.placeholder_image)
                                error(R.drawable.error_image)
                            }
                        ),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${item.productName} (x${item.quantity})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Order Summary
            Text(
                text = "Total Amount: $${order.totalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Order Date: ${order.orderDate}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Status: ${order.status}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (order.status == "Delivered") Color.Green else Color.Red
            )
        }
    }
}

