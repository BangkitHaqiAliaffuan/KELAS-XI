package com.kelasxi.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * ShimmerLoadingBox - Komponen shimmer loading generik.
 * Digunakan sebagai pengganti CircularProgressIndicator agar lebih sesuai konten.
 */
@Composable
fun ShimmerLoadingBox(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0).copy(alpha = 0.9f),
        Color(0xFFF5F5F5).copy(alpha = 0.5f),
        Color(0xFFE0E0E0).copy(alpha = 0.9f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    )
}

/**
 * ShimmerPickupCard - Skeleton untuk card pickup di HomeScreen.
 */
@Composable
fun ShimmerPickupCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerLoadingBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }
}

/**
 * ShimmerStatCard - Skeleton untuk stat card di HomeScreen.
 */
@Composable
fun ShimmerStatCard() {
    ShimmerLoadingBox(
        modifier = Modifier
            .width(130.dp)
            .height(90.dp)
    )
}

/**
 * ShimmerProductGrid - Skeleton untuk grid produk di MarketplaceScreen.
 */
@Composable
fun ShimmerProductGrid() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerLoadingBox(modifier = Modifier.weight(1f).height(220.dp))
                ShimmerLoadingBox(modifier = Modifier.weight(1f).height(220.dp))
            }
        }
    }
}
