package com.kelasxi.waveoffood.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kelasxi.waveoffood.data.preferences.UserPreferencesManager
import com.kelasxi.waveoffood.data.preferences.UserProfile
import com.kelasxi.waveoffood.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // User profile state
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    // Login state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        // Observe login status dan user profile
        viewModelScope.launch {
            combine(
                authRepository.isLoggedIn,
                authRepository.userProfile
            ) { isLoggedIn, userProfile ->
                _isLoggedIn.value = isLoggedIn
                _userProfile.value = if (isLoggedIn) userProfile else null
            }
        }
        
        // Auto login saat aplikasi dibuka
        checkAutoLogin()
    }
    
    // Cek auto login
    private fun checkAutoLogin() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val result = authRepository.autoLogin()
                if (result.isSuccess) {
                    // Auto login berhasil, user sudah login
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAutoLoginSuccess = true
                    )
                } else {
                    // Auto login gagal, user perlu login manual
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAutoLoginSuccess = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAutoLoginSuccess = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    // Login dengan email dan password
    fun signIn(email: String, password: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = authRepository.signInWithEmailAndPassword(email, password, rememberLogin)
                
                if (result.isSuccess) {
                    // Login berhasil - set success state dan tunggu flow update
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        errorMessage = null
                    )
                    
                    // Log untuk debugging
                    Log.d("AuthViewModel", "Login success - isLoginSuccess set to true")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                    Log.e("AuthViewModel", "Login failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )
                Log.e("AuthViewModel", "Login exception: ${e.message}")
            }
        }
    }
    
    // Register dengan email dan password
    fun signUp(email: String, password: String, name: String, phone: String = "", rememberLogin: Boolean = true) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = authRepository.createUserWithEmailAndPassword(email, password, name, phone, rememberLogin)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    // Update profil user
    fun updateProfile(name: String? = null, phone: String? = null, address: String? = null, avatarUrl: String? = null) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = authRepository.updateUserProfile(name, phone, address, avatarUrl)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isUpdateSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Update failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    // Logout
    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _uiState.value = AuthUiState() // Reset state
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Logout failed"
                )
            }
        }
    }
    
    // Reset password
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = authRepository.sendPasswordResetEmail(email)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isResetPasswordSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Reset password failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    // Sinkronisasi data user
    fun syncUserData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val result = authRepository.syncUserData()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = if (result.isFailure) result.exceptionOrNull()?.message else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Sync failed"
                )
            }
        }
    }
    
    // Clear error message
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Clear success states
    fun clearSuccessStates() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccess = false,
            isRegistrationSuccess = false,
            isUpdateSuccess = false,
            isResetPasswordSuccess = false
        )
    }
}

// UI State data class
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val isResetPasswordSuccess: Boolean = false,
    val isAutoLoginSuccess: Boolean? = null, // null = belum dicek, true = berhasil, false = gagal
    val errorMessage: String? = null
)

// Factory untuk membuat AuthViewModel
class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val userPreferencesManager = UserPreferencesManager(context)
            val authRepository = AuthRepository(userPreferencesManager = userPreferencesManager)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
