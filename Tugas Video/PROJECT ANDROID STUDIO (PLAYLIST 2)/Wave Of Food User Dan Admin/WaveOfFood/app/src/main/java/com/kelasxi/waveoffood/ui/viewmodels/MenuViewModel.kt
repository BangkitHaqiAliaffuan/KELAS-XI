package com.kelasxi.waveoffood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kelasxi.waveoffood.data.models.Category
import com.kelasxi.waveoffood.data.models.Food
import com.kelasxi.waveoffood.ui.screens.menu.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class MenuViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // State flows
    private val _allFoods = MutableStateFlow<List<Food>>(emptyList())
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _selectedFood = MutableStateFlow<Food?>(null)
    val selectedFood: StateFlow<Food?> = _selectedFood.asStateFlow()

    // Filtered foods based on search and category
    val filteredFoods: StateFlow<List<Food>> = combine(
        _allFoods,
        _searchQuery,
        _selectedCategoryId
    ) { foods, query, categoryId ->
        filterFoods(foods, query, categoryId)
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                // Load categories and foods in parallel
                val categoriesDeferred = async { loadCategories() }
                val foodsDeferred = async { loadFoods() }
                
                categoriesDeferred.await()
                foodsDeferred.await()
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load menu data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadCategories() {
        try {
            val snapshot = firestore.collection("categories").get().await()
            val categoryList = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Category>()?.copy(id = doc.id)
            }.sortedBy { it.name }
            _categories.value = categoryList
        } catch (e: Exception) {
            throw Exception("Failed to load categories: ${e.message}")
        }
    }

    private suspend fun loadFoods() {
        try {
            val snapshot = firestore.collection("foods").get().await()
            val foodList = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Food>()?.copy(id = doc.id)
            }.sortedBy { it.name }
            _allFoods.value = foodList
        } catch (e: Exception) {
            throw Exception("Failed to load foods: ${e.message}")
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategoryId.value = null
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.GRID) {
            ViewMode.LIST
        } else {
            ViewMode.GRID
        }
    }

    fun loadFoodDetail(foodId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                // First check if food is already in allFoods
                val existingFood = _allFoods.value.find { it.id == foodId }
                if (existingFood != null) {
                    _selectedFood.value = existingFood
                } else {
                    // Load from Firestore
                    val doc = firestore.collection("foods").document(foodId).get().await()
                    if (doc.exists()) {
                        val food = doc.toObject<Food>()?.copy(id = doc.id)
                        _selectedFood.value = food
                    } else {
                        _selectedFood.value = null
                        _errorMessage.value = "Food not found"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load food detail: ${e.message}"
                _selectedFood.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterFoods(
        foods: List<Food>,
        query: String,
        categoryId: String?
    ): List<Food> {
        var filteredList = foods

        // Filter by category
        if (categoryId != null) {
            filteredList = filteredList.filter { food ->
                food.categoryId == categoryId
            }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val lowercaseQuery = query.lowercase().trim()
            filteredList = filteredList.filter { food ->
                food.name.lowercase().contains(lowercaseQuery) ||
                food.description.lowercase().contains(lowercaseQuery) ||
                food.ingredients.any { ingredient ->
                    ingredient.lowercase().contains(lowercaseQuery)
                }
            }
        }

        return filteredList
    }

    // Additional filter functions
    fun filterByPrice(minPrice: Long? = null, maxPrice: Long? = null) {
        val currentFoods = _allFoods.value
        val filteredList = currentFoods.filter { food ->
            val priceInRange = when {
                minPrice != null && maxPrice != null -> food.price in minPrice..maxPrice
                minPrice != null -> food.price >= minPrice
                maxPrice != null -> food.price <= maxPrice
                else -> true
            }
            priceInRange
        }
        // Apply this filter to current search and category filters
        _allFoods.value = filteredList
    }

    fun filterByRating(minRating: Double) {
        val currentFoods = _allFoods.value
        val filteredList = currentFoods.filter { food ->
            food.rating >= minRating
        }
        _allFoods.value = filteredList
    }

    fun filterByPreparationTime(maxTime: Int) {
        val currentFoods = _allFoods.value
        val filteredList = currentFoods.filter { food ->
            food.preparationTime <= maxTime
        }
        _allFoods.value = filteredList
    }

    fun sortFoods(sortBy: SortOption) {
        val currentFoods = _allFoods.value
        val sortedList = when (sortBy) {
            SortOption.NAME_ASC -> currentFoods.sortedBy { it.name }
            SortOption.NAME_DESC -> currentFoods.sortedByDescending { it.name }
            SortOption.PRICE_ASC -> currentFoods.sortedBy { it.price }
            SortOption.PRICE_DESC -> currentFoods.sortedByDescending { it.price }
            SortOption.RATING_DESC -> currentFoods.sortedByDescending { it.rating }
            SortOption.POPULAR -> currentFoods.sortedByDescending { if (it.isPopular) 1 else 0 }
            SortOption.PREPARATION_TIME -> currentFoods.sortedBy { it.preparationTime }
        }
        _allFoods.value = sortedList
    }

    fun getFoodsByCategory(categoryId: String): List<Food> {
        return _allFoods.value.filter { it.categoryId == categoryId }
    }

    fun getPopularFoods(): List<Food> {
        return _allFoods.value.filter { it.isPopular }
    }

    fun getFoodById(foodId: String): Food? {
        return _allFoods.value.find { it.id == foodId }
    }

    fun refreshData() {
        loadAllData()
    }
}

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    RATING_DESC,
    POPULAR,
    PREPARATION_TIME
}
