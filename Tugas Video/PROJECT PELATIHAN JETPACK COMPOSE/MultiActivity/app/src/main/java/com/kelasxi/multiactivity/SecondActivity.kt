package com.kelasxi.multiactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kelasxi.multiactivity.ui.theme.MultiActivityTheme


class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiActivityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SecondScreen(modifier = Modifier.padding(innerPadding)) {
                        // finish activity when Back button clicked
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SecondScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(modifier = modifier) {
        Text(text = "This is SecondActivity", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondPreview() {
    MultiActivityTheme {
        SecondScreen(onBack = { /* do nothing in preview */ })
    }
}
