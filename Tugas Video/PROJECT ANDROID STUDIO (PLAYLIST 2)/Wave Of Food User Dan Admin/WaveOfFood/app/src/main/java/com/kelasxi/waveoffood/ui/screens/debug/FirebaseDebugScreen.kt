package com.kelasxi.waveoffood.ui.screens.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.utils.FirebaseConfigChecker

/**
 * Debug screen untuk mengecek konfigurasi Firebase
 * Hanya untuk development, hapus di production
 */
@Composable
fun FirebaseDebugScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var firebaseAuthStatus by remember { mutableStateOf("Checking...") }
    var firestoreStatus by remember { mutableStateOf("Checking...") }
    var currentUser by remember { mutableStateOf<String?>(null) }
    var googleServicesStatus by remember { mutableStateOf("Checking...") }
    
    LaunchedEffect(Unit) {
        try {
            // Check Firebase Auth
            val auth = FirebaseAuth.getInstance()
            firebaseAuthStatus = if (auth.app != null) {
                "✅ Connected (App: ${auth.app.name})"
            } else {
                "❌ Not initialized"
            }
            
            // Check current user
            currentUser = auth.currentUser?.let { user ->
                "User ID: ${user.uid}\nEmail: ${user.email}\nEmail Verified: ${user.isEmailVerified}"
            } ?: "No user logged in"
            
            // Check Firestore
            val firestore = FirebaseFirestore.getInstance()
            firestoreStatus = if (firestore.app != null) {
                "✅ Connected (App: ${firestore.app.name})"
            } else {
                "❌ Not initialized"
            }
            
            // Check google-services.json
            googleServicesStatus = try {
                val appId = auth.app.options.applicationId
                if (appId.isNotEmpty()) {
                    "✅ google-services.json loaded\nApp ID: $appId"
                } else {
                    "❌ google-services.json not found"
                }
            } catch (e: Exception) {
                "❌ Error: ${e.message}"
            }
            
        } catch (e: Exception) {
            firebaseAuthStatus = "❌ Error: ${e.message}"
            firestoreStatus = "❌ Error: ${e.message}"
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Firebase Configuration Debug",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "This screen helps debug Firebase integration issues.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Firebase Auth Status
        StatusCard(
            title = "Firebase Authentication",
            status = firebaseAuthStatus,
            isSuccess = firebaseAuthStatus.contains("✅")
        )
        
        // Firestore Status
        StatusCard(
            title = "Cloud Firestore",
            status = firestoreStatus,
            isSuccess = firestoreStatus.contains("✅")
        )
        
        // Google Services Status
        StatusCard(
            title = "Google Services Configuration",
            status = googleServicesStatus,
            isSuccess = googleServicesStatus.contains("✅")
        )
        
        // Current User
        StatusCard(
            title = "Current User",
            status = currentUser ?: "Loading...",
            isSuccess = currentUser?.contains("User ID") == true
        )
        
        // Test Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Quick Tests",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val result = FirebaseConfigChecker.checkFirebaseConfiguration()
                        firebaseAuthStatus = if (result) "✅ Test passed" else "❌ Test failed"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Firebase")
                }
                
                OutlinedButton(
                    onClick = { onBack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
            }
        }
        
        // Troubleshooting Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Troubleshooting Tips",
                    fontWeight = FontWeight.Bold
                )
                
                val tips = listOf(
                    "1. Make sure google-services.json is in app/ folder",
                    "2. Check if Firebase plugin is applied in build.gradle.kts",
                    "3. Verify internet connection",
                    "4. Ensure Firebase project is properly configured",
                    "5. Check if Authentication and Firestore are enabled in Firebase Console"
                )
                
                tips.forEach { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    status: String,
    isSuccess: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) 
                Color.Green.copy(alpha = 0.1f) 
            else 
                Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isSuccess) Color.Green else Color.Red,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
