package com.kelasxi.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.weatherapp.model.WeatherResponse
import com.kelasxi.weatherapp.repository.WeatherRepository
import com.kelasxi.weatherapp.utils.Constants
import com.kelasxi.weatherapp.utils.DummyData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "WeatherViewModel"
    }
    
    private val repository = WeatherRepository()
    
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    fun getCurrentWeather(latitude: Double, longitude: Double) {
        Log.d(TAG, "getCurrentWeather called with lat: $latitude, lon: $longitude")
        Log.d(TAG, "API Key status: ${if (Constants.isApiKeySet()) "SET" else "NOT_SET"}")
        
        if (!Constants.isApiKeySet()) {
            Log.i(TAG, "Using dummy data - API key not set")
            // Gunakan dummy data untuk demo
            loadDummyData()
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting API call...")
                _isLoading.value = true
                _errorMessage.value = ""
                
                val response = repository.getCurrentWeather(latitude, longitude)
                Log.d(TAG, "API response received - Success: ${response.isSuccessful}, Code: ${response.code()}")
                
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Weather data received for city: ${response.body()?.cityName}")
                    _weatherData.value = response.body()
                    _errorMessage.value = ""
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "API call failed - Code: ${response.code()}, Message: ${response.message()}")
                    Log.e(TAG, "Error body: $errorBody")
                    
                    val errorMsg = when (response.code()) {
                        401 -> "API key tidak valid. Periksa konfigurasi API key Anda."
                        404 -> "Lokasi tidak ditemukan"
                        429 -> "Terlalu banyak permintaan. Coba lagi nanti."
                        else -> "Gagal memuat data cuaca (Code: ${response.code()})"
                    }
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in getCurrentWeather", e)
                val errorMsg = when {
                    e.message?.contains("timeout", true) == true -> "Koneksi timeout. Periksa koneksi internet Anda."
                    e.message?.contains("network", true) == true -> "Tidak ada koneksi internet"
                    e.message?.contains("host", true) == true -> "Server tidak dapat dijangkau"
                    else -> "Error: ${e.message}"
                }
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "getCurrentWeather finished")
            }
        }
    }
    
    fun getCurrentWeatherByCity(cityName: String) {
        Log.d(TAG, "getCurrentWeatherByCity called with city: $cityName")
        Log.d(TAG, "API Key status: ${if (Constants.isApiKeySet()) "SET" else "NOT_SET"}")
        
        if (!Constants.isApiKeySet()) {
            Log.i(TAG, "Using dummy data for city search - API key not set")
            // Gunakan dummy data untuk demo
            loadDummyDataByCity(cityName)
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting API call for city: $cityName")
                _isLoading.value = true
                _errorMessage.value = ""
                
                val response = repository.getCurrentWeatherByCity(cityName)
                Log.d(TAG, "City API response received - Success: ${response.isSuccessful}, Code: ${response.code()}")
                
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Weather data received for city: ${response.body()?.cityName}")
                    _weatherData.value = response.body()
                    _errorMessage.value = ""
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "City API call failed - Code: ${response.code()}, Message: ${response.message()}")
                    Log.e(TAG, "Error body: $errorBody")
                    
                    val errorMsg = when (response.code()) {
                        401 -> "API key tidak valid"
                        404 -> "Kota '$cityName' tidak ditemukan"
                        429 -> "Terlalu banyak permintaan. Coba lagi nanti."
                        else -> "Kota tidak ditemukan (Code: ${response.code()})"
                    }
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in getCurrentWeatherByCity", e)
                val errorMsg = when {
                    e.message?.contains("timeout", true) == true -> "Koneksi timeout. Periksa koneksi internet Anda."
                    e.message?.contains("network", true) == true -> "Tidak ada koneksi internet"
                    e.message?.contains("host", true) == true -> "Server tidak dapat dijangkau"
                    else -> "Error: ${e.message}"
                }
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "getCurrentWeatherByCity finished")
            }
        }
    }
    
    private fun loadDummyData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulasi loading
            _weatherData.value = DummyData.createDummyWeatherResponse()
            _errorMessage.value = ""
            _isLoading.value = false
        }
    }
    
    private fun loadDummyDataByCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulasi loading
            
            val dummyData = when (cityName.lowercase()) {
                "bandung" -> DummyData.createCloudyWeatherResponse()
                "bogor" -> DummyData.createRainyWeatherResponse()
                else -> DummyData.createDummyWeatherResponse().copy(cityName = cityName)
            }
            
            _weatherData.value = dummyData
            _errorMessage.value = ""
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}
