package com.trashbin.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateHelper {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun formatDate(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDateTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { dateTimeFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { timeFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatRelativeTime(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            val now = Date()
            val diff = now.time - date?.time ?: now.time

            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

            when {
                days > 0 -> "${days} hari lalu"
                hours > 0 -> "${hours} jam lalu"
                minutes > 0 -> "${minutes} menit lalu"
                else -> "Baru saja"
            }
        } catch (e: Exception) {
            dateString
        }
    }

    fun isToday(dateString: String): Boolean {
        return try {
            val date = inputFormat.parse(dateString)
            val today = Date()
            outputFormat.format(date) == outputFormat.format(today)
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentDateTime(): String {
        return inputFormat.format(Date())
    }
}