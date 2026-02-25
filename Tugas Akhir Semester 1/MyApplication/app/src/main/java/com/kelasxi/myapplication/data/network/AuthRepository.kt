package com.kelasxi.myapplication.data.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// ── DataStore singleton extension ────────────────────────────────
val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

object TokenStore {
    val KEY_TOKEN = stringPreferencesKey("auth_token")
    val KEY_USER_NAME = stringPreferencesKey("user_name")
    val KEY_USER_EMAIL = stringPreferencesKey("user_email")

    suspend fun save(context: Context, token: String, user: UserDto) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_EMAIL] = user.email
        }
    }

    suspend fun clear(context: Context) {
        context.authDataStore.edit { it.clear() }
    }

    fun tokenFlow(context: Context): Flow<String?> =
        context.authDataStore.data.map { it[KEY_TOKEN] }

    suspend fun getToken(context: Context): String? =
        tokenFlow(context).first()
}

// ─────────────────────────────────────────────────────────────────
// Sealed result wrapper used by ViewModel
// ─────────────────────────────────────────────────────────────────
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

// ─────────────────────────────────────────────────────────────────
// Repository
// ─────────────────────────────────────────────────────────────────
class AuthRepository(private val context: Context) {

    private val api = RetrofitClient.api

    suspend fun login(email: String, password: String): AuthResult<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                TokenStore.save(context, body.token!!, body.user!!)
                AuthResult.Success(body)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server. Cek koneksi internet.")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String?,
        password: String
    ): AuthResult<AuthResponse> {
        return try {
            val response = api.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    phone = phone?.ifBlank { null },
                    password = password,
                    password_confirmation = password
                )
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                TokenStore.save(context, body.token!!, body.user!!)
                AuthResult.Success(body)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server. Cek koneksi internet.")
        }
    }

    suspend fun logout(): AuthResult<Unit> {
        return try {
            val token = TokenStore.getToken(context) ?: return AuthResult.Error("Belum login.")
            val response = api.logout("Bearer $token")
            TokenStore.clear(context)
            if (response.isSuccessful) AuthResult.Success(Unit)
            else AuthResult.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            // Even if the server call fails, clear local token
            TokenStore.clear(context)
            AuthResult.Success(Unit)
        }
    }

    fun tokenFlow(): Flow<String?> = TokenStore.tokenFlow(context)

    // ── Parse Laravel validation errors e.g. {"message":"...","errors":{...}} ──
    private fun parseError(raw: String?): String {
        if (raw.isNullOrBlank()) return "Terjadi kesalahan."
        return try {
            val json = org.json.JSONObject(raw)
            // Try to pull first validation error message
            val errors = json.optJSONObject("errors")
            if (errors != null) {
                val firstKey = errors.keys().next()
                errors.getJSONArray(firstKey).getString(0)
            } else {
                json.optString("message", "Terjadi kesalahan.")
            }
        } catch (e: Exception) {
            "Terjadi kesalahan."
        }
    }
}
