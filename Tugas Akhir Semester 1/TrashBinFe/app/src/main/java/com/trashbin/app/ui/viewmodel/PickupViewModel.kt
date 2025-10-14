package com.trashbin.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.model.ApiResponse
import com.trashbin.app.data.model.PickupItemRequest
import com.trashbin.app.data.model.PickupRequest
import com.trashbin.app.data.model.PickupResponse
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.data.repository.Result
import kotlinx.coroutines.launch

class PickupViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService

    private val _categories = MutableLiveData<Result<List<WasteCategory>>>()
    val categories: LiveData<Result<List<WasteCategory>>> = _categories

    val selectedItems = MutableLiveData<MutableList<PickupItemRequest>>(mutableListOf())

    private val _createState = MutableLiveData<Result<PickupResponse>>()
    val createState: LiveData<Result<PickupResponse>> = _createState

    private val _pickups = MutableLiveData<Result<List<PickupResponse>>>()
    val pickups: LiveData<Result<List<PickupResponse>>> = _pickups

    fun loadCategories() {
        viewModelScope.launch {
            android.util.Log.d("PickupViewModel", "Loading categories...")
            _categories.value = Result.Loading
            try {
                val response = apiService.getWasteCategories()
                android.util.Log.d("PickupViewModel", "Categories API response: ${response.code()}")
                android.util.Log.d("PickupViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Check if success field exists and is true, OR if success field is null but data exists
                    if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                        val data = responseBody.data!!
                        android.util.Log.d("PickupViewModel", "Categories loaded successfully: ${data.size} items")
                        data.forEach { category ->
                            android.util.Log.d("PickupViewModel", "Category: ${category.name} - ${category.id}")
                        }
                        _categories.value = Result.Success(data)
                    } else {
                        val errorMsg = responseBody?.message ?: response.message() ?: "Error loading categories"
                        android.util.Log.e("PickupViewModel", "Categories API error: $errorMsg")
                        _categories.value = Result.Error(errorMsg)
                    }
                } else {
                    val errorMsg = response.body()?.message ?: response.message() ?: "Error loading categories"
                    android.util.Log.e("PickupViewModel", "Categories API error: $errorMsg")
                    _categories.value = Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                android.util.Log.e("PickupViewModel", "Exception loading categories", e)
                _categories.value = Result.Error(e.message ?: "Network error")
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
            _createState.value = Result.Loading
            try {
                val items = selectedItems.value ?: mutableListOf()
                android.util.Log.d("PickupViewModel", "Creating pickup with ${items.size} items")
                android.util.Log.d("PickupViewModel", "Address: $address, Lat: $lat, Lng: $lng, Date: $date")
                android.util.Log.d("PickupViewModel", "Items: $items")
                
                val request = PickupRequest(address, lat, lng, date, items, notes)
                val response = apiService.createPickup(request)
                
                android.util.Log.d("PickupViewModel", "Response: ${response.code()}")
                android.util.Log.d("PickupViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Check if success field exists and is true, OR if success field is null but data exists
                    if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                        _createState.value = Result.Success(responseBody!!.data!!)
                    } else {
                        val errorMessage = responseBody?.message ?: response.message() ?: "Error creating pickup"
                        _createState.value = Result.Error(errorMessage)
                    }
                } else {
                    val errorMessage = response.body()?.message ?: response.message() ?: "Error creating pickup"
                    _createState.value = Result.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("PickupViewModel", "Error creating pickup", e)
                _createState.value = Result.Error(e.message ?: "Error")
            }
        }
    }

    fun loadPickups(status: String? = null) {
        viewModelScope.launch {
            _pickups.value = Result.Loading
            try {
                val response = apiService.getPickups(status, null)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Check if success field exists and is true, OR if success field is null but data exists
                    if (responseBody?.success == true || (responseBody?.success == null && responseBody?.data != null)) {
                        _pickups.value = Result.Success(responseBody!!.data!!)
                    } else {
                        _pickups.value = Result.Error(responseBody?.message ?: response.message() ?: "Error")
                    }
                } else {
                    _pickups.value = Result.Error(response.message() ?: "Error")
                }
            } catch (e: Exception) {
                _pickups.value = Result.Error(e.message ?: "Error")
            }
        }
    }
}