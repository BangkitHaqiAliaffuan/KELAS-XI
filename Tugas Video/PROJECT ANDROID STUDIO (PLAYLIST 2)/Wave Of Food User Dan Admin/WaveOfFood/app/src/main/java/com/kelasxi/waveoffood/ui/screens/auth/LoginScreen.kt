package com.kelasxi.waveoffood.ui.screens.auth

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.waveoffood.ui.theme.*
import com.kelasxi.waveoffood.ui.viewmodel.AuthViewModel
import com.kelasxi.waveoffood.ui.viewmodel.AuthViewModelFactory
import com.kelasxi.waveoffood.ui.viewmodel.GoogleSignInViewModel
import com.kelasxi.waveoffood.ui.viewmodel.GoogleSignInViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val googleSignInViewModel: GoogleSignInViewModel = viewModel(
        factory = GoogleSignInViewModelFactory(context)
    )
    
    val uiState by authViewModel.uiState.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showGoogleSetupDialog by remember { mutableStateOf(false) }
    
    // Observe login success - Priority utama
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            // Clear state terlebih dahulu
            authViewModel.clearSuccessStates()
            // Navigate ke home
            onNavigateToHome()
        }
    }
    
    // Observe auto login success
    LaunchedEffect(uiState.isAutoLoginSuccess) {
        if (uiState.isAutoLoginSuccess == true) {
            // Auto login berhasil, langsung ke home
            onNavigateToHome()
        }
    }
    
    // Observe login state from persistent storage
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && !uiState.isLoading && uiState.isAutoLoginSuccess != false) {
            onNavigateToHome()
        }
    }
    
    // Observe error
    LaunchedEffect(uiState.errorMessage) {
        errorMessage = uiState.errorMessage
    }
    
    // Observe Google Sign-In error
    LaunchedEffect(googleSignInViewModel.errorMessage) {
        if (googleSignInViewModel.errorMessage != null) {
            errorMessage = googleSignInViewModel.errorMessage
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "screen_alpha"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
        
        // Debug logging
        try {
            val prefsManager = com.kelasxi.waveoffood.utils.PersistentLoginManager.getUserPreferencesManager(context)
            val isLoggedInLocal = prefsManager.isLoggedIn.first()
            val rememberLoginLocal = prefsManager.rememberLogin.first()
            Log.d("LoginScreen", "App started - isLoggedIn: $isLoggedInLocal, rememberLogin: $rememberLoginLocal")
        } catch (e: Exception) {
            Log.e("LoginScreen", "Error checking login status: ${e.message}")
        }
    }
    
    // Show loading during auto login check
    if (uiState.isAutoLoginSuccess == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LightGray,
                            PureWhite,
                            LightGray.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = OrangePrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Checking login status...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumGray
                )
            }
        }
        return
    }
    
    // Auto login berhasil, navigasi ke home (handled by LaunchedEffect above)
    if (uiState.isAutoLoginSuccess == true) {
        // LaunchedEffect akan handle navigasi
        return
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightGray,
                        PureWhite,
                        LightGray.copy(alpha = 0.5f)
                    )
                )
            )
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // App Logo
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(CornerRadius.large),
                colors = CardDefaults.cardColors(
                    containerColor = OrangePrimary.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍕",
                        fontSize = 36.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Welcome Text
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    fontSize = 28.sp
                )
            )
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MediumGray,
                    fontSize = 16.sp
                )
            )
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Email Input
            AnimatedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = OrangePrimary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            // Password Input
            AnimatedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = OrangePrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility 
                                         else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" 
                                               else "Show password",
                            tint = MediumGray
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None 
                                     else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            
            // Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.small),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OrangePrimary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Login Button
            AnimatedGradientButton(
                text = "Sign In",
                isLoading = uiState.isLoading,
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Clear previous error
                        errorMessage = null
                        authViewModel.clearError()
                        
                        // Perform Firebase authentication
                        authViewModel.signIn(email.trim(), password, rememberLogin = true)
                    } else {
                        errorMessage = "Please enter both email and password"
                    }
                }
            )
            
            // Error Message Display
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(Spacing.medium))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = DividerColor
                )
                Text(
                    text = "  Or sign in with  ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MediumGray
                    )
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = DividerColor
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            // Social Login Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                SocialLoginButton(
                    text = "Google",
                    icon = "🔍",
                    backgroundColor = Color(0xFFDB4437),
                    modifier = Modifier.weight(1f),
                    isLoading = googleSignInViewModel.isLoading,
                    onClick = { 
                        errorMessage = null
                        googleSignInViewModel.signInWithGoogle(
                            onSuccess = { onNavigateToHome() },
                            onFailure = { error -> 
                                errorMessage = error
                                // Show setup dialog if no credentials available
                                if (error.contains("No credentials available") || 
                                    error.contains("Tidak ada akun Google")) {
                                    showGoogleSetupDialog = true
                                }
                            }
                        )
                    }
                )
                
                SocialLoginButton(
                    text = "Facebook",
                    icon = "📘",
                    backgroundColor = Color(0xFF4267B2),
                    modifier = Modifier.weight(1f),
                    isLoading = false,
                    onClick = { /* Handle Facebook login */ }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Sign Up Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MediumGray
                    )
                )
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
        }
    }
}

@Composable
private fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isFocused by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = DividerColor,
            focusedLabelColor = OrangePrimary,
            unfocusedLabelColor = MediumGray,
            cursorColor = OrangePrimary
        ),
        singleLine = true
    )
}

@Composable
private fun AnimatedGradientButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight)
            .clip(RoundedCornerShape(CornerRadius.medium)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(OrangePrimary, OrangeSecondary)
                    )
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

@Composable
private fun SocialLoginButton(
    text: String,
    icon: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(ComponentSize.buttonHeightMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(CornerRadius.medium),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.small
        ),
        enabled = !isLoading
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PureWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = icon,
                    fontSize = 16.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
    
    // Google Setup Dialog
    if (showGoogleSetupDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleSetupDialog = false },
            title = {
                Text(
                    text = "Setup Google Sign-In",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Untuk mengaktifkan Google Sign-In, ikuti langkah berikut:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "1. Buka Google Cloud Console\n" +
                              "2. Tambahkan SHA-1 fingerprint:\n" +
                              "   C1:D9:47:2D:B0:60:56:24:12:0A:39:13:08:95:4B:36:68:FB:31:AC\n" +
                              "3. Aktifkan Google Sign-In API\n" +
                              "4. Download google-services.json terbaru\n" +
                              "5. Pastikan Google Play Services terinstall",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showGoogleSetupDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
