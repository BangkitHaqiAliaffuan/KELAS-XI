package com.kelasxi.myapplication.data.network

import com.google.gson.annotations.SerializedName

// ── Request DTOs ──────────────────────────────────────────────────

data class CreateOrderRequest(
    @SerializedName("listing_id")       val listing_id: Long,
    @SerializedName("quantity")         val quantity: Int = 1,
    @SerializedName("notes")            val notes: String? = null,
    @SerializedName("shipping_address") val shipping_address: String,
    @SerializedName("latitude")         val latitude: Double? = null,
    @SerializedName("longitude")        val longitude: Double? = null
)

data class CancelOrderRequest(
    @SerializedName("reason") val reason: String? = null
)

data class PayOrderRequest(
    @SerializedName("payment_method") val payment_method: String,
    @SerializedName("payment_proof")  val payment_proof: String? = null
)

data class EmptyRequest(
    @SerializedName("_") val placeholder: String? = null
)

data class ToggleWishlistRequest(
    @SerializedName("listing_id") val listing_id: Long
)

data class CreateListingRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price")       val price: Long,
    @SerializedName("category")    val category: String,   // furniture|electronics|clothing|books|others
    @SerializedName("condition")   val condition: String,  // like_new|good|fair
    @SerializedName("stock")       val stock: Int = 1
)

data class UpdateListingRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price")       val price: Long,
    @SerializedName("category")    val category: String,   // furniture|electronics|clothing|books|others
    @SerializedName("condition")   val condition: String,  // like_new|good|fair
    @SerializedName("stock")       val stock: Int = 1
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
    @SerializedName("is_active")      val is_active: Boolean = true,
    @SerializedName("stock")          val stock: Int = 1,
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
    @SerializedName("latitude")             val latitude: Double? = null,
    @SerializedName("longitude")            val longitude: Double? = null,
    @SerializedName("ordered_at")           val ordered_at: String,
    @SerializedName("confirmed_at")         val confirmed_at: String? = null,
    @SerializedName("shipped_at")           val shipped_at: String? = null,
    @SerializedName("completed_at")         val completed_at: String? = null,
    @SerializedName("cancelled_at")         val cancelled_at: String? = null,
    @SerializedName("estimated_arrival")    val estimated_arrival: String? = null,
    @SerializedName("listing")              val listing: ListingDto? = null,
    // ── Mayar payment fields ──────────────────────────────────────
    @SerializedName("payment_status")       val payment_status: String? = "unpaid",
    @SerializedName("mayar_payment_link")   val mayar_payment_link: String? = null,
    @SerializedName("mayar_payment_id")     val mayar_payment_id: String? = null,
    @SerializedName("paid_at")              val paid_at: String? = null,
    // ── Rating fields ─────────────────────────────────────────────
    @SerializedName("courier_rating")       val courier_rating: Int? = null,
    @SerializedName("courier_review")       val courier_review: String? = null,
    @SerializedName("listing_rating")       val listing_rating: Int? = null,
    @SerializedName("listing_review")       val listing_review: String? = null,
    @SerializedName("rated_at")             val rated_at: String? = null
)

// ── Rating request ────────────────────────────────────────────────────────────────
data class RateOrderRequest(
    @SerializedName("courier_rating")  val courier_rating: Int,
    @SerializedName("courier_review")  val courier_review: String?,
    @SerializedName("listing_rating")  val listing_rating: Int,
    @SerializedName("listing_review")  val listing_review: String?
)

/** Response from POST /orders/{id}/pay — contains Mayar payment link */
data class PayOrderResponse(
    @SerializedName("message")       val message: String? = null,
    @SerializedName("payment_link")  val payment_link: String,
    @SerializedName("payment_id")    val payment_id: String,
    @SerializedName("data")          val data: OrderDto
)

/** Response from GET /orders/{id}/payment-status */
data class PaymentStatusResponse(
    @SerializedName("payment_status") val payment_status: String,
    @SerializedName("order_status")   val order_status: String,
    @SerializedName("data")           val data: OrderDto
)

data class WishlistListResponse(
    @SerializedName("data") val data: List<ListingDto>
)

data class ToggleWishlistResponse(
    @SerializedName("wishlisted") val wishlisted: Boolean,
    @SerializedName("message")    val message: String
)

// ─────────────────────────────────────────────────────────────────
// Mayar Sales Transactions  (GET /api/orders/sales-transactions)
// ─────────────────────────────────────────────────────────────────

data class SalesTransactionDto(
    @SerializedName("id")             val id: String,
    @SerializedName("transactionId")  val transactionId: String? = null,
    @SerializedName("status")         val status: String = "",
    @SerializedName("mayar_status")   val mayarStatus: String = "unpaid",
    @SerializedName("amount")         val amount: Long = 0L,
    @SerializedName("customerName")   val customerName: String = "",
    @SerializedName("customerEmail")  val customerEmail: String? = null,
    @SerializedName("description")    val description: String? = null,
    @SerializedName("createdAt")      val createdAt: String? = null
)

data class SalesSummaryDto(
    @SerializedName("total_transactions") val totalTransactions: Int = 0,
    @SerializedName("total_paid")         val totalPaid: Int = 0,
    @SerializedName("total_unpaid")       val totalUnpaid: Int = 0,
    @SerializedName("total_revenue")      val totalRevenue: Long = 0L
)

data class SalesTransactionsResponse(
    @SerializedName("data")    val data: List<SalesTransactionDto>,
    @SerializedName("summary") val summary: SalesSummaryDto
)

// ─────────────────────────────────────────────────────────────────
// Cart Checkout (POST /api/orders/checkout-cart)
// ─────────────────────────────────────────────────────────────────

data class CartCheckoutItemRequest(
    @SerializedName("listing_id") val listing_id: Long,
    @SerializedName("quantity")   val quantity: Int = 1
)

data class CartCheckoutRequest(
    @SerializedName("shipping_address") val shipping_address: String,
    @SerializedName("notes")            val notes: String? = null,
    @SerializedName("items")            val items: List<CartCheckoutItemRequest>,
    @SerializedName("latitude")         val latitude: Double? = null,
    @SerializedName("longitude")        val longitude: Double? = null
)

data class CartCheckoutResponse(
    @SerializedName("message")          val message: String? = null,
    @SerializedName("cart_checkout_id") val cart_checkout_id: String,
    @SerializedName("payment_link")     val payment_link: String,
    @SerializedName("payment_id")       val payment_id: String,
    @SerializedName("total")            val total: Long,
    @SerializedName("orders")           val orders: List<OrderDto>
)

data class CartCheckoutGroupDto(
    @SerializedName("cart_checkout_id") val cart_checkout_id: String,
    @SerializedName("payment_link")     val payment_link: String? = null,
    @SerializedName("payment_id")       val payment_id: String? = null,
    @SerializedName("payment_status")   val payment_status: String = "unpaid",
    @SerializedName("order_status")     val order_status: String = "pending",
    @SerializedName("total")            val total: Long = 0L,
    @SerializedName("shipping_address") val shipping_address: String = "",
    @SerializedName("latitude")         val latitude: Double? = null,
    @SerializedName("longitude")        val longitude: Double? = null,
    @SerializedName("ordered_at")       val ordered_at: String = "",
    @SerializedName("orders")           val orders: List<OrderDto>
)

data class CartCheckoutsListResponse(
    @SerializedName("data") val data: List<CartCheckoutGroupDto>
)

data class CartCheckoutStatusResponse(
    @SerializedName("payment_status") val payment_status: String,
    @SerializedName("order_status")   val order_status: String,
    @SerializedName("orders")         val orders: List<OrderDto>
)
