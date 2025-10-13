package com.trashbin.app.ui.maps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.sdk.common.CommonDependencyRegistry
import com.tomtom.sdk.common.configuration.MapConfiguration
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.gesture.OnMapClickListener
import com.tomtom.sdk.map.display.markers.Marker
import com.tomtom.sdk.map.display.markers.MarkerOptions
import com.tomtom.sdk.search.SearchDependencyRegistry
import com.tomtom.sdk.search.SearchOptions
import com.tomtom.sdk.search.SearchQuery
import com.tomtom.sdk.search.SearchService
import com.tomtom.sdk.geocoding.GeocodingService
import com.tomtom.sdk.geocoding.GeocodingDependencyRegistry
import com.tomtom.sdk.geocoding.data.GeocodingQuery
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.tomtom.sdk.common.data.Position
import com.trashbin.app.R
import com.trashbin.app.databinding.ActivityMapPickerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MapPickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapPickerBinding
    private lateinit var tomTomMap: TomTomMap
    private lateinit var searchService: SearchService
    private lateinit var geocodingService: GeocodingService
    private var selectedPosition: Position? = null
    private var selectedAddress: String? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TomTom Services
        val mapConfiguration = MapConfiguration(
            apiKey = getString(R.string.tomtom_api_key),
            context = applicationContext
        )
        
        val mapOptions = MapOptions(
            configuration = mapConfiguration,
            view = binding.tomtomMap
        )
        
        // Initialize TomTom map
        val map = CommonDependencyRegistry.createMapDisplay().create(mapOptions)
        tomTomMap = map
        
        // Initialize services
        searchService = SearchDependencyRegistry.createSearchService(
            context = applicationContext,
            apiKey = getString(R.string.tomtom_api_key)
        )
        
        geocodingService = GeocodingDependencyRegistry.createGeocodingService(
            context = applicationContext,
            apiKey = getString(R.string.tomtom_api_key)
        )

        setupUI()
        setupInitialMap()
    }

    private fun setupInitialMap() {
        // Set initial position to Jakarta
        val jakartaPosition = Position(latitude = -6.2088, longitude = 106.8456)
        tomTomMap.setCameraPosition(jakartaPosition, zoom = 15.0)
        
        // Add initial marker
        addMarkerAtPosition(jakartaPosition, "Pilih Lokasi")
        selectedPosition = jakartaPosition
        
        // Update address for initial position
        updateAddressFromPosition(jakartaPosition)
        
        // Set map click listener to update position when user clicks on map
        tomTomMap.addOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(position: Position) {
                addMarkerAtPosition(position, "Lokasi Anda")
                selectedPosition = position
                updateAddressFromPosition(position)
            }
        })
    }

    private fun addMarkerAtPosition(position: Position, title: String) {
        marker?.let { tomTomMap.removeMarker(it) }
        val markerOptions = MarkerOptions(
            position = position,
            title = title
        )
        marker = tomTomMap.addMarker(markerOptions)
    }

    private fun updateAddressFromPosition(position: Position) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocodingQuery = GeocodingQuery(
                    position = position,
                    language = Locale.getDefault().language
                )
                
                val results = geocodingService.geocode(geocodingQuery)
                if (results.isNotEmpty()) {
                    val address = results.first().address
                    selectedAddress = buildAddressString(address)
                    
                    withContext(Dispatchers.Main) {
                        binding.tvAddress.text = selectedAddress
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun buildAddressString(address: com.tomtom.sdk.geocoding.data.Address): String {
        val addressParts = mutableListOf<String>()
        
        address.street?.let { addressParts.add(it) }
        address.city?.let { addressParts.add(it) }
        address.country?.let { addressParts.add(it) }
        
        return addressParts.joinToString(", ")
    }

    private fun setupUI() {
        // Set up search functionality
        setupSearchFunctionality()

        // Current location button
        binding.fabCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        // Confirm button
        binding.btnConfirm.setOnClickListener {
            if (selectedPosition != null && !selectedAddress.isNullOrEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("lat", selectedPosition?.latitude)
                    putExtra("lng", selectedPosition?.longitude)
                    putExtra("address", selectedAddress)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        // Back button
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSearchFunctionality() {
        val searchInput = binding.searchInput
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Handle text change if needed
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length > 3) {  // Start search after 3 characters
                    performSearch(query)
                }
            }
        })
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val searchQuery = SearchQuery(query)
                val searchOptions = SearchOptions(
                    position = tomTomMap.cameraPosition.position,
                    language = Locale.getDefault().language
                )
                
                val results = searchService.search(searchQuery, searchOptions)
                if (results.isNotEmpty()) {
                    val firstResult = results.first()
                    val position = firstResult.position
                    
                    withContext(Dispatchers.Main) {
                        // Move camera to found location
                        tomTomMap.setCameraPosition(position, zoom = 15.0)
                        
                        // Add marker
                        addMarkerAtPosition(position, firstResult.address?.freeformAddress ?: "Lokasi")
                        
                        // Update selected position and address
                        selectedPosition = position
                        selectedAddress = firstResult.address?.freeformAddress
                        binding.tvAddress.text = selectedAddress
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentLocation() {
        // This would require location permissions and implementation
        // For now, we'll just center on a default location
        val jakartaPosition = Position(latitude = -6.2088, longitude = 106.8456)
        tomTomMap.setCameraPosition(jakartaPosition, zoom = 15.0)
        addMarkerAtPosition(jakartaPosition, "Lokasi Anda")
        selectedPosition = jakartaPosition
        updateAddressFromPosition(jakartaPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        marker?.let { tomTomMap.removeMarker(it) }
        tomTomMap.destroy()
    }
}