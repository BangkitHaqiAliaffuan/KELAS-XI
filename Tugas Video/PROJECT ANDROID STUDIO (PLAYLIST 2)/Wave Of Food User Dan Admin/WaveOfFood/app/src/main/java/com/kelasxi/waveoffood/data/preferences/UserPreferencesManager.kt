package com.kelasxi.waveoffood.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_ADDRESS = stringPreferencesKey("user_address")
        private val USER_AVATAR_URL = stringPreferencesKey("user_avatar_url")
        private val REMEMBER_LOGIN = booleanPreferencesKey("remember_login")
    }
    
    // Flow untuk mengamati status login
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }
    
    // Flow untuk mengamati data user
    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            userId = preferences[USER_ID] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            name = preferences[USER_NAME] ?: "",
            phone = preferences[USER_PHONE] ?: "",
            address = preferences[USER_ADDRESS] ?: "",
            avatarUrl = preferences[USER_AVATAR_URL] ?: ""
        )
    }
    
    // Flow untuk mengamati status remember login
    val rememberLogin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_LOGIN] ?: false
    }
    
    // Simpan data login user
    suspend fun saveUserLogin(
        userId: String,
        email: String,
        name: String = "",
        phone: String = "",
        address: String = "",
        avatarUrl: String = "",
        rememberLogin: Boolean = true
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[USER_PHONE] = phone
            preferences[USER_ADDRESS] = address
            preferences[USER_AVATAR_URL] = avatarUrl
            preferences[REMEMBER_LOGIN] = rememberLogin
        }
    }
    
    // Update profil user
    suspend fun updateUserProfile(
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        avatarUrl: String? = null
    ) {
        context.dataStore.edit { preferences ->
            name?.let { preferences[USER_NAME] = it }
            phone?.let { preferences[USER_PHONE] = it }
            address?.let { preferences[USER_ADDRESS] = it }
            avatarUrl?.let { preferences[USER_AVATAR_URL] = it }
        }
    }
    
    // Hapus data login (logout)
    suspend fun clearUserLogin() {
        context.dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
            preferences.remove(USER_PHONE)
            preferences.remove(USER_ADDRESS)
            preferences.remove(USER_AVATAR_URL)
            preferences.remove(REMEMBER_LOGIN)
        }
    }
    
    // Get user ID secara langsung (untuk keperluan sinkron)
    suspend fun getUserId(): String {
        var userId = ""
        context.dataStore.data.collect { preferences ->
            userId = preferences[USER_ID] ?: ""
        }
        return userId
    }
    
    // Get email secara langsung (untuk keperluan sinkron)
    suspend fun getUserEmail(): String {
        var email = ""
        context.dataStore.data.collect { preferences ->
            email = preferences[USER_EMAIL] ?: ""
        }
        return email
    }
    
    // Set remember login preference
    suspend fun setRememberLogin(remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_LOGIN] = remember
        }
    }
}

// Data class untuk profil user
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val avatarUrl: String = ""
)
