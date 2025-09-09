package com.kelasxi.waveoffood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.kelasxi.waveoffood.data.models.User
import com.kelasxi.waveoffood.data.models.Order
import com.kelasxi.waveoffood.data.models.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory
    
    private val _favoriteFoods = MutableStateFlow<List<Food>>(emptyList())
    val favoriteFoods: StateFlow<List<Food>> = _favoriteFoods
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage
    
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()
                    
                    if (userDoc.exists()) {
                        val user = userDoc.toObject(User::class.java)
                        _user.value = user
                    } else {
                        // Create user document if doesn't exist
                        val newUser = User(
                            uid = currentUser.uid,
                            email = currentUser.email ?: "",
                            name = currentUser.displayName ?: "User"
                        )
                        firestore.collection("users")
                            .document(currentUser.uid)
                            .set(newUser)
                            .await()
                        _user.value = newUser
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateUserProfile(
        name: String,
        phone: String,
        address: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val updates = mapOf(
                        "name" to name,
                        "phone" to phone,
                        "address" to address,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .update(updates)
                        .await()
                    
                    // Update local state
                    _user.value = _user.value?.copy(
                        name = name,
                        phone = phone,
                        address = address
                    )
                    
                    onSuccess()
                } else {
                    onError("User not authenticated")
                }
            } catch (e: Exception) {
                onError("Failed to update profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadOrderHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val ordersSnapshot = firestore.collection("orders")
                        .whereEqualTo("userId", currentUser.uid)
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .await()
                    
                    val orders = ordersSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)
                    }
                    
                    _orderHistory.value = orders
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load order history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadFavoriteFoods() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = _user.value
                if (currentUser != null && currentUser.favoritefoods.isNotEmpty()) {
                    val foodsSnapshot = firestore.collection("foods")
                        .whereIn("id", currentUser.favoritefoods)
                        .get()
                        .await()
                    
                    val foods = foodsSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Food::class.java)
                    }
                    
                    _favoriteFoods.value = foods
                } else {
                    _favoriteFoods.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load favorite foods: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavoriteFood(foodId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                val user = _user.value
                if (currentUser != null && user != null) {
                    val currentFavorites = user.favoritefoods.toMutableList()
                    
                    if (currentFavorites.contains(foodId)) {
                        currentFavorites.remove(foodId)
                    } else {
                        currentFavorites.add(foodId)
                    }
                    
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .update("favoritefoods", currentFavorites)
                        .await()
                    
                    // Update local state
                    _user.value = user.copy(favoritefoods = currentFavorites)
                    
                    // Reload favorite foods
                    loadFavoriteFoods()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update favorites: ${e.message}"
            }
        }
    }
    
    fun isFavorite(foodId: String): Boolean {
        return _user.value?.favoritefoods?.contains(foodId) == true
    }
    
    fun signOut() {
        auth.signOut()
        _user.value = null
        _orderHistory.value = emptyList()
        _favoriteFoods.value = emptyList()
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}
