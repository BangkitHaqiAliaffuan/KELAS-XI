package com.kelasxi.waveoffood.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.waveoffood.data.models.Food
import com.kelasxi.waveoffood.ui.theme.*
import com.kelasxi.waveoffood.ui.viewmodels.MenuViewModel
import com.kelasxi.waveoffood.ui.viewmodels.CartViewModel
import com.kelasxi.waveoffood.ui.viewmodels.ProfileViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    foodId: String,
    onNavigateBack: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val food by menuViewModel.selectedFood.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()
    val errorMessage by menuViewModel.errorMessage.collectAsState()
    var quantity by remember { mutableStateOf(1) }
    
    LaunchedEffect(foodId) {
        menuViewModel.loadFoodDetail(foodId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = food?.name ?: "Food Detail",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (food != null) {
                        IconButton(
                            onClick = { 
                                profileViewModel.toggleFavoriteFood(food!!.id)
                            }
                        ) {
                            Icon(
                                imageVector = if (profileViewModel.isFavorite(food!!.id)) 
                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (profileViewModel.isFavorite(food!!.id)) 
                                    Color.Red else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (food != null) {
                FoodDetailBottomBar(
                    food = food!!,
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    onAddToCart = {
                        repeat(quantity) {
                            cartViewModel.addToCart(food!!)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryColor
                    )
                }
                
                errorMessage.isNotBlank() -> {
                    ErrorMessage(
                        message = errorMessage,
                        onRetry = { menuViewModel.loadFoodDetail(foodId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                food == null -> {
                    EmptyMessage(
                        message = "Food not found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    FoodDetailContent(
                        food = food!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodDetailContent(
    food: Food,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp), // Space for bottom bar
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Food Image
        item {
            FoodImageSection(food = food)
        }
        
        // Basic Info
        item {
            FoodInfoSection(food = food)
        }
        
        // Description
        item {
            DescriptionSection(description = food.description)
        }
        
        // Ingredients
        item {
            IngredientsSection(ingredients = food.ingredients)
        }
        
        // Nutrition Info
        if (food.nutritionInfo != null) {
            item {
                NutritionSection(nutritionInfo = food.nutritionInfo!!)
            }
        }
        
        // Additional Info
        item {
            AdditionalInfoSection(food = food)
        }
    }
}

@Composable
private fun FoodImageSection(
    food: Food
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = food.imageUrl,
            contentDescription = food.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Popular Badge
        if (food.isPopular) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = PrimaryColor
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Popular",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Popular",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Availability Status
        if (!food.isAvailable) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = "Not Available",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FoodInfoSection(
    food: Food
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Name and Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = formatCurrency(food.price),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Rating, Time, and Category
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            if (food.rating > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB000),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = food.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Preparation Time
            if (food.preparationTime > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Time",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${food.preparationTime} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionSection(
    description: String
) {
    if (description.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun IngredientsSection(
    ingredients: List<String>
) {
    if (ingredients.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingredients) { ingredient ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PrimaryColor.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                PrimaryColor.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = ingredient,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionSection(
    nutritionInfo: Map<String, Any>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nutrition Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                nutritionInfo.forEach { (key, value) ->
                    NutritionItem(
                        label = key.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        value = when (value) {
                            is Number -> value.toString()
                            else -> value.toString()
                        },
                        unit = when (key.lowercase()) {
                            "calories" -> "kcal"
                            "protein", "fat", "carbs" -> "g"
                            else -> ""
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value$unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdditionalInfoSection(
    food: Food
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(
                label = "Category ID",
                value = food.categoryId
            )
            
            InfoRow(
                label = "Food ID",
                value = food.id
            )
            
            InfoRow(
                label = "Availability",
                value = if (food.isAvailable) "Available" else "Not Available"
            )
            
            if (food.createdAt != null) {
                InfoRow(
                    label = "Created",
                    value = formatDate(food.createdAt)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FoodDetailBottomBar(
    food: Food,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quantity Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Qty:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(30.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                enabled = food.isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = Color.White
                    )
                    Text(
                        text = if (food.isAvailable) "Add to Cart" else "Unavailable",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptyMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "Not Found",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
}

private fun formatDate(dateValue: Any?): String {
    return try {
        when (dateValue) {
            is String -> {
                // If it's already a string, format it
                if (dateValue.contains("T")) {
                    dateValue.substringBefore("T")
                } else {
                    dateValue
                }
            }
            is com.google.firebase.Timestamp -> {
                // If it's a Firebase Timestamp, convert to date string
                val date = dateValue.toDate()
                java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            }
            else -> "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}
