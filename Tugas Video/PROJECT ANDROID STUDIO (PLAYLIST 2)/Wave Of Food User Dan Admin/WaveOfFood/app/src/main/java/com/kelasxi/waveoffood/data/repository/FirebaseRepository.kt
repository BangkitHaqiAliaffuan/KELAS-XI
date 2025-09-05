package com.kelasxi.waveoffood.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelasxi.waveoffood.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        const val TAG = "FirebaseRepository"
        const val CATEGORIES_COLLECTION = "categories"
        const val FOODS_COLLECTION = "foods"
        const val USERS_COLLECTION = "users"
        const val ORDERS_COLLECTION = "orders"
    }
    
    // ===== CATEGORIES =====
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            Log.d(TAG, "Fetching categories...")
            
            // Simplified query to avoid index issues
            val snapshot = db.collection(CATEGORIES_COLLECTION)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val categories = snapshot.toObjects(Category::class.java)
            Log.d(TAG, "Categories fetched successfully: ${categories.size} items")
            
            // Sort manually to avoid composite index requirement
            val sortedCategories = categories.sortedBy { it.name }
            Result.success(sortedCategories)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching categories: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getCategoriesFlow(): Flow<List<Category>> = flow {
        try {
            val snapshot = db.collection(CATEGORIES_COLLECTION)
                .whereEqualTo("isActive", true)
                .orderBy("name")
                .get()
                .await()
            
            val categories = snapshot.toObjects(Category::class.java)
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    // ===== FOODS =====
    suspend fun getFoods(): Result<List<Food>> {
        return try {
            Log.d(TAG, "Fetching all foods...")
            
            val snapshot = db.collection(FOODS_COLLECTION)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            
            val foods = snapshot.toObjects(Food::class.java)
            Log.d(TAG, "Foods fetched successfully: ${foods.size} items")
            
            // Sort manually
            val sortedFoods = foods.sortedBy { it.name }
            Result.success(sortedFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching foods: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getFoodsByCategory(categoryId: String): Result<List<Food>> {
        return try {
            Log.d(TAG, "Fetching foods for category: $categoryId")
            
            val snapshot = db.collection(FOODS_COLLECTION)
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            
            val foods = snapshot.toObjects(Food::class.java)
            Log.d(TAG, "Foods by category fetched: ${foods.size} items for category $categoryId")
            
            // Sort manually
            val sortedFoods = foods.sortedBy { it.name }
            Result.success(sortedFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching foods by category: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPopularFoods(): Result<List<Food>> {
        return try {
            val snapshot = db.collection(FOODS_COLLECTION)
                .whereEqualTo("isPopular", true)
                .whereEqualTo("isAvailable", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            
            val foods = snapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRecommendedFoods(): Result<List<Food>> {
        return try {
            val snapshot = db.collection(FOODS_COLLECTION)
                .whereEqualTo("isAvailable", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(8)
                .get()
                .await()
            
            val foods = snapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodById(foodId: String): Result<Food?> {
        return try {
            val snapshot = db.collection(FOODS_COLLECTION)
                .document(foodId)
                .get()
                .await()
            
            val food = snapshot.toObject(Food::class.java)
            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchFoods(query: String): Result<List<Food>> {
        return try {
            Log.d(TAG, "Searching foods with query: $query")
            
            // Get all available foods first, then filter manually
            val snapshot = db.collection(FOODS_COLLECTION)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            
            val allFoods = snapshot.toObjects(Food::class.java)
            
            // Filter and sort manually
            val filteredFoods = allFoods.filter { food ->
                food.name.contains(query, ignoreCase = true)
            }.sortedBy { it.name }
            
            Log.d(TAG, "Search completed: ${filteredFoods.size} foods found for query '$query'")
            Result.success(filteredFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching foods: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // ===== USERS =====
    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: User): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== ORDERS =====
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = db.collection(ORDERS_COLLECTION).document()
            val orderWithId = order.copy(orderId = docRef.id)
            
            docRef.set(orderWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = db.collection(ORDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrderById(orderId: String): Result<Order?> {
        return try {
            val snapshot = db.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()
            
            val order = snapshot.toObject(Order::class.java)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            db.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update("status", status)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
