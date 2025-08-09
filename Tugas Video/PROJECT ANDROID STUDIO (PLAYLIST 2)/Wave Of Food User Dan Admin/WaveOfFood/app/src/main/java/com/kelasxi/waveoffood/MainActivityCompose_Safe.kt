package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme

/**
 * Safe version of MainActivityCompose for testing
 * This is a minimal version to test if basic Compose works
 */
class MainActivityCompose_Safe : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivityCompose_Safe", "🔥 Safe version starting...")

        try {
            setContent {
                WaveOfFoodTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Wave Of Food",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            Text(
                                text = "Main Activity Compose is working!",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "✅ Compose UI loaded successfully",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            Log.d("MainActivityCompose_Safe", "✅ Safe version loaded successfully")
        } catch (e: Exception) {
            Log.e("MainActivityCompose_Safe", "❌ Error in safe version: ${e.message}", e)
        }
    }
}
