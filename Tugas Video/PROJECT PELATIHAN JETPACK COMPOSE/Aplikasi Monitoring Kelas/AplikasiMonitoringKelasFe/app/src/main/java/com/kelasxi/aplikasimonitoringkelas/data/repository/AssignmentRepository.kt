package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AssignmentRepository(private val apiService: ApiService) {

    // Get all assignments
    suspend fun getAssignments(
        token: String,
        kelas: String? = null,
        mataPelajaran: String? = null,
        tipe: String? = null
    ): Result<AssignmentsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAssignments("Bearer $token", kelas, mataPelajaran, tipe)
                if (response.isSuccessful && response.body() != null) {
                    val assignmentsResponse = response.body()!!
                    if (assignmentsResponse.success) {
                        Result.success(assignmentsResponse)
                    } else {
                        Result.failure(Exception(assignmentsResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get assignments: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Get assignment detail
    suspend fun getAssignmentDetail(token: String, id: Int): Result<AssignmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAssignmentDetail("Bearer $token", id)
                if (response.isSuccessful && response.body() != null) {
                    val assignmentResponse = response.body()!!
                    if (assignmentResponse.success) {
                        Result.success(assignmentResponse)
                    } else {
                        Result.failure(Exception(assignmentResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get assignment: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Create assignment (Guru only)
    suspend fun createAssignment(
        token: String,
        kelas: String,
        mataPelajaran: String,
        judul: String,
        deskripsi: String,
        deadline: String,
        tipe: String,
        bobot: Int,
        file: File?
    ): Result<AssignmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val kelasBody = kelas.toRequestBody("text/plain".toMediaTypeOrNull())
                val mataPelajaranBody = mataPelajaran.toRequestBody("text/plain".toMediaTypeOrNull())
                val judulBody = judul.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val deadlineBody = deadline.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipeBody = tipe.toRequestBody("text/plain".toMediaTypeOrNull())
                val bobotBody = bobot.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = file?.let {
                    val requestFile = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", it.name, requestFile)
                }

                val response = apiService.createAssignment(
                    "Bearer $token",
                    kelasBody,
                    mataPelajaranBody,
                    judulBody,
                    deskripsiBody,
                    deadlineBody,
                    tipeBody,
                    bobotBody,
                    filePart
                )

                if (response.isSuccessful && response.body() != null) {
                    val assignmentResponse = response.body()!!
                    if (assignmentResponse.success) {
                        Result.success(assignmentResponse)
                    } else {
                        Result.failure(Exception(assignmentResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to create assignment: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Submit assignment (Siswa)
    suspend fun submitAssignment(
        token: String,
        assignmentId: Int,
        keterangan: String?,
        file: File
    ): Result<SubmissionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val keteranganBody = keterangan?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.submitAssignment(
                    "Bearer $token",
                    assignmentId,
                    keteranganBody,
                    filePart
                )

                if (response.isSuccessful && response.body() != null) {
                    val submissionResponse = response.body()!!
                    if (submissionResponse.success) {
                        Result.success(submissionResponse)
                    } else {
                        Result.failure(Exception(submissionResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to submit assignment: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Get submissions for assignment (Guru)
    suspend fun getAssignmentSubmissions(token: String, assignmentId: Int): Result<SubmissionsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAssignmentSubmissions("Bearer $token", assignmentId)
                if (response.isSuccessful && response.body() != null) {
                    val submissionsResponse = response.body()!!
                    if (submissionsResponse.success) {
                        Result.success(submissionsResponse)
                    } else {
                        Result.failure(Exception(submissionsResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get submissions: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }
}
