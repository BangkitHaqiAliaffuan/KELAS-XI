package com.kelasxi.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.network.Address
import com.kelasxi.myapplication.data.network.AddressRepository
import com.kelasxi.myapplication.data.network.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AddressRepository(application.applicationContext)

    // ── Address list ──────────────────────────────────────────────
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ── Add address ───────────────────────────────────────────────
    private val _isAdding = MutableStateFlow(false)
    val isAdding: StateFlow<Boolean> = _isAdding.asStateFlow()

    private val _addSuccess = MutableStateFlow<String?>(null)
    val addSuccess: StateFlow<String?> = _addSuccess.asStateFlow()

    private val _addError = MutableStateFlow<String?>(null)
    val addError: StateFlow<String?> = _addError.asStateFlow()

    // ── Delete address ────────────────────────────────────────────
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteSuccess = MutableStateFlow<String?>(null)
    val deleteSuccess: StateFlow<String?> = _deleteSuccess.asStateFlow()

    // ── Set default ───────────────────────────────────────────────
    private val _isSettingDefault = MutableStateFlow(false)
    val isSettingDefault: StateFlow<Boolean> = _isSettingDefault.asStateFlow()

    // ─────────────────────────────────────────────────────────────
    // Load addresses from API
    // ─────────────────────────────────────────────────────────────
    fun loadAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = repository.getAddresses()) {
                is AuthResult.Success -> _addresses.value = result.data
                is AuthResult.Error   -> _error.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Add a new address
    // ─────────────────────────────────────────────────────────────
    fun addAddress(
        label: String,
        recipientName: String,
        phone: String,
        fullAddress: String,
        city: String,
        province: String,
        postalCode: String,
        isDefault: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isAdding.value = true
            _addError.value = null
            when (val result = repository.addAddress(
                label, recipientName, phone, fullAddress, city, province, postalCode, isDefault
            )) {
                is AuthResult.Success -> {
                    // If this is set as default, update local list flags
                    val newAddr = result.data
                    _addresses.value = if (newAddr.isDefault) {
                        listOf(newAddr) + _addresses.value.map { it.copy(isDefault = false) }
                    } else {
                        listOf(newAddr) + _addresses.value
                    }
                    _addSuccess.value = "Alamat berhasil ditambahkan! 📍"
                    onSuccess()
                }
                is AuthResult.Error -> _addError.value = result.message
                else -> {}
            }
            _isAdding.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Set an address as default
    // ─────────────────────────────────────────────────────────────
    fun setDefaultAddress(addressId: String) {
        val id = addressId.toLongOrNull() ?: return
        viewModelScope.launch {
            _isSettingDefault.value = true
            when (val result = repository.setDefaultAddress(id)) {
                is AuthResult.Success -> {
                    // Update local list: unset all defaults, then set the chosen one
                    _addresses.value = _addresses.value.map { a ->
                        a.copy(isDefault = a.id == addressId)
                    }
                    // Re-sort: default first
                    _addresses.value = _addresses.value.sortedByDescending { it.isDefault }
                }
                is AuthResult.Error -> _error.value = result.message
                else -> {}
            }
            _isSettingDefault.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Delete an address
    // ─────────────────────────────────────────────────────────────
    fun deleteAddress(addressId: String) {
        val id = addressId.toLongOrNull() ?: return
        viewModelScope.launch {
            _isDeleting.value = true
            when (val result = repository.deleteAddress(id)) {
                is AuthResult.Success -> {
                    val deleted = _addresses.value.find { it.id == addressId }
                    val remaining = _addresses.value.filter { it.id != addressId }
                    // If deleted was default and there are remaining, mark first as default
                    _addresses.value = if (deleted?.isDefault == true && remaining.isNotEmpty()) {
                        listOf(remaining.first().copy(isDefault = true)) + remaining.drop(1)
                    } else {
                        remaining
                    }
                    _deleteSuccess.value = "Alamat berhasil dihapus. 🗑️"
                }
                is AuthResult.Error -> _error.value = result.message
                else -> {}
            }
            _isDeleting.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Dismiss helpers
    // ─────────────────────────────────────────────────────────────
    fun dismissError()         { _error.value = null }
    fun dismissAddSuccess()    { _addSuccess.value = null }
    fun dismissAddError()      { _addError.value = null }
    fun dismissDeleteSuccess() { _deleteSuccess.value = null }
}
