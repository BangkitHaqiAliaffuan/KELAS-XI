package com.kelasxi.myapplication.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body body: GoogleLoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") bearer: String
    ): Response<MessageResponse>

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") bearer: String
    ): Response<AuthResponse>

    // ── Pickup endpoints ─────────────────────────────────────────

    @GET("pickups")
    suspend fun getPickups(
        @Header("Authorization") bearer: String
    ): Response<PickupListResponse>

    @POST("pickups")
    suspend fun createPickup(
        @Header("Authorization") bearer: String,
        @Body body: CreatePickupRequest
    ): Response<PickupSingleResponse>

    @GET("pickups/{id}")
    suspend fun getPickup(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<PickupSingleResponse>

    @POST("pickups/{id}/cancel")
    suspend fun cancelPickup(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: CancelPickupRequest = CancelPickupRequest()
    ): Response<PickupSingleResponse>

    // ── Marketplace endpoints ─────────────────────────────────────

    /** GET /api/marketplace?category=&search= */
    @GET("marketplace")
    suspend fun getListings(
        @Header("Authorization") bearer: String,
        @Query("category") category: String? = null,
        @Query("search")   search: String? = null
    ): Response<ListingListResponse>

    /** GET /api/marketplace/mine — seller's own listings */
    @GET("marketplace/mine")
    suspend fun getMyListings(
        @Header("Authorization") bearer: String
    ): Response<ListingListResponse>

    /** GET /api/marketplace/{id} */
    @GET("marketplace/{id}")
    suspend fun getListing(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<ListingSingleResponse>

    /** POST /api/marketplace — create a new listing (multipart for image upload) */
    @Multipart
    @POST("marketplace")
    suspend fun createListing(
        @Header("Authorization") bearer: String,
        @Part("name")        name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price")       price: RequestBody,
        @Part("category")    category: RequestBody,
        @Part("condition")   condition: RequestBody,
        @Part("stock")       stock: RequestBody? = null,
        @Part            image: MultipartBody.Part? = null
    ): Response<ListingSingleResponse>

    /** DELETE /api/marketplace/{id} — deactivate seller's listing */
    @DELETE("marketplace/{id}")
    suspend fun deleteListing(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<Map<String, String>>

    /** POST /api/marketplace/{id}?_method=PUT — update a listing (multipart for image) */
    @Multipart
    @POST("marketplace/{id}")
    suspend fun updateListing(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Part("_method")     method: RequestBody,
        @Part("name")        name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price")       price: RequestBody,
        @Part("category")    category: RequestBody,
        @Part("condition")   condition: RequestBody,
        @Part("stock")       stock: RequestBody? = null,
        @Part            image: MultipartBody.Part? = null
    ): Response<ListingSingleResponse>

    // ── Order endpoints ───────────────────────────────────────────

    /** GET /api/orders — list buyer's orders */
    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") bearer: String
    ): Response<OrderListResponse>

    /** POST /api/orders — create an order (buy a listing) */
    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") bearer: String,
        @Body body: CreateOrderRequest
    ): Response<OrderSingleResponse>

    /** GET /api/orders/{id} */
    @GET("orders/{id}")
    suspend fun getOrder(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<OrderSingleResponse>

    /** POST /api/orders/{id}/pay — initiate Mayar payment, returns link */
    @POST("orders/{id}/pay")
    suspend fun payOrder(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: EmptyRequest = EmptyRequest()
    ): Response<PayOrderResponse>

    /** GET /api/orders/{id}/payment-status — poll Mayar payment status */
    @GET("orders/{id}/payment-status")
    suspend fun getPaymentStatus(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<PaymentStatusResponse>

    /** POST /api/orders/{id}/cancel */
    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: CancelOrderRequest = CancelOrderRequest()
    ): Response<OrderSingleResponse>

    /** GET /api/orders/sales-transactions — Mayar paid+unpaid history (seller) */
    @GET("orders/sales-transactions")
    suspend fun getSalesTransactions(
        @Header("Authorization") bearer: String
    ): Response<SalesTransactionsResponse>

    /** POST /api/orders/checkout-cart — cart checkout, 1 Mayar payment for multiple items */
    @POST("orders/checkout-cart")
    suspend fun cartCheckout(
        @Header("Authorization") bearer: String,
        @Body body: CartCheckoutRequest
    ): Response<CartCheckoutResponse>

    /** GET /api/orders/cart-checkouts — list all buyer's cart checkout groups */
    @GET("orders/cart-checkouts")
    suspend fun getCartCheckouts(
        @Header("Authorization") bearer: String
    ): Response<CartCheckoutsListResponse>

    /** GET /api/orders/cart-checkout/{id}/payment-status */
    @GET("orders/cart-checkout/{cartCheckoutId}/payment-status")
    suspend fun getCartCheckoutStatus(
        @Header("Authorization") bearer: String,
        @Path("cartCheckoutId") cartCheckoutId: String
    ): Response<CartCheckoutStatusResponse>

    /** POST /api/orders/cart-checkout/{id}/cancel */
    @POST("orders/cart-checkout/{cartCheckoutId}/cancel")
    suspend fun cancelCartCheckout(
        @Header("Authorization") bearer: String,
        @Path("cartCheckoutId") cartCheckoutId: String,
        @Body body: CancelOrderRequest = CancelOrderRequest()
    ): Response<CartCheckoutStatusResponse>

    // ── Wishlist endpoints ────────────────────────────────────────

    /** GET /api/wishlist — list all wishlisted items */
    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") bearer: String
    ): Response<WishlistListResponse>

    /** POST /api/wishlist/toggle — add or remove from wishlist */
    @POST("wishlist/toggle")
    suspend fun toggleWishlist(
        @Header("Authorization") bearer: String,
        @Body body: ToggleWishlistRequest
    ): Response<ToggleWishlistResponse>

    // ── Address endpoints ─────────────────────────────────────────

    /** GET /api/addresses — list all user's addresses */
    @GET("addresses")
    suspend fun getAddresses(
        @Header("Authorization") bearer: String
    ): Response<AddressListResponse>

    /** POST /api/addresses — add a new address */
    @POST("addresses")
    suspend fun addAddress(
        @Header("Authorization") bearer: String,
        @Body body: AddAddressRequest
    ): Response<AddressSingleResponse>

    /** PATCH /api/addresses/{id}/default — set address as default */
    @PATCH("addresses/{id}/default")
    suspend fun setDefaultAddress(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<AddressSingleResponse>

    /** DELETE /api/addresses/{id} — delete an address */
    @DELETE("addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<Map<String, String>>

    // ── Courier endpoints ─────────────────────────────────────────

    /** GET /api/courier/me — courier profile */
    @GET("courier/me")
    suspend fun getCourierMe(
        @Header("Authorization") bearer: String
    ): Response<CourierMeResponse>

    /** GET /api/courier/pickups — pickups assigned to this courier */
    @GET("courier/pickups")
    suspend fun getCourierPickups(
        @Header("Authorization") bearer: String
    ): Response<CourierPickupListResponse>

    /** GET /api/courier/available-pickups — unassigned searching pickups */
    @GET("courier/available-pickups")
    suspend fun getAvailablePickups(
        @Header("Authorization") bearer: String
    ): Response<CourierPickupListResponse>

    /** POST /api/courier/pickups/{id}/accept — accept a searching pickup */
    @POST("courier/pickups/{id}/accept")
    suspend fun acceptPickup(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<AcceptPickupResponse>

    /** PATCH /api/courier/pickups/{id}/status — update pickup status */
    @PATCH("courier/pickups/{id}/status")
    suspend fun updatePickupStatus(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: UpdatePickupStatusRequest
    ): Response<CourierPickupSingleResponse>

    /** PATCH /api/courier/availability — toggle online/offline */
    @PATCH("courier/availability")
    suspend fun updateCourierAvailability(
        @Header("Authorization") bearer: String,
        @Body body: CourierAvailabilityRequest
    ): Response<CourierAvailabilityResponse>

    // ── Courier Order (marketplace delivery) endpoints ────────────

    /** GET /api/courier/available-orders — paid orders awaiting a courier */
    @GET("courier/available-orders")
    suspend fun getAvailableOrders(
        @Header("Authorization") bearer: String
    ): Response<CourierOrderListResponse>

    /** GET /api/courier/orders — orders assigned to this courier */
    @GET("courier/orders")
    suspend fun getCourierOrders(
        @Header("Authorization") bearer: String
    ): Response<CourierOrderListResponse>

    /** POST /api/courier/orders/{id}/accept — accept an order delivery */
    @POST("courier/orders/{id}/accept")
    suspend fun acceptOrder(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: EmptyRequest = EmptyRequest()
    ): Response<CourierOrderSingleResponse>

    /** PATCH /api/courier/orders/{id}/status — update order delivery status */
    @PATCH("courier/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: UpdatePickupStatusRequest
    ): Response<CourierOrderSingleResponse>
}

