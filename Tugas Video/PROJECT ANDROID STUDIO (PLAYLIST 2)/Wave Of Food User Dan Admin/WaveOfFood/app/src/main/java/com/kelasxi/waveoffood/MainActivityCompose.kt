package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.fragment.*
import com.kelasxi.waveoffood.ui.components.EnhancedBottomNavigation
import com.kelasxi.waveoffood.ui.components.getDefaultNavigationItems
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import com.kelasxi.waveoffood.utils.FirestoreSampleData

/**
 * Main Activity with Enhanced Material 3 Compose Support
 * Uses EnhancedBottomNavigation Compose component
 */
class MainActivityCompose : FragmentActivity() {

    private val useEnhancedCompose = true // Flag to enable Compose versions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LOG TEST - Ensure onCreate() is called
        Log.d("MainActivityCompose", "🔥 MainActivityCompose onCreate() started")

        try {
            // Initialize Firebase safely
            initializeFirebase()

            // Set Compose content with error handling
            setContent {
                WaveOfFoodTheme {
                    MainScreen()
                }
            }

            Log.d("MainActivityCompose", "🔥 MainActivityCompose onCreate() completed")
        } catch (e: Exception) {
            Log.e("MainActivityCompose", "❌ Critical error in onCreate: ${e.message}", e)
            e.printStackTrace()
            // Don't crash the app, let it try to continue
        }
    }

    private fun initializeFirebase() {
        try {
            Log.d("MainActivityCompose", "🔥 Starting Firebase initialization...")
            
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("Firebase", "✅ Firebase initialized successfully!")
            } else {
                Log.d("Firebase", "✅ Firebase already initialized!")
            }

            // Test Firestore connection safely
            FirebaseFirestore.getInstance()
                .collection("test")
                .document("connection")
                .set(mapOf("status" to "connected", "timestamp" to System.currentTimeMillis()))
                .addOnSuccessListener {
                    Log.d("Firebase", "✅ Firestore connection successful!")
                    
                    // Import sample data in background after Firebase is ready
                    try {
                        Log.d("MainActivityCompose", "🔥 Starting sample data import...")
                        FirestoreSampleData.importSampleMenuData(this@MainActivityCompose)
                    } catch (e: Exception) {
                        Log.e("MainActivityCompose", "❌ Error importing sample data: ${e.message}")
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("Firebase", "❌ Firestore connection failed: ${error.message}")
                }
        } catch (e: Exception) {
            Log.e("Firebase", "❌ Firebase initialization failed: ${e.message}")
            e.printStackTrace()
        }
    }

    @Composable
    private fun MainScreen() {
        var selectedRoute by remember { mutableStateOf("home") }
        var fragmentContainerId by remember { mutableStateOf<Int?>(null) }
        
        // Safely get navigation items
        val navigationItems = try {
            getDefaultNavigationItems()
        } catch (e: Exception) {
            Log.e("MainActivityCompose", "❌ Error getting navigation items: ${e.message}", e)
            emptyList() // Return empty list as fallback
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fragment container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Fragment container view (we'll embed the fragment here)
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { context ->
                        android.widget.FrameLayout(context).apply {
                            id = android.view.View.generateViewId()
                            fragmentContainerId = this.id
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Load fragment based on selected route - dengan delay untuk memastikan container siap
                LaunchedEffect(selectedRoute, fragmentContainerId) {
                    fragmentContainerId?.let { containerId ->
                        // Add small delay to ensure the container is ready
                        kotlinx.coroutines.delay(100)
                        loadFragmentForRoute(selectedRoute, containerId)
                    }
                }
            }

            // Enhanced Bottom Navigation
            if (navigationItems.isNotEmpty()) {
                EnhancedBottomNavigation(
                    items = navigationItems,
                    selectedItem = selectedRoute,
                    onItemSelected = { route ->
                        selectedRoute = route
                    }
                )
            } else {
                // Fallback navigation jika gagal load items
                Text(
                    text = "Loading navigation...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    private fun loadFragmentForRoute(route: String, containerId: Int) {
        // Run on main thread to ensure UI operations are safe
        runOnUiThread {
            try {
                Log.d("MainActivityCompose", "🔥 Loading fragment for route: $route with containerId: $containerId")
                
                // Check if activity is still valid
                if (isDestroyed || isFinishing) {
                    Log.w("MainActivityCompose", "⚠️ Activity is destroyed/finishing, skipping fragment load")
                    return@runOnUiThread
                }
                
                val fragment = try {
                    when (route) {
                        "home" -> if (useEnhancedCompose) {
                            HomeFragmentWithCompose()
                        } else {
                            HomeFragmentEnhanced()
                        }
                        "menu" -> if (useEnhancedCompose) {
                            MenuFragmentWithCompose()
                        } else {
                            MenuFragment()
                        }
                        "cart" -> if (useEnhancedCompose) {
                            CartFragmentWithCompose()
                        } else {
                            CartFragmentEnhanced()
                        }
                        "profile" -> if (useEnhancedCompose) {
                            ProfileFragmentWithCompose()
                        } else {
                            ProfileFragmentEnhanced()
                        }
                        else -> if (useEnhancedCompose) {
                            HomeFragmentWithCompose()
                        } else {
                            HomeFragmentEnhanced()
                        }
                    }
                } catch (fragmentError: Exception) {
                    Log.e("MainActivityCompose", "❌ Error creating fragment for route: $route, falling back to HomeFragmentEnhanced", fragmentError)
                    // Fallback to a safe fragment
                    try {
                        HomeFragmentEnhanced()
                    } catch (fallbackError: Exception) {
                        Log.e("MainActivityCompose", "❌ Even fallback fragment failed: ${fallbackError.message}", fallbackError)
                        return@runOnUiThread
                    }
                }
                
                loadFragment(fragment, containerId)
            } catch (e: Exception) {
                Log.e("MainActivityCompose", "❌ Error loading fragment for route: $route", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * Function to replace fragment in FrameLayout
     */
    private fun loadFragment(fragment: Fragment, containerId: Int) {
        try {
            Log.d("MainActivityCompose", "🔥 Loading fragment: ${fragment.javaClass.simpleName} with containerId: $containerId")
            
            // Check if activity is not destroyed before fragment transaction
            if (!isDestroyed && !isFinishing) {
                supportFragmentManager.beginTransaction()
                    .replace(containerId, fragment)
                    .commitAllowingStateLoss()
                    
                Log.d("MainActivityCompose", "✅ Fragment loaded successfully")
            } else {
                Log.w("MainActivityCompose", "⚠️ Activity is destroyed/finishing, skipping fragment transaction")
            }
        } catch (e: Exception) {
            Log.e("MainActivityCompose", "❌ Error loading fragment: ${e.message}", e)
            e.printStackTrace()
        }
    }
}
