package com.trashbin.app.data.repository

import com.trashbin.app.data.api.ApiService
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.LoginResponse
import com.trashbin.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import com.trashbin.app.data.repository.RepositoryResult

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        passwordConfirmation: String,
        role: String,
        lat: Double?,
        lng: Double?
    ): RepositoryResult<LoginResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.register(name, email, phone, password, passwordConfirmation, role, lat, lng)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUser(loginResponse.user)
                RepositoryResult.Success(loginResponse)
            } else {
                // Handle error response from Laravel
                val errorMessage = when (response.code()) {
                    422 -> "Validation failed"
                    else -> "Registration failed"
                }
                RepositoryResult.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "Registration error", e)
        }
    }

    suspend fun login(email: String, password: String): RepositoryResult<LoginResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.login(email, password)
            android.util.Log.d("AuthRepository", "Response successful: ${response.isSuccessful}")
            android.util.Log.d("AuthRepository", "Response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "Response body: ${response.body()}")
            
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                android.util.Log.d("AuthRepository", "Login successful, saving token: ${loginResponse.token}")
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUser(loginResponse.user)
                RepositoryResult.Success(loginResponse)
            } else {
                val errorMessage = if (response.code() == 401) {
                    "Invalid credentials"
                } else {
                    "Login failed"
                }
                android.util.Log.d("AuthRepository", "Login failed: $errorMessage")
                RepositoryResult.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "Login error", e)
        }
    }

    suspend fun getProfile(): RepositoryResult<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getProfile()
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { user ->
                        tokenManager.saveUser(user) // Update user data
                        RepositoryResult.Success(user)
                    } ?: RepositoryResult.Error("Failed to get profile: No data returned", null)
                } else {
                    RepositoryResult.Error("Failed to get profile: ${responseBody?.message}", null)
                }
            } else {
                RepositoryResult.Error("Failed to get profile: ${response.body()?.message}", null)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "Profile fetch error", e)
        }
    }

    suspend fun updateProfile(userData: Map<String, Any>): RepositoryResult<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.updateProfile(userData)
            if (response.isSuccessful) {
                val responseBody = response.body()
                // Check if success field exists and is true, OR if success field is null but data exists
                if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                    responseBody?.data?.let { user ->
                        tokenManager.saveUser(user) // Update user data
                        RepositoryResult.Success(user)
                    } ?: RepositoryResult.Error("Failed to update profile: No data returned", null)
                } else {
                    RepositoryResult.Error("Failed to update profile: ${responseBody?.message}", null)
                }
            } else {
                RepositoryResult.Error("Failed to update profile: ${response.body()?.message}", null)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "Profile update error", e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}