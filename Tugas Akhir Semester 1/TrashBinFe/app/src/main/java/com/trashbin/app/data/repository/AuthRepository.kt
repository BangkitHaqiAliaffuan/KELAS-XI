package com.trashbin.app.data.repository

import com.trashbin.app.data.api.ApiService
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.LoginResponse
import com.trashbin.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

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
    ): Result<LoginResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.register(name, email, phone, password, passwordConfirmation, role, lat, lng)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { loginResponse ->
                    tokenManager.saveToken(loginResponse.token)
                    tokenManager.saveUser(loginResponse.user)
                    Result.Success(loginResponse)
                } ?: Result.Error("Registration failed: No data returned")
            } else {
                // Handle error response from Laravel
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    errorBody
                } else {
                    response.body()?.message ?: "Registration failed"
                }
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            // Parse error body dari HTTP exception untuk validasi errors
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody ?: "Request failed: ${e.message()}"
            Result.Error(errorMessage, e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.login(email, password)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { loginResponse ->
                    tokenManager.saveToken(loginResponse.token)
                    tokenManager.saveUser(loginResponse.user)
                    Result.Success(loginResponse)
                } ?: Result.Error("Login failed: No data returned")
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun getProfile(): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { user ->
                    tokenManager.saveUser(user) // Update user data
                    Result.Success(user)
                } ?: Result.Error("Failed to get profile: No data returned")
            } else {
                Result.Error("Failed to get profile: ${response.body()?.message}")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    suspend fun updateProfile(userData: Map<String, Any>): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.updateProfile(userData)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { user ->
                    tokenManager.saveUser(user) // Update user data
                    Result.Success(user)
                } ?: Result.Error("Failed to update profile: No data returned")
            } else {
                Result.Error("Failed to update profile: ${response.body()?.message}")
            }
        } catch (e: IOException) {
            Result.Error("Network error occurred", e)
        } catch (e: HttpException) {
            Result.Error("Request failed: ${e.message()}", e)
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred", e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}