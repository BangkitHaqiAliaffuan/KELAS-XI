package com.kelasxi.waveoffood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.kelasxi.waveoffood.ui.components.*
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import com.kelasxi.waveoffood.models.FoodModel
import com.kelasxi.waveoffood.models.CartItemModel
import com.kelasxi.waveoffood.repository.FirebaseRepository
import com.kelasxi.waveoffood.utils.CartManager
import android.widget.Toast
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Enhanced Home Fragment with Material 3 Compose UI
 * Integrates with Firebase data and provides modern UI
 */
class HomeFragmentWithCompose : Fragment() {

    private val firebaseRepository = FirebaseRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WaveOfFoodTheme {
                    HomeScreen(
                        onAddToCart = { food -> addToCart(food) }
                    )
                }
            }
        }
    }

    private fun addToCart(food: FoodModel) {
        try {
            // Convert FoodModel to CartItemModel using food ID as identifier
            val cartItem = CartItemModel(
                id = food.id,  // Use food ID instead of random UUID
                foodName = food.name,
                foodPrice = food.getFormattedPrice(),
                foodDescription = food.description,
                foodImage = food.imageUrl,
                foodCategory = food.categoryId,
                quantity = 1
            )
            
            // Add to cart using CartManager
            CartManager.addToCart(cartItem)
            
            // Show success toast
            context?.let { ctx ->
                Toast.makeText(ctx, "✅ ${food.name} berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("HomeFragmentWithCompose", "Error adding to cart", e)
            context?.let { ctx ->
                Toast.makeText(ctx, "Error menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddToCart: (FoodModel) -> Unit = {}
) {
    // Simple state management
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var popularFoods by remember { mutableStateOf(emptyList<FoodModel>()) }
    var categories by remember { mutableStateOf(listOf("Pizza", "Burger", "Pasta", "Salad", "Dessert", "Drinks")) }

    // Load data effect
    LaunchedEffect(Unit) {
        isLoading = true
        // Simulate loading
        kotlinx.coroutines.delay(1000)
        
        // Create sample data
        popularFoods = listOf(
            FoodModel().apply {
                id = "1"
                name = "Margherita Pizza"
                description = "Classic Italian pizza with fresh tomatoes and mozzarella"
                imageUrl = "https://picsum.photos/300/200?random=1"
                price = 1299L // 12.99 in cents
                categoryId = "pizza"
                isPopular = true
                rating = 4.5
                isAvailable = true
                preparationTime = 20
            },
            FoodModel().apply {
                id = "2"
                name = "Chicken Burger"
                description = "Juicy grilled chicken with fresh lettuce and tomato"
                imageUrl = "https://picsum.photos/300/200?random=2"
                price = 899L // 8.99 in cents
                categoryId = "burger"
                isPopular = true
                rating = 4.3
                isAvailable = true
                preparationTime = 15
            },
            FoodModel().apply {
                id = "3"
                name = "Caesar Salad"
                description = "Fresh romaine lettuce with parmesan and croutons"
                imageUrl = "https://picsum.photos/300/200?random=3"
                price = 799L // 7.99 in cents
                categoryId = "salad"
                isPopular = true
                rating = 4.2
                isAvailable = true
                preparationTime = 10
            },
            FoodModel().apply {
                id = "4"
                name = "Pasta Carbonara"
                description = "Creamy pasta with bacon and parmesan cheese"
                imageUrl = "https://picsum.photos/300/200?random=4"
                price = 1199L // 11.99 in cents
                categoryId = "pasta"
                isPopular = true
                rating = 4.6
                isAvailable = true
                preparationTime = 18
            },
            FoodModel().apply {
                id = "5"
                name = "Chocolate Cake"
                description = "Rich chocolate cake with layers of frosting"
                imageUrl = "https://picsum.photos/300/200?random=5"
                price = 699L // 6.99 in cents
                categoryId = "dessert"
                isPopular = true
                rating = 4.4
                isAvailable = true
                preparationTime = 5
            },
            FoodModel().apply {
                id = "6"
                name = "Fish & Chips"
                description = "Crispy battered fish with golden fries"
                imageUrl = "https://picsum.photos/300/200?random=6"
                price = 999L // 9.99 in cents
                categoryId = "seafood"
                isPopular = true
                rating = 4.1
                isAvailable = true
                preparationTime = 25
            }
        )
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Consistency with navbar padding
    ) {
        // Location Header
        LocationHeader(
            location = "Jakarta, Indonesia",
            onLocationClick = { /* Handle location selection */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp)) // Reduced spacing for consistency
        
        // Search Bar
        CompactSearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onSearch = { /* Handle search */ }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.padding(24.dp),
                    shape = RoundedCornerShape(20.dp), // Consistency with navbar rounded corners
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp, // Reduced elevation for subtle effect
                    tonalElevation = 0.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp) // More generous padding for loading state
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35), // Use exact navbar orange color
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Loading delicious food...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium // Consistency with navbar font weight
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp), // Consistent spacing
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp) // Proper padding for navbar (80dp + 8dp)
            ) {
                // Categories Section
                if (categories.isNotEmpty()) {
                    item {
                        CategoriesSection(
                            categories = categories,
                            onCategoryClick = { category ->
                                // Handle category selection
                            }
                        )
                    }
                }
                
                // Popular Foods Section
                if (popularFoods.isNotEmpty()) {
                    item {
                        PopularFoodsSection(
                            foods = popularFoods,
                            onFoodClick = { food ->
                                // Handle food selection
                            },
                            onAddToCart = { food ->
                                onAddToCart(food)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationHeader(
    location: String,
    onLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Greeting Text
        Text(
            text = "Good morning! 👋",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Delivery Location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color(0xFFFF6B35), // Use navbar orange color
                modifier = Modifier.size(18.dp)
            )
            
            Text(
                text = "Deliver to ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold, // Consistency with navbar bold text
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            TextButton(
                onClick = { /* Navigate to all categories */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF6B35) // Use navbar orange color
                )
            ) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold // Consistency with navbar bold text
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Consistency with navbar padding
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
private fun PopularFoodsSection(
    foods: List<FoodModel>,
    onFoodClick: (FoodModel) -> Unit,
    onAddToCart: (FoodModel) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Foods",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            TextButton(
                onClick = { /* Navigate to all popular foods */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF6B35) // Use navbar orange color
                )
            ) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold // Consistency with navbar bold text
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Consistency with navbar
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(520.dp), // Adjusted for better card display
            contentPadding = PaddingValues(4.dp)
        ) {
            items(foods) { food ->
                EnhancedFoodCard(
                    food = food,
                    onClick = { onFoodClick(food) },
                    onAddToCart = { onAddToCart(food) }
                )
            }
        }
    }
}
