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
    val KEY_ROLE = stringPreferencesKey("role")   // "user" | "courier"

    suspend fun save(context: Context, token: String, user: UserDto) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_ROLE] = "user"
        }
    }

    suspend fun saveCourier(context: Context, token: String, courier: CourierProfileDto) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_NAME] = courier.name
            prefs[KEY_USER_EMAIL] = courier.email
            prefs[KEY_ROLE] = "courier"
        }
    }

    suspend fun clear(context: Context) {
        context.authDataStore.edit { it.clear() }
    }

    fun tokenFlow(context: Context): Flow<String?> =
        context.authDataStore.data.map { it[KEY_TOKEN] }

    fun roleFlow(context: Context): Flow<String?> =
        context.authDataStore.data.map { it[KEY_ROLE] }

    suspend fun getToken(context: Context): String? =
        tokenFlow(context).first()

    suspend fun getRole(context: Context): String? =
        roleFlow(context).first()
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
                if (body.role == "courier" && body.courier != null) {
                    TokenStore.saveCourier(context, body.token!!, body.courier)
                } else {
                    TokenStore.save(context, body.token!!, body.user!!)
                }
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

    suspend fun loginWithGoogle(idToken: String): AuthResult<AuthResponse> {
        return try {
            val response = api.loginWithGoogle(GoogleLoginRequest(idToken))
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

    /** Fetch fresh user profile from GET /api/auth/me */
    suspend fun fetchMe(): AuthResult<UserDto> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.me("Bearer $token")
            if (response.isSuccessful) {
                val user = response.body()?.user
                    ?: return AuthResult.Error("Data user tidak ditemukan.")
                AuthResult.Success(user)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    fun tokenFlow(): Flow<String?> = TokenStore.tokenFlow(context)

    fun roleFlow(): Flow<String?> = TokenStore.roleFlow(context)

    // ── Courier endpoints ──────────────────────────────────────────
    suspend fun getCourierMe(): AuthResult<CourierProfileDto> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.getCourierMe("Bearer $token")
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.courier)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun getCourierPickups(): AuthResult<List<CourierPickupDto>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.getCourierPickups("Bearer $token")
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun getAvailablePickups(): AuthResult<List<CourierPickupDto>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.getAvailablePickups("Bearer $token")
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun acceptPickup(id: Long): AuthResult<CourierPickupDto> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.acceptPickup("Bearer $token", id)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun updatePickupStatus(id: Long, status: String): AuthResult<CourierPickupDto> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.updatePickupStatus("Bearer $token", id, UpdatePickupStatusRequest(status))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun toggleAvailability(isAvailable: Boolean): AuthResult<CourierAvailabilityResponse> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login.")
            val response = api.updateCourierAvailability("Bearer $token", CourierAvailabilityRequest(isAvailable))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ── Courier Order (marketplace delivery) ─────────────────────

    suspend fun getAvailableOrders(): AuthResult<List<CourierOrderDto>> {
        return try {
            val token = TokenStore.getToken(context) ?: return AuthResult.Error("Belum login.")
            val response = api.getAvailableOrders("Bearer $token")
            if (response.isSuccessful) AuthResult.Success(response.body()!!.data)
            else AuthResult.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun getCourierOrders(): AuthResult<List<CourierOrderDto>> {
        return try {
            val token = TokenStore.getToken(context) ?: return AuthResult.Error("Belum login.")
            val response = api.getCourierOrders("Bearer $token")
            if (response.isSuccessful) AuthResult.Success(response.body()!!.data)
            else AuthResult.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun acceptOrder(id: Long): AuthResult<CourierOrderDto> {
        return try {
            val token = TokenStore.getToken(context) ?: return AuthResult.Error("Belum login.")
            val response = api.acceptOrder("Bearer $token", id)
            if (response.isSuccessful) AuthResult.Success(response.body()!!.data)
            else AuthResult.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    suspend fun updateOrderStatus(id: Long, status: String): AuthResult<CourierOrderDto> {
        return try {
            val token = TokenStore.getToken(context) ?: return AuthResult.Error("Belum login.")
            val response = api.updateOrderStatus("Bearer $token", id, UpdatePickupStatusRequest(status))
            if (response.isSuccessful) AuthResult.Success(response.body()!!.data)
            else AuthResult.Error(parseError(response.errorBody()?.string()))
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ── Parse Laravel validation errors ──────────────────────────
    private fun parseError(raw: String?): String {
        if (raw.isNullOrBlank()) return "Terjadi kesalahan."
        return try {
            val json = org.json.JSONObject(raw)
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
