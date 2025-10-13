package com.kelasxi.trashbinfe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.trashbin.app.ui.splash.SplashActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Redirect to the proper SplashActivity
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}

