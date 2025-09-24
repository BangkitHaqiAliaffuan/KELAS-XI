package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kelasxi.simpleweatherapp.data.model.WeatherResponse

/**
 * Composable function untuk menampilkan data cuaca utama
 * @param weatherData data cuaca yang akan ditampilkan
 */
@Composable
fun WeatherDisplay(
    weatherData: WeatherResponse,
    modifier: Modifier = Modifier
) {
    // Main weather card dengan background semi-transparent
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${weatherData.location.name}, ${weatherData.location.country}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            
            // Temperature dengan shadow untuk readability
            Text(
                text = "${weatherData.current.tempC.toInt()}°C",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weather condition with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Weather icon from API
                AsyncImage(
                    model = "https:${weatherData.current.condition.icon}",
                    contentDescription = "Weather icon",
                    modifier = Modifier.size(64.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = weatherData.current.condition.text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}