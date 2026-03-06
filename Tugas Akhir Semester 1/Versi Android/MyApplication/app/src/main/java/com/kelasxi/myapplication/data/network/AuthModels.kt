package com.kelasxi.myapplication.data.network

// ─────────────────────────────────────────────────────────────────
// Request bodies
// ─────────────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,
    val password_confirmation: String
)

data class GoogleLoginRequest(
    val id_token: String
)

// ─────────────────────────────────────────────────────────────────
// Response bodies
// ─────────────────────────────────────────────────────────────────

/**
 * Shared login response — either user or courier.
 * role = "user" | "courier"
 */
data class AuthResponse(
    val message: String,
    val role: String? = null,       // "user" | "courier"
    val token: String? = null,
    val user: UserDto? = null,
    val courier: CourierProfileDto? = null
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val avatar_path: String?,
    val total_pickups: Int,
    val items_sold: Int,
    val co2_saved: Float,
    val points_balance: Int,
    val member_since: String
)

data class CourierProfileDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val avatar_path: String?,
    val vehicle_type: String?,
    val vehicle_plate: String?,
    val status: String?,
    val is_available: Boolean = true,
    val rating: Double = 0.0,
    val total_deliveries: Int = 0
)

data class MessageResponse(
    val message: String
)

// ─────────────────────────────────────────────────────────────────
// Mapper — convert API DTO to UI model
// ─────────────────────────────────────────────────────────────────
fun UserDto.toUserProfile(): com.kelasxi.myapplication.model.UserProfile =
    com.kelasxi.myapplication.model.UserProfile(
        name           = name,
        email          = email,
        memberSince    = member_since,
        totalPickups   = total_pickups,
        itemsSold      = items_sold,
        totalWeightKg  = 0f,   // computed from pickup list, not from /me
        avatarUrl      = avatar_path ?: ""
    )
