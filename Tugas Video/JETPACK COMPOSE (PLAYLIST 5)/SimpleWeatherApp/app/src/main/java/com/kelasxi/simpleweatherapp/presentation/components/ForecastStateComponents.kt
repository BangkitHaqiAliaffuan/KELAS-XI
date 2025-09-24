package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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

/**
 * Composable untuk loading state forecast
 */
@Composable
fun ForecastLoadingIndicator(
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
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Loading Forecast",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Loading Forecast...",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * Composable untuk error state forecast
 */
@Composable
fun ForecastErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Forecast Error",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Forecast Error",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(25.dp),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Retry Forecast",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
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
    }
}