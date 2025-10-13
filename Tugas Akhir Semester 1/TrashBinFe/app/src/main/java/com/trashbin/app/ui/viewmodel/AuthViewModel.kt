package com.trashbin.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.User
import com.trashbin.app.data.repository.AuthRepository
import com.trashbin.app.data.repository.Result
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository(RetrofitClient.apiService, TokenManager.getInstance())

    private val _loginState = MutableLiveData<Result<*>>()
    val loginState: LiveData<Result<*>> = _loginState

    private val _registerState = MutableLiveData<Result<*>>()
    val registerState: LiveData<Result<*>> = _registerState

    private val _profileState = MutableLiveData<Result<*>>()
    val profileState: LiveData<Result<*>> = _profileState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String,
        address: String?,
        lat: Double?,
        lng: Double?
    ) {
        viewModelScope.launch {
            _registerState.value = Result.Loading
            val result = repository.register(name, email, phone, password, role, lat, lng)
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