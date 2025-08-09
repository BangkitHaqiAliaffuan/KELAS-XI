package com.kelasxi.waveoffood.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Category Card with Material 3 Design
 * Clean, modern category display with consistent styling
 */
@Composable
fun CategoryCard(
    category: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .width(110.dp)
            .height(110.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Category Icon/Image with better styling
            Surface(
                modifier = Modifier
                    .size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(getCategoryIcon(category))
                            .crossfade(true)
                            .build(),
                        contentDescription = category,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Category Name with better typography
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Get category icon URL based on category name
 * Replace this with your actual category icon logic
 */
private fun getCategoryIcon(category: String): String {
    return when (category.lowercase()) {
        "pizza" -> "https://picsum.photos/100/100?random=1"
        "burger" -> "https://picsum.photos/100/100?random=2"
        "pasta" -> "https://picsum.photos/100/100?random=3"
        "salad" -> "https://picsum.photos/100/100?random=4"
        "dessert" -> "https://picsum.photos/100/100?random=5"
        "drinks" -> "https://picsum.photos/100/100?random=6"
        else -> "https://picsum.photos/100/100?random=7"
    }
}
