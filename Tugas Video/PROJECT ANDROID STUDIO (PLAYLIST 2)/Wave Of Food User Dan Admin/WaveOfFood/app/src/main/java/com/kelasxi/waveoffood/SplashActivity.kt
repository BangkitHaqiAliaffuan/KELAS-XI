package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Splash Screen yang akan tampil selama 2 detik
 * Cek status login pengguna menggunakan Firebase Auth
 */
class SplashActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Penundaan 2 detik menggunakan Handler
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }
    
    /**
     * Cek status login pengguna
     * Jika sudah login, arahkan ke MainActivity
     * Jika belum, arahkan ke LoginActivity
     */
    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // Pengguna sudah login, arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Pengguna belum login, arahkan ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        finish() // Tutup SplashActivity
    }
}
