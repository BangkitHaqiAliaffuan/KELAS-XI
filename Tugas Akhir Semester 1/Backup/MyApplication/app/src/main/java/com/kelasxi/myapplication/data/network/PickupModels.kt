package com.kelasxi.myapplication.data.network

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────────
// Request bodies
// ─────────────────────────────────────────────────────────────────

/**
 * Sent to POST /api/pickups
 * trash_types must match WasteCategory.type values: organic, plastic, electronic, glass
 * pickup_date and pickup_time are set automatically by the server to the current time.
 */
data class CreatePickupRequest(
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String? = null,
    val estimated_weight_kg: Double? = null,
    val trash_types: List<String> // e.g. ["plastic","organic"]
)

data class CancelPickupRequest(
    val reason: String? = null
)

/** Sent to POST /api/pickups/{id}/rate */
data class RatePickupRequest(
    @SerializedName("courier_rating") val courier_rating: Int,
    @SerializedName("courier_review") val courier_review: String? = null
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
    val courier_rating: Int? = null,
    val courier_review: String? = null,
    val rated_at: String? = null,
    val courier: CourierDto?,
    val trash_types: List<TrashTypeDto>
)

data class CourierDto(
    val id: Long,
    val name: String,
    val phone: String?,
    val avatar_path: String?,
    val vehicle_type: String?,
    val vehicle_plate: String?,
    val rating: Double?,
    val status: String?
)

data class TrashTypeDto(
    val id: Long,
    val type: String,             // organic | plastic | electronic | glass
    val label: String,
    val emoji: String,
    val estimated_weight_kg: Double?
)

// ─────────────────────────────────────────────────────────────────
// Courier-side DTOs
// ─────────────────────────────────────────────────────────────────

data class UpdatePickupStatusRequest(
    val status: String              // "on_the_way" | "done"
)

data class CourierAvailabilityRequest(
    val is_available: Boolean
)

data class CourierAvailabilityResponse(
    val message: String,
    val is_available: Boolean
)

data class CourierMeResponse(
    val courier: CourierProfileDto
)

data class CourierPickupListResponse(
    val data: List<CourierPickupDto>
)

data class CourierPickupSingleResponse(
    val message: String? = null,
    val data: CourierPickupDto
)

/** Pickup as seen by the courier — includes customer info, no courier field */
data class CourierPickupDto(
    val id: Long,
    val status: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val pickup_date: String,
    val pickup_time: String,
    val notes: String?,
    val estimated_weight_kg: Double?,
    val points_awarded: Int,
    val completed_at: String?,
    val cancelled_at: String?,
    val cancellation_reason: String?,
    val created_at: String?,
    val customer: CustomerDto?,
    val trash_types: List<TrashTypeDto>
)

data class CustomerDto(
    val id: Long,
    val name: String,
    val phone: String?
)

data class AcceptPickupResponse(
    val message: String,
    val data: CourierPickupDto
)

// ─────────────────────────────────────────────────────────────────
// Courier Order DTOs  (marketplace delivery)
// ─────────────────────────────────────────────────────────────────

/** Order as seen by the courier — includes buyer info and product snapshot */
data class CourierOrderDto(
    val id: Long,
    val status: String,           // pending | confirmed | shipped | completed | cancelled
    val shipping_address: String,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val quantity: Int,
    val total_price: Long,
    val created_at: String?,
    val buyer: CustomerDto?,       // reuse CustomerDto (id, name, phone)
    val product_name: String?,     // snapshot name from listing
    val product_image_url: String? = null,
    val cart_checkout_id: String? = null
)

data class CourierOrderListResponse(
    val data: List<CourierOrderDto>
)

data class CourierOrderSingleResponse(
    val message: String? = null,
    val data: CourierOrderDto
)
