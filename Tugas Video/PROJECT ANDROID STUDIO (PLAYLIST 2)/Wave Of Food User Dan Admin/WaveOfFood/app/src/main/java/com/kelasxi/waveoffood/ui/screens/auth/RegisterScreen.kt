package com.kelasxi.waveoffood.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.waveoffood.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "screen_alpha"
    )
    
    // Password strength calculation
    val passwordStrength = calculatePasswordStrength(password)
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
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
                .verticalScroll(scrollState)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.large))
            
            // App Logo
            Card(
                modifier = Modifier.size(70.dp),
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
                        fontSize = 32.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            // Welcome Text
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    fontSize = 26.sp
                )
            )
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MediumGray,
                    fontSize = 16.sp
                )
            )
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Name Input
            AnimatedTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Enter your full name",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = OrangePrimary
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
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
            
            // Phone Input
            AnimatedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                placeholder = "Enter your phone number",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = OrangePrimary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            // Password Input
            Column {
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
                
                // Password Strength Indicator
                if (password.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.small))
                    PasswordStrengthIndicator(strength = passwordStrength)
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            // Confirm Password Input
            AnimatedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                placeholder = "Confirm your password",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password",
                        tint = OrangePrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility 
                                         else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" 
                                               else "Show password",
                            tint = MediumGray
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None 
                                     else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPassword.isNotEmpty() && password != confirmPassword
            )
            
            // Password Match Indicator
            if (confirmPassword.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (password == confirmPassword) Icons.Default.Check 
                                     else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (password == confirmPassword) GreenSuccess else ErrorColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = if (password == confirmPassword) "Passwords match" 
                               else "Passwords don't match",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (password == confirmPassword) GreenSuccess else ErrorColor
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            // Terms and Conditions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = OrangePrimary,
                        uncheckedColor = MediumGray
                    )
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "I agree to the ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MediumGray
                    )
                )
                Text(
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OrangePrimary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { /* Navigate to terms */ }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Register Button
            AnimatedGradientButton(
                text = "Create Account",
                isLoading = isLoading,
                enabled = name.isNotEmpty() && email.isNotEmpty() && 
                         password.isNotEmpty() && password == confirmPassword && 
                         agreeToTerms,
                onClick = {
                    isLoading = true
                    // Simulate API call
                    onNavigateToHome()
                }
            )
            
            Spacer(modifier = Modifier.height(Spacing.xLarge))
            
            // Sign In Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MediumGray
                    )
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(
    strength: PasswordStrength
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password strength: ",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MediumGray
                )
            )
            Text(
                text = strength.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = strength.color,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = strength.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = strength.color,
            trackColor = strength.color.copy(alpha = 0.2f)
        )
    }
}

private data class PasswordStrength(
    val label: String,
    val progress: Float,
    val color: Color
)

private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.length < 4) {
        return PasswordStrength("Weak", 0.25f, ErrorColor)
    }
    
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    
    return when (score) {
        0, 1 -> PasswordStrength("Weak", 0.25f, ErrorColor)
        2 -> PasswordStrength("Fair", 0.5f, WarningColor)
        3 -> PasswordStrength("Good", 0.75f, BlueInfo)
        else -> PasswordStrength("Strong", 1.0f, GreenSuccess)
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = DividerColor,
            focusedLabelColor = OrangePrimary,
            unfocusedLabelColor = MediumGray,
            cursorColor = OrangePrimary,
            errorBorderColor = ErrorColor,
            errorLabelColor = ErrorColor
        ),
        singleLine = true
    )
}

@Composable
private fun AnimatedGradientButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight),
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
                        Brush.horizontalGradient(
                            colors = listOf(OrangePrimary, OrangeSecondary)
                        )
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
