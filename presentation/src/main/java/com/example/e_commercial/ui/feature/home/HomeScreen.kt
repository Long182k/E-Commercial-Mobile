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
import com.example.domain.model.Category
import com.example.e_commercial.ui.feature.profile.ProfileViewModel
import androidx.compose.ui.platform.testTag
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import androidx.compose.foundation.border


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsState()
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    val feature = remember { mutableStateOf<List<Product>>(emptyList()) }
    val popular = remember { mutableStateOf<List<Product>>(emptyList()) }
    val categories = remember { mutableStateOf<List<Category>>(emptyList()) }

    val selectedCategoryId = remember { mutableStateOf<Int?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    val filteredFeatured = remember { mutableStateOf<List<Product>>(emptyList()) }
    val filteredPopular = remember { mutableStateOf<List<Product>>(emptyList()) }

    val viewAllFeaturedState = remember { mutableStateOf(false) }
    val viewAllPopularState = remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery.value, selectedCategoryId.value, feature.value, popular.value) {
        val query = searchQuery.value
        
        // First filter by search query
        val searchFilteredFeatured = if (query.isBlank()) feature.value
            else feature.value.filter { it.title.contains(query, ignoreCase = true) }
        
        val searchFilteredPopular = if (query.isBlank()) popular.value
            else popular.value.filter { it.title.contains(query, ignoreCase = true) }
        
        // Then filter by category if one is selected
        filteredFeatured.value = if (selectedCategoryId.value != null) {
            searchFilteredFeatured.filter { it.categoryId == selectedCategoryId.value }
        } else {
            searchFilteredFeatured
        }
        
        filteredPopular.value = if (selectedCategoryId.value != null) {
            searchFilteredPopular.filter { it.categoryId == selectedCategoryId.value }
        } else {
            searchFilteredPopular
        }
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
                .testTag("homeScreen")
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
                                CategoriesSection(
                                    categories = categories.value,
                                    selectedCategoryId = selectedCategoryId.value,
                                    onCategorySelected = { newCategoryId ->
                                        selectedCategoryId.value = newCategoryId
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                               // Best Sellers Section
                               ProductsSection(
                                title = "Best Sellers",
                                products = filteredPopular.value,
                                onClick = {
                                    navController.navigate(ProductDetails(UIProductModel.fromProduct(it)))
                                },
                                viewAllState = viewAllPopularState
                            )

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
fun CategoriesSection(categories: List<Category>, selectedCategoryId: Int? = null, onCategorySelected: (Int?) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                categoryTitle = category.title,
                categoryId = category.id,
                selected = category.id == selectedCategoryId,
                onClick = {
                    // If clicking the already selected category, deselect it
                    if (category.id == selectedCategoryId) {
                        onCategorySelected(null)
                    } else {
                        onCategorySelected(category.id)
                    }
                }
            )
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                // Only show fire icon for Best Sellers section
                if (title == "Best Sellers") {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFFF9800)
                    )
                }
            }
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
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user = viewModel.user.observeAsState()
    val refreshTrigger = viewModel.userRefreshTrigger.collectAsState()
    
    // Refresh user data when trigger changes
    LaunchedEffect(refreshTrigger.value) {
        viewModel.refreshUserData()
    }

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
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.value?.avatarUrl?.let { url ->
                                url.replace("http://", "https://")
                                   .replace("/upload/", "/upload/q_auto,f_auto/")
                            })
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_default_img),
                        placeholder = painterResource(id = R.drawable.ic_default_img)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Online Shopping",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.value?.name ?: "User",
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
fun CategoryChip(
    categoryTitle: String,
    categoryId: Int,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = categoryTitle.replaceFirstChar { it.uppercase() },
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
    LaunchedEffect(product) {
        Log.d("HomeScreen", "Product image URL: ${product.image}")
    }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "${product.sellNumber ?: 0}",
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
    categories: List<Category>,
    isLoading: Boolean = false,
    errorMsg: String? = null,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClick: (Product) -> Unit,
    onCartClicked: () -> Unit,
    selectedCategoryId: Int? = null,
    onCategorySelected: (Int?) -> Unit
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
                    items(categories, key = { it.id }) { category ->
                        CategoryChip(
                            categoryTitle = category.title,
                            categoryId = category.id,
                            selected = category.id == selectedCategoryId,
                            onClick = {
                                // If clicking the already selected category, deselect it
                                if (category.id == selectedCategoryId) {
                                    onCategorySelected(null)
                                } else {
                                    onCategorySelected(category.id)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

  // Best Sellers Section
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
                    text = "Best Sellers",
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

      
    }
}

