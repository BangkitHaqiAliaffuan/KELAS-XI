package com.kelasxi.waveoffood.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first

/**
 * Debug utility untuk mengecek status persistent login
 */
object LoginDebugUtils {
    
    private const val TAG = "LoginDebug"
    
    suspend fun logCurrentLoginStatus(context: Context) {
        try {
            val prefsManager = PersistentLoginManager.getUserPreferencesManager(context)
            
            val isLoggedIn = prefsManager.isLoggedIn.first()
            val rememberLogin = prefsManager.rememberLogin.first()
            val userProfile = prefsManager.userProfile.first()
            
            Log.d(TAG, "=== LOGIN STATUS DEBUG ===")
            Log.d(TAG, "isLoggedIn: $isLoggedIn")
            Log.d(TAG, "rememberLogin: $rememberLogin")
            Log.d(TAG, "userProfile.userId: ${userProfile.userId}")
            Log.d(TAG, "userProfile.email: ${userProfile.email}")
            Log.d(TAG, "userProfile.name: ${userProfile.name}")
            
            // Check Firebase Auth status
            val firebaseUser = FirebaseConfigChecker.getCurrentUser()
            Log.d(TAG, "Firebase currentUser: ${firebaseUser?.uid}")
            Log.d(TAG, "Firebase email: ${firebaseUser?.email}")
            
            Log.d(TAG, "========================")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking login status: ${e.message}")
        }
    }
    
    suspend fun forceSetLoginStatus(context: Context, email: String, password: String) {
        try {
            val prefsManager = PersistentLoginManager.getUserPreferencesManager(context)
            
            // Simulate successful login for testing
            prefsManager.saveUserLogin(
                userId = "debug-user-123",
                email = email,
                name = "Debug User",
                phone = "",
                address = "",
                avatarUrl = "",
                rememberLogin = true
            )
            
            Log.d(TAG, "Force login status set for: $email")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting login status: ${e.message}")
        }
    }
    
    suspend fun clearLoginStatus(context: Context) {
        try {
            val prefsManager = PersistentLoginManager.getUserPreferencesManager(context)
            prefsManager.clearUserLogin()
            
            Log.d(TAG, "Login status cleared")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing login status: ${e.message}")
        }
    }
}
