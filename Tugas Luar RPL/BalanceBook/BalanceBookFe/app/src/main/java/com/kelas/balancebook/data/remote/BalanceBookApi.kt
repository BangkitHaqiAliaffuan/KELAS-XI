package com.kelas.balancebook.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BalanceBookApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("google-login")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): AuthResponse

    @GET("me")
    suspend fun me(): MeResponse

    @POST("logout")
    suspend fun logout(): MessageResponse

    @GET("dashboard/summary")
    suspend fun dashboardSummary(): DashboardSummaryResponse

    @GET("reports/overview")
    suspend fun reportOverview(@Query("period") period: String): ReportResponse

    @GET("transactions")
    suspend fun transactions(@QueryMap options: Map<String, String>): TransactionListResponse

    @GET("transactions/recent")
    suspend fun recentTransactions(@Query("limit") limit: Int = 5): TransactionListResponse

    @POST("transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): CreateTransactionResponse

    @GET("settings")
    suspend fun settings(): SettingsResponse

    @PATCH("settings")
    suspend fun updateSettings(@Body request: UpdateSettingsRequest): UpdateSettingsResponse

    @GET("export/csv")
    suspend fun exportCsv(): Response<ResponseBody>
}
