package com.trashbin.app.ui.maps

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.trashbin.app.R
import com.trashbin.app.databinding.ActivityMapPickerBinding
import java.util.*

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapPickerBinding
    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            val apiKey = getString(R.string.google_maps_api_key)
            Places.initialize(applicationContext, apiKey)
        }
        placesClient = Places.createClient(this)

        setupMapFragment()
        setupUI()
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        // Set up search bar
        setupPlacesAutocomplete()

        // Current location button
        binding.fabCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        // Confirm button
        binding.btnConfirm.setOnClickListener {
            if (selectedLatLng != null && !selectedAddress.isNullOrEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("lat", selectedLatLng?.latitude)
                    putExtra("lng", selectedLatLng?.longitude)
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

    private fun setupPlacesAutocomplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        autocompleteFragment.setOnPlaceSelectedListener(object : AutocompleteSupportFragment.OnPlaceSelectedListener {
            override fun onPlaceSelected(place: Place) {
                selectedLatLng = place.latLng
                selectedAddress = place.address

                // Move camera to selected location
                selectedLatLng?.let { latLng ->
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    
                    // Update address text
                    binding.tvAddress.text = selectedAddress
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                // Handle error
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in the initial position and move the camera
        val jakarta = LatLng(-6.2088, 106.8456) // Default to Jakarta
        mMap.addMarker(MarkerOptions().position(jakarta).title("Pilih Lokasi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // Add draggable marker
        var marker = mMap.addMarker(
            MarkerOptions()
                .position(jakarta)
                .draggable(true)
                .title("Lokasi Anda")
        )

        selectedLatLng = jakarta

        // Update address initially
        updateAddress(jakarta)

        // Add camera idle listener to update address when map moves
        mMap.setOnCameraIdleListener {
            val center = mMap.cameraPosition.target
            marker?.remove()
            marker = mMap.addMarker(
                MarkerOptions()
                    .position(center)
                    .draggable(true)
                    .title("Lokasi Anda")
            )
            updateAddress(center)
            selectedLatLng = center
        }

        // Add marker drag listener
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}

            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                val position = marker.position
                updateAddress(position)
                selectedLatLng = position
            }
        })
    }

    private fun updateAddress(latLng: LatLng) {
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    runOnUiThread {
                        selectedAddress = addresses[0].getAddressLine(0)
                        binding.tvAddress.text = selectedAddress
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun getCurrentLocation() {
        // This would require location permissions and implementation
        // For now, we'll just center on a default location
        val location = LatLng(-6.2088, 106.8456) // Jakarta
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}