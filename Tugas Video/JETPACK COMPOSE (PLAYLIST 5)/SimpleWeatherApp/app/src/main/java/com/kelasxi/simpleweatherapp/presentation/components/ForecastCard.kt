package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kelasxi.simpleweatherapp.data.model.ForecastResponse
import com.kelasxi.simpleweatherapp.data.model.ForecastDay
import com.kelasxi.simpleweatherapp.data.model.HourWeather
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable untuk menampilkan forecast card dengan glass morphism design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastCard(
    forecastData: ForecastResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Forecast",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "3-Day Forecast",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Forecast Days
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                forecastData.forecast.forecastDays.forEach { forecastDay ->
                    ForecastDayItem(
                        forecastDay = forecastDay,
                        isToday = isToday(forecastDay.date)
                    )
                }
            }
        }
    }
}

/**
 * Composable untuk menampilkan item forecast harian
 */
@Composable
fun ForecastDayItem(
    forecastDay: ForecastDay,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) {
                Color.White.copy(alpha = 0.25f)
            } else {
                Color.White.copy(alpha = 0.15f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isToday) {
                            listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.2f)
                            )
                        } else {
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            // Date and Weather Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isToday) "Today" else formatDate(forecastDay.date),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                    Text(
                        text = forecastDay.day.condition.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = "https:${forecastDay.day.condition.icon}",
                        contentDescription = forecastDay.day.condition.text,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${forecastDay.day.maxTempC.toInt()}°",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                    blurRadius = 3f
                                )
                            )
                        )
                        Text(
                            text = "${forecastDay.day.minTempC.toInt()}°",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }
            }
            
            // Additional Weather Details
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailChip(
                    label = "Rain",
                    value = "${forecastDay.day.totalPrecipMm.toInt()}mm"
                )
                WeatherDetailChip(
                    label = "Humidity",
                    value = "${forecastDay.day.avgHumidity}%"
                )
                WeatherDetailChip(
                    label = "Wind",
                    value = "${forecastDay.day.maxWindKph.toInt()}km/h"
                )
            }
            
            // Hourly Forecast untuk hari ini
            if (isToday) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Hourly Forecast",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                HourlyForecastRow(hours = forecastDay.hours)
            }
        }
    }
}

/**
 * Composable untuk menampilkan hourly forecast dalam row
 */
@Composable
fun HourlyForecastRow(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        // Ambil hanya 8 jam ke depan dari jam saat ini
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val filteredHours = hours.filter { hour ->
            val hourTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .parse(hour.time)?.let {
                    Calendar.getInstance().apply { time = it }.get(Calendar.HOUR_OF_DAY)
                } ?: 0
            hourTime >= currentHour
        }.take(8)
        
        items(filteredHours) { hour ->
            HourlyForecastItem(hour = hour)
        }
    }
}

/**
 * Composable untuk menampilkan item forecast per jam
 */
@Composable
fun HourlyForecastItem(
    hour: HourWeather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(80.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatHour(hour.time),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                textAlign = TextAlign.Center
            )
            
            AsyncImage(
                model = "https:${hour.condition.icon}",
                contentDescription = hour.condition.text,
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = "${hour.tempC.toInt()}°",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                textAlign = TextAlign.Center
            )
            
            if (hour.chanceOfRain > 0) {
                Text(
                    text = "${hour.chanceOfRain}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Composable untuk weather detail chip
 */
@Composable
fun WeatherDetailChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}

/**
 * Helper functions
 */
private fun isToday(dateString: String): Boolean {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    return dateString == today
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

private fun formatHour(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timeString.split(" ").lastOrNull()?.take(5) ?: ""
    }
}