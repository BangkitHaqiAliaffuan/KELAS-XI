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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.kelasxi.waveoffood.ui.components.*
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import com.kelasxi.waveoffood.models.FoodModel
import kotlinx.coroutines.launch

/**
 * Enhanced Menu Fragment with Material 3 Compose UI
 * Features comprehensive filtering, search, and category browsing
 */
class MenuFragmentWithCompose : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WaveOfFoodTheme {
                    MenuScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreen() {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var sortOption by remember { mutableStateOf("Popular") }
    var isLoading by remember { mutableStateOf(false) }
    var allFoods by remember { mutableStateOf(emptyList<FoodModel>()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val categories = listOf("All", "Pizza", "Burger", "Pasta", "Salad", "Dessert", "Drinks", "Seafood", "Asian", "Mexican")
    val sortOptions = listOf("Popular", "Price: Low to High", "Price: High to Low", "Rating", "Newest")

    // Load data effect
    LaunchedEffect(Unit) {
        isLoading = true
        // Simulate loading
        kotlinx.coroutines.delay(800)
        
        // Create comprehensive sample data
        allFoods = createSampleMenuData()
        isLoading = false
    }

    // Filter foods based on search and category
    val filteredFoods = remember(allFoods, searchText, selectedCategory, sortOption) {
        var filtered = allFoods

        // Filter by category
        if (selectedCategory != "All") {
            filtered = filtered.filter { it.categoryId.equals(selectedCategory, ignoreCase = true) }
        }

        // Filter by search text
        if (searchText.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
            }
        }

        // Sort based on option
        when (sortOption) {
            "Popular" -> filtered.sortedByDescending { it.rating }
            "Price: Low to High" -> filtered.sortedBy { it.price }
            "Price: High to Low" -> filtered.sortedByDescending { it.price }
            "Rating" -> filtered.sortedByDescending { it.rating }
            "Newest" -> filtered // Keep original order for newest
            else -> filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        MenuHeader(
            onFilterClick = { showFilterSheet = true }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Search Bar
        CompactSearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onSearch = { /* Handle search */ }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Category Filter Row
        CategoryFilterRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Results and Sort Info
        ResultsHeader(
            totalResults = filteredFoods.size,
            sortOption = sortOption,
            onSortClick = { showFilterSheet = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Food Grid
        if (isLoading) {
            LoadingSection()
        } else if (filteredFoods.isEmpty()) {
            EmptyStateSection(searchText = searchText, category = selectedCategory)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredFoods) { food ->
                    EnhancedFoodCard(
                        food = food,
                        onClick = { /* Navigate to detail */ },
                        onAddToCart = { /* Add to cart */ }
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            sortOptions = sortOptions,
            selectedSort = sortOption,
            onSortSelected = { sortOption = it },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun MenuHeader(
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Our Menu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Discover delicious dishes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = onFilterClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter"
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = selectedCategory == category,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedCategory == category,
                    borderColor = if (selectedCategory == category) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun ResultsHeader(
    totalResults: Int,
    sortOption: String,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$totalResults items found",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        TextButton(
            onClick = onSortClick
        ) {
            Text(
                text = "Sort: $sortOption",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Text(
                text = "Loading menu...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateSection(
    searchText: String,
    category: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🍽️",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "No items found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (searchText.isNotBlank()) {
                    "Try adjusting your search or filters"
                } else {
                    "No items available in $category category"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    sortOptions: List<String>,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Sort Options",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            sortOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSort == option,
                        onClick = { 
                            onSortSelected(option)
                            onDismiss()
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun createSampleMenuData(): List<FoodModel> {
    return listOf(
        // Pizza Category
        FoodModel().apply {
            id = "1"; name = "Margherita Pizza"; description = "Classic Italian pizza with fresh tomatoes and mozzarella"
            imageUrl = "https://picsum.photos/300/200?random=1"; price = 1299L; categoryId = "Pizza"
            isPopular = true; rating = 4.5; isAvailable = true; preparationTime = 20
        },
        FoodModel().apply {
            id = "2"; name = "Pepperoni Pizza"; description = "Spicy pepperoni with melted cheese"
            imageUrl = "https://picsum.photos/300/200?random=2"; price = 1499L; categoryId = "Pizza"
            isPopular = true; rating = 4.6; isAvailable = true; preparationTime = 22
        },
        FoodModel().apply {
            id = "3"; name = "BBQ Chicken Pizza"; description = "Grilled chicken with BBQ sauce and onions"
            imageUrl = "https://picsum.photos/300/200?random=3"; price = 1699L; categoryId = "Pizza"
            isPopular = false; rating = 4.3; isAvailable = true; preparationTime = 25
        },
        
        // Burger Category
        FoodModel().apply {
            id = "4"; name = "Classic Beef Burger"; description = "Juicy beef patty with lettuce, tomato, and cheese"
            imageUrl = "https://picsum.photos/300/200?random=4"; price = 899L; categoryId = "Burger"
            isPopular = true; rating = 4.4; isAvailable = true; preparationTime = 15
        },
        FoodModel().apply {
            id = "5"; name = "Chicken Deluxe"; description = "Grilled chicken with avocado and special sauce"
            imageUrl = "https://picsum.photos/300/200?random=5"; price = 1099L; categoryId = "Burger"
            isPopular = true; rating = 4.2; isAvailable = true; preparationTime = 18
        },
        FoodModel().apply {
            id = "6"; name = "Veggie Burger"; description = "Plant-based patty with fresh vegetables"
            imageUrl = "https://picsum.photos/300/200?random=6"; price = 799L; categoryId = "Burger"
            isPopular = false; rating = 4.0; isAvailable = true; preparationTime = 12
        },
        
        // Pasta Category
        FoodModel().apply {
            id = "7"; name = "Spaghetti Carbonara"; description = "Creamy pasta with bacon and parmesan"
            imageUrl = "https://picsum.photos/300/200?random=7"; price = 1199L; categoryId = "Pasta"
            isPopular = true; rating = 4.6; isAvailable = true; preparationTime = 18
        },
        FoodModel().apply {
            id = "8"; name = "Penne Arrabbiata"; description = "Spicy tomato sauce with garlic and herbs"
            imageUrl = "https://picsum.photos/300/200?random=8"; price = 999L; categoryId = "Pasta"
            isPopular = false; rating = 4.1; isAvailable = true; preparationTime = 16
        },
        
        // Salad Category
        FoodModel().apply {
            id = "9"; name = "Caesar Salad"; description = "Crisp romaine with parmesan and croutons"
            imageUrl = "https://picsum.photos/300/200?random=9"; price = 799L; categoryId = "Salad"
            isPopular = true; rating = 4.2; isAvailable = true; preparationTime = 10
        },
        FoodModel().apply {
            id = "10"; name = "Greek Salad"; description = "Fresh vegetables with feta cheese and olives"
            imageUrl = "https://picsum.photos/300/200?random=10"; price = 899L; categoryId = "Salad"
            isPopular = false; rating = 4.3; isAvailable = true; preparationTime = 8
        },
        
        // Dessert Category
        FoodModel().apply {
            id = "11"; name = "Chocolate Cake"; description = "Rich chocolate cake with layers of frosting"
            imageUrl = "https://picsum.photos/300/200?random=11"; price = 699L; categoryId = "Dessert"
            isPopular = true; rating = 4.4; isAvailable = true; preparationTime = 5
        },
        FoodModel().apply {
            id = "12"; name = "Tiramisu"; description = "Classic Italian dessert with coffee and mascarpone"
            imageUrl = "https://picsum.photos/300/200?random=12"; price = 799L; categoryId = "Dessert"
            isPopular = true; rating = 4.5; isAvailable = true; preparationTime = 3
        },
        
        // Drinks Category
        FoodModel().apply {
            id = "13"; name = "Fresh Orange Juice"; description = "Freshly squeezed orange juice"
            imageUrl = "https://picsum.photos/300/200?random=13"; price = 399L; categoryId = "Drinks"
            isPopular = false; rating = 4.0; isAvailable = true; preparationTime = 2
        },
        FoodModel().apply {
            id = "14"; name = "Iced Coffee"; description = "Cold brew coffee with ice and cream"
            imageUrl = "https://picsum.photos/300/200?random=14"; price = 499L; categoryId = "Drinks"
            isPopular = true; rating = 4.1; isAvailable = true; preparationTime = 3
        },
        
        // Seafood Category
        FoodModel().apply {
            id = "15"; name = "Grilled Salmon"; description = "Fresh salmon with herbs and lemon"
            imageUrl = "https://picsum.photos/300/200?random=15"; price = 1899L; categoryId = "Seafood"
            isPopular = true; rating = 4.7; isAvailable = true; preparationTime = 20
        },
        FoodModel().apply {
            id = "16"; name = "Fish & Chips"; description = "Crispy battered fish with golden fries"
            imageUrl = "https://picsum.photos/300/200?random=16"; price = 1299L; categoryId = "Seafood"
            isPopular = true; rating = 4.3; isAvailable = true; preparationTime = 25
        }
    )
}
