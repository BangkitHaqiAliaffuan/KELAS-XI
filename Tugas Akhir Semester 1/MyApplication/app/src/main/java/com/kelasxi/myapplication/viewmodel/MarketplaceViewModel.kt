package com.kelasxi.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MarketplaceViewModel : ViewModel() {
    private val _allProducts = MutableStateFlow(MockData.products)
    private val _filteredProducts = MutableStateFlow(MockData.products)
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun selectProduct(product: Product) { _selectedProduct.value = product }
    fun clearProduct() { _selectedProduct.value = null }

    fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (current.contains(productId)) current.remove(productId) else current.add(productId)
        _wishlist.value = current
    }

    fun addToCart() { _cartCount.value++ }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val category = _selectedCategory.value
        _filteredProducts.value = _allProducts.value.filter { product ->
            val matchesCategory = category == ProductCategory.ALL || product.category == category
            val matchesQuery = query.isEmpty() || product.name.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
    }
}
