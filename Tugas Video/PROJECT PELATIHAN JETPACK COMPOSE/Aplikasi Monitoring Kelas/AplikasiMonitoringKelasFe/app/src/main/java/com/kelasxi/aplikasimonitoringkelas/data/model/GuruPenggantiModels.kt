package com.kelasxi.aplikasimonitoringkelas.data.model

// Models untuk Guru Pengganti
data class GuruPenggantiResponse(
    val success: Boolean,
    val message: String,
    val data: List<GuruPengganti>
)

data class GuruPengganti(
    val id: Int,
    val guru_pengganti_id: Int,
    val guru_asli_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?,
    val assigned_by: Int,
    val guruPengganti: Guru,
    val guruAsli: Guru?,
    val created_at: String,
    val updated_at: String
)

data class GuruPenggantiRequest(
    val guru_pengganti_id: Int,
    val guru_asli_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?
)

// Models untuk Kelas Kosong
data class KelasKosongResponse(
    val success: Boolean,
    val message: String,
    val data: List<KelasKosong>,
    val summary: KelasKosongSummary
)

data class KelasKosong(
    val jadwal_id: Int?,
    val monitoring_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val guru: Guru,
    val jam_mulai: String?,
    val jam_selesai: String?,
    val ruang: String?,
    val tanggal: String,
    val hari: String,
    val status: String, // "Tidak Ada Laporan" or "Tidak Hadir"
    val catatan: String?
)

data class KelasKosongSummary(
    val total_jadwal: Int,
    val total_kelas_kosong: Int,
    val tanggal: String,
    val hari: String
)
