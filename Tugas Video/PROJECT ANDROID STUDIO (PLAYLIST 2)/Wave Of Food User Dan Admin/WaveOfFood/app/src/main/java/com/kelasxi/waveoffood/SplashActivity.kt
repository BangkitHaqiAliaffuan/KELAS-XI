package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import kotlinx.coroutines.delay

/**
 * Creative Animated Splash Screen with Material 3 Design
 * Features wave animations, floating elements, and smooth transitions
 */
class SplashActivity : ComponentActivity() {
    
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        setContent {
            WaveOfFoodTheme {
                CreativeSplashScreen(
                    onAnimationComplete = { checkUserStatus() }
                )
            }
        }
    }
    
    /**
     * Check user login status and navigate accordingly
     */
    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        
        val intent = if (currentUser != null) {
            Intent(this, MainActivityCompose::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}

@Composable
private fun CreativeSplashScreen(
    onAnimationComplete: () -> Unit
) {
    // Animation states
    var animationState by remember { mutableStateOf(0) }
    
    // Logo animation
    val logoScale by animateFloatAsState(
        targetValue = if (animationState >= 1) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    // Wave rotation animation
    val waveRotation by animateFloatAsState(
        targetValue = if (animationState >= 2) 360f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "waveRotation"
    )
    
    // Text fade animation
    val textAlpha by animateFloatAsState(
        targetValue = if (animationState >= 3) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearOutSlowInEasing
        ),
        label = "textAlpha"
    )
    
    // Background gradient animation
    val gradientOffset by animateFloatAsState(
        targetValue = if (animationState >= 4) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseInOutCubic
        ),
        label = "gradientOffset"
    )
    
    // Start animation sequence
    LaunchedEffect(Unit) {
        delay(300)
        animationState = 1 // Logo appears
        delay(500)
        animationState = 2 // Wave starts rotating
        delay(800)
        animationState = 3 // Text fades in
        delay(600)
        animationState = 4 // Background completes
        delay(1200)
        onAnimationComplete()
    }
    
    // Dynamic gradient background
    val dynamicGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f + gradientOffset * 0.2f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f + gradientOffset * 0.15f),
            MaterialTheme.colorScheme.surface
        ),
        startY = 0f,
        endY = 1000f * (1f + gradientOffset * 0.5f)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = dynamicGradient),
        contentAlignment = Alignment.Center
    ) {
        // Floating decorative circles
        FloatingCircles(animationOffset = gradientOffset)
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Wave decoration behind logo
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated wave background
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(waveRotation)
                        .scale(logoScale),
                    shape = RoundedCornerShape(30.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shadowElevation = 8.dp
                ) {}
                
                // Secondary wave
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .rotate(-waveRotation * 0.7f)
                        .scale(logoScale),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {}
                
                // Logo container
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(logoScale),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 12.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        // You can replace this with your app logo
                        Text(
                            text = "🌊",
                            fontSize = 40.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App title with creative typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha)
            ) {
                Text(
                    text = "Wave Of Food",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Delicious food, delivered with care",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Animated loading indicator
                PulsingLoadingIndicator(isVisible = animationState >= 3)
            }
        }
    }
}

@Composable
private fun FloatingCircles(animationOffset: Float) {
    // Multiple floating circles with different animations
    repeat(6) { index ->
        val delay = index * 200f
        val size = (20 + index * 8).dp
        val xOffset = (index - 3) * 80f
        val yOffset = (index % 3 - 1) * 150f
        
        val floatAnimation by animateFloatAsState(
            targetValue = if (animationOffset > 0) 10f else 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 500,
                    delayMillis = delay.toInt(),
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float$index"
        )
        
        Surface(
            modifier = Modifier
                .size(size)
                .offset(
                    x = xOffset.dp,
                    y = (yOffset + floatAnimation).dp
                )
                .alpha(0.3f * animationOffset),
            shape = CircleShape,
            color = if (index % 2 == 0) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else 
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        ) {}
    }
}

@Composable
private fun PulsingLoadingIndicator(isVisible: Boolean) {
    val pulseScale by animateFloatAsState(
        targetValue = if (isVisible) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    if (isVisible) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Surface(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(if (index == 1) pulseScale else 1f),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (index == 1) 1f else 0.6f
                    )
                ) {}
            }
        }
    }
}
