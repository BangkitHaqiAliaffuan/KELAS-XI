package com.kelasxi.waveoffood.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Enhanced Bottom Navigation with Material 3 Design
 * Features smooth animations, badge support, and modern styling
 */

data class NavigationItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val badgeCount: Int? = null
)

@Composable
fun EnhancedBottomNavigation(
    items: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                EnhancedBottomNavigationItem(
                    item = item,
                    selected = selectedItem == item.route,
                    onClick = { onItemSelected(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedBottomNavigationItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            .background(
                color = if (selected) {
                    androidx.compose.ui.graphics.Color(0xFF4CAF50) // primary_green_light
                } else {
                    androidx.compose.ui.graphics.Color.Transparent
                },
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with Badge
            Box {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = if (selected) {
                        androidx.compose.ui.graphics.Color.White
                    } else {
                        androidx.compose.ui.graphics.Color(0xFF757575) // gray_600
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
                
                // Badge
                if (item.badgeCount != null && item.badgeCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd),
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFF6B35), // accent_orange
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ) {
                        Text(
                            text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Label
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) {
                    androidx.compose.ui.graphics.Color.White
                } else {
                    androidx.compose.ui.graphics.Color(0xFF757575) // gray_600
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Default navigation items for Wave Of Food app
 */
fun getDefaultNavigationItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(
            route = "home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home"
        ),
        NavigationItem(
            route = "menu",
            selectedIcon = Icons.Filled.Restaurant,
            unselectedIcon = Icons.Outlined.Restaurant,
            label = "Menu"
        ),
        NavigationItem(
            route = "cart",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            label = "Cart",
            badgeCount = 3 // Example badge count
        ),
        NavigationItem(
            route = "profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            label = "Profile"
        )
    )
}
