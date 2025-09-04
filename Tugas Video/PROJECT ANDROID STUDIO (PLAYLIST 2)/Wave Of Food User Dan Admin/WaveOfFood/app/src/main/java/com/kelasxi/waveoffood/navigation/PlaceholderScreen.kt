package com.kelasxi.waveoffood.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kelasxi.waveoffood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkGray
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PureWhite,
                titleContentColor = DarkGray
            )
        )
        
        // Content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚧",
                    style = MaterialTheme.typography.displayLarge
                )
                
                Spacer(modifier = Modifier.height(Spacing.medium))
                
                Text(
                    text = "Coming Soon!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MediumGray
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.large)
                )
                
                Spacer(modifier = Modifier.height(Spacing.xLarge))
                
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(ComponentSize.buttonHeightMedium)
                ) {
                    Text(
                        text = "Go Back",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                    )
                }
            }
        }
    }
}
