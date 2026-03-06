package com.kelasxi.myapplication.data.network

import android.content.Context
import com.kelasxi.myapplication.model.Courier
import com.kelasxi.myapplication.model.PickupRequest
import com.kelasxi.myapplication.model.PickupStatus
import com.kelasxi.myapplication.model.TrashType

class PickupRepository(private val context: Context) {

    private val api = RetrofitClient.api

    // ─────────────────────────────────────────────────────────────
    // GET /api/pickups  →  list of PickupRequest (domain model)
    // ─────────────────────────────────────────────────────────────
    suspend fun getPickups(): AuthResult<List<PickupRequest>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.getPickups("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                AuthResult.Success(body.data.map { it.toDomain() })
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/pickups  →  single PickupRequest (domain model)
    // ─────────────────────────────────────────────────────────────
    suspend fun createPickup(
        address: String,
        pickupDate: String,   // yyyy-MM-dd
        pickupTime: String,   // HH:mm
        trashTypes: List<TrashType>,
        notes: String?,
        estimatedWeightKg: Double? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): AuthResult<PickupRequest> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val body = CreatePickupRequest(
                address                = address,
                latitude               = latitude,
                longitude              = longitude,
                pickup_date            = pickupDate,
                pickup_time            = pickupTime,
                notes                  = notes?.ifBlank { null },
                estimated_weight_kg    = estimatedWeightKg,
                trash_types            = trashTypes.map { it.name.lowercase() }
            )

            val response = api.createPickup("Bearer $token", body)
            if (response.isSuccessful) {
                val dto = response.body()!!.data
                AuthResult.Success(dto.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/pickups/{id}/cancel
    // ─────────────────────────────────────────────────────────────
    suspend fun cancelPickup(id: Long, reason: String? = null): AuthResult<PickupRequest> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.cancelPickup("Bearer $token", id, CancelPickupRequest(reason))
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
    // Map PickupDto  →  domain PickupRequest
    // ─────────────────────────────────────────────────────────────
    private fun PickupDto.toDomain(): PickupRequest {
        val domainTrashTypes = trash_types.mapNotNull { dto ->
            TrashType.entries.firstOrNull { it.name.equals(dto.type, ignoreCase = true) }
        }
        val domainStatus = when (status) {
            "searching"  -> PickupStatus.SEARCHING
            "on_the_way" -> PickupStatus.ON_THE_WAY
            "done"       -> PickupStatus.DONE
            "cancelled"  -> PickupStatus.CANCELLED
            else         -> PickupStatus.PENDING
        }
        return PickupRequest(
            id                  = id.toString(),
            date                = pickup_date,
            time                = pickup_time,
            address             = address,
            trashTypes          = domainTrashTypes.ifEmpty { listOf(TrashType.ORGANIC) },
            status              = domainStatus,
            notes               = notes ?: "",
            estimatedWeightKg   = estimated_weight_kg,
            pointsAwarded       = points_awarded,
            completedAt         = completed_at,
            cancelledAt         = cancelled_at,
            cancellationReason  = cancellation_reason,
            createdAt           = created_at,
            courier             = courier?.let {
                Courier(
                    id           = it.id,
                    name         = it.name,
                    phone        = it.phone,
                    avatarUrl    = it.avatar_path,
                    vehicleType  = it.vehicle_type,
                    vehiclePlate = it.vehicle_plate,
                    rating       = it.rating,
                    status       = it.status
                )
            }
        )
    }

    // ─────────────────────────────────────────────────────────────
    // Shared error parser (same logic as AuthRepository)
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
