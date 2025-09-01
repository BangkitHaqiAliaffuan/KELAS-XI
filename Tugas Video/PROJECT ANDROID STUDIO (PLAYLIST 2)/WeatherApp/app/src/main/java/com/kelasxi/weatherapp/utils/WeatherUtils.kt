package com.kelasxi.weatherapp.utils

import android.content.Context
import com.kelasxi.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*

object WeatherUtils {
    
    fun kelvinToCelsius(kelvin: Double): Int {
        return (kelvin - 273.15).toInt()
    }
    
    fun getWeatherIcon(weatherCondition: String): Int {
        return when (weatherCondition.lowercase()) {
            "clear" -> R.drawable.sunny
            "clouds" -> R.drawable.white_cloud
            "rain", "drizzle" -> R.drawable.rain
            "snow" -> R.drawable.snow
            "thunderstorm" -> R.drawable.rain
            "mist", "fog", "haze" -> R.drawable.cloud_black
            else -> R.drawable.sunny
        }
    }
    
    fun getWeatherAnimation(weatherCondition: String): Int {
        return when (weatherCondition.lowercase()) {
            "clear" -> R.raw.sun
            "clouds" -> R.raw.cloud
            "rain", "drizzle" -> R.raw.rain
            "snow" -> R.raw.snow
            "thunderstorm" -> R.raw.rain
            else -> R.raw.sun
        }
    }
    
    fun getWeatherBackground(weatherCondition: String): Int {
        return when (weatherCondition.lowercase()) {
            "clear" -> R.drawable.sunny_background
            "clouds" -> R.drawable.colud_background
            "rain", "drizzle" -> R.drawable.rain_background
            "snow" -> R.drawable.snow_background
            "thunderstorm" -> R.drawable.rain_background
            else -> R.drawable.sunny_background
        }
    }
    
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(date)
    }
    
    fun getCurrentDate(): String {
        val currentDate = Date()
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(currentDate)
    }
    
    fun getWindDirection(degree: Int?): String {
        if (degree == null) return "N/A"
        
        return when (degree) {
            in 0..22, in 338..360 -> "Utara"
            in 23..67 -> "Timur Laut"
            in 68..112 -> "Timur"
            in 113..157 -> "Tenggara"
            in 158..202 -> "Selatan"
            in 203..247 -> "Barat Daya"
            in 248..292 -> "Barat"
            in 293..337 -> "Barat Laut"
            else -> "N/A"
        }
    }
    
    fun capitalizeWords(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
            }
        }
    }
}
