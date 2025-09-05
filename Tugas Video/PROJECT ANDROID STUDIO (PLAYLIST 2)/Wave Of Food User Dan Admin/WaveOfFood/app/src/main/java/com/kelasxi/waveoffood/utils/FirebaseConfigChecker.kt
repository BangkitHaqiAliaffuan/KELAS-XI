package com.kelasxi.waveoffood.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Utility class untuk mengecek konfigurasi Firebase
 */
object FirebaseConfigChecker {
    
    private const val TAG = "FirebaseConfig"
    
    fun checkFirebaseConfiguration(): Boolean {
        return try {
            // Check Firebase Auth
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth initialized: ${auth.app.name}")
            
            // Check Firestore
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firestore initialized: ${firestore.app.name}")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase configuration error: ${e.message}")
            false
        }
    }
    
    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser
    
    fun isUserLoggedInFirebase(): Boolean {
        return getCurrentUser() != null
    }
    
    fun getFirebaseUserId(): String? {
        return getCurrentUser()?.uid
    }
    
    fun getFirebaseUserEmail(): String? {
        return getCurrentUser()?.email
    }
}
