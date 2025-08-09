package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.fragment.*
import com.kelasxi.waveoffood.ui.components.EnhancedBottomNavigation
import com.kelasxi.waveoffood.ui.components.getDefaultNavigationItems
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import com.kelasxi.waveoffood.utils.FirestoreSampleData

/**
 * Main Activity with Enhanced Material 3 Compose Support
 * Uses EnhancedBottomNavigation Compose component with Fragment support
 */
class MainActivity : FragmentActivity() {

    private val useEnhancedCompose = true // Flag to enable Compose versions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LOG TEST - Ensure onCreate() is called
        Log.d("MainActivity", "🔥 MainActivity onCreate() started")

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
                    // Import sample data in background after Firebase is ready
                    Log.d("MainActivity", "🔥 Starting sample data import...")
                    FirestoreSampleData.importSampleMenuData(this@MainActivity)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "❌ Firestore connection failed", e)
                }

        } catch (e: Exception) {
            Log.e("Firebase", "❌ Firebase initialization failed: ${e.message}", e)
            Log.e("Firebase", "❌ Exception details: ${e.printStackTrace()}")
        }

        // Set Compose content with Enhanced Bottom Navigation
        setContent {
            WaveOfFoodTheme {
                MainScreen()
            }
        }

        Log.d("MainActivity", "🔥 MainActivity onCreate() completed")
    }

    @Composable
    private fun MainScreen() {
        var selectedRoute by remember { mutableStateOf("home") }
        val navigationItems = getDefaultNavigationItems()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fragment container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Load fragment based on selected route
                LaunchedEffect(selectedRoute) {
                    loadFragmentForRoute(selectedRoute)
                }
                
                // Fragment container view (we'll embed the fragment here)
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { context ->
                        android.widget.FrameLayout(context).apply {
                            id = R.id.fragmentContainer
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Enhanced Bottom Navigation (Compose)
            EnhancedBottomNavigation(
                items = navigationItems,
                selectedItem = selectedRoute,
                onItemSelected = { route ->
                    selectedRoute = route
                }
            )
        }
    }

    private fun loadFragmentForRoute(route: String) {
        val fragment = when (route) {
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
        
        loadFragment(fragment)
    }

    /**
     * Function to replace fragment in FrameLayout
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}