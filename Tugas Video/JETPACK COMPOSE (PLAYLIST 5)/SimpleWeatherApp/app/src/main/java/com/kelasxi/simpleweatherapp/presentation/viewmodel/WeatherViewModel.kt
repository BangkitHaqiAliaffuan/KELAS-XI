package com.kelasxi.simpleweatherapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.simpleweatherapp.data.location.LocationManager
import com.kelasxi.simpleweatherapp.data.model.ForecastUiState
import com.kelasxi.simpleweatherapp.data.model.WeatherUiState
import com.kelasxi.simpleweatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state dan logic aplikasi cuaca
 * Menggunakan MVVM pattern dengan StateFlow untuk reactive UI
 * Enhanced dengan location detection
 */
class WeatherViewModel(private val context: Context) : ViewModel() {
    
    private val repository = WeatherRepository()
    private val locationManager = LocationManager(context)
    
    // Private mutable state untuk internal use
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    
    // Public read-only state untuk UI
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    // State untuk forecast
    private val _forecastState = MutableStateFlow<ForecastUiState>(ForecastUiState.Loading)
    val forecastState: StateFlow<ForecastUiState> = _forecastState.asStateFlow()
    
    // State untuk search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // State untuk location permission
    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()
    
    init {
        // Cek permission dan load weather berdasarkan lokasi atau default
        initializeWeatherData()
    }
    
    /**
     * Initialize weather data berdasarkan lokasi device atau fallback ke default
     */
    private fun initializeWeatherData() {
        viewModelScope.launch {
            _hasLocationPermission.value = locationManager.hasLocationPermission()
            
            if (locationManager.hasLocationPermission()) {
                loadWeatherFromCurrentLocation()
            } else {
                // Fallback ke Jakarta jika tidak ada permission
                searchWeather("Jakarta")
            }
        }
    }
    
    /**
     * Load weather data dari lokasi saat ini
     */
    fun loadWeatherFromCurrentLocation() {
        if (!locationManager.hasLocationPermission()) {
            _uiState.value = WeatherUiState.Error("Location permission required for current location")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            
            try {
                // Coba dapatkan lokasi saat ini
                val location = locationManager.getCurrentLocation() 
                    ?: locationManager.getLastKnownLocation()
                
                if (location != null) {
                    val coordinates = locationManager.locationToCoordinates(location)
                    repository.getWeatherByCoordinates(coordinates)
                        .onSuccess { weatherResponse ->
                            _uiState.value = WeatherUiState.Success(weatherResponse)
                            // Auto load forecast for current location
                            loadForecastFromCurrentLocation()
                        }
                        .onFailure { exception ->
                            _uiState.value = WeatherUiState.Error(
                                exception.message ?: "Failed to load weather for current location"
                            )
                        }
                } else {
                    // Fallback ke Jakarta jika gagal mendapatkan lokasi
                    searchWeather("Jakarta")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(
                    "Failed to get current location: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update location permission status
     */
    fun updateLocationPermission(granted: Boolean) {
        _hasLocationPermission.value = granted
        if (granted) {
            loadWeatherFromCurrentLocation()
        }
    }
    
    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Melakukan pencarian cuaca berdasarkan nama kota
     * @param cityName nama kota yang akan dicari
     */
    fun searchWeather(cityName: String) {
        if (cityName.isBlank()) {
            _uiState.value = WeatherUiState.Error("Please enter a city name")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            
            repository.getWeatherData(cityName.trim())
                .onSuccess { weatherResponse ->
                    _uiState.value = WeatherUiState.Success(weatherResponse)
                    // Auto load forecast when current weather is loaded successfully
                    loadForecast(cityName.trim())
                }
                .onFailure { exception ->
                    _uiState.value = WeatherUiState.Error(
                        exception.message ?: "Failed to load weather data"
                    )
                }
        }
    }
    
    /**
     * Load forecast data berdasarkan nama kota
     * @param cityName nama kota untuk forecast
     * @param days jumlah hari forecast (default 3)
     */
    fun loadForecast(cityName: String, days: Int = 3) {
        if (cityName.isBlank()) return
        
        viewModelScope.launch {
            _forecastState.value = ForecastUiState.Loading
            
            repository.getForecastData(cityName.trim(), days)
                .onSuccess { forecastResponse ->
                    _forecastState.value = ForecastUiState.Success(forecastResponse)
                }
                .onFailure { exception ->
                    _forecastState.value = ForecastUiState.Error(
                        exception.message ?: "Failed to load forecast data"
                    )
                }
        }
    }
    
    /**
     * Load forecast berdasarkan lokasi saat ini
     * @param days jumlah hari forecast (default 3)
     */
    fun loadForecastFromCurrentLocation(days: Int = 3) {
        if (!locationManager.hasLocationPermission()) {
            _forecastState.value = ForecastUiState.Error("Location permission required for forecast")
            return
        }
        
        viewModelScope.launch {
            _forecastState.value = ForecastUiState.Loading
            
            try {
                val location = locationManager.getCurrentLocation() 
                    ?: locationManager.getLastKnownLocation()
                
                if (location != null) {
                    val coordinates = locationManager.locationToCoordinates(location)
                    repository.getForecastByCoordinates(coordinates, days)
                        .onSuccess { forecastResponse ->
                            _forecastState.value = ForecastUiState.Success(forecastResponse)
                        }
                        .onFailure { exception ->
                            _forecastState.value = ForecastUiState.Error(
                                exception.message ?: "Failed to load forecast for current location"
                            )
                        }
                } else {
                    _forecastState.value = ForecastUiState.Error("Unable to get current location")
                }
            } catch (e: Exception) {
                _forecastState.value = ForecastUiState.Error(
                    "Failed to get current location: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Retry untuk melakukan request ulang dengan query terakhir
     */
    fun retry() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            searchWeather(currentQuery)
        } else {
            searchWeather("Jakarta") // Default fallback
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        if (_uiState.value is WeatherUiState.Error) {
            _uiState.value = WeatherUiState.Loading
        }
    }
}