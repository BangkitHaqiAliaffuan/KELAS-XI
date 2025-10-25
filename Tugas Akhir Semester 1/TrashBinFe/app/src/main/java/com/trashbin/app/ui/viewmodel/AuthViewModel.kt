package com.trashbin.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.LoginResponse
import com.trashbin.app.data.model.User
import com.trashbin.app.data.repository.AuthRepository
import com.trashbin.app.data.repository.RepositoryResult
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<RepositoryResult<LoginResponse>>()
    val loginResult: LiveData<RepositoryResult<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<RepositoryResult<LoginResponse>>()
    val registerResult: LiveData<RepositoryResult<LoginResponse>> = _registerResult

    private val _profileResult = MutableLiveData<RepositoryResult<User>>()
    val profileResult: LiveData<RepositoryResult<User>> = _profileResult
    
    // Alias for registerResult to match what RegisterActivity expects
    val registerState: LiveData<RepositoryResult<LoginResponse>> = _registerResult

    companion object {
        @JvmStatic
        fun create(): AuthViewModel {
            val apiService = com.trashbin.app.data.api.RetrofitClient.apiService
            val tokenManager = com.trashbin.app.data.api.TokenManager.getInstance()
            val repository = com.trashbin.app.data.repository.AuthRepository(apiService, tokenManager)
            return AuthViewModel(repository)
        }
    }

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "login() called with email: $email")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("AuthViewModel", "Calling repository.login()")
                val result = repository.login(email, password)
                _isLoading.value = false
                
                Log.d("AuthViewModel", "Repository result: $result")
                _loginResult.value = result
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                _isLoading.value = false
                _loginResult.value = RepositoryResult.Error(e.message ?: "Login error", e)
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        passwordConfirmation: String,
        role: String,
        lat: Double?,
        lng: Double?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.register(name, email, phone, password, passwordConfirmation, role, lat, lng)
                _isLoading.value = false
                _registerResult.value = result
            } catch (e: Exception) {
                _isLoading.value = false
                _registerResult.value = RepositoryResult.Error(e.message ?: "Registration error", e)
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getProfile()
                _isLoading.value = false
                _profileResult.value = result
            } catch (e: Exception) {
                _isLoading.value = false
                _profileResult.value = RepositoryResult.Error(e.message ?: "Profile fetch error", e)
            }
        }
    }

    fun updateProfile(userData: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.updateProfile(userData)
                _isLoading.value = false
                _profileResult.value = result
            } catch (e: Exception) {
                _isLoading.value = false
                _profileResult.value = RepositoryResult.Error(e.message ?: "Profile update error", e)
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}