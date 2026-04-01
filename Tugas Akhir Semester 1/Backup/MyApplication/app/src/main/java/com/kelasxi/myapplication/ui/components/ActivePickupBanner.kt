package com.kelasxi.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.myapplication.model.PickupRequest
import com.kelasxi.myapplication.model.PickupStatus
import com.kelasxi.myapplication.ui.theme.*

/**
 * ActivePickupBanner - Banner yang ditampilkan di atas HomeScreen
 * saat user memiliki pickup aktif (SEARCHING/PENDING/ON_THE_WAY).
 * Memudahkan akses langsung ke detail pickup tanpa scroll.
 */
@Composable
fun ActivePickupBanner(
    pickup: PickupRequest,
    onClick: () -> Unit
) {
    // Pulse animation untuk dot indikator
    val infiniteTransition = rememberInfiniteTransition(label = "banner_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    val (gradientColors, statusText) = when (pickup.status) {
        PickupStatus.SEARCHING  -> Pair(listOf(Color(0xFFFF8C42), Color(0xFFFFB347)), "🔍 Mencari Kurir...")
        PickupStatus.PENDING    -> Pair(listOf(Color(0xFFFFAB00), Color(0xFFFFD54F)), "⏳ Kurir Ditemukan")
        PickupStatus.ON_THE_WAY -> Pair(listOf(GreenDeep, GreenMedium), "🚛 Kurir Dalam Perjalanan")
        else                    -> Pair(listOf(GreenDeep, GreenLight), "✅ Selesai")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "Pickup aktif: $statusText. Ketuk untuk melihat detail"
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated status dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color.White.copy(alpha = dotAlpha),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))

            // Icon truck
            Icon(
                imageVector = Icons.Filled.LocalShipping,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pickup Aktif",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID #${pickup.id} • ${pickup.address.take(25)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
