package com.kelasxi.weatherapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.kelasxi.weatherapp.databinding.ActivityMainBinding
import com.kelasxi.weatherapp.model.WeatherResponse
import com.kelasxi.weatherapp.utils.LocationUtils
import com.kelasxi.weatherapp.utils.WeatherUtils
import com.kelasxi.weatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    
    // Location Permission Launcher
    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            showLocationPermissionDialog()
        }
    }
    
    // Location Callback
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                Log.d(TAG, "Location received: ${location.latitude}, ${location.longitude}")
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                Log.d(TAG, "Calling weatherViewModel.getCurrentWeather with coordinates")
                weatherViewModel.getCurrentWeather(currentLatitude, currentLongitude)
                fusedLocationClient.removeLocationUpdates(this)
            } ?: Log.w(TAG, "Location result is null")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        setupLocation()
        setupObservers()
        setupClickListeners()
        
        // Load current date
        binding.tvCurrentDate.text = WeatherUtils.getCurrentDate()
        
        // Show API info if using dummy data
        if (!com.kelasxi.weatherapp.utils.Constants.isApiKeySet()) {
            Toast.makeText(this, "Demo mode: Menampilkan data dummy. Silakan dapatkan API key dari OpenWeatherMap untuk data real.", Toast.LENGTH_LONG).show()
        }
        
        // Check permission and get location
        checkLocationPermission()
    }
    
    private fun setupViews() {
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        
        // Setup swipe refresh
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.blue_primary)
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshWeatherData()
        }
    }
    
    private fun setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000 // 10 seconds
        ).build()
    }
    
    private fun setupObservers() {
        weatherViewModel.weatherData.observe(this) { weatherData ->
            Log.d(TAG, "Weather data received in MainActivity: ${weatherData.cityName}")
            updateWeatherUI(weatherData)
        }
        
        weatherViewModel.isLoading.observe(this) { isLoading ->
            Log.d(TAG, "Loading state changed: $isLoading")
            showLoading(isLoading)
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        
        weatherViewModel.errorMessage.observe(this) { errorMessage ->
            Log.d(TAG, "Error message received: $errorMessage")
            if (errorMessage.isNotEmpty()) {
                showError(errorMessage)
            } else {
                hideError()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            showSearchCityDialog()
        }
    }
    
    private fun checkLocationPermission() {
        when {
            LocationUtils.hasLocationPermission(this) -> {
                if (LocationUtils.isLocationEnabled(this)) {
                    getCurrentLocation()
                } else {
                    showLocationSettingsDialog()
                }
            }
            else -> {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    
    private fun getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation called")
        if (!LocationUtils.hasLocationPermission(this)) {
            Log.w(TAG, "Location permission not granted")
            return
        }
        
        try {
            Log.d(TAG, "Requesting location updates...")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when requesting location", e)
            Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun refreshWeatherData() {
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            weatherViewModel.getCurrentWeather(currentLatitude, currentLongitude)
        } else {
            getCurrentLocation()
        }
    }
    
    private fun updateWeatherUI(weatherData: WeatherResponse) {
        binding.apply {
            // Update basic info
            tvCityName.text = weatherData.cityName
            tvTemperature.text = "${weatherData.main.temperature.toInt()}°C"
            tvWeatherDescription.text = WeatherUtils.capitalizeWords(weatherData.weather[0].description)
            tvFeelsLike.text = "Terasa seperti ${weatherData.main.feelsLike.toInt()}°C"
            
            // Update detailed info
            tvTempRange.text = "${weatherData.main.tempMin.toInt()}°C - ${weatherData.main.tempMax.toInt()}°C"
            tvHumidity.text = "${weatherData.main.humidity}%"
            tvWindSpeed.text = "${(weatherData.wind.speed * 3.6).toInt()} km/h"
            tvSunrise.text = WeatherUtils.formatTime(weatherData.sys.sunrise)
            tvSunset.text = WeatherUtils.formatTime(weatherData.sys.sunset)
            
            // Update weather animation
            val animationRes = WeatherUtils.getWeatherAnimation(weatherData.weather[0].main)
            weatherAnimation.setAnimation(animationRes)
            weatherAnimation.playAnimation()
            
            // Update background
            val backgroundRes = WeatherUtils.getWeatherBackground(weatherData.weather[0].main)
            main.setBackgroundResource(backgroundRes)
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.weatherContent.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
    
    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
        binding.weatherContent.visibility = View.GONE
    }
    
    private fun hideError() {
        binding.tvError.visibility = View.GONE
        binding.weatherContent.visibility = View.VISIBLE
    }
    
    private fun showSearchCityDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_city, null)
        val etCityName = dialogView.findViewById<TextInputEditText>(R.id.etCityName)
        val btnSearch = dialogView.findViewById<MaterialButton>(R.id.btnSearch)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        btnSearch.setOnClickListener {
            val cityName = etCityName.text.toString().trim()
            if (cityName.isNotEmpty()) {
                weatherViewModel.getCurrentWeatherByCity(cityName)
                dialog.dismiss()
            } else {
                etCityName.error = "Masukkan nama kota"
            }
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Izin Lokasi Diperlukan")
            .setMessage("Aplikasi memerlukan izin lokasi untuk menampilkan cuaca di lokasi Anda saat ini.")
            .setPositiveButton("Pengaturan") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Gunakan fitur pencarian kota untuk melihat cuaca", Toast.LENGTH_LONG).show()
            }
            .show()
    }
    
    private fun showLocationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Aktifkan Lokasi")
            .setMessage("Silakan aktifkan layanan lokasi di pengaturan perangkat.")
            .setPositiveButton("Pengaturan") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Gunakan fitur pencarian kota untuk melihat cuaca", Toast.LENGTH_LONG).show()
            }
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        if (LocationUtils.hasLocationPermission(this) && LocationUtils.isLocationEnabled(this)) {
            if (currentLatitude == 0.0 && currentLongitude == 0.0) {
                getCurrentLocation()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}