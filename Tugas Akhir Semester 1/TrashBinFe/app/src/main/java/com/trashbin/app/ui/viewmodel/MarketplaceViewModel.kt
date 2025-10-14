package com.trashbin.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.model.ApiResponse
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.data.model.Order
import com.trashbin.app.data.model.PaginatedListings
import com.trashbin.app.data.repository.Result
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MarketplaceViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    
    private val _listings = MutableLiveData<Result<PaginatedListings>>()
    val listings: LiveData<Result<PaginatedListings>> = _listings
    
    private val _createListingState = MutableLiveData<Result<MarketplaceListing>>()
    val createListingState: LiveData<Result<MarketplaceListing>> = _createListingState
    
    private val _createOrderState = MutableLiveData<Result<Order>>()
    val createOrderState: LiveData<Result<Order>> = _createOrderState
    
    private val _orders = MutableLiveData<Result<List<Order>>>()
    val orders: LiveData<Result<List<Order>>> = _orders

    fun loadListings(
        categoryId: Int? = null,
        condition: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        lat: Double? = null,
        lng: Double? = null,
        radius: Double? = null,
        search: String? = null
    ) {
        viewModelScope.launch {
            _listings.value = Result.Loading
            try {
                val response = apiService.getListings(
                    categoryId, condition, minPrice, maxPrice, lat, lng, radius, search, null
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _listings.value = Result.Success(response.body()!!.data!!)
                } else {
                    _listings.value = Result.Error(response.message() ?: "Error")
                }
            } catch (e: Exception) {
                _listings.value = Result.Error(e.message ?: "Error")
            }
        }
    }
    
    fun createListing(
        categoryId: Int,
        title: String,
        description: String,
        quantity: Int,
        pricePerUnit: Double,
        condition: String,
        location: String,
        lat: Double,
        lng: Double,
        photoFiles: List<File>
    ) {
        viewModelScope.launch {
            _createListingState.value = Result.Loading
            try {
                val photos = photoFiles.map { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaType())
                    MultipartBody.Part.createFormData("photos[]", file.name, requestFile)
                }
                
                val response = apiService.createListing(
                    categoryId, title, description, quantity, pricePerUnit,
                    condition, location, lat, lng, photos
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _createListingState.value = Result.Success(response.body()!!.data!!)
                } else {
                    _createListingState.value = Result.Error(response.message() ?: "Error creating listing")
                }
            } catch (e: Exception) {
                _createListingState.value = Result.Error(e.message ?: "Error")
            }
        }
    }
    
    fun createOrder(listingId: Int, quantity: Int, shippingAddress: String, notes: String?) {
        viewModelScope.launch {
            _createOrderState.value = Result.Loading
            try {
                val request = mutableMapOf<String, Any>(
                    "listing_id" to listingId,
                    "quantity" to quantity,
                    "shipping_address" to shippingAddress
                )
                notes?.let { request["notes"] = it }
                
                val response = apiService.createOrder(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _createOrderState.value = Result.Success(response.body()!!.data!!)
                } else {
                    _createOrderState.value = Result.Error(response.message() ?: "Error creating order")
                }
            } catch (e: Exception) {
                _createOrderState.value = Result.Error(e.message ?: "Error")
            }
        }
    }
    
    fun loadOrders(role: String, status: String? = null) {
        viewModelScope.launch {
            _orders.value = Result.Loading
            try {
                val response = apiService.getOrders(role, status, null)
                if (response.isSuccessful && response.body()?.success == true) {
                    _orders.value = Result.Success(response.body()!!.data!!)
                } else {
                    _orders.value = Result.Error(response.message() ?: "Error")
                }
            } catch (e: Exception) {
                _orders.value = Result.Error(e.message ?: "Error")
            }
        }
    }
}