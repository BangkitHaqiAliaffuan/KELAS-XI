package com.kelasxi.myapplication.data.network

import android.content.Context

class AddressRepository(private val context: Context) {

    private val api = RetrofitClient.api

    // ─────────────────────────────────────────────────────────────
    // GET /api/addresses
    // ─────────────────────────────────────────────────────────────
    suspend fun getAddresses(): AuthResult<List<Address>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.getAddresses("Bearer $token")
            if (response.isSuccessful) {
                val list = response.body()!!.data.map { it.toDomain() }
                AuthResult.Success(list)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/addresses
    // ─────────────────────────────────────────────────────────────
    suspend fun addAddress(
        label: String,
        recipientName: String,
        phone: String,
        fullAddress: String,
        city: String,
        province: String,
        postalCode: String,
        isDefault: Boolean = false
    ): AuthResult<Address> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val body = AddAddressRequest(
                label          = label,
                recipient_name = recipientName,
                phone          = phone,
                full_address   = fullAddress,
                city           = city,
                province       = province,
                postal_code    = postalCode,
                is_default     = isDefault
            )
            val response = api.addAddress("Bearer $token", body)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PATCH /api/addresses/{id}/default
    // ─────────────────────────────────────────────────────────────
    suspend fun setDefaultAddress(id: Long): AuthResult<Address> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.setDefaultAddress("Bearer $token", id)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE /api/addresses/{id}
    // ─────────────────────────────────────────────────────────────
    suspend fun deleteAddress(id: Long): AuthResult<String> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.deleteAddress("Bearer $token", id)
            if (response.isSuccessful) {
                val msg = response.body()?.get("message") ?: "Alamat dihapus."
                AuthResult.Success(msg)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Shared error parser
    // ─────────────────────────────────────────────────────────────
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
        } catch (_: Exception) {
            "Terjadi kesalahan."
        }
    }
}
