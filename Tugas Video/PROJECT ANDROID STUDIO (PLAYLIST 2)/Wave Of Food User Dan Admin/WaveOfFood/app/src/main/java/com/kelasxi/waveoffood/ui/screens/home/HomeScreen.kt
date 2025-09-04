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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kelasxi.waveoffood.ui.theme.*

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val isSelected: Boolean = false
)

data class Restaurant(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Float,
    val deliveryTime: String,
    val deliveryFee: String,
    val isFreeDelivery: Boolean = false,
    val isPopular: Boolean = false
)

data class Food(
    val id: String,
    val name: String,
    val restaurant: String,
    val price: Double,
    val imageUrl: String,
    val rating: Float,
    val isRecommended: Boolean = false
)

@Composable
fun HomeScreen(
    onNavigateToRestaurant: (String) -> Unit,
    onNavigateToFood: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    
    // Sample data
    val categories = listOf(
        Category("pizza", "Pizza", "🍕"),
        Category("burger", "Burger", "🍔"),
        Category("sushi", "Sushi", "🍣"),
        Category("pasta", "Pasta", "🍝"),
        Category("dessert", "Dessert", "🍰"),
        Category("drinks", "Drinks", "🥤")
    )
    
    val restaurants = listOf(
        Restaurant("1", "Pizza Palace", "", 4.5f, "25-30 min", "$2.99", true, true),
        Restaurant("2", "Burger King", "", 4.2f, "20-25 min", "$1.99", false, true),
        Restaurant("3", "Sushi Master", "", 4.8f, "30-35 min", "$3.99", false, false),
        Restaurant("4", "Pasta House", "", 4.3f, "15-20 min", "Free", true, false)
    )
    
    val recommendedFoods = listOf(
        Food("1", "Margherita Pizza", "Pizza Palace", 12.99, "", 4.5f, true),
        Food("2", "Chicken Burger", "Burger King", 8.99, "", 4.2f, true),
        Food("3", "Salmon Roll", "Sushi Master", 15.99, "", 4.8f, true),
        Food("4", "Carbonara", "Pasta House", 11.99, "", 4.3f, false)
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray),
        contentPadding = PaddingValues(bottom = Spacing.xLarge)
    ) {
        // Header Section
        item {
            HomeHeader(
                onProfileClick = onNavigateToProfile
            )
        }
        
        // Search Section
        item {
            SearchSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
        }
        
        // Categories Section
        item {
            CategoriesSection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }
        
        // Banner Section
        item {
            BannerSection()
        }
        
        // Popular Near You Section
        item {
            SectionHeader(
                title = "Popular Near You",
                onSeeAllClick = { /* Navigate to restaurants list */ }
            )
        }
        
        item {
            PopularRestaurantsSection(
                restaurants = restaurants,
                onRestaurantClick = onNavigateToRestaurant
            )
        }
        
        // Recommended Section
        item {
            SectionHeader(
                title = "Recommended for You",
                onSeeAllClick = { /* Navigate to recommended foods */ }
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
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToFood(food.id) }
                    )
                }
                
                // Add empty space if odd number of items
                if (foodPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onProfileClick: () -> Unit
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
        modifier = Modifier.clickable { onClick() }
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
                    text = category.icon,
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
private fun PopularRestaurantsSection(
    restaurants: List<Restaurant>,
    onRestaurantClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        items(restaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
    ) {
        Column {
            // Restaurant Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(OrangePrimary.copy(0.3f), OrangeSecondary.copy(0.1f))
                        )
                    )
            ) {
                // Placeholder for restaurant image
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏪",
                        fontSize = 48.sp
                    )
                }
                
                // Favorite button
                IconButton(
                    onClick = { /* Handle favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = PureWhite,
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    )
                }
                
                // Free delivery badge
                if (restaurant.isFreeDelivery) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(Spacing.small),
                        colors = CardDefaults.cardColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Free Delivery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PureWhite,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Restaurant Info
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = YellowRating,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = restaurant.rating.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MediumGray
                        )
                    )
                    Text(
                        text = restaurant.deliveryFee,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (restaurant.isFreeDelivery) GreenSuccess else MediumGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedFoodCard(
    food: Food,
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
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangePrimary.copy(0.2f),
                                OrangeSecondary.copy(0.1f)
                            )
                        )
                    )
            ) {
                // Placeholder for food image
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = 48.sp
                    )
                }
                
                // Recommended badge
                if (food.isRecommended) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(Spacing.small),
                        colors = CardDefaults.cardColors(containerColor = RedAccent),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Recommended",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PureWhite,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
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
                    text = food.restaurant,
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
                        text = "$${food.price}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    )
                    
                    // Add button
                    Card(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onClick() },
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
