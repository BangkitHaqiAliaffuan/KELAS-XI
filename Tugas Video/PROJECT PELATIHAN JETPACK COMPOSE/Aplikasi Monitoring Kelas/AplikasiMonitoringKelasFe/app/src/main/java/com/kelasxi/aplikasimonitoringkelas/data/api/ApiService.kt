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

    // Assignments
    @GET("assignments")
    suspend fun getAssignments(
        @Header("Authorization") token: String,
        @Query("kelas") kelas: String? = null,
        @Query("mata_pelajaran") mataPelajaran: String? = null,
        @Query("tipe") tipe: String? = null
    ): Response<AssignmentsResponse>

    @GET("assignments/{id}")
    suspend fun getAssignmentDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AssignmentResponse>

    @Multipart
    @POST("assignments")
    suspend fun createAssignment(
        @Header("Authorization") token: String,
        @Part("kelas") kelas: okhttp3.RequestBody,
        @Part("mata_pelajaran") mataPelajaran: okhttp3.RequestBody,
        @Part("judul") judul: okhttp3.RequestBody,
        @Part("deskripsi") deskripsi: okhttp3.RequestBody,
        @Part("deadline") deadline: okhttp3.RequestBody,
        @Part("tipe") tipe: okhttp3.RequestBody,
        @Part("bobot") bobot: okhttp3.RequestBody,
        @Part file: okhttp3.MultipartBody.Part?
    ): Response<AssignmentResponse>

    @Multipart
    @POST("assignments/{id}/submit")
    suspend fun submitAssignment(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("keterangan") keterangan: okhttp3.RequestBody?,
        @Part file: okhttp3.MultipartBody.Part
    ): Response<SubmissionResponse>

    @GET("assignments/{id}/submissions")
    suspend fun getAssignmentSubmissions(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<SubmissionsResponse>

    // Grades
    @GET("grades")
    suspend fun getGrades(
        @Header("Authorization") token: String,
        @Query("mata_pelajaran") mataPelajaran: String? = null,
        @Query("kelas") kelas: String? = null
    ): Response<GradesResponse>

    @GET("grades/siswa/{id}")
    suspend fun getSiswaGrades(
        @Header("Authorization") token: String,
        @Path("id") siswaId: Int
    ): Response<SiswaGradesResponse>

    @FormUrlEncoded
    @POST("grades")
    suspend fun createGrade(
        @Header("Authorization") token: String,
        @Field("siswa_id") siswaId: Int,
        @Field("assignment_id") assignmentId: Int,
        @Field("nilai") nilai: Double,
        @Field("catatan") catatan: String?
    ): Response<GradeResponse>

    @FormUrlEncoded
    @PUT("grades/{id}")
    suspend fun updateGrade(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("nilai") nilai: Double,
        @Field("catatan") catatan: String?
    ): Response<GradeResponse>
}