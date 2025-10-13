package com.trashbin.app.ui.maps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.trashbin.app.BuildConfig
import com.trashbin.app.R
import com.trashbin.app.databinding.ActivityMapPickerBinding
import kotlinx.coroutines.*
import java.util.*

/**
 * MapPickerActivity menggunakan Google Maps SDK
 * 
 * Keuntungan Google Maps vs TomTom:
 * ✅ Repository publik (tidak perlu autentikasi khusus)
 * ✅ Free tier: 25,000 requests/day untuk Maps SDK
 * ✅ Dokumentasi lengkap dan komunitas besar
 * ✅ Terintegrasi dengan Google Services
 * ✅ Places API untuk search yang powerful
 * 
 * Batasan Google Maps Free Tier:
 * - Dynamic Maps: $7/1000 requests setelah free tier
 * - Geocoding: $5/1000 requests setelah free tier 
 * - Places API: $17/1000 requests setelah free tier
 * 
 * Untuk development dan testing: GRATIS sampai 25k requests/day
 */
class GoogleMapPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    
    companion object {
        const val EXTRA_SELECTED_POSITION = "selected_position"
        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        const val EXTRA_SELECTED_LAT = "selected_lat"
        const val EXTRA_SELECTED_LNG = "selected_lng"
        
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        
        // Default position (Jakarta, Indonesia)
        private val DEFAULT_POSITION = LatLng(-6.2088, 106.8456)
    }
    
    private lateinit var binding: ActivityMapPickerBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var geocoder: Geocoder
    
    private var selectedPosition: LatLng? = null
    private var selectedAddress: String? = null
    private var currentMarker: Marker? = null
    private var isLocationPermissionGranted = false
    
    // Location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (isLocationPermissionGranted) {
            enableMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeServices()
        setupMapFragment()
        setupUI()
        requestLocationPermission()
    }

    private fun initializeServices() {
        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)
        }
        
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMapSettings()
        setupMapInteractions()
        moveToDefaultLocation()
    }

    private fun setupMapSettings() {
        googleMap.apply {
            // Enable zoom controls
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = false // Kita pakai custom button
            
            // Set map type
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (isLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }
        }
    }

    private fun setupMapInteractions() {
        // Map click listener untuk memilih lokasi
        googleMap.setOnMapClickListener { latLng ->
            selectPosition(latLng)
        }
        
        // Marker drag listener jika diperlukan
        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                selectPosition(marker.position)
            }
        })
    }

    private fun moveToDefaultLocation() {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, 12f)
        )
    }

    private fun selectPosition(latLng: LatLng) {
        selectedPosition = latLng
        
        // Hapus marker lama
        currentMarker?.remove()
        
        // Tambah marker baru
        currentMarker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Lokasi Terpilih")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        
        // Animate camera ke posisi baru
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        
        // Lakukan reverse geocoding
        performReverseGeocoding(latLng)
        
        // Enable confirm button
        binding.btnConfirm.isEnabled = true
    }

    private fun performReverseGeocoding(latLng: LatLng) {
        binding.progressBar.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        selectedAddress = buildAddressString(address)
                    } else {
                        selectedAddress = "Alamat tidak ditemukan"
                    }
                    
                    updateAddressDisplay()
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    selectedAddress = "Error mendapatkan alamat: ${e.message}"
                    updateAddressDisplay()
                }
            }
        }
    }

    private fun buildAddressString(address: Address): String {
        return buildString {
            // Nomor dan nama jalan
            address.subThoroughfare?.let { append("$it ") }
            address.thoroughfare?.let { append("$it, ") }
            
            // Area/Kelurahan
            address.subLocality?.let { append("$it, ") }
            
            // Kota
            address.locality?.let { append("$it, ") }
            
            // Provinsi
            address.adminArea?.let { append("$it, ") }
            
            // Negara
            address.countryName?.let { append(it) }
        }.trimEnd(',', ' ')
    }

    private fun updateAddressDisplay() {
        binding.tvSelectedAddress.text = selectedAddress ?: "Pilih lokasi pada peta"
        
        // Update marker title jika ada
        currentMarker?.title = selectedAddress?.take(50) ?: "Lokasi Terpilih"
    }

    private fun setupUI() {
        // Setup search dengan Places Autocomplete
        setupPlacesAutocomplete()
        
        // Setup confirm button
        binding.btnConfirm.setOnClickListener {
            confirmSelection()
        }
        
        // Setup current location button
        binding.fabCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }
        
        // Initially disable confirm button
        binding.btnConfirm.isEnabled = false
    }

    private fun setupPlacesAutocomplete() {
        // Manual search implementation jika tidak menggunakan AutocompleteSupportFragment
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()
                if (!query.isNullOrBlank() && query.length > 2) {
                    // Implement Places API search atau gunakan Geocoder
                    searchPlaces(query)
                }
            }
        })
    }

    private fun searchPlaces(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Gunakan Geocoder untuk search (gratis, tapi terbatas)
                val addresses = geocoder.getFromLocationName(query, 5)
                
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val firstAddress = addresses[0]
                        val latLng = LatLng(firstAddress.latitude, firstAddress.longitude)
                        
                        // Pindah kamera dan select posisi
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                        )
                        selectPosition(latLng)
                    }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle search error
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (!isLocationPermissionGranted) {
            requestLocationPermission()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                binding.progressBar.visibility = View.GONE
                
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                    )
                    
                    selectPosition(currentLatLng)
                } ?: run {
                    // Fallback to default location
                    selectPosition(DEFAULT_POSITION)
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                selectPosition(DEFAULT_POSITION)
            }
    }

    private fun confirmSelection() {
        selectedPosition?.let { position ->
            val intent = Intent().apply {
                putExtra(EXTRA_SELECTED_POSITION, "${position.latitude},${position.longitude}")
                putExtra(EXTRA_SELECTED_ADDRESS, selectedAddress)
                putExtra(EXTRA_SELECTED_LAT, position.latitude)
                putExtra(EXTRA_SELECTED_LNG, position.longitude)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
        // Refresh location jika diperlukan
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup jika diperlukan
    }
}