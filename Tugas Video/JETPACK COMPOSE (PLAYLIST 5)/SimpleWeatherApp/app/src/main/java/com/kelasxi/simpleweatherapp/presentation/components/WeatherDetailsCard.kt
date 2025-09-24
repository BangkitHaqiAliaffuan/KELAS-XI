package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kelasxi.simpleweatherapp.data.model.WeatherResponse

/**
 * Composable function untuk card detail cuaca
 * @param weatherData data cuaca yang akan ditampilkan
 */
@Composable
fun WeatherDetailsCard(
    weatherData: WeatherResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Weather Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Humidity
            WeatherDetailRow(
                icon = Icons.Default.Info,
                label = "Humidity",
                value = "${weatherData.current.humidity}%"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Wind Speed
            WeatherDetailRow(
                icon = Icons.Default.Refresh,
                label = "Wind Speed",
                value = "${weatherData.current.windKph} km/h"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // UV Index
            WeatherDetailRow(
                icon = Icons.Default.Star,
                label = "UV Index",
                value = weatherData.current.uvIndex.toString()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Precipitation
            WeatherDetailRow(
                icon = Icons.Default.Info,
                label = "Precipitation",
                value = "${weatherData.current.precipMm} mm"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Local Time
            WeatherDetailRow(
                icon = Icons.Default.DateRange,
                label = "Local Time",
                value = formatLocalTime(weatherData.location.localTime)
            )
        }
    }
}

/**
 * Composable function untuk satu baris detail cuaca
 * @param icon ikon untuk detail
 * @param label label detail
 * @param value value detail
 */
@Composable
private fun WeatherDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Format local time untuk tampilan yang lebih readable
 * @param localTime string waktu dari API
 * @return formatted time string
 */
private fun formatLocalTime(localTime: String): String {
    return try {
        // API biasanya mengembalikan format: "2023-12-07 14:30"
        val parts = localTime.split(" ")
        if (parts.size >= 2) {
            val date = parts[0]
            val time = parts[1]
            "$time, $date"
        } else {
            localTime
        }
    } catch (e: Exception) {
        localTime
    }
}