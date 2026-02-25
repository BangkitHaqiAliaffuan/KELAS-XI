package com.kelasxi.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.network.AuthRepository
import com.kelasxi.myapplication.data.network.AuthResult
import com.kelasxi.myapplication.data.network.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

// ─────────────────────────────────────────────────────────────────
// UI state
// ─────────────────────────────────────────────────────────────────
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: UserDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // One-shot event fired after logout completes — NavGraph collects this to navigate
    private val _logoutEvent = Channel<Unit>(Channel.BUFFERED)
    val logoutEvent = _logoutEvent.receiveAsFlow()

    init {
        // Restore login state from persisted token on app start
        repository.tokenFlow()
            .onEach { token ->
                _uiState.value = _uiState.value.copy(isLoggedIn = !token.isNullOrBlank())
            }
            .launchIn(viewModelScope)
    }

    // ─────────────────────────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email dan password wajib diisi.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.login(email.trim(), password)) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    user = result.data.user,
                    successMessage = result.data.message
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                is AuthResult.Loading -> Unit
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Register
    // ─────────────────────────────────────────────────────────────
    fun register(name: String, email: String, phone: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nama, email, dan password wajib diisi.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.register(name.trim(), email.trim(), phone.trim(), password)) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    user = result.data.user,
                    successMessage = result.data.message
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                is AuthResult.Loading -> Unit
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.logout()
            _uiState.value = AuthUiState() // reset to initial (isLoggedIn = false)
            _logoutEvent.send(Unit)        // signal NavGraph to navigate to Login
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
