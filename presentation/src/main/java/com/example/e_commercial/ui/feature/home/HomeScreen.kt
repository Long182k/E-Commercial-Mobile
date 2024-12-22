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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.style.TextAlign
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

    val viewAllFeaturedState = remember { mutableStateOf(false) }
    val viewAllPopularState = remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery.value, feature.value, popular.value) {
        val query = searchQuery.value
        filteredFeatured.value = if (query.isBlank()) feature.value
        else feature.value.filter { it.title.contains(query, ignoreCase = true) }

        filteredPopular.value = if (query.isBlank()) popular.value
        else popular.value.filter { it.title.contains(query, ignoreCase = true) }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // White background
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // Profile Header
                    ProfileHeader(
                        onCartClicked = {
                            navController.navigate(CartScreen) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        navController = navController
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    SearchBar(
                        value = searchQuery.value,
                        onTextChanged = { searchQuery.value = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    when (val state = uiState.value) {
                        is HomeScreenUIEvents.Loading -> {
                            LoadingState()
                        }
                        is HomeScreenUIEvents.Error -> {
                            ErrorState(errorMessage = state.message)
                        }
                        is HomeScreenUIEvents.Success -> {
                            feature.value = state.featured
                            popular.value = state.popularProducts
                            categories.value = state.categories

                            // Categories Section
                            if (categories.value.isNotEmpty()) {
                                CategoriesSection(categories = categories.value)
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Featured Products Section
                            ProductsSection(
                                title = "Featured Products",
                                products = filteredFeatured.value,
                                onClick = {
                                    navController.navigate(ProductDetails(UIProductModel.fromProduct(it)))
                                },
                                viewAllState = viewAllFeaturedState
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Popular Products Section
                            ProductsSection(
                                title = "Popular Products",
                                products = filteredPopular.value,
                                onClick = {
                                    navController.navigate(ProductDetails(UIProductModel.fromProduct(it)))
                                },
                                viewAllState = viewAllPopularState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
@Composable
fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun CategoriesSection(categories: List<String>) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(category = category)
        }
    }
}
@Composable
fun ProductsSection(
    title: String,
    products: List<Product>,
    onClick: (Product) -> Unit,
    viewAllState: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(
                onClick = { viewAllState.value = !viewAllState.value }
            ) {
                Text(
                    text = if (viewAllState.value) "Show Less" else "View All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        if (viewAllState.value) {
            val chunkedProducts = products.chunked(2)
            chunkedProducts.forEach { productPair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    productPair.forEach { product ->
                        ProductItem(product = product, onClick = onClick)
                    }
                    if (productPair.size == 1) {
                        Spacer(modifier = Modifier.width(160.dp))
                    }
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(products) { product ->
                    ProductItem(product = product, onClick = onClick)
                }
            }
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

