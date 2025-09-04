package com.kelasxi.waveoffood.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.waveoffood.ui.theme.*

@Composable
fun FoodCard(
    title: String,
    subtitle: String,
    price: String,
    imageUrl: String = "",
    rating: Float = 0f,
    isFavorite: Boolean = false,
    isRecommended: Boolean = false,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    var isFavoriteState by remember { mutableStateOf(isFavorite) }
    
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavoriteState) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favorite_scale"
    )
    
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
    ) {
        Column {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ComponentSize.cardHeightSmall)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangePrimary.copy(alpha = 0.3f),
                                OrangeSecondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                // Placeholder food emoji
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = 48.sp
                    )
                }
                
                // Favorite button
                IconButton(
                    onClick = {
                        isFavoriteState = !isFavoriteState
                        onFavoriteClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.small)
                ) {
                    Icon(
                        imageVector = if (isFavoriteState) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavoriteState) RedAccent else PureWhite,
                        modifier = Modifier
                            .scale(favoriteScale)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    )
                }
                
                // Recommended badge
                if (isRecommended) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(Spacing.small),
                        colors = CardDefaults.cardColors(containerColor = RedAccent),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Recommended",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PureWhite,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Content Section
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MediumGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (rating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingDisplay(rating = rating, size = 12.dp)
                }
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceTag(price = price)
                    
                    // Add button
                    Card(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onAddClick() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = OrangePrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to cart",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    icon: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .size(60.dp)
                .scale(animatedScale),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) OrangePrimary else PureWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) Elevation.medium else Elevation.small
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.small))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) OrangePrimary else MediumGray
            )
        )
    }
}

@Composable
fun RatingDisplay(
    rating: Float,
    size: androidx.compose.ui.unit.Dp = 16.dp,
    maxRating: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Rating star",
                tint = YellowRating,
                modifier = Modifier.size(size)
            )
        }
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                color = DarkGray,
                fontWeight = FontWeight.Medium,
                fontSize = (size.value * 0.8).sp
            )
        )
    }
}

@Composable
fun PriceTag(
    price: String,
    originalPrice: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (originalPrice != null) {
            Text(
                text = originalPrice,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MediumGray,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        
        Text(
            text = price,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
        )
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 0
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = modifier
    ) {
        // Decrease button
        IconButton(
            onClick = { 
                if (quantity > minQuantity) onQuantityChange(quantity - 1) 
            },
            enabled = quantity > minQuantity,
            modifier = Modifier
                .size(ComponentSize.iconSize)
                .background(
                    color = if (quantity > minQuantity) OrangePrimary else MediumGray.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = PureWhite,
                modifier = Modifier.size(16.dp)
            )
        }
        
        // Quantity display
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )
        )
        
        // Increase button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier
                .size(ComponentSize.iconSize)
                .background(
                    color = OrangePrimary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = PureWhite,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    colors: List<Color> = listOf(OrangePrimary, OrangeSecondary)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(ComponentSize.buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled && !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(colors)
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                MediumGray.copy(alpha = 0.5f),
                                MediumGray.copy(alpha = 0.3f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = PureWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.small))
                    }
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = PureWhite
                    )
                }
            }
        }
    }
}
