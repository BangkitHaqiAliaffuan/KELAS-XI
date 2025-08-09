package com.kelasxi.waveoffood.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelasxi.waveoffood.models.*
import kotlinx.coroutines.tasks.await

/**
 * Firebase Repository for Enhanced Design
 * Handles all Firebase operations with new data structure
 */
class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Collections
    private val categoriesCollection = firestore.collection("categories")
    private val foodsCollection = firestore.collection("foods")
    private val usersCollection = firestore.collection("users")
    private val ordersCollection = firestore.collection("orders")
    private val promotionsCollection = firestore.collection("promotions")
    private val reviewsCollection = firestore.collection("reviews")
    
    // Authentication
    suspend fun registerUser(email: String, password: String, name: String): Result<UserModel> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val userModel = UserModel(
                    uid = user.uid,
                    name = name,
                    email = email,
                    address = "",
                    profileImage = "",
                    profileImageUrl = "",
                    phone = ""
                )
                
                // Save user to Firestore
                usersCollection.document(user.uid).set(userModel).await()
                Result.success(userModel)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun loginUser(email: String, password: String): Result<UserModel> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val userDoc = usersCollection.document(user.uid).get().await()
                val userModel = userDoc.toObject(UserModel::class.java)
                
                if (userModel != null) {
                    Result.success(userModel)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUser() = auth.currentUser
    
    fun signOut() = auth.signOut()
    
    // Categories
    suspend fun getCategories(): Result<List<CategoryModel>> {
        return try {
            val snapshot = categoriesCollection
                .whereEqualTo("isActive", true)
                .orderBy("name")
                .get()
                .await()
            
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CategoryModel::class.java)
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Foods
    suspend fun getFoodsByCategory(categoryId: String): Result<List<FoodModel>> {
        return try {
            val snapshot = foodsCollection
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isAvailable", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodModel::class.java)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPopularFoods(): Result<List<FoodModel>> {
        return try {
            val snapshot = foodsCollection
                .whereEqualTo("isPopular", true)
                .whereEqualTo("isAvailable", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodModel::class.java)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllFoods(): Result<List<FoodModel>> {
        return try {
            val snapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodModel::class.java)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchFoods(query: String): Result<List<FoodModel>> {
        return try {
            val snapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodModel::class.java)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Cart Operations
    suspend fun addToCart(userId: String, cartItem: CartItemModel): Result<Unit> {
        return try {
            usersCollection
                .document(userId)
                .collection("cart")
                .document(cartItem.id ?: "unknown")
                .set(cartItem)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCartItems(userId: String): Result<List<CartItemModel>> {
        return try {
            val snapshot = usersCollection
                .document(userId)
                .collection("cart")
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val cartItems = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CartItemModel::class.java)
            }
            Result.success(cartItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCartItem(userId: String, cartItem: CartItemModel): Result<Unit> {
        return try {
            usersCollection
                .document(userId)
                .collection("cart")
                .document(cartItem.id ?: "unknown")
                .set(cartItem)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromCart(userId: String, cartItemId: String): Result<Unit> {
        return try {
            usersCollection
                .document(userId)
                .collection("cart")
                .document(cartItemId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val cartRef = usersCollection.document(userId).collection("cart")
            val snapshot = cartRef.get().await()
            
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Favorites
    suspend fun addToFavorites(userId: String, foodId: String): Result<Unit> {
        return try {
            val favoriteData = hashMapOf(
                "foodId" to foodId,
                "addedAt" to System.currentTimeMillis()
            )
            
            usersCollection
                .document(userId)
                .collection("favorites")
                .document(foodId)
                .set(favoriteData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromFavorites(userId: String, foodId: String): Result<Unit> {
        return try {
            usersCollection
                .document(userId)
                .collection("favorites")
                .document(foodId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFavorites(userId: String): Result<List<String>> {
        return try {
            val snapshot = usersCollection
                .document(userId)
                .collection("favorites")
                .get()
                .await()
            
            val favoriteIds = snapshot.documents.map { it.id }
            Result.success(favoriteIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Orders
    suspend fun createOrder(order: OrderModel): Result<String> {
        return try {
            val orderRef = ordersCollection.document()
            val orderWithId = order.copy(orderId = orderRef.id)
            
            orderRef.set(orderWithId).await()
            
            // Clear user's cart
            clearCart(order.userId)
            
            Result.success(orderRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserOrders(userId: String): Result<List<OrderModel>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderModel::class.java)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Promotions
    suspend fun getActivePromotions(): Result<List<PromotionModel>> {
        return try {
            val currentTime = System.currentTimeMillis()
            
            val snapshot = promotionsCollection
                .whereEqualTo("isActive", true)
                .whereGreaterThan("validUntil", currentTime)
                .orderBy("validUntil")
                .get()
                .await()
            
            val promotions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PromotionModel::class.java)
            }
            Result.success(promotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // User Profile
    suspend fun updateUserProfile(userModel: UserModel): Result<Unit> {
        return try {
            usersCollection
                .document(userModel.uid)
                .set(userModel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(userId: String): Result<UserModel?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(UserModel::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
