package com.kelasxi.waveoffood.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Floating Action Button with Badge for Cart
 * Shows cart item count with Material 3 styling
 */
@Composable
fun CartFloatingActionButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ShoppingCart
) {
    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Shopping Cart",
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Badge with item count
        AnimatedVisibility(
            visible = itemCount > 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Badge(
                modifier = Modifier
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(20.dp),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Text(
                    text = if (itemCount > 99) "99+" else itemCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Enhanced Badge Component
 * Can be used independently or with other components
 */
@Composable
fun WaveBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    contentColor: Color = MaterialTheme.colorScheme.onError,
    maxDisplayCount: Int = 99
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > maxDisplayCount) "${maxDisplayCount}+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Status Badge for Orders/Items
 * Shows status with appropriate colors
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "pending" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        "confirmed" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        "preparing" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        "ready" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "delivered" -> Color(0xFF2E7D32) to Color.White // Success green
        "cancelled" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Rating Badge with Star
 * Shows rating with star icon
 */
@Composable
fun RatingBadge(
    rating: Float,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2E7D32), // Green color for good ratings
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart, // Replace with star icon
                contentDescription = "Rating",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            
            if (showText) {
                Text(
                    text = rating.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
