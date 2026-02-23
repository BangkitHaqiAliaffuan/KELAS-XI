package com.kelasxi.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _recentPickups = MutableStateFlow(MockData.recentPickups)
    val recentPickups: StateFlow<List<PickupRequest>> = _recentPickups.asStateFlow()

    private val _statsCards = MutableStateFlow(MockData.statsCards)
    val statsCards: StateFlow<List<StatCard>> = _statsCards.asStateFlow()

    private val _selectedTrashTypes = MutableStateFlow<Set<TrashType>>(emptySet())
    val selectedTrashTypes: StateFlow<Set<TrashType>> = _selectedTrashTypes.asStateFlow()

    private val _address = MutableStateFlow("Jl. Sudirman No. 12, Jakarta Pusat")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _selectedDate = MutableStateFlow("Pilih Tanggal")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow("Pilih Waktu")
    val selectedTime: StateFlow<String> = _selectedTime.asStateFlow()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    fun toggleTrashType(type: TrashType) {
        val current = _selectedTrashTypes.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _selectedTrashTypes.value = current
    }

    fun updateAddress(value: String) { _address.value = value }
    fun updateNotes(value: String) { _notes.value = value }
    fun updateDate(value: String) { _selectedDate.value = value }
    fun updateTime(value: String) { _selectedTime.value = value }

    fun submitPickup() {
        _showSuccessDialog.value = true
    }

    fun dismissSuccessDialog() {
        _showSuccessDialog.value = false
        _selectedTrashTypes.value = emptySet()
        _notes.value = ""
        _selectedDate.value = "Pilih Tanggal"
        _selectedTime.value = "Pilih Waktu"
    }
}
