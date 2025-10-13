package com.trashbin.app.utils

import android.content.Context
import android.location.Location
import java.util.concurrent.TimeUnit

object LocationHelper {
    fun getCurrentLocation(context: Context, callback: (Pair<Double, Double>?) -> Unit) {
        // This implementation would require location permissions and implementation
        // For now, we'll return null indicating location is not available
        callback(null)
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0].toDouble() / 1000 // Convert to km
    }

    fun formatDistance(km: Double): String {
        return if (km < 1) {
            "${(km * 1000).toInt()}m"
        } else {
            String.format("%.1fkm", km)
        }
    }

    fun openTomTomNavigation(context: Context, lat: Double, lng: Double) {
        val uri = "tomtom://navigate?destination=$lat,$lng"
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        // This will open the TomTom app if installed, or default to other navigation options
        context.startActivity(intent)
    }
    
    fun openGoogleMapsNavigation(context: Context, lat: Double, lng: Double) {
        val uri = "google.navigation:q=$lat,$lng"
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }
}