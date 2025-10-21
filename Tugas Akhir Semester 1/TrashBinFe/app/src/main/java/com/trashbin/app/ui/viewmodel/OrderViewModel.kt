package com.trashbin.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.model.Order
import com.trashbin.app.data.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableLiveData<Result<List<Order>>>()
    val orders: LiveData<Result<List<Order>>> = _orders

    private val _orderAction = MutableLiveData<Result<Order>>()
    val orderAction: LiveData<Result<Order>> = _orderAction

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadOrders(role: String, status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("OrderViewModel", "Loading orders - role: $role, status: $status")
            
            try {
                val result = repository.getMyOrders(role, status)
                _orders.value = result
                Log.d("OrderViewModel", "Orders loaded: ${result.getOrNull()?.size ?: 0} items")
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error loading orders", e)
                _orders.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmOrder(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("OrderViewModel", "Confirming order: $orderId")
            
            try {
                val result = repository.confirmOrder(orderId)
                _orderAction.value = result
                
                if (result.isSuccess) {
                    Log.d("OrderViewModel", "Order confirmed successfully")
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error confirming order", e)
                _orderAction.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeOrder(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("OrderViewModel", "Completing order: $orderId")
            
            try {
                val result = repository.completeOrder(orderId)
                _orderAction.value = result
                
                if (result.isSuccess) {
                    Log.d("OrderViewModel", "Order completed successfully")
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error completing order", e)
                _orderAction.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class OrderViewModelFactory(private val repository: OrderRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
