package com.kelasxi.waveoffood.ui.screens.onboarding

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.ui.theme.*
import kotlinx.coroutines.delay

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: List<Color>,
    val illustration: String // Emoji as placeholder for illustrations
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToAuth: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Choose Your Favorite Food",
            description = "Discover thousands of delicious recipes from around the world",
            imageRes = R.drawable.ic_launcher_foreground, // Placeholder
            backgroundColor = listOf(
                Color(0xFFFFF3E0),
                Color(0xFFFFE0B2)
            ),
            illustration = "🍽️"
        ),
        OnboardingPage(
            title = "Fast & Safe Delivery",
            description = "Get your food delivered in 30 minutes or less with our express service",
            imageRes = R.drawable.ic_launcher_foreground, // Placeholder
            backgroundColor = listOf(
                Color(0xFFE8F5E8),
                Color(0xFFC8E6C9)
            ),
            illustration = "🚀"
        ),
        OnboardingPage(
            title = "Easy Payment Method",
            description = "Pay with credit card, PayPal, or cash. Quick and secure checkout",
            imageRes = R.drawable.ic_launcher_foreground, // Placeholder
            backgroundColor = listOf(
                Color(0xFFE3F2FD),
                Color(0xFFBBDEFB)
            ),
            illustration = "💳"
        )
    )
    
    val pagerState = rememberPagerState()
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "content_alpha"
    )
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    pages[pagerState.currentPage].backgroundColor
                )
            )
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top spacing
        Spacer(modifier = Modifier.height(Spacing.xLarge))
        
        // Pager content
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(
                page = pages[page],
                pageIndex = page
            )
        }
        
        // Page indicators
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(Spacing.medium),
            activeColor = OrangePrimary,
            inactiveColor = OrangePrimary.copy(alpha = 0.3f),
            indicatorWidth = 12.dp,
            indicatorHeight = 12.dp,
            spacing = 8.dp
        )
        
        Spacer(modifier = Modifier.height(Spacing.large))
        
        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skip button
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = onNavigateToAuth,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MediumGray
                    )
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            
            // Next/Get Started button
            AnimatedNextButton(
                isLastPage = pagerState.currentPage == pages.size - 1,
                onNextClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Auto scroll to next page (optional)
                    }
                },
                onGetStartedClick = onNavigateToAuth
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.xLarge))
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "page_transition")
    
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000 + (pageIndex * 200),
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration area
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(y = floatingOffset.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(
                        color = PureWhite.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            )
            
            // Main illustration (using emoji for now)
            Text(
                text = page.illustration,
                fontSize = 120.sp,
                modifier = Modifier.offset(y = (-10).dp)
            )
            
            // Floating decorative elements
            repeat(3) { index ->
                val decorativeOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 8000 + (index * 1000),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "decorative_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .offset(
                            x = (80 + index * 20).dp,
                            y = (60 + index * 15).dp
                        )
                        .background(
                            color = OrangePrimary.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xLarge))
        
        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                fontSize = 28.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.medium)
        )
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MediumGray,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.large)
        )
    }
}

@Composable
private fun AnimatedNextButton(
    isLastPage: Boolean,
    onNextClick: () -> Unit,
    onGetStartedClick: () -> Unit
) {
    val buttonText = if (isLastPage) "Get Started" else "Next"
    val buttonWidth by animateIntAsState(
        targetValue = if (isLastPage) 200 else 120,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_width"
    )
    
    Button(
        onClick = if (isLastPage) onGetStartedClick else onNextClick,
        modifier = Modifier
            .width(buttonWidth.dp)
            .height(ComponentSize.buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrangePrimary
        ),
        shape = RoundedCornerShape(CornerRadius.large),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.medium
        )
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            color = PureWhite
        )
    }
}
