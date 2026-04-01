package com.kelas.balancebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelas.balancebook.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Kata sandi tidak boleh kosong"
                return@setOnClickListener
            }

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            // TODO: Implement actual authentication logic
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            // TODO: Implement forgot password flow
            Toast.makeText(this, "Fitur lupa kata sandi segera hadir", Toast.LENGTH_SHORT).show()
        }

        binding.btnGoogle.setOnClickListener {
            // TODO: Implement Google Sign-In
            Toast.makeText(this, "Login dengan Google segera hadir", Toast.LENGTH_SHORT).show()
        }
    }
}
