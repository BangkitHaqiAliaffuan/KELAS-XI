package com.trashbin.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.trashbin.app.R
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    
    // Views
    private lateinit var tvWelcome: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var btnPickup: MaterialButton
    private lateinit var btnMarketplace: MaterialButton
    private lateinit var btnProfile: MaterialButton
    private lateinit var btnLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createLayout()
        setupClickListeners()
        
        // Check required permissions
        checkPermissions()
        
        // Handle deep links
        handleDeepLink()
        
        // Delay session check to ensure proper initialization
        // This prevents potential race condition where token is not yet available 
        // when MainActivity is launched after login/registration
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 100) // 100ms delay to ensure token is properly loaded
    }
    
    private fun createLayout() {
        // Main container
        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(dpToPx(24), dpToPx(48), dpToPx(24), dpToPx(24))
        }
        
        // Welcome Section
        tvWelcome = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            text = "Welcome to TrashBin"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
        
        tvSubtitle = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(48)
            }
            text = "Manage your waste efficiently"
            textSize = 16f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }
        
        // Menu Buttons
        btnPickup = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            ).apply {
                bottomMargin = dpToPx(16)
            }
            text = "🗑️ Request Pickup"
            textSize = 16f
            gravity = Gravity.CENTER
        }
        
        btnMarketplace = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            ).apply {
                bottomMargin = dpToPx(16)
            }
            text = "🛒 Marketplace"
            textSize = 16f
            gravity = Gravity.CENTER
        }
        
        btnProfile = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            ).apply {
                bottomMargin = dpToPx(16)
            }
            text = "👤 Profile"
            textSize = 16f
            gravity = Gravity.CENTER
        }
        
        // Spacer
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        
        btnLogout = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(50)
            )
            text = "Logout"
            textSize = 14f
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.gray_600))
        }
        
        // Add views to main layout
        mainLayout.addView(tvWelcome)
        mainLayout.addView(tvSubtitle)
        mainLayout.addView(btnPickup)
        mainLayout.addView(btnMarketplace)
        mainLayout.addView(btnProfile)
        mainLayout.addView(spacer)
        mainLayout.addView(btnLogout)
        
        setContentView(mainLayout)
    }
    
    private fun setupClickListeners() {
        btnPickup.setOnClickListener {
            startActivity(Intent(this, com.trashbin.app.ui.pickup.PickupRequestActivity::class.java))
        }
        
        btnMarketplace.setOnClickListener {
            startActivity(Intent(this, com.trashbin.app.ui.marketplace.MarketplaceActivity::class.java))
        }
        
        btnProfile.setOnClickListener {
            startActivity(Intent(this, com.trashbin.app.ui.profile.ProfileActivity::class.java))
        }
        
        btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        TokenManager.getInstance().clearToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
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
                    Toast.makeText(this, "Opening pickup: $pickupId", Toast.LENGTH_SHORT).show()
                }
                uri.path?.startsWith("/order/") == true -> {
                    val orderId = uri.pathSegments.getOrNull(1)?.toIntOrNull()
                    Toast.makeText(this, "Opening order: $orderId", Toast.LENGTH_SHORT).show()
                }
                uri.path?.startsWith("/listing/") == true -> {
                    val listingId = uri.pathSegments.getOrNull(1)?.toIntOrNull()
                    Toast.makeText(this, "Opening listing: $listingId", Toast.LENGTH_SHORT).show()
                }
            }
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