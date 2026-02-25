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

// ─────────────────────────────────────────────────────────────────
// Response bodies
// ─────────────────────────────────────────────────────────────────

data class AuthResponse(
    val message: String,
    val token: String?,
    val user: UserDto?
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

data class MessageResponse(
    val message: String
)
