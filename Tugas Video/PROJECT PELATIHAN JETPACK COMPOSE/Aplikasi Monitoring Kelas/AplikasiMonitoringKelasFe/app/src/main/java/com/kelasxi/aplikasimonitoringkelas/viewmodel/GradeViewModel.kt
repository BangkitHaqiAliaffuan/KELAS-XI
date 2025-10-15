package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Grade
import com.kelasxi.aplikasimonitoringkelas.data.model.GradeStatistics
import com.kelasxi.aplikasimonitoringkelas.data.repository.GradeRepository
import kotlinx.coroutines.launch

class GradeViewModel : ViewModel() {
    private val repository = GradeRepository(RetrofitClient.apiService)

    val grades = mutableStateOf<List<Grade>>(emptyList())
    val statistics = mutableStateOf<GradeStatistics?>(null)
    val isLoading = mutableStateOf(false)
    val isSubmitting = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    // Load grades
    fun loadGrades(
        token: String,
        mataPelajaran: String? = null,
        kelas: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getGrades(token, mataPelajaran, kelas)
            
            result.onSuccess { response ->
                grades.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Load grades for specific siswa
    fun loadSiswaGrades(token: String, siswaId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getSiswaGrades(token, siswaId)
            
            result.onSuccess { response ->
                grades.value = response.data.grades
                statistics.value = response.data.statistics
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Create grade (Guru only)
    fun createGrade(
        token: String,
        siswaId: Int,
        assignmentId: Int,
        nilai: Double,
        catatan: String?
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.createGrade(token, siswaId, assignmentId, nilai, catatan)
            
            result.onSuccess { response ->
                successMessage.value = "Nilai berhasil disimpan"
                isSubmitting.value = false
                // Reload grades
                loadGrades(token)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
            }
        }
    }

    // Update grade (Guru only)
    fun updateGrade(
        token: String,
        gradeId: Int,
        nilai: Double,
        catatan: String?
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.updateGrade(token, gradeId, nilai, catatan)
            
            result.onSuccess { response ->
                successMessage.value = "Nilai berhasil diupdate"
                isSubmitting.value = false
                // Reload grades
                loadGrades(token)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
            }
        }
    }

    // Clear messages
    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun clearSuccess() {
        successMessage.value = null
    }
}
