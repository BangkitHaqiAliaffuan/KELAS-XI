package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity untuk halaman login pengguna
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Inisialisasi views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        
        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        setupClickListeners()
    }
    
    /**
     * Setup click listeners untuk button dan text view
     */
    private fun setupClickListeners() {
        // OnClickListener untuk tombol Login
        btnLogin.setOnClickListener {
            performLogin()
        }
        
        // OnClickListener untuk TextView Register
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    /**
     * Proses login pengguna
     */
    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validasi input
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }
        
        // Disable tombol login saat proses
        btnLogin.isEnabled = false
        btnLogin.text = "Loading..."
        
        // Proses login menggunakan Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Enable kembali tombol login
                btnLogin.isEnabled = true
                btnLogin.text = "Login"
                
                if (task.isSuccessful) {
                    // Login berhasil
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    
                    // Arahkan ke MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Tutup LoginActivity
                } else {
                    // Login gagal
                    val errorMessage = task.exception?.message ?: "Login gagal"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}
