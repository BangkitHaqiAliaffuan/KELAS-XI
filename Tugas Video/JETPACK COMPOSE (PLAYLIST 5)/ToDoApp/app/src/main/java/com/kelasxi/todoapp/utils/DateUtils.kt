package com.kelasxi.todoapp.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility object untuk formatting tanggal
 * Menyediakan fungsi helper untuk format tanggal dalam aplikasi
 */
object DateUtils {
    
    /**
     * Format tanggal sesuai spesifikasi: "HH:MM a, d MMM"
     * Contoh: "10:30 AM, 24 Sep"
     */
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("HH:mm a, d MMM", Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format tanggal lengkap untuk keperluan lain
     * Contoh: "24 September 2025, 10:30 AM"
     */
    fun formatFullDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("d MMMM yyyy, HH:mm a", Locale.getDefault())
        return formatter.format(date)
    }
}