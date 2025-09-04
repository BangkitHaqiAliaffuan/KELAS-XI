package com.kelasxi.waveoffood.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.waveoffood.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_transition")
    
    // Logo animations
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )
    
    val textOffsetY by animateFloatAsState(
        targetValue = if (textVisible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "text_offset"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 500),
        label = "text_alpha"
    )
    
    // Background gradient rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_rotation"
    )
    
    // Trigger animations
    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        textVisible = true
        delay(2000)
        onNavigateToOnboarding()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        OrangePrimary,
                        OrangeSecondary,
                        Color(0xFFFFB74D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles background effect
        repeat(5) { index ->
            val particleOffset by infiniteTransition.animateFloat(
                initialValue = -50f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (index * 500),
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particle_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(
                        x = (index * 60 - 120).dp + particleOffset.dp,
                        y = (index * 40 - 80).dp
                    )
                    .alpha(0.3f)
                    .background(
                        color = PureWhite,
                        shape = CircleShape
                    )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo (using text for now - you can replace with actual logo)
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = PureWhite.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍕",
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // App Name
            Text(
                text = "WaveOfFood",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                ),
                modifier = Modifier
                    .offset(y = textOffsetY.dp)
                    .alpha(textAlpha)
            )
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            // Tagline
            Text(
                text = "Delicious Food Delivered",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PureWhite.copy(alpha = 0.9f),
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .offset(y = textOffsetY.dp)
                    .alpha(textAlpha)
            )
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .alpha(textAlpha),
                color = PureWhite,
                strokeWidth = 3.dp
            )
        }
    }
}
