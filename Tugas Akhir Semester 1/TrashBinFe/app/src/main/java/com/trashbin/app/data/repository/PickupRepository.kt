package com.trashbin.app.data.repository

import com.trashbin.app.data.api.ApiService
import com.trashbin.app.data.model.PickupRequest
import com.trashbin.app.data.model.PickupResponse
import com.trashbin.app.data.model.WasteCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class PickupRepository(
    private val apiService: ApiService
) {
    private var wasteCategoriesCache: List<WasteCategory>? = null
    private var cacheTimestamp: Long = 0
    private val CACHE_DURATION = TimeUnit.HOURS.toMillis(1) // 1 hour cache

    suspend fun createPickup(pickupRequest: PickupRequest): Result<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.createPickup(pickupRequest)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { pickup ->
                    Result.Success(pickup)
                } ?: Result.Error("Failed to create pickup: No data returned")
            } else {
                Result.Error(response.body()?.message ?: "Failed to create pickup")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getPickupHistory(status: String? = null, page: Int? = null): Result<List<PickupResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getPickups(status, page)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { pickups ->
                    Result.Success(pickups)
                } ?: Result.Error("Failed to get pickup history: No data returned")
            } else {
                Result.Error(response.body()?.message ?: "Failed to get pickup history")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getPickupDetail(id: Int): Result<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getPickupDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { pickup ->
                    Result.Success(pickup)
                } ?: Result.Error("Failed to get pickup detail: No data returned")
            } else {
                Result.Error(response.body()?.message ?: "Failed to get pickup detail")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun cancelPickup(id: Int): Result<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.cancelPickup(id, mapOf("reason" to "User cancelled"))
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { pickup ->
                    Result.Success(pickup)
                } ?: Result.Error("Failed to cancel pickup: No data returned")
            } else {
                Result.Error(response.body()?.message ?: "Failed to cancel pickup")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getWasteCategories(): Result<List<WasteCategory>> = withContext(Dispatchers.IO) {
        // Check if we have a valid cache
        if (wasteCategoriesCache != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_DURATION) {
            return@withContext Result.Success(wasteCategoriesCache!!)
        }

        return@withContext try {
            val response = apiService.getWasteCategories()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { categories ->
                    wasteCategoriesCache = categories
                    cacheTimestamp = System.currentTimeMillis()
                    Result.Success(categories)
                } ?: Result.Error("Failed to get waste categories: No data returned")
            } else {
                Result.Error(response.body()?.message ?: "Failed to get waste categories")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }
}