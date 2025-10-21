package com.trashbin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trashbin.app.data.repository.PickupRepository

@Suppress("UNCHECKED_CAST")
class PickupViewModelFactory(
    private val repository: PickupRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PickupViewModel::class.java)) {
            return PickupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}