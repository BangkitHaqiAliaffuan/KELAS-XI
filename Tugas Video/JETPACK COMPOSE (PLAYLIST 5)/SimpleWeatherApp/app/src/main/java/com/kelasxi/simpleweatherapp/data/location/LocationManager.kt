package com.kelasxi.simpleweatherapp.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * LocationManager untuk mengelola location services
 * Menggunakan Google Play Services Location API
 */
class LocationManager(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Mengecek apakah permission location sudah diberikan
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || 
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Mendapatkan lokasi saat ini dari device
     * @return Location object atau null jika gagal
     */
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            try {
                val cancellationTokenSource = CancellationTokenSource()
                
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
                
                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
                
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }
    
    /**
     * Mendapatkan lokasi terakhir yang diketahui
     * Lebih cepat dari getCurrentLocation tapi mungkin tidak akurat
     */
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }
    
    /**
     * Convert Location ke format koordinat string untuk API
     * @param location Location object
     * @return String dalam format "latitude,longitude"
     */
    fun locationToCoordinates(location: Location): String {
        return "${location.latitude},${location.longitude}"
    }
}