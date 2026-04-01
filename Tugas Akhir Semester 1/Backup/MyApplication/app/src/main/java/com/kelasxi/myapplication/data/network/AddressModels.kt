package com.kelasxi.myapplication.data.network

import com.google.gson.annotations.SerializedName

// ── Address Domain Model ──────────────────────────────────────────

data class Address(
    val id: String,
    val label: String,
    val recipientName: String,
    val phone: String,
    val fullAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val isDefault: Boolean,
    val createdAt: String? = null
)

// ── Request DTOs ──────────────────────────────────────────────────

data class AddAddressRequest(
    @SerializedName("label")          val label: String,
    @SerializedName("recipient_name") val recipient_name: String,
    @SerializedName("phone")          val phone: String,
    @SerializedName("full_address")   val full_address: String,
    @SerializedName("city")           val city: String,
    @SerializedName("province")       val province: String,
    @SerializedName("postal_code")    val postal_code: String,
    @SerializedName("is_default")     val is_default: Boolean = false
)

// ── Response DTOs ─────────────────────────────────────────────────

data class AddressListResponse(
    @SerializedName("data") val data: List<AddressDto>
)

data class AddressSingleResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("data")    val data: AddressDto
)

data class AddressDto(
    @SerializedName("id")             val id: String,
    @SerializedName("label")          val label: String,
    @SerializedName("recipient_name") val recipient_name: String,
    @SerializedName("phone")          val phone: String,
    @SerializedName("full_address")   val full_address: String,
    @SerializedName("city")           val city: String,
    @SerializedName("province")       val province: String,
    @SerializedName("postal_code")    val postal_code: String,
    @SerializedName("is_default")     val is_default: Boolean = false,
    @SerializedName("created_at")     val created_at: String? = null
)

// ── DTO → Domain mapper ───────────────────────────────────────────

fun AddressDto.toDomain(): Address = Address(
    id            = id,
    label         = label,
    recipientName = recipient_name,
    phone         = phone,
    fullAddress   = full_address,
    city          = city,
    province      = province,
    postalCode    = postal_code,
    isDefault     = is_default,
    createdAt     = created_at
)
