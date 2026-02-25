package com.kelasxi.myapplication.data.network

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────────
// Request bodies
// ─────────────────────────────────────────────────────────────────

/**
 * Sent to POST /api/pickups
 * trash_types must match WasteCategory.type values: organic, plastic, electronic, glass
 */
data class CreatePickupRequest(
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val pickup_date: String,      // format: yyyy-MM-dd
    val pickup_time: String,      // format: HH:mm
    val notes: String? = null,
    val trash_types: List<String> // e.g. ["plastic","organic"]
)

data class CancelPickupRequest(
    val reason: String? = null
)

// ─────────────────────────────────────────────────────────────────
// Response bodies
// ─────────────────────────────────────────────────────────────────

data class PickupListResponse(
    val data: List<PickupDto>
)

data class PickupSingleResponse(
    val message: String? = null,
    val data: PickupDto
)

data class PickupDto(
    val id: Long,
    val status: String,           // pending | on_the_way | done | cancelled
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val pickup_date: String,      // yyyy-MM-dd
    val pickup_time: String,      // HH:mm
    val notes: String?,
    val points_awarded: Int,
    val estimated_weight_kg: Double?,
    val completed_at: String?,
    val cancelled_at: String?,
    val cancellation_reason: String?,
    val created_at: String?,
    val trash_types: List<TrashTypeDto>
)

data class TrashTypeDto(
    val id: Long,
    val type: String,             // organic | plastic | electronic | glass
    val label: String,
    val emoji: String,
    val estimated_weight_kg: Double?
)
