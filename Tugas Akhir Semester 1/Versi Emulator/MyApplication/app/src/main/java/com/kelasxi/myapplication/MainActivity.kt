package com.kelasxi.myapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelasxi.myapplication.ui.navigation.TrashCareNavGraph
import com.kelasxi.myapplication.ui.theme.TrashCareTheme
import com.kelasxi.myapplication.util.LanguageManager

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Apply saved locale every time the Activity is (re-)created so that
        // string resources resolve in the correct language after a language switch.
        super.attachBaseContext(LanguageManager.applyLocale(newBase))
    }

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

