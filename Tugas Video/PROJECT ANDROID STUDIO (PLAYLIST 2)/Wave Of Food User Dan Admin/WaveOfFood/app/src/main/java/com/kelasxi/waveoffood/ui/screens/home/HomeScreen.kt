package com.kelasxi.waveoffood.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.waveoffood.data.models.*
import com.kelasxi.waveoffood.ui.theme.*
import com.kelasxi.waveoffood.ui.viewmodels.HomeViewModel
import com.kelasxi.waveoffood.ui.viewmodels.CartViewModel

@Composable
fun HomeScreen(
    onNavigateToRestaurant: (String) -> Unit,
    onNavigateToFood: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit = {}
) {
    val homeViewModel: HomeViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    
    // Observe ViewModels
    val categories by homeViewModel.categories.collectAsState()
    val popularFoods by homeViewModel.popularFoods.collectAsState()
    val recommendedFoods by homeViewModel.recommendedFoods.collectAsState()
    val selectedCategoryId by homeViewModel.selectedCategoryId.collectAsState()
    val foods by homeViewModel.foods.collectAsState()
    val searchResults by homeViewModel.searchResults.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()
    
    // Cart state
    val cartCount by cartViewModel.cartCount.collectAsState()
    
    var searchQueryState by remember { mutableStateOf("") }
    
    // Show error message if any
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // You can show a snackbar or toast here
            homeViewModel.clearError()
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray),
        contentPadding = PaddingValues(bottom = Spacing.xLarge)
    ) {
        // Header Section
        item {
            HomeHeader(
                cartCount = cartCount,
                onProfileClick = onNavigateToProfile,
                onCartClick = onNavigateToCart
            )
        }
        
        // Search Section
        item {
            SearchSection(
                searchQuery = searchQueryState,
                onSearchQueryChange = { 
                    searchQueryState = it
                    homeViewModel.searchFoods(it)
                }
            )
        }
        
        // Show search results if searching
        if (searchQueryState.isNotEmpty() && searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "Search Results (${searchResults.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    ),
                    modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small)
                )
            }
            
            items(searchResults.chunked(2)) { foodPair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    foodPair.forEach { food ->
                        RecommendedFoodCard(
                            food = food,
                            cartViewModel = cartViewModel,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToFood(food.id) }
                        )
                    }
                    
                    if (foodPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else if (searchQueryState.isEmpty()) {
            // Normal home content when not searching
            
            // Categories Section
            item {
                CategoriesSection(
                    categories = categories,
                    selectedCategory = selectedCategoryId,
                    onCategorySelected = { categoryId ->
                        homeViewModel.selectCategory(categoryId)
                    }
                )
            }
            
            // Show foods for selected category
            if (selectedCategoryId.isNotEmpty() && foods.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Foods in ${categories.find { it.id == selectedCategoryId }?.name ?: "Category"}",
                        onSeeAllClick = { }
                    )
                }
                
                items(foods.chunked(2)) { foodPair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        foodPair.forEach { food ->
                            RecommendedFoodCard(
                                food = food,
                                cartViewModel = cartViewModel,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToFood(food.id) }
                            )
                        }
                        
                        if (foodPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            // Banner Section
            item {
                BannerSection()
            }
            
            // Popular Foods Section
            if (popularFoods.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Popular Foods",
                        onSeeAllClick = { }
                    )
                }
                
                item {
                    PopularFoodsSection(
                        foods = popularFoods,
                        cartViewModel = cartViewModel,
                        onFoodClick = onNavigateToFood
                    )
                }
            }
            
            // Recommended Section
            if (recommendedFoods.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recommended for You",
                        onSeeAllClick = { }
                    )
                }
                
                items(recommendedFoods.chunked(2)) { foodPair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        foodPair.forEach { food ->
                            RecommendedFoodCard(
                                food = food,
                                cartViewModel = cartViewModel,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToFood(food.id) }
                            )
                        }
                        
                        if (foodPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else if (searchQueryState.isNotEmpty() && searchResults.isEmpty()) {
            // No search results
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🔍",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text(
                            text = "No foods found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MediumGray
                            )
                        )
                        Text(
                            text = "Try searching with different keywords",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MediumGray
                            )
                        )
                    }
                }
            }
        }
        
        // Loading indicator
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = OrangePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    cartCount: Int = 0,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning! 👋",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = OrangePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Jakarta, Indonesia",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MediumGray
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MediumGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cart Icon with badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .clickable { onCartClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    tint = DarkGray,
                    modifier = Modifier.size(24.dp)
                )
                
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 10.dp, y = (-10).dp)
                            .background(
                                color = OrangePrimary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
            
            // Profile Avatar
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onProfileClick() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = OrangePrimary.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium)
            .padding(bottom = Spacing.medium),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search for restaurants or food",
                    color = MediumGray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MediumGray
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = OrangePrimary,
                    modifier = Modifier.clickable { /* Handle voice search */ }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = OrangePrimary
            ),
            singleLine = true
        )
    }
}

@Composable
private fun CategoriesSection(
    categories: List<Category>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "What do you want to eat?",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGray
            ),
            modifier = Modifier.padding(horizontal = Spacing.medium)
        )
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = category.id == selectedCategory,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "category_scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .scale(scale)
    ) {
        Card(
            modifier = Modifier
                .size(64.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) OrangePrimary else PureWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) Elevation.medium else Elevation.small
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍽️", // Default food icon
                    fontSize = 28.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.small))
        
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) OrangePrimary else MediumGray
            )
        )
    }
}

@Composable
private fun BannerSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(vertical = Spacing.medium)
    ) {
        items(3) { index ->
            PromoBanner(
                title = "Get 50% OFF",
                subtitle = "On your first order",
                backgroundColor = when (index) {
                    0 -> listOf(OrangePrimary, OrangeSecondary)
                    1 -> listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    else -> listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                }
            )
        }
    }
}

@Composable
private fun PromoBanner(
    title: String,
    subtitle: String,
    backgroundColor: List<Color>
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp),
        shape = RoundedCornerShape(CornerRadius.large),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(backgroundColor)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PureWhite.copy(alpha = 0.9f)
                    )
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Button(
                    onClick = { /* Handle promo click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = Spacing.medium, vertical = 4.dp)
                ) {
                    Text(
                        text = "Order Now",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium)
            .padding(top = Spacing.large, bottom = Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )
        )
        
        TextButton(
            onClick = onSeeAllClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = OrangePrimary
            )
        ) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun PopularFoodsSection(
    foods: List<Food>,
    cartViewModel: CartViewModel,
    onFoodClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        items(foods) { food ->
            PopularFoodCard(
                food = food,
                cartViewModel = cartViewModel,
                onClick = { onFoodClick(food.id) }
            )
        }
    }
}

@Composable
private fun PopularFoodCard(
    food: Food,
    cartViewModel: CartViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
    ) {
        Column {
            // Food Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(LightGray)
            ) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.small)
                        .size(32.dp)
                        .background(
                            color = PureWhite.copy(alpha = 0.9f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Food Item", // Placeholder for category name
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MediumGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp ${food.price}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = OrangePrimary,
                                shape = CircleShape
                            )
                            .clickable { cartViewModel.addToCart(food) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to cart",
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendedFoodCard(
    food: Food,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(bottom = Spacing.medium)
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
    ) {
        Column {
            // Food Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(LightGray)
            ) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.small)
                        .size(32.dp)
                        .background(
                            color = PureWhite.copy(alpha = 0.9f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Rating badge
                if (food.rating > 0) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(Spacing.small),
                        colors = CardDefaults.cardColors(containerColor = DarkGray.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", food.rating),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PureWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
            
            // Food Info
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Food Item", // Placeholder for category name
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MediumGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp ${food.price}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    )
                    
                    // Add button
                    Card(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { cartViewModel.addToCart(food) },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = OrangePrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to cart",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
