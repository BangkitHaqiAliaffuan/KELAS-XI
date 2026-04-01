package com.kelasxi.myapplication.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────── SPLASH SCREEN ───────────────────────────────

@Composable
fun SplashScreen(onSplashDone: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Bounce-in animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alpha.animateTo(1f, animationSpec = tween(500))
        textAlpha.animateTo(1f, animationSpec = tween(600))
        delay(1800)
        onSplashDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenDeep, GreenMedium, GreenLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = 100.dp, y = (-120).dp)
                .alpha(0.15f)
                .background(Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-80).dp, y = 150.dp)
                .alpha(0.1f)
                .background(Color.White, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon with scale animation
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("♻️", fontSize = 64.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text = "TrashCare",
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🌍 Bersama Jaga Kebersihan Bumi",
                modifier = Modifier.alpha(textAlpha.value),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .size(32.dp),
                color = Color.White.copy(alpha = 0.7f),
                strokeWidth = 2.dp
            )
        }

        // Bottom tagline
        Text(
            text = "v1.0.0",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(textAlpha.value),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

// ─────────────────── ONBOARDING SCREEN ───────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pages = MockData.onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Skip button top right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(visible = !isLastPage) {
                    TextButton(onClick = onGetStarted) {
                        Text(
                            "Lewati",
                            color = GreenDeep,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    onboardingPage = pages[page],
                    pageIndex = page
                )
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == pagerState.currentPage
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .size(width, 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GreenDeep else GreenPale)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Next / Get Started Button
                Button(
                    onClick = {
                        if (isLastPage) {
                            onGetStarted()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GreenDeep, GreenMedium, GreenLight)
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = isLastPage,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "buttonLabel"
                        ) { last ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (last) {
                                    Text("🚀", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Mulai Sekarang!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        "Selanjutnya",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    onboardingPage: com.kelasxi.myapplication.model.OnboardingPage,
    pageIndex: Int
) {
    val gradients = listOf(
        listOf(GreenDeep.copy(alpha = 0.08f), GreenPale.copy(alpha = 0.3f)),
        listOf(GreenMedium.copy(alpha = 0.08f), GreenLighter.copy(alpha = 0.25f)),
        listOf(GreenLight.copy(alpha = 0.08f), GreenPale.copy(alpha = 0.2f))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(
                    brush = Brush.radialGradient(gradients[pageIndex]),
                    shape = CircleShape
                )
                .border(2.dp, GreenPale, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner circle decoration
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        GreenPale.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(onboardingPage.emoji, fontSize = 80.sp)
            }

            // Decorative small circles around
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = 70.dp, y = (-70).dp)
                    .background(GreenLight.copy(alpha = 0.4f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = (-75).dp, y = 50.dp)
                    .background(GreenMedium.copy(alpha = 0.3f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .offset(x = 60.dp, y = 60.dp)
                    .background(GreenPale, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = onboardingPage.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = onboardingPage.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

@Preview(showBackground = true, name = "Splash Screen")
@Composable
fun SplashScreenPreview() {
    TrashCareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GreenDeep, GreenMedium, GreenLight)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("♻️", fontSize = 64.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "TrashCare",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    "🌍 Bersama Jaga Kebersihan Bumi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Onboarding Screen")
@Composable
fun OnboardingScreenPreview() {
    TrashCareTheme {
        OnboardingScreen(onGetStarted = {})
    }
}
