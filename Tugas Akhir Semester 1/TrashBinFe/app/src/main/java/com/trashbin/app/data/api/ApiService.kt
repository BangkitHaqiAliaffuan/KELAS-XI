package com.trashbin.app.data.api

import com.trashbin.app.data.model.ApiResponse
import com.trashbin.app.data.model.ClassificationResult
import com.trashbin.app.data.model.CreateOrderRequest
import com.trashbin.app.data.model.LoginResponse
import com.trashbin.app.data.model.User
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.data.model.PickupRequest
import com.trashbin.app.data.model.PickupResponse
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.data.model.Order
import com.trashbin.app.data.model.PaginatedListings
import com.trashbin.app.data.model.PointsBalance
import com.trashbin.app.data.model.PointsHistory
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Authentication endpoints
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("role") role: String,
        @Field("lat") lat: Double?,
        @Field("lng") lng: Double?
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Body userData: Map<String, Any>
    ): Response<ApiResponse<User>>

    // FCM Token
    @FormUrlEncoded
    @POST("auth/fcm-token")
    suspend fun updateFcmToken(
        @Field("fcm_token") fcmToken: String
    ): Response<ApiResponse<String>>

    // Waste Categories
    @GET("waste-categories")
    suspend fun getWasteCategories(): Response<ApiResponse<List<WasteCategory>>>

    // Pickups
    @POST("pickups")
    suspend fun createPickup(
        @Body pickupRequest: PickupRequest
    ): Response<ApiResponse<PickupResponse>>

    @GET("pickups")
    suspend fun getPickups(
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null
    ): Response<ApiResponse<List<PickupResponse>>>

    @GET("pickups/{id}")
    suspend fun getPickupDetail(
        @Path("id") id: Int
    ): Response<ApiResponse<PickupResponse>>

    @PUT("pickups/{id}/cancel")
    suspend fun cancelPickup(
        @Path("id") id: Int,
        @Body reason: Map<String, String>
    ): Response<ApiResponse<PickupResponse>>

    // Collector endpoints
    @GET("collector/available-pickups")
    suspend fun getAvailablePickups(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double
    ): Response<ApiResponse<List<PickupResponse>>>

    @POST("collector/pickups/{id}/accept")
    suspend fun acceptPickup(
        @Path("id") id: Int
    ): Response<ApiResponse<PickupResponse>>

    @PUT("collector/pickups/{id}/update-status")
    suspend fun updatePickupStatus(
        @Path("id") id: Int,
        @Body statusData: Map<String, String>
    ): Response<ApiResponse<PickupResponse>>

    @POST("collector/pickups/{id}/confirm-weight")
    suspend fun confirmWeight(
        @Path("id") id: Int,
        @Body itemsData: Map<String, Any>
    ): Response<ApiResponse<PickupResponse>>

    // Marketplace
    @GET("marketplace/listings")
    suspend fun getListings(
        @Query("category_id") categoryId: Int? = null,
        @Query("condition") condition: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("radius") radius: Double? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null
    ): Response<ApiResponse<PaginatedListings>>

    @Multipart
    @POST("marketplace/listings")
    suspend fun createListing(
        @Part("category_id") categoryId: Int,
        @Part("title") title: String,
        @Part("description") description: String,
        @Part("quantity") quantity: Int,
        @Part("price_per_unit") pricePerUnit: Double,
        @Part("condition") condition: String,
        @Part("location") location: String,
        @Part("lat") lat: Double,
        @Part("lng") lng: Double,
        @Part photos: List<MultipartBody.Part>
    ): Response<ApiResponse<MarketplaceListing>>

    @GET("marketplace/listings/{id}")
    suspend fun getListingDetail(
        @Path("id") id: Int
    ): Response<ApiResponse<MarketplaceListing>>

    @PUT("marketplace/listings/{id}")
    suspend fun updateListing(
        @Path("id") id: Int,
        @Body listingData: Map<String, Any>
    ): Response<ApiResponse<MarketplaceListing>>

    @DELETE("marketplace/listings/{id}")
    suspend fun deleteListing(
        @Path("id") id: Int
    ): Response<ApiResponse<String>>

    // Orders
    @POST("marketplace/orders")
    suspend fun createOrder(
        @Body orderData: CreateOrderRequest
    ): Response<ApiResponse<Order>>

    @GET("marketplace/orders")
    suspend fun getOrders(
        @Query("role") role: String, // buyer or seller
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null
    ): Response<ApiResponse<List<Order>>>

    @PUT("orders/{id}/confirm")
    suspend fun confirmOrder(
        @Path("id") id: Int
    ): Response<ApiResponse<Order>>

    @PUT("orders/{id}/ship")
    suspend fun shipOrder(
        @Path("id") id: Int,
        @Body trackingData: Map<String, String>
    ): Response<ApiResponse<Order>>

    @PUT("orders/{id}/complete")
    suspend fun completeOrder(
        @Path("id") id: Int
    ): Response<ApiResponse<Order>>

    @POST("orders/{id}/review")
    suspend fun reviewOrder(
        @Path("id") id: Int,
        @Body reviewData: Map<String, Any>
    ): Response<ApiResponse<Order>>

    // Points
    @GET("points")
    suspend fun getPointsBalance(): Response<ApiResponse<PointsBalance>>

    @GET("points/history")
    suspend fun getPointsHistory(
        @Query("page") page: Int? = null
    ): Response<ApiResponse<List<PointsHistory>>>
    
    // Waste Classification (AI)
    @Multipart
    @POST("waste/classify")
    suspend fun classifyWaste(
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<ClassificationResult>>
}