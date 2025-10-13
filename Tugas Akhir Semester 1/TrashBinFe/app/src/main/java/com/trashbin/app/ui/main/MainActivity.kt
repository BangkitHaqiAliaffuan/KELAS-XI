package com.trashbin.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trashbin.app.R
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.databinding.ActivityMainBinding
import com.trashbin.app.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        checkPermissions()
        handleDeepLink()
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up bottom navigation with NavController
        navView.setupWithNavController(navController)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val neededPermissions = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), 1001)
        }
    }

    private fun handleDeepLink() {
        intent?.data?.let { uri ->
            when {
                uri.path?.startsWith("/pickup/") == true -> {
                    val pickupId = uri.pathSegments.getOrNull(1)?.toIntOrNull()
                    // Navigate to PickupDetailActivity with pickupId
                }
                uri.path?.startsWith("/order/") == true -> {
                    val orderId = uri.pathSegments.getOrNull(1)?.toIntOrNull()
                    // Navigate to OrderDetailActivity with orderId
                }
                uri.path?.startsWith("/listing/") == true -> {
                    val listingId = uri.pathSegments.getOrNull(1)?.toIntOrNull()
                    // Navigate to ListingDetailActivity with listingId
                }
            }
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        if (navController.currentDestination?.id == R.id.homeFragment) {
            // If on home fragment, allow back press to exit
            super.onBackPressed()
        } else {
            // Otherwise navigate back to home
            navController.navigate(R.id.homeFragment)
        }
    }
    
    private fun checkSession() {
        if (!TokenManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}