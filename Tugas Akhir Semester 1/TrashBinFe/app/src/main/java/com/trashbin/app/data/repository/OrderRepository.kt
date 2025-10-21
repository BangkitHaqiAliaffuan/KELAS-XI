package com.trashbin.app.data.repository

import android.util.Log
import com.trashbin.app.data.api.ApiService
import com.trashbin.app.data.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error(val message: String, val exception: Exception? = null) : RepositoryResult<Nothing>()
    object Loading : RepositoryResult<Nothing>()
}

class OrderRepository(private val apiService: ApiService) {

    suspend fun getMyOrders(role: String, status: String? = null): RepositoryResult<List<Order>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("OrderRepository", "Fetching orders - role: $role, status: $status")
            val response = apiService.getOrders(role, status)
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("OrderRepository", "Response body: $body")
                
                if (body != null) {
                    val success = body.success ?: true
                    if (success && body.data != null) {
                        Log.d("OrderRepository", "Orders fetched successfully: ${body.data.size} items")
                        RepositoryResult.Success(body.data)
                    } else {
                        val error = body.message ?: "Failed to fetch orders"
                        Log.e("OrderRepository", "API error: $error")
                        RepositoryResult.Error(error)
                    }
                } else {
                    Log.e("OrderRepository", "Response body is null")
                    RepositoryResult.Error("Empty response")
                }
            } else {
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                Log.e("OrderRepository", errorMsg)
                RepositoryResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception fetching orders", e)
            RepositoryResult.Error("Exception: ${e.message}", e)
        }
    }

    suspend fun confirmOrder(orderId: Int): RepositoryResult<Order> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("OrderRepository", "Confirming order: $orderId")
            val response = apiService.confirmOrder(orderId)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.data != null) {
                    Log.d("OrderRepository", "Order confirmed successfully")
                    RepositoryResult.Success(body.data)
                } else {
                    RepositoryResult.Error(body?.message ?: "Failed to confirm order")
                }
            } else {
                RepositoryResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception confirming order", e)
            RepositoryResult.Error("Exception: ${e.message}", e)
        }
    }

    suspend fun completeOrder(orderId: Int): RepositoryResult<Order> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("OrderRepository", "Completing order: $orderId")
            val response = apiService.completeOrder(orderId)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.data != null) {
                    Log.d("OrderRepository", "Order completed successfully")
                    RepositoryResult.Success(body.data)
                } else {
                    RepositoryResult.Error(body?.message ?: "Failed to complete order")
                }
            } else {
                RepositoryResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception completing order", e)
            RepositoryResult.Error("Exception: ${e.message}", e)
        }
    }
}
