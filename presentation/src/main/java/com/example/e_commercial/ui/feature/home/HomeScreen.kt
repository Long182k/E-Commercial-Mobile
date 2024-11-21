package com.example.e_commercial.ui.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.model.Product
import com.example.e_commercial.R
import com.example.e_commercial.model.UIProductModel
import com.example.e_commercial.navigation.CartScreen
import com.example.e_commercial.navigation.NotificationScreen
import com.example.e_commercial.navigation.ProductDetails
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.e_commercial.ui.feature.profile.ProfileViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsState()
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    val feature = remember { mutableStateOf<List<Product>>(emptyList()) }
    val popular = remember { mutableStateOf<List<Product>>(emptyList()) }
    val categories = remember { mutableStateOf<List<String>>(emptyList()) }

    val searchQuery = remember { mutableStateOf("") }
    val filteredFeatured = remember { mutableStateOf<List<Product>>(emptyList()) }
    val filteredPopular = remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(searchQuery.value, feature.value, popular.value) {
        val query = searchQuery.value
        filteredFeatured.value = if (query.isBlank()) {
            feature.value
        } else {
            feature.value.filter { it.title.contains(query, ignoreCase = true) }
        }
        filteredPopular.value = if (query.isBlank()) {
            popular.value
        } else {
            popular.value.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState.value) {
                is HomeScreenUIEvents.Loading -> {
                    loading.value = true
                    error.value = null
                }

                is HomeScreenUIEvents.Success -> {
                    val data = (uiState.value as HomeScreenUIEvents.Success)
                    feature.value = data.featured
                    popular.value = data.popularProducts
                    categories.value = data.categories
                    loading.value = false
                    error.value = null
                }

                is HomeScreenUIEvents.Error -> {
                    val errorMsg = (uiState.value as HomeScreenUIEvents.Error).message
                    loading.value = false
                    error.value = errorMsg
                }
            }
            HomeContent(
                navController = navController,
                featured = filteredFeatured.value,
                popularProducts = filteredPopular.value,
                categories = categories.value,
                isLoading = loading.value,
                errorMsg = error.value,
                searchQuery = searchQuery.value,
                onSearchQueryChange = { searchQuery.value = it },
                onClick = {
                    navController.navigate(ProductDetails(UIProductModel.fromProduct(it)))
                },
                onCartClicked = {
                    navController.navigate(CartScreen) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileHeader(
    onCartClicked: () -> Unit,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel() // Use ProfileViewModel here
) {
    // Access the user state directly with .value
    val user = viewModel.user.observeAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Online Shopping",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.value?.name ?: "User", // Access user.name with .value
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { navController.navigate(NotificationScreen) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onCartClicked,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = "Navigate to Cart",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBar(value: String, onTextChanged: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            placeholder = {
                Text(
                    text = "Search products...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        )
    }
}

@Composable
fun CategoryChip(category: String, selected: Boolean = false) {
    Surface(
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = category.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White
            else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProductItem(product: Product, onClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp)
            .clickable { onClick(product) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "$${product.price}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFC107)
                        )
                        Text(
                            text = "4.5",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HomeContent(
    navController: NavController,
    featured: List<Product>,
    popularProducts: List<Product>,
    categories: List<String>,
    isLoading: Boolean = false,
    errorMsg: String? = null,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClick: (Product) -> Unit,
    onCartClicked: () -> Unit
) {
    val viewAllFeaturedState = remember { mutableStateOf(false) }
    val viewAllPopularState = remember { mutableStateOf(false) }

    LazyColumn {
        item {
            ProfileHeader(onCartClicked = onCartClicked, navController = navController) // Pass navController here
            Spacer(modifier = Modifier.size(16.dp))
            SearchBar(value = searchQuery, onTextChanged = onSearchQueryChange)
            Spacer(modifier = Modifier.size(16.dp))
        }

        item {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                    Text(text = "Loading...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            errorMsg?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }

            // Categories Section
            if (categories.isNotEmpty()) {
                LazyRow {
                    items(categories, key = { it }) { category ->
                        CategoryChip(category = category)
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        // Featured Products Section
        item {
            if (featured.isNotEmpty()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { viewAllFeaturedState.value = !viewAllFeaturedState.value }
                        ) {
                            Text(if (viewAllFeaturedState.value) "Show Less" else "View All")
                        }
                    }

                    if (viewAllFeaturedState.value) {
                        val featuredChunked = featured.chunked(2)
                        featuredChunked.forEach { productPair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                productPair.forEach { product ->
                                    ProductItem(
                                        product = product,
                                        onClick = onClick
                                    )
                                }
                                if (productPair.size == 1) {
                                    Spacer(modifier = Modifier.width(160.dp))
                                }
                            }
                        }
                    } else {
                        LazyRow {
                            items(featured, key = { it.id }) { product ->
                                ProductItem(product = product, onClick = onClick)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        // Popular Products Section
        item {
            if (popularProducts.isNotEmpty()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { viewAllPopularState.value = !viewAllPopularState.value }
                        ) {
                            Text(if (viewAllPopularState.value) "Show Less" else "View All")
                        }
                    }

                    if (viewAllPopularState.value) {
                        val popularChunked = popularProducts.chunked(2)
                        popularChunked.forEach { productPair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                productPair.forEach { product ->
                                    ProductItem(
                                        product = product,
                                        onClick = onClick
                                    )
                                }
                                if (productPair.size == 1) {
                                    Spacer(modifier = Modifier.width(160.dp))
                                }
                            }
                        }
                    } else {
                        LazyRow {
                            items(popularProducts, key = { it.id }) { product ->
                                ProductItem(product = product, onClick = onClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

