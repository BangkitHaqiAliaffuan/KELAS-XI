package com.kelasxi.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.network.AuthRepository
import com.kelasxi.myapplication.data.network.AuthResult
import com.kelasxi.myapplication.data.network.CourierPickupDto
import com.kelasxi.myapplication.data.network.CourierProfileDto
import com.kelasxi.myapplication.data.network.TokenStore
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class CourierUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val profile: CourierProfileDto? = null,
    val pickups: List<CourierPickupDto> = emptyList(),
    val availablePickups: List<CourierPickupDto> = emptyList(),
    val isAvailable: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CourierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(CourierUiState())
    val uiState: StateFlow<CourierUiState> = _uiState.asStateFlow()

    private val _logoutEvent = Channel<Unit>(Channel.BUFFERED)
    val logoutEvent = _logoutEvent.receiveAsFlow()

    init {
        // Data loading is triggered by NavGraph after login (token already saved),
        // OR on app restart if a token already exists in DataStore.
        viewModelScope.launch {
            val token = TokenStore.getToken(getApplication())
            if (token != null) {
                loadProfile()
                loadPickups()
                loadAvailablePickups()
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            when (val result = repository.getCourierMe()) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    profile = result.data,
                    isAvailable = result.data.is_available
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun loadPickups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getCourierPickups()) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pickups = result.data
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun loadAvailablePickups() {
        viewModelScope.launch {
            when (val result = repository.getAvailablePickups()) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    availablePickups = result.data
                )
                is AuthResult.Error -> Unit // silently ignore
                else -> Unit
            }
        }
    }

    /** Called by PullToRefreshBox — runs all three loads in parallel */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            val profileJob = async { repository.getCourierMe() }
            val pickupsJob = async { repository.getCourierPickups() }
            val availableJob = async { repository.getAvailablePickups() }

            val profileResult = profileJob.await()
            val pickupsResult = pickupsJob.await()
            val availableResult = availableJob.await()

            var newState = _uiState.value.copy(isRefreshing = false)
            if (profileResult is AuthResult.Success)
                newState = newState.copy(profile = profileResult.data, isAvailable = profileResult.data.is_available)
            if (pickupsResult is AuthResult.Success)
                newState = newState.copy(pickups = pickupsResult.data)
            if (availableResult is AuthResult.Success)
                newState = newState.copy(availablePickups = availableResult.data)
            _uiState.value = newState
        }
    }

    fun acceptPickup(pickupId: Long) {
        viewModelScope.launch {
            when (val result = repository.acceptPickup(pickupId)) {
                is AuthResult.Success -> {
                    // Remove from available, add to assigned pickups
                    _uiState.value = _uiState.value.copy(
                        availablePickups = _uiState.value.availablePickups.filter { it.id != pickupId },
                        pickups = listOf(result.data) + _uiState.value.pickups,
                        successMessage = "Pickup diterima! Segera hubungi pelanggan."
                    )
                    loadProfile() // refresh total_deliveries
                }
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun ignorePickup(pickupId: Long) {
        // Just remove from local list without calling API — courier simply doesn't accept
        _uiState.value = _uiState.value.copy(
            availablePickups = _uiState.value.availablePickups.filter { it.id != pickupId }
        )
    }

    fun updateStatus(pickupId: Long, status: String) {
        viewModelScope.launch {
            when (val result = repository.updatePickupStatus(pickupId, status)) {
                is AuthResult.Success -> {
                    val updated = result.data
                    if (status == "cancelled") {
                        // Courier cancelled → pickup goes back to 'searching', remove from our list
                        _uiState.value = _uiState.value.copy(
                            pickups = _uiState.value.pickups.filter { it.id != pickupId },
                            successMessage = "Pickup dibatalkan. Pickup kembali ke antrian pencarian."
                        )
                        // Reload available pickups — the cancelled one may re-appear
                        loadAvailablePickups()
                        loadProfile()
                    } else {
                        // Replace the updated pickup in the list
                        val newList = _uiState.value.pickups.map {
                            if (it.id == updated.id) updated else it
                        }
                        _uiState.value = _uiState.value.copy(
                            pickups = newList,
                            successMessage = when (status) {
                                "on_the_way" -> "Pengiriman dimulai!"
                                "done" -> "Pickup selesai! Poin diberikan ke pelanggan."
                                else -> "Status diperbarui."
                            }
                        )
                        // If done, refresh profile (total_deliveries may change)
                        if (status == "done") loadProfile()
                    }
                }
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun toggleAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            when (val result = repository.toggleAvailability(isAvailable)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAvailable = result.data.is_available
                    )
                    // When going online, load available pickups immediately
                    if (result.data.is_available) loadAvailablePickups()
                    else _uiState.value = _uiState.value.copy(availablePickups = emptyList())
                }
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = CourierUiState()
            _logoutEvent.send(Unit)
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
