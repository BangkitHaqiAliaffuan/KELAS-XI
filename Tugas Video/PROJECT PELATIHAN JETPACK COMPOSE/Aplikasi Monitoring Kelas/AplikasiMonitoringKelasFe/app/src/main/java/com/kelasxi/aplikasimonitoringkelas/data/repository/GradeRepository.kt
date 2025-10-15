package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GradeRepository(private val apiService: ApiService) {

    // Get all grades (filtered by role in backend)
    suspend fun getGrades(
        token: String,
        mataPelajaran: String? = null,
        kelas: String? = null
    ): Result<GradesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGrades("Bearer $token", mataPelajaran, kelas)
                if (response.isSuccessful && response.body() != null) {
                    val gradesResponse = response.body()!!
                    if (gradesResponse.success) {
                        Result.success(gradesResponse)
                    } else {
                        Result.failure(Exception(gradesResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get grades: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Get grades for specific siswa
    suspend fun getSiswaGrades(token: String, siswaId: Int): Result<SiswaGradesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSiswaGrades("Bearer $token", siswaId)
                if (response.isSuccessful && response.body() != null) {
                    val siswaGradesResponse = response.body()!!
                    if (siswaGradesResponse.success) {
                        Result.success(siswaGradesResponse)
                    } else {
                        Result.failure(Exception(siswaGradesResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to get student grades: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Create grade (Guru only)
    suspend fun createGrade(
        token: String,
        siswaId: Int,
        assignmentId: Int,
        nilai: Double,
        catatan: String?
    ): Result<GradeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createGrade(
                    "Bearer $token",
                    siswaId,
                    assignmentId,
                    nilai,
                    catatan
                )
                if (response.isSuccessful && response.body() != null) {
                    val gradeResponse = response.body()!!
                    if (gradeResponse.success) {
                        Result.success(gradeResponse)
                    } else {
                        Result.failure(Exception(gradeResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to create grade: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }

    // Update grade (Guru only)
    suspend fun updateGrade(
        token: String,
        gradeId: Int,
        nilai: Double,
        catatan: String?
    ): Result<GradeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateGrade(
                    "Bearer $token",
                    gradeId,
                    nilai,
                    catatan
                )
                if (response.isSuccessful && response.body() != null) {
                    val gradeResponse = response.body()!!
                    if (gradeResponse.success) {
                        Result.success(gradeResponse)
                    } else {
                        Result.failure(Exception(gradeResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to update grade: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to connect to server: ${e.message}"))
            }
        }
    }
}
