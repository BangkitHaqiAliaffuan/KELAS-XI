package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.fragment.CartFragmentEnhanced
import com.kelasxi.waveoffood.fragment.HomeFragmentEnhanced
import com.kelasxi.waveoffood.fragment.MenuFragment
import com.kelasxi.waveoffood.fragment.ProfileFragmentEnhanced
import com.kelasxi.waveoffood.utils.FirestoreSampleData

/**
 * Activity utama dengan BottomNavigationView untuk navigasi fragment
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // LOG TEST - Pastikan onCreate() dipanggil
        Log.d("MainActivity", "🔥 MainActivity onCreate() started")
        Log.d("MainActivity", "🔥 Setting content view...")
        
        setContentView(R.layout.activity_main)
        
        Log.d("MainActivity", "🔥 Content view set successfully")
        
        // Test Firebase initialization
        Log.d("MainActivity", "🔥 Starting Firebase initialization...")
        
        try {
            Log.d("MainActivity", "🔥 Calling FirebaseApp.initializeApp()...")
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "✅ Firebase initialized successfully!")
            
            Log.d("MainActivity", "🔥 Getting Firestore instance...")
            
            // Test Firestore connection
            FirebaseFirestore.getInstance()
                .collection("test")
                .document("connection")
                .set(mapOf("status" to "connected", "timestamp" to System.currentTimeMillis()))
                .addOnSuccessListener {
                    Log.d("Firebase", "✅ Firestore connection successful!")
                    // Sekarang bisa panggil import data
                    FirestoreSampleData.importSampleMenuData(this)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "❌ Firestore connection failed", e)
                }
                
        } catch (e: Exception) {
            Log.e("Firebase", "❌ Firebase initialization failed: ${e.message}", e)
            Log.e("Firebase", "❌ Exception details: ${e.printStackTrace()}")
        }
        
        Log.d("MainActivity", "🔥 Firebase setup completed, continuing with UI setup...")
        
        // Inisialisasi views
        bottomNavigation = findViewById(R.id.bottomNavigation)
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Load HomeFragmentEnhanced secara default
        if (savedInstanceState == null) {
            loadFragment(HomeFragmentEnhanced())
        }
        
        Log.d("MainActivity", "🔥 MainActivity onCreate() completed")
    }
    
    /**
     * Setup BottomNavigationView dengan listener
     */
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragmentEnhanced())
                    true
                }
                R.id.nav_menu -> {
                    loadFragment(MenuFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragmentEnhanced())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragmentEnhanced())
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * Fungsi untuk mengganti fragment di dalam FrameLayout
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}