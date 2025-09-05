package com.kelasxi.waveoffood.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.waveoffood.data.models.*
import com.kelasxi.waveoffood.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    // Categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    // Foods
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods.asStateFlow()
    
    private val _popularFoods = MutableStateFlow<List<Food>>(emptyList())
    val popularFoods: StateFlow<List<Food>> = _popularFoods.asStateFlow()
    
    private val _recommendedFoods = MutableStateFlow<List<Food>>(emptyList())
    val recommendedFoods: StateFlow<List<Food>> = _recommendedFoods.asStateFlow()
    
    // Search
    private val _searchResults = MutableStateFlow<List<Food>>(emptyList())
    val searchResults: StateFlow<List<Food>> = _searchResults.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isLoadingCategories = MutableStateFlow(false)
    val isLoadingCategories: StateFlow<Boolean> = _isLoadingCategories.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Selected category
    private val _selectedCategoryId = MutableStateFlow("")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        Log.d(TAG, "Loading initial data - categories, popular foods, and recommended foods")
        loadCategories()
        loadPopularFoods()
        loadRecommendedFoods()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            Log.d(TAG, "Loading categories...")
            _isLoadingCategories.value = true
            repository.getCategories()
                .onSuccess { categories ->
                    Log.d(TAG, "Categories loaded successfully: ${categories.size} items")
                    _categories.value = categories
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load categories: ${exception.message}", exception)
                    _errorMessage.value = "Failed to load categories: ${exception.message}"
                }
            _isLoadingCategories.value = false
        }
    }
    
    fun loadPopularFoods() {
        viewModelScope.launch {
            Log.d(TAG, "Loading popular foods...")
            _isLoading.value = true
            repository.getPopularFoods()
                .onSuccess { foods ->
                    Log.d(TAG, "Popular foods loaded successfully: ${foods.size} items")
                    _popularFoods.value = foods
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load popular foods: ${exception.message}", exception)
                    _errorMessage.value = "Failed to load popular foods: ${exception.message}"
                }
            _isLoading.value = false
        }
    }
    
    fun loadRecommendedFoods() {
        viewModelScope.launch {
            Log.d(TAG, "Loading recommended foods...")
            _isLoading.value = true
            repository.getRecommendedFoods()
                .onSuccess { foods ->
                    Log.d(TAG, "Recommended foods loaded successfully: ${foods.size} items")
                    _recommendedFoods.value = foods
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load recommended foods: ${exception.message}", exception)
                    _errorMessage.value = "Failed to load recommended foods: ${exception.message}"
                }
            _isLoading.value = false
        }
    }
    
    fun selectCategory(categoryId: String) {
        Log.d(TAG, "Selecting category: $categoryId")
        _selectedCategoryId.value = categoryId
        if (categoryId.isEmpty()) {
            _foods.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            Log.d(TAG, "Loading foods for category: $categoryId")
            _isLoading.value = true
            repository.getFoodsByCategory(categoryId)
                .onSuccess { foods ->
                    Log.d(TAG, "Foods for category '$categoryId' loaded successfully: ${foods.size} items")
                    _foods.value = foods
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load foods for category '$categoryId': ${exception.message}", exception)
                    _errorMessage.value = "Failed to load foods for category: ${exception.message}"
                }
            _isLoading.value = false
        }
    }
    
    fun searchFoods(query: String) {
        Log.d(TAG, "Searching foods with query: '$query'")
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            repository.searchFoods(query.trim())
                .onSuccess { foods ->
                    Log.d(TAG, "Search completed: ${foods.size} foods found for query '$query'")
                    _searchResults.value = foods
                }
                .onFailure { exception ->
                    Log.e(TAG, "Search failed for query '$query': ${exception.message}", exception)
                    _errorMessage.value = "Search failed: ${exception.message}"
                }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun refresh() {
        Log.d(TAG, "Refreshing all data")
        clearError()
        loadInitialData()
    }
}
