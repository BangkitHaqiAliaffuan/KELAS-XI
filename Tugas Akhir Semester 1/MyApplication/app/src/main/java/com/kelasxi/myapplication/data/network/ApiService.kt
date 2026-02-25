package com.kelasxi.myapplication.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") bearer: String
    ): Response<MessageResponse>

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") bearer: String
    ): Response<AuthResponse>

    // ── Pickup endpoints ─────────────────────────────────────────

    /** GET /api/pickups — fetch all pickups for the logged-in user */
    @GET("pickups")
    suspend fun getPickups(
        @Header("Authorization") bearer: String
    ): Response<PickupListResponse>

    /** POST /api/pickups — create a new pickup request */
    @POST("pickups")
    suspend fun createPickup(
        @Header("Authorization") bearer: String,
        @Body body: CreatePickupRequest
    ): Response<PickupSingleResponse>

    /** GET /api/pickups/{id} — get a single pickup by id */
    @GET("pickups/{id}")
    suspend fun getPickup(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<PickupSingleResponse>

    /** POST /api/pickups/{id}/cancel — cancel a pending pickup */
    @POST("pickups/{id}/cancel")
    suspend fun cancelPickup(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body body: CancelPickupRequest = CancelPickupRequest()
    ): Response<PickupSingleResponse>
}
