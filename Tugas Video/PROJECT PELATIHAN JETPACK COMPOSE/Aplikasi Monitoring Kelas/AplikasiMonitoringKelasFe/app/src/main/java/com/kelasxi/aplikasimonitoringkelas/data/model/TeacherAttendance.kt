package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.annotations.SerializedName

data class TeacherAttendance(
    @SerializedName("id") val id: Int,
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam_masuk") val jamMasuk: String?,
    @SerializedName("status") val status: String, // hadir, telat, tidak_hadir
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("created_by") val createdBy: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("schedule") val schedule: Schedule?,
    @SerializedName("guru") val guru: User?,
    @SerializedName("created_by_user") val createdByUser: User?
)

data class TodayScheduleWithAttendance(
    @SerializedName("schedule") val schedule: Schedule,
    @SerializedName("attendance") val attendance: TeacherAttendance?,
    @SerializedName("has_attendance") val hasAttendance: Boolean,
    @SerializedName("status") val status: String // hadir, telat, tidak_hadir, belum_dicatat
)

data class TodaySchedulesResponse(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("hari") val hari: String,
    @SerializedName("total_schedules") val totalSchedules: Int,
    @SerializedName("sudah_dicatat") val sudahDicatat: Int,
    @SerializedName("belum_dicatat") val belumDicatat: Int,
    @SerializedName("data") val data: List<TodayScheduleWithAttendance>
)

data class TodayAttendanceResponse(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("total") val total: Int,
    @SerializedName("hadir") val hadir: Int,
    @SerializedName("telat") val telat: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    @SerializedName("data") val data: List<TeacherAttendance>
)

data class AttendanceStatistics(
    @SerializedName("total") val total: Int,
    @SerializedName("hadir") val hadir: Int,
    @SerializedName("telat") val telat: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    @SerializedName("percentage_hadir") val percentageHadir: Double,
    @SerializedName("percentage_telat") val percentageTelat: Double
)

data class TeacherAttendanceRequest(
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam_masuk") val jamMasuk: String,
    @SerializedName("status") val status: String,
    @SerializedName("keterangan") val keterangan: String?
)

data class TeacherAttendanceUpdateRequest(
    @SerializedName("jam_masuk") val jamMasuk: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keterangan: String?
)

data class PaginatedResponse<T>(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<T>,
    @SerializedName("first_page_url") val firstPageUrl: String,
    @SerializedName("from") val from: Int?,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("last_page_url") val lastPageUrl: String,
    @SerializedName("next_page_url") val nextPageUrl: String?,
    @SerializedName("path") val path: String,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("prev_page_url") val prevPageUrl: String?,
    @SerializedName("to") val to: Int?,
    @SerializedName("total") val total: Int
)
