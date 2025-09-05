
package com.kelasxi.waveoffood.utils

import kotlinx.coroutines.flow.first

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kelasxi.waveoffood.data.preferences.UserPreferencesManager
import com.kelasxi.waveoffood.data.repository.AuthRepository
import com.kelasxi.waveoffood.ui.viewmodel.AuthViewModel

/**
 * Singleton class untuk menyediakan dependencies yang diperlukan
 * untuk persistent login system
 */
object PersistentLoginManager {
    
    @Volatile
    private var userPreferencesManager: UserPreferencesManager? = null
    
    @Volatile
    private var authRepository: AuthRepository? = null
    
    fun getUserPreferencesManager(context: Context): UserPreferencesManager {
        return userPreferencesManager ?: synchronized(this) {
            userPreferencesManager ?: UserPreferencesManager(context.applicationContext).also {
                userPreferencesManager = it
            }
        }
    }
    
    fun getAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            authRepository ?: AuthRepository(
                userPreferencesManager = getUserPreferencesManager(context)
            ).also {
                authRepository = it
            }
        }
    }
    
    fun getAuthViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(getAuthRepository(context)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

/**
 * Extension functions untuk memudahkan penggunaan
 */
suspend fun Context.isUserLoggedIn(): Boolean {
    return PersistentLoginManager.getUserPreferencesManager(this)
        .isLoggedIn
        .first()
}

suspend fun Context.getCurrentUserProfile(): com.kelasxi.waveoffood.data.preferences.UserProfile? {
    val prefsManager = PersistentLoginManager.getUserPreferencesManager(this)
    val isLoggedIn = prefsManager.isLoggedIn.first()
    return if (isLoggedIn) {
        prefsManager.userProfile.first()
    } else {
        null
    }
}

suspend fun Context.signOutUser() {
    PersistentLoginManager.getAuthRepository(this).signOut()
}
