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
import com.trashbin.app.data.repository.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Result<LoginResponse>>()
    val loginState: LiveData<Result<LoginResponse>> = _loginState

    private val _loginResult = MutableLiveData<kotlin.Result<LoginResponse>>()
    val loginResult: LiveData<kotlin.Result<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerState = MutableLiveData<Result<LoginResponse>>()
    val registerState: LiveData<Result<LoginResponse>> = _registerState

    private val _profileState = MutableLiveData<Result<User>>()
    val profileState: LiveData<Result<User>> = _profileState

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
                when (result) {
                    is Result.Success -> {
                        Log.d("AuthViewModel", "Login successful, setting loginResult to success")
                        _loginResult.value = kotlin.Result.success(result.data)
                    }
                    is Result.Error -> {
                        Log.d("AuthViewModel", "Login failed: ${result.message}")
                        _loginResult.value = kotlin.Result.failure(Exception(result.message))
                    }
                    else -> {
                        Log.d("AuthViewModel", "Unknown result type")
                        _loginResult.value = kotlin.Result.failure(Exception("Unknown error"))
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                _isLoading.value = false
                _loginResult.value = kotlin.Result.failure(e)
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
            _registerState.value = Result.Loading
            val result = repository.register(name, email, phone, password, passwordConfirmation, role, lat, lng)
            _registerState.value = result
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = Result.Loading
            val result = repository.getProfile()
            _profileState.value = result
        }
    }

    fun logout() {
        repository.logout()
    }
}