package com.kelasxi.blogapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

/**
 * Application class untuk inisialisasi Firebase
 */
class BlogAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Preload Firebase instances for easier access later
        val auth = Firebase.auth
        val firestore = Firebase.firestore

        // Note: real error handling & configuration should be added as needed
    }
}
