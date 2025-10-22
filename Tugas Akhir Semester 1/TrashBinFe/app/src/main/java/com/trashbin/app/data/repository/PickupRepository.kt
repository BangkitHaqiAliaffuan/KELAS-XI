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

    suspend fun createPickup(pickupRequest: PickupRequest): RepositoryResult<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.createPickup(pickupRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { pickup ->
                        RepositoryResult.Success(pickup)
                    } ?: RepositoryResult.Error("Failed to create pickup: No data returned")
                } else {
                    RepositoryResult.Error(responseBody?.message ?: "Failed to create pickup")
                }
            } else {
                RepositoryResult.Error(response.body()?.message ?: "Failed to create pickup")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("Network error occurred", e)
        } catch (e: HttpException) {
            RepositoryResult.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            RepositoryResult.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getPickupHistory(status: String? = null, page: Int? = null): RepositoryResult<List<PickupResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getPickups(status, page)
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { pickups ->
                        RepositoryResult.Success(pickups)
                    } ?: RepositoryResult.Error("Failed to get pickup history: No data returned")
                } else {
                    RepositoryResult.Error(responseBody?.message ?: "Failed to get pickup history")
                }
            } else {
                RepositoryResult.Error(response.body()?.message ?: "Failed to get pickup history")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("Network error occurred", e)
        } catch (e: HttpException) {
            RepositoryResult.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            RepositoryResult.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getPickupDetail(id: Int): RepositoryResult<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getPickupDetail(id)
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { pickup ->
                        RepositoryResult.Success(pickup)
                    } ?: RepositoryResult.Error("Failed to get pickup detail: No data returned")
                } else {
                    RepositoryResult.Error(responseBody?.message ?: "Failed to get pickup detail")
                }
            } else {
                RepositoryResult.Error(response.body()?.message ?: "Failed to get pickup detail")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("Network error occurred", e)
        } catch (e: HttpException) {
            RepositoryResult.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            RepositoryResult.Error("An unexpected error occurred", e)
        }
    }

    suspend fun cancelPickup(id: Int, reason: String? = null): RepositoryResult<PickupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reasonParam = reason ?: "User cancelled"
            val response = apiService.cancelPickup(id, mapOf("reason" to reasonParam))
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { pickup ->
                        RepositoryResult.Success(pickup)
                    } ?: RepositoryResult.Error("Failed to cancel pickup: No data returned")
                } else {
                    RepositoryResult.Error(responseBody?.message ?: "Failed to cancel pickup")
                }
            } else {
                RepositoryResult.Error(response.body()?.message ?: "Failed to cancel pickup")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("Network error occurred", e)
        } catch (e: HttpException) {
            RepositoryResult.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            RepositoryResult.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getWasteCategories(): RepositoryResult<List<WasteCategory>> = withContext(Dispatchers.IO) {
        // Check if we have a valid cache
        if (wasteCategoriesCache != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_DURATION) {
            return@withContext RepositoryResult.Success(wasteCategoriesCache!!)
        }

        return@withContext try {
            val response = apiService.getWasteCategories()
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { categories ->
                        wasteCategoriesCache = categories
                        cacheTimestamp = System.currentTimeMillis()
                        RepositoryResult.Success(categories)
                    } ?: RepositoryResult.Error("Failed to get waste categories: No data returned")
                } else {
                    RepositoryResult.Error(responseBody?.message ?: "Failed to get waste categories")
                }
            } else {
                RepositoryResult.Error(response.body()?.message ?: "Failed to get waste categories")
            }
        } catch (e: IOException) {
            RepositoryResult.Error("Network error occurred", e)
        } catch (e: HttpException) {
            RepositoryResult.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            RepositoryResult.Error("An unexpected error occurred", e)
        }
    }
}