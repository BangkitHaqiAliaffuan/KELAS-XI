package com.trashbin.app.ui.maps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.tomtom.sdk.common.data.Position
import com.tomtom.sdk.geocoding.GeocodingDependencyRegistry
import com.tomtom.sdk.geocoding.GeocodingService
import com.tomtom.sdk.geocoding.data.GeocodingQuery
import com.tomtom.sdk.map.display.MapFragment
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.gesture.OnMapClickListener
import com.tomtom.sdk.map.display.markers.Marker
import com.tomtom.sdk.map.display.markers.MarkerOptions
import com.tomtom.sdk.search.SearchDependencyRegistry
import com.tomtom.sdk.search.SearchService
import com.tomtom.sdk.search.data.SearchQuery
import com.tomtom.sdk.search.data.SearchOptions
import com.trashbin.app.BuildConfig
import com.trashbin.app.R
import com.trashbin.app.databinding.ActivityMapPickerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MapPickerActivity - Updated untuk TomTom SDK 1.26.0
 * 
 * Fitur yang tersedia dengan Free Tier:
 * - Map Display (50,000 tile requests/day)
 * - Basic Search (2,500 non-tile requests/day) 
 * - Geocoding (2,500 requests/day)
 * - Location Picker
 * 
 * Fitur Premium (memerlukan subscription):
 * - Turn-by-turn Navigation
 * - Real-time Traffic
 * - Advanced Routing
 */
class MapPickerActivityUpdated : AppCompatActivity() {
    
    companion object {
        const val EXTRA_SELECTED_POSITION = "selected_position"
        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        
        // Default position (Jakarta, Indonesia)
        private val DEFAULT_POSITION = Position(-6.2088, 106.8456)
    }
    
    private lateinit var binding: ActivityMapPickerBinding
    private lateinit var tomTomMap: TomTomMap
    private lateinit var searchService: SearchService
    private lateinit var geocodingService: GeocodingService
    
    private var selectedPosition: Position? = null
    private var selectedAddress: String? = null
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeTomTomServices()
        setupMapFragment()
        setupUI()
    }

    private fun initializeTomTomServices() {
        // Initialize Search Service dengan Free Tier limitations
        searchService = SearchDependencyRegistry.INSTANCE.searchService
        
        // Initialize Geocoding Service untuk reverse geocoding
        geocodingService = GeocodingDependencyRegistry.INSTANCE.geocodingService
    }

    private fun setupMapFragment() {
        // Konfigurasi MapOptions untuk SDK 1.26.0
        val mapOptions = MapOptions(
            mapKey = BuildConfig.TOMTOM_API_KEY
        )
        
        // Buat MapFragment
        val mapFragment = MapFragment.newInstance(mapOptions)
        
        // Tambahkan fragment ke container
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
            
        // Setup map ready callback
        mapFragment.getMapAsync { map ->
            this.tomTomMap = map
            setupMapInteractions()
            moveToDefaultLocation()
        }
    }

    private fun setupMapInteractions() {
        // Setup map click listener untuk location picking
        tomTomMap.addOnMapClickListener(OnMapClickListener { position ->
            selectPosition(position)
            true // Consume the event
        })
    }

    private fun moveToDefaultLocation() {
        // Pindah ke lokasi default dengan zoom level yang sesuai
        tomTomMap.moveCamera(
            com.tomtom.sdk.map.display.camera.CameraOptions(
                position = DEFAULT_POSITION,
                zoom = 12.0
            )
        )
    }

    private fun selectPosition(position: Position) {
        selectedPosition = position
        
        // Hapus marker lama jika ada
        currentMarker?.let { tomTomMap.removeMarker(it) }
        
        // Tambah marker baru
        val markerOptions = MarkerOptions(
            coordinate = position,
            // Bisa customize marker appearance di sini
        )
        currentMarker = tomTomMap.addMarker(markerOptions)
        
        // Lakukan reverse geocoding untuk mendapatkan alamat
        performReverseGeocoding(position)
    }

    private fun performReverseGeocoding(position: Position) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Reverse geocoding dengan Free Tier (2500 requests/day)
                val query = GeocodingQuery.ReverseGeocodingQuery(position)
                val response = geocodingService.reverseGeocoding(query)
                
                response.onSuccess { result ->
                    val address = result.addresses.firstOrNull()
                    selectedAddress = address?.let { buildAddressString(it) } ?: "Alamat tidak ditemukan"
                    
                    withContext(Dispatchers.Main) {
                        updateAddressDisplay()
                    }
                }
                
                response.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        selectedAddress = "Error: ${error.message}"
                        updateAddressDisplay()
                    }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    selectedAddress = "Error mendapatkan alamat"
                    updateAddressDisplay()
                }
            }
        }
    }

    private fun buildAddressString(address: com.tomtom.sdk.geocoding.data.Address): String {
        return buildString {
            address.streetNumber?.let { append("$it ") }
            address.streetName?.let { append("$it, ") }
            address.municipality?.let { append("$it, ") }
            address.countrySubdivision?.let { append("$it, ") }
            address.country?.let { append(it) }
        }.trimEnd(',', ' ')
    }

    private fun updateAddressDisplay() {
        binding.tvSelectedAddress.text = selectedAddress ?: "Pilih lokasi pada peta"
    }

    private fun setupUI() {
        // Setup search functionality
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { query ->
                    if (query.length > 2) {
                        performSearch(query)
                    }
                }
            }
        })

        // Setup confirm button
        binding.btnConfirm.setOnClickListener {
            selectedPosition?.let { position ->
                val intent = Intent().apply {
                    putExtra(EXTRA_SELECTED_POSITION, position)
                    putExtra(EXTRA_SELECTED_ADDRESS, selectedAddress)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        // Setup current location button
        binding.fabCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Search dengan Free Tier limitations (2500 requests/day)
                val searchQuery = SearchQuery.SearchQuery(query)
                val searchOptions = SearchOptions(
                    // Batasi hasil untuk menghemat quota
                    limit = 5
                )
                
                val response = searchService.search(searchQuery, searchOptions)
                
                response.onSuccess { result ->
                    val firstResult = result.results.firstOrNull()
                    firstResult?.let { searchResult ->
                        withContext(Dispatchers.Main) {
                            // Pindah kamera ke hasil pencarian pertama
                            tomTomMap.moveCamera(
                                com.tomtom.sdk.map.display.camera.CameraOptions(
                                    position = searchResult.place.coordinate,
                                    zoom = 15.0
                                )
                            )
                            
                            // Select posisi hasil pencarian
                            selectPosition(searchResult.place.coordinate)
                        }
                    }
                }
                
                response.onFailure { error ->
                    // Handle search error
                    withContext(Dispatchers.Main) {
                        // Show error message to user
                    }
                }
                
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private fun getCurrentLocation() {
        // Implementasi untuk mendapatkan lokasi saat ini
        // Memerlukan location permissions dan LocationProvider
        // Untuk simplicity, gunakan lokasi default untuk demo
        selectPosition(DEFAULT_POSITION)
        
        tomTomMap.moveCamera(
            com.tomtom.sdk.map.display.camera.CameraOptions(
                position = DEFAULT_POSITION,
                zoom = 15.0
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup resources jika diperlukan
    }
}