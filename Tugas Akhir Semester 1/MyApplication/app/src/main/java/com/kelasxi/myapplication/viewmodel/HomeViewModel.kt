package com.kelasxi.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.data.network.AuthResult
import com.kelasxi.myapplication.data.network.PickupRepository
import com.kelasxi.myapplication.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PickupRepository(application.applicationContext)

    // ── Pickup list ───────────────────────────────────────────────
    private val _recentPickups = MutableStateFlow<List<PickupRequest>>(emptyList())
    val recentPickups: StateFlow<List<PickupRequest>> = _recentPickups.asStateFlow()

    private val _isLoadingPickups = MutableStateFlow(false)
    val isLoadingPickups: StateFlow<Boolean> = _isLoadingPickups.asStateFlow()

    private val _pickupsError = MutableStateFlow<String?>(null)
    val pickupsError: StateFlow<String?> = _pickupsError.asStateFlow()

    // ── Stats ─────────────────────────────────────────────────────
    private val _statsCards = MutableStateFlow(MockData.statsCards)
    val statsCards: StateFlow<List<StatCard>> = _statsCards.asStateFlow()

    // ── Form state ────────────────────────────────────────────────
    private val _selectedTrashTypes = MutableStateFlow<Set<TrashType>>(emptySet())
    val selectedTrashTypes: StateFlow<Set<TrashType>> = _selectedTrashTypes.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    /** Display label shown in the button, e.g. "25 Feb 2026" */
    private val _selectedDate = MutableStateFlow("Pilih Tanggal")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    /** API-ready value, e.g. "2026-02-25" */
    private val _selectedDateRaw = MutableStateFlow("")

    private val _selectedTime = MutableStateFlow("Pilih Waktu")
    val selectedTime: StateFlow<String> = _selectedTime.asStateFlow()

    // ── Submit state ──────────────────────────────────────────────
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError: StateFlow<String?> = _submitError.asStateFlow()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    // ─────────────────────────────────────────────────────────────
    init {
        loadPickups()
    }

    // ── Load pickups from API ─────────────────────────────────────
    fun loadPickups() {
        viewModelScope.launch {
            _isLoadingPickups.value = true
            _pickupsError.value = null
            when (val result = repository.getPickups()) {
                is AuthResult.Success -> _recentPickups.value = result.data
                is AuthResult.Error   -> _pickupsError.value = result.message
                else -> Unit
            }
            _isLoadingPickups.value = false
        }
    }

    // ── Form interactions ─────────────────────────────────────────
    fun toggleTrashType(type: TrashType) {
        val current = _selectedTrashTypes.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _selectedTrashTypes.value = current
    }

    fun updateAddress(value: String) { _address.value = value }
    fun updateNotes(value: String)   { _notes.value = value }

    /**
     * Called from the DatePicker with the display-formatted string (e.g. "25 Feb 2026").
     * We also derive the ISO yyyy-MM-dd value required by the API.
     */
    fun updateDate(displayValue: String) {
        _selectedDate.value = displayValue
        // Convert display label back to yyyy-MM-dd for the API
        try {
            val displayFmt = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
            val parsed = displayFmt.parse(displayValue)
            _selectedDateRaw.value = if (parsed != null) {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsed)
            } else displayValue
        } catch (_: Exception) {
            _selectedDateRaw.value = displayValue
        }
    }

    fun updateTime(value: String) { _selectedTime.value = value }

    fun dismissError() { _submitError.value = null }

    // ── Submit pickup ─────────────────────────────────────────────
    fun submitPickup() {
        val addr = _address.value.trim()
        val date = _selectedDateRaw.value
        val time = _selectedTime.value
        val types = _selectedTrashTypes.value.toList()

        if (addr.isBlank()) { _submitError.value = "Masukkan alamat penjemputan."; return }
        if (date.isBlank() || date == "Pilih Tanggal") { _submitError.value = "Pilih tanggal penjemputan."; return }
        if (time.isBlank() || time == "Pilih Waktu")   { _submitError.value = "Pilih waktu penjemputan."; return }
        if (types.isEmpty()) { _submitError.value = "Pilih minimal satu jenis sampah."; return }

        viewModelScope.launch {
            _isSubmitting.value = true
            _submitError.value  = null

            when (val result = repository.createPickup(
                address     = addr,
                pickupDate  = date,
                pickupTime  = time,
                trashTypes  = types,
                notes       = _notes.value.ifBlank { null }
            )) {
                is AuthResult.Success -> {
                    // Prepend the new pickup to the list so it appears at the top
                    _recentPickups.value = listOf(result.data) + _recentPickups.value
                    _showSuccessDialog.value = true
                }
                is AuthResult.Error -> _submitError.value = result.message
                else -> Unit
            }
            _isSubmitting.value = false
        }
    }

    fun dismissSuccessDialog() {
        _showSuccessDialog.value = false
        _selectedTrashTypes.value = emptySet()
        _notes.value = ""
        _selectedDate.value    = "Pilih Tanggal"
        _selectedDateRaw.value = ""
        _selectedTime.value    = "Pilih Waktu"
    }
}
