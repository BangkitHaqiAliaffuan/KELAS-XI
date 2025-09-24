package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Composable function untuk loading indicator
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = Color.White,
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Loading weather data...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Composable function untuk error state
 * @param errorMessage pesan error yang akan ditampilkan
 * @param onRetry callback saat tombol retry ditekan
 */
@Composable
fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = Color.White,
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.wrapContentWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Try Again",
                fontWeight = FontWeight.Medium
            )
        }
    }
}