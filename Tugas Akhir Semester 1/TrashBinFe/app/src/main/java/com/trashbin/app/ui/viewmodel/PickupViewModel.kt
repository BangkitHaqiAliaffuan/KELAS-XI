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
import com.trashbin.app.utils.Result
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
            _categories.value = Result.Loading
            try {
                val response = apiService.getWasteCategories()
                if (response.isSuccessful && response.body()?.success == true) {
                    _categories.value = Result.Success(response.body()!!.data!!)
                } else {
                    _categories.value = Result.Error(response.message() ?: "Error")
                }
            } catch (e: Exception) {
                _categories.value = Result.Error(e.message ?: "Error")
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
                val request = PickupRequest(address, lat, lng, date, selectedItems.value!!, notes)
                val response = apiService.createPickup(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _createState.value = Result.Success(response.body()!!.data!!)
                } else {
                    _createState.value = Result.Error(response.message() ?: "Error creating pickup")
                }
            } catch (e: Exception) {
                _createState.value = Result.Error(e.message ?: "Error")
            }
        }
    }

    fun loadPickups(status: String? = null) {
        viewModelScope.launch {
            _pickups.value = Result.Loading
            try {
                val response = apiService.getPickups(status, null)
                if (response.isSuccessful && response.body()?.success == true) {
                    _pickups.value = Result.Success(response.body()!!.data!!)
                } else {
                    _pickups.value = Result.Error(response.message() ?: "Error")
                }
            } catch (e: Exception) {
                _pickups.value = Result.Error(e.message ?: "Error")
            }
        }
    }
}