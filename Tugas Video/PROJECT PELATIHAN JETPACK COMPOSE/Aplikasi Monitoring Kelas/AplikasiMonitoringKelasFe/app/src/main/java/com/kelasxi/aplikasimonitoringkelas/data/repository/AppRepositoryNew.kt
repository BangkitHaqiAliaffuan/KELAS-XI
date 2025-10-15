package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val apiService: ApiService) {
    
    // ==================== JADWAL PELAJARAN ====================
    
    suspend fun getSchedules(token: String, hari: String? = null, kelas: String? = null): Result<ScheduleResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSchedules("Bearer $token", hari, kelas)
                if (response.isSuccessful && response.body() != null) {
                    val scheduleResponse = response.body()!!
                    if (scheduleResponse.success) {
                        Result.success(scheduleResponse)
                    } else {
                        Result.failure(Exception(scheduleResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil jadwal: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== ADMIN: USER MANAGEMENT ====================
    
    suspend fun getUsers(token: String): Result<UsersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUsers("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val usersResponse = response.body()!!
                    if (usersResponse.success) {
                        Result.success(usersResponse)
                    } else {
                        Result.failure(Exception(usersResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data users: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun createUser(token: String, request: CreateUserRequest): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createUser("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal membuat user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun updateUserRole(token: String, userId: Int, role: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUserRole("Bearer $token", userId, UpdateRoleRequest(role))
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengupdate role: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun banUser(token: String, userId: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.banUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal ban user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun unbanUser(token: String, userId: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.unbanUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal unban user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun deleteUser(token: String, userId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menghapus user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== SISWA: MONITORING ====================
    
    suspend fun storeMonitoring(token: String, request: MonitoringRequest): Result<MonitoringResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.storeMonitoring("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menyimpan monitoring: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getMyReports(token: String, tanggal: String? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyReports("Bearer $token", tanggal)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil laporan: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== KURIKULUM & KEPALA SEKOLAH: MONITORING ====================
    
    suspend fun getMonitoring(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMonitoring("Bearer $token", tanggal, kelas, guruId)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data monitoring: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getKelasKosong(token: String, tanggal: String? = null): Result<KelasKosongResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getKelasKosong("Bearer $token", tanggal)
                if (response.isSuccessful && response.body() != null) {
                    val kelasKosongResponse = response.body()!!
                    if (kelasKosongResponse.success) {
                        Result.success(kelasKosongResponse)
                    } else {
                        Result.failure(Exception(kelasKosongResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil kelas kosong: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== KURIKULUM: GURU PENGGANTI ====================
    
    suspend fun getGuruPengganti(token: String, tanggal: String? = null, kelas: String? = null): Result<GuruPenggantiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGuruPengganti("Bearer $token", tanggal, kelas)
                if (response.isSuccessful && response.body() != null) {
                    val guruPenggantiResponse = response.body()!!
                    if (guruPenggantiResponse.success) {
                        Result.success(guruPenggantiResponse)
                    } else {
                        Result.failure(Exception(guruPenggantiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun createGuruPengganti(token: String, request: GuruPenggantiRequest): Result<GuruPengganti> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createGuruPengganti("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menambahkan guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun updateGuruPengganti(token: String, id: Int, request: GuruPenggantiRequest): Result<GuruPengganti> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateGuruPengganti("Bearer $token", id, request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengupdate guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun deleteGuruPengganti(token: String, id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteGuruPengganti("Bearer $token", id)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menghapus guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== LOGOUT ====================
    
    suspend fun logout(token: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout("Bearer $token")
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Logout gagal: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal logout: ${e.message}"))
            }
        }
    }
}
