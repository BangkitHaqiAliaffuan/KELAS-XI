package com.trashbin.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.model.ApiResponse
import com.trashbin.app.data.model.PickupItemRequest
import com.trashbin.app.data.model.PickupRequest
import com.trashbin.app.data.model.PickupResponse
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.data.repository.PickupRepository
import com.trashbin.app.data.repository.RepositoryResult
import kotlinx.coroutines.launch

class PickupViewModel(private val repository: PickupRepository) : ViewModel() {
    
    private val _categories = MutableLiveData<RepositoryResult<List<WasteCategory>>>()
    val categories: LiveData<RepositoryResult<List<WasteCategory>>> = _categories

    val selectedItems = MutableLiveData<MutableList<PickupItemRequest>>(mutableListOf())

    private val _createState = MutableLiveData<RepositoryResult<PickupResponse>>()
    val createState: LiveData<RepositoryResult<PickupResponse>> = _createState

    private val _pickups = MutableLiveData<RepositoryResult<List<PickupResponse>>>()
    val pickups: LiveData<RepositoryResult<List<PickupResponse>>> = _pickups
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _pickupAction = MutableLiveData<RepositoryResult<PickupResponse>>()
    val pickupAction: LiveData<RepositoryResult<PickupResponse>> = _pickupAction

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("PickupViewModel", "Loading categories...")
            _categories.value = RepositoryResult.Loading
            try {
                val response = repository.getWasteCategories()
                when (response) {
                    is RepositoryResult.Success -> {
                        android.util.Log.d("PickupViewModel", "Categories loaded: ${response.data.size} items")
                    }
                    is RepositoryResult.Error -> {
                        android.util.Log.e("PickupViewModel", "Error from repository: ${response.message}")
                    }
                    else -> {}
                }
                _categories.value = response
            } catch (e: Exception) {
                android.util.Log.e("PickupViewModel", "Exception loading categories", e)
                _categories.value = RepositoryResult.Error(e.message ?: "Network error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addItem(categoryId: Int, weight: Double, photoUrl: String? = null) {
        val current = selectedItems.value ?: mutableListOf()
        current.add(PickupItemRequest(categoryId, weight, photoUrl))
        selectedItems.value = current
    }

    fun removeItem(index: Int) {
        val current = selectedItems.value ?: mutableListOf()
        if (index >= 0 && index < current.size) {
            current.removeAt(index)
            selectedItems.value = current
        }
    }

    fun createPickup(address: String, lat: Double, lng: Double, date: String, notes: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _createState.value = RepositoryResult.Loading
            try {
                val items = selectedItems.value ?: mutableListOf()
                android.util.Log.d("PickupViewModel", "Creating pickup with ${items.size} items")
                android.util.Log.d("PickupViewModel", "Address: $address, Lat: $lat, Lng: $lng, Date: $date")
                android.util.Log.d("PickupViewModel", "Items: $items")
                
                val request = PickupRequest(address, lat, lng, date, items, notes)
                val result = repository.createPickup(request)
                _createState.value = result
                
                when (result) {
                    is RepositoryResult.Success -> {
                        android.util.Log.d("PickupViewModel", "Pickup created successfully")
                    }
                    is RepositoryResult.Error -> {
                        android.util.Log.e("PickupViewModel", "Error creating pickup: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("PickupViewModel", "Error creating pickup", e)
                _createState.value = RepositoryResult.Error(e.message ?: "Error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPickups(status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _pickups.value = RepositoryResult.Loading
            try {
                val result = repository.getPickupHistory(status, null)
                _pickups.value = result
                
                when (result) {
                    is RepositoryResult.Success -> {
                        android.util.Log.d("PickupViewModel", "Pickups loaded: ${result.data.size} items")
                    }
                    is RepositoryResult.Error -> {
                        android.util.Log.e("PickupViewModel", "Error loading pickups: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _pickups.value = RepositoryResult.Error(e.message ?: "Error")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun cancelPickup(id: Int, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.cancelPickup(id, reason)
                _pickupAction.value = result
                
                when (result) {
                    is RepositoryResult.Success -> {
                        android.util.Log.d("PickupViewModel", "Pickup cancelled successfully")
                    }
                    is RepositoryResult.Error -> {
                        android.util.Log.e("PickupViewModel", "Error cancelling pickup: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _pickupAction.value = RepositoryResult.Error(e.message ?: "Error cancelling pickup")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // This method is not needed as we already have pickups field that serves the same purpose
    // We can remove the _myPickups field since it's redundant with _pickups
}