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
    
    private val _listingDetail = MutableLiveData<Result<MarketplaceListing>>()
    val listingDetail: LiveData<Result<MarketplaceListing>> = _listingDetail

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
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        _listings.value = Result.Success(body.data)
                    } else if (body?.data != null) {
                        // Handle case where success field is missing but data exists
                        _listings.value = Result.Success(body.data)
                    } else {
                        _listings.value = Result.Error(body?.message ?: "Error loading listings")
                    }
                } else {
                    _listings.value = Result.Error(response.message() ?: "Error loading listings")
                }
            } catch (e: Exception) {
                _listings.value = Result.Error(e.message ?: "An error occurred")
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
    
    fun createOrder(listingId: Int, quantity: Double, shippingAddress: String, notes: String?) {
        viewModelScope.launch {
            _createOrderState.value = Result.Loading
            try {
                val request = com.trashbin.app.data.model.CreateOrderRequest(
                    listingId = listingId,
                    quantity = quantity,
                    shippingAddress = shippingAddress,
                    notes = notes
                )
                
                android.util.Log.d("MarketplaceViewModel", "Creating order: listingId=$listingId, quantity=$quantity, address=$shippingAddress")
                
                val response = apiService.createOrder(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        android.util.Log.d("MarketplaceViewModel", "Order created successfully: ${body.data.id}")
                        _createOrderState.value = Result.Success(body.data)
                    } else if (body?.data != null) {
                        _createOrderState.value = Result.Success(body.data)
                    } else {
                        android.util.Log.e("MarketplaceViewModel", "Order creation failed: ${body?.message}")
                        _createOrderState.value = Result.Error(body?.message ?: "Error creating order")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("MarketplaceViewModel", "Order creation failed: ${response.code()} - $errorBody")
                    _createOrderState.value = Result.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceViewModel", "Exception creating order", e)
                _createOrderState.value = Result.Error(e.message ?: "An error occurred")
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
    
    fun loadListingDetail(listingId: Int) {
        viewModelScope.launch {
            _listingDetail.value = Result.Loading
            try {
                val response = apiService.getListingDetail(listingId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        _listingDetail.value = Result.Success(body.data)
                    } else if (body?.data != null) {
                        // Handle case where success field is missing but data exists
                        _listingDetail.value = Result.Success(body.data)
                    } else {
                        _listingDetail.value = Result.Error(body?.message ?: "Error loading listing detail")
                    }
                } else {
                    _listingDetail.value = Result.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceViewModel", "Error loading listing detail", e)
                _listingDetail.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}