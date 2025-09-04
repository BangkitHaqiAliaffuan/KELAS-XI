package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.fragment.HomeFragmentWithCompose
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme

/**
 * Main Activity with Enhanced Material 3 Compose Support
 * Uses EnhancedBottomNavigation Compose component
 */
class MainActivityCompose : FragmentActivity() {

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
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Embed the fragment here
                        androidx.compose.ui.viewinterop.AndroidView(
                            factory = { context ->
                                android.widget.FrameLayout(context).apply {
                                    id = android.view.View.generateViewId()
                                    supportFragmentManager.beginTransaction()
                                        .replace(id, HomeFragmentWithCompose())
                                        .commit()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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
                }
                .addOnFailureListener { error ->
                    Log.e("Firebase", "❌ Firestore connection failed: ${error.message}")
                }
        } catch (e: Exception) {
            Log.e("Firebase", "❌ Firebase initialization failed: ${e.message}")
            e.printStackTrace()
        }
    }
}