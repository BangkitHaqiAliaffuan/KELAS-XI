package com.kelasxi.myapplication.data.network

import com.google.gson.annotations.SerializedName

// ── Request DTOs ──────────────────────────────────────────────────

data class CreateOrderRequest(
    @SerializedName("listing_id")       val listing_id: Long,
    @SerializedName("quantity")         val quantity: Int = 1,
    @SerializedName("notes")            val notes: String? = null,
    @SerializedName("shipping_address") val shipping_address: String
)

data class CancelOrderRequest(
    @SerializedName("reason") val reason: String? = null
)

data class PayOrderRequest(
    @SerializedName("payment_method") val payment_method: String,
    @SerializedName("payment_proof")  val payment_proof: String? = null
)

data class ToggleWishlistRequest(
    @SerializedName("listing_id") val listing_id: Long
)

data class CreateListingRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price")       val price: Long,
    @SerializedName("category")    val category: String,   // furniture|electronics|clothing|books|others
    @SerializedName("condition")   val condition: String   // like_new|good|fair
)

data class UpdateListingRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price")       val price: Long,
    @SerializedName("category")    val category: String,   // furniture|electronics|clothing|books|others
    @SerializedName("condition")   val condition: String   // like_new|good|fair
)

// ── Response DTOs ─────────────────────────────────────────────────

data class ListingListResponse(
    @SerializedName("data") val data: List<ListingDto>
)

data class ListingSingleResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("data")    val data: ListingDto
)

data class ListingDto(
    @SerializedName("id")             val id: String,
    @SerializedName("name")           val name: String,
    @SerializedName("price")          val price: Long,
    @SerializedName("seller_name")    val seller_name: String,
    @SerializedName("seller_rating")  val seller_rating: Float,
    @SerializedName("description")    val description: String,
    @SerializedName("category")       val category: String,
    @SerializedName("condition")      val condition: String,
    @SerializedName("image_url")      val image_url: String? = null,
    @SerializedName("is_wishlisted")  val is_wishlisted: Boolean = false,
    @SerializedName("is_sold")        val is_sold: Boolean = false,
    @SerializedName("views_count")    val views_count: Int = 0,
    @SerializedName("created_at")     val created_at: String? = null
)

data class OrderListResponse(
    @SerializedName("data") val data: List<OrderDto>
)

data class OrderSingleResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("data")    val data: OrderDto
)

data class OrderDto(
    @SerializedName("id")                   val id: String,
    @SerializedName("status")               val status: String,
    @SerializedName("total_price")          val total_price: Long,
    @SerializedName("quantity")             val quantity: Int,
    @SerializedName("notes")                val notes: String? = null,
    @SerializedName("shipping_address")     val shipping_address: String,
    @SerializedName("cancellation_reason")  val cancellation_reason: String? = null,
    @SerializedName("ordered_at")           val ordered_at: String,
    @SerializedName("confirmed_at")         val confirmed_at: String? = null,
    @SerializedName("shipped_at")           val shipped_at: String? = null,
    @SerializedName("completed_at")         val completed_at: String? = null,
    @SerializedName("cancelled_at")         val cancelled_at: String? = null,
    @SerializedName("estimated_arrival")    val estimated_arrival: String? = null,
    @SerializedName("listing")              val listing: ListingDto? = null
)

data class WishlistListResponse(
    @SerializedName("data") val data: List<ListingDto>
)

data class ToggleWishlistResponse(
    @SerializedName("wishlisted") val wishlisted: Boolean,
    @SerializedName("message")    val message: String
)
