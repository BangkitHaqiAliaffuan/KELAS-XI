package com.kelasxi.aplikasimonitoringkelas.data.api

import com.kelasxi.aplikasimonitoringkelas.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Any>
    
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<LoginResponse>
    
    // Jadwal Pelajaran (Semua Role)
    @GET("jadwal")
    suspend fun getSchedules(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas") kelas: String? = null
    ): Response<ScheduleResponse>
    
    // Users Management (Admin Only)
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<UsersResponse>
    
    @PUT("users/{id}/role")
    suspend fun updateUserRole(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UpdateRoleRequest
    ): Response<LoginResponse>
    
    // Monitoring
    @GET("monitoring")
    suspend fun getMonitoring(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas") kelas: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<MonitoringListResponse>
    
    @POST("monitoring/store")
    suspend fun storeMonitoring(
        @Header("Authorization") token: String,
        @Body request: MonitoringRequest
    ): Response<MonitoringResponse>
}