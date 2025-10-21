package com.trashbin.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trashbin.app.data.model.Order
import com.trashbin.app.data.repository.OrderRepository
import com.trashbin.app.data.repository.RepositoryResult
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableLiveData<RepositoryResult<List<Order>>>()
    val orders: LiveData<RepositoryResult<List<Order>>> = _orders

    private val _orderAction = MutableLiveData<RepositoryResult<Order>>()
    val orderAction: LiveData<RepositoryResult<Order>> = _orderAction

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadOrders(role: String, status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("OrderViewModel", "Loading orders - role: $role, status: $status")
            
            try {
                val result = repository.getMyOrders(role, status)
                _orders.value = result
                when (result) {
                    is RepositoryResult.Success -> {
                        Log.d("OrderViewModel", "Orders loaded: ${result.data.size} items")
                    }
                    is RepositoryResult.Error -> {
                        Log.e("OrderViewModel", "Error from repository: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error loading orders", e)
                _orders.value = RepositoryResult.Error(e.message ?: "Error occurred")
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
                
                when (result) {
                    is RepositoryResult.Success -> {
                        Log.d("OrderViewModel", "Order confirmed successfully")
                    }
                    is RepositoryResult.Error -> {
                        Log.e("OrderViewModel", "Error confirming order: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error confirming order", e)
                _orderAction.value = RepositoryResult.Error(e.message ?: "Error occurred")
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
                
                when (result) {
                    is RepositoryResult.Success -> {
                        Log.d("OrderViewModel", "Order completed successfully")
                    }
                    is RepositoryResult.Error -> {
                        Log.e("OrderViewModel", "Error completing order: ${result.message}")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error completing order", e)
                _orderAction.value = RepositoryResult.Error(e.message ?: "Error occurred")
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
