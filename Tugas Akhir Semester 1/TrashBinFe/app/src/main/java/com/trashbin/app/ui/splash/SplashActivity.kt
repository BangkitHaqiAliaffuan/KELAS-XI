package com.trashbin.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.ui.auth.LoginActivity
import com.trashbin.app.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide action bar for full screen
        supportActionBar?.hide()
        
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (TokenManager.isLoggedIn()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            
            startActivity(intent)
            finish()
        }, 2000) // 2 seconds delay
    }
}