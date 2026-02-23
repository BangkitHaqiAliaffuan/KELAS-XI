package com.kelasxi.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelasxi.myapplication.ui.navigation.TrashCareNavGraph
import com.kelasxi.myapplication.ui.theme.TrashCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrashCareTheme {
                TrashCareNavGraph()
            }
        }
    }
}
