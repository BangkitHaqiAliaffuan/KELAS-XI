package com.kelasxi.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.network.AuthRepository
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

    private val repository     = PickupRepository(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    // ── User info ─────────────────────────────────────────────────
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // ── Pickup list ───────────────────────────────────────────────
    private val _recentPickups = MutableStateFlow<List<PickupRequest>>(emptyList())
    val recentPickups: StateFlow<List<PickupRequest>> = _recentPickups.asStateFlow()

    private val _isLoadingPickups = MutableStateFlow(false)
    val isLoadingPickups: StateFlow<Boolean> = _isLoadingPickups.asStateFlow()

    private val _pickupsError = MutableStateFlow<String?>(null)
    val pickupsError: StateFlow<String?> = _pickupsError.asStateFlow()

    // ── Stats (derived from live data + user profile) ─────────────
    private val _statsCards = MutableStateFlow<List<StatCard>>(emptyList())
    val statsCards: StateFlow<List<StatCard>> = _statsCards.asStateFlow()

    /** Total recycled weight (kg) across non-cancelled pickups — exposed for ProfileScreen */
    private val _totalWeightKg = MutableStateFlow(0.0)
    val totalWeightKg: StateFlow<Double> = _totalWeightKg.asStateFlow()

    // ── Form state ────────────────────────────────────────────────
    private val _selectedTrashTypes = MutableStateFlow<Set<TrashType>>(emptySet())
    val selectedTrashTypes: StateFlow<Set<TrashType>> = _selectedTrashTypes.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _latitude = MutableStateFlow<Double?>(null)
    val latitude: StateFlow<Double?> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow<Double?>(null)
    val longitude: StateFlow<Double?> = _longitude.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    /** Display label shown in the button, e.g. "25 Feb 2026" */
    private val _selectedDate = MutableStateFlow("Pilih Tanggal")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    /** API-ready value, e.g. "2026-02-25" */
    private val _selectedDateRaw = MutableStateFlow("")

    private val _selectedTime = MutableStateFlow("Pilih Waktu")
    val selectedTime: StateFlow<String> = _selectedTime.asStateFlow()

    private val _estimatedWeightKg = MutableStateFlow<Double?>(null)
    val estimatedWeightKg: StateFlow<Double?> = _estimatedWeightKg.asStateFlow()

    // ── Submit state ──────────────────────────────────────────────
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError: StateFlow<String?> = _submitError.asStateFlow()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    // ── Selected pickup (for detail screen) ───────────────────────
    private val _selectedPickup = MutableStateFlow<PickupRequest?>(null)
    val selectedPickup: StateFlow<PickupRequest?> = _selectedPickup.asStateFlow()

    fun selectPickup(pickup: PickupRequest) { _selectedPickup.value = pickup }
    fun clearSelectedPickup() { _selectedPickup.value = null }

    // ─────────────────────────────────────────────────────────────
    init {
        loadPickupsAndStats()
    }

    // ── Load pickups + user data (called on login and manual refresh) ─
    fun loadPickups() {
        viewModelScope.launch {
            launch { loadPickupsInternal() }
            launch { loadUserData() }
        }
    }

    private fun loadPickupsAndStats() {
        viewModelScope.launch {
            // Fire both in parallel
            launch { loadPickupsInternal() }
            launch { loadUserData() }
        }
    }

    private suspend fun loadPickupsInternal() {
        _isLoadingPickups.value = true
        _pickupsError.value = null
        when (val result = repository.getPickups()) {
            is AuthResult.Success -> {
                _recentPickups.value = result.data
                refreshStats()
            }
            is AuthResult.Error -> _pickupsError.value = result.message
            else -> Unit
        }
        _isLoadingPickups.value = false
    }

    /** Sum estimated_weight_kg of non-cancelled pickups and update the recycled stat card. */
    private fun refreshStats() {
        val pickups = _recentPickups.value
        val recycledKg = pickups
            .filter { it.status != com.kelasxi.myapplication.model.PickupStatus.CANCELLED }
            .sumOf { it.estimatedWeightKg ?: 0.0 }
        _totalWeightKg.value = recycledKg
        val current = _statsCards.value.toMutableList()
        val recycledLabel = formatWeight(recycledKg)
        // Update or set the recycled card (index 2)
        if (current.size >= 3) {
            current[2] = StatCard(recycledLabel, "Didaur Ulang", "♻️")
            _statsCards.value = current
        }
    }

    // ── Load user profile for greeting + stats ────────────────────
    private fun loadUserData() {
        viewModelScope.launch {
            when (val result = authRepository.fetchMe()) {
                is AuthResult.Success -> {
                    val u = result.data
                    _userName.value = u.name.substringBefore(" ") // first name only
                    _statsCards.value = listOf(
                        StatCard("${u.total_pickups}",  "Total Pickup",  "🚛"),
                        StatCard("${u.items_sold}",     "Items Terjual", "🛒"),
                        StatCard("0 kg",                "Didaur Ulang",  "♻️"),
                        StatCard("${u.points_balance}", "Poin",          "⭐")
                    )
                    // Now that stats are seeded, apply the recycled weight if pickups loaded
                    refreshStats()
                }
                else -> {
                    // Fallback — pickups may already be loaded
                    val pickupCount = _recentPickups.value.size
                    _statsCards.value = listOf(
                        StatCard("$pickupCount", "Total Pickup",  "🚛"),
                        StatCard("-",            "Items Terjual", "🛒"),
                        StatCard("0 kg",         "Didaur Ulang",  "♻️"),
                        StatCard("-",            "Poin",          "⭐")
                    )
                    refreshStats()
                }
            }
        }
    }

    private fun formatWeight(kg: Double): String {
        return if (kg == 0.0) "0 kg"
        else if (kg < 1.0)   "${(kg * 1000).toInt()} g"
        else if (kg % 1.0 == 0.0) "${kg.toInt()} kg"
        else String.format("%.1f kg", kg)
    }

    // ── Form interactions ─────────────────────────────────────────
    fun toggleTrashType(type: TrashType) {
        val current = _selectedTrashTypes.value.toMutableSet()
        if (current.contains(type)) current.remove(type) else current.add(type)
        _selectedTrashTypes.value = current
    }

    fun updateAddress(value: String) { _address.value = value }
    fun updateNotes(value: String)   { _notes.value = value }
    fun updateEstimatedWeight(value: Double?) { _estimatedWeightKg.value = value }

    /** Called after user confirms a pin location on the map picker. */
    fun updateCoordinates(lat: Double, lng: Double, resolvedAddress: String) {
        _latitude.value = lat
        _longitude.value = lng
        if (resolvedAddress.isNotBlank()) _address.value = resolvedAddress
    }

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
                address             = addr,
                pickupDate          = date,
                pickupTime          = time,
                trashTypes          = types,
                notes               = _notes.value.ifBlank { null },
                estimatedWeightKg   = _estimatedWeightKg.value,
                latitude            = _latitude.value,
                longitude           = _longitude.value
            )) {
                is AuthResult.Success -> {
                    // Prepend the new pickup to the list so it appears at the top
                    _recentPickups.value = listOf(result.data) + _recentPickups.value
                    _showSuccessDialog.value = true
                    // Refresh stats so total_pickups reflects the increment on the server
                    loadUserData()
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
        _estimatedWeightKg.value = null
        _selectedDate.value    = "Pilih Tanggal"
        _selectedDateRaw.value = ""
        _selectedTime.value    = "Pilih Waktu"
        _latitude.value  = null
        _longitude.value = null
    }
}
