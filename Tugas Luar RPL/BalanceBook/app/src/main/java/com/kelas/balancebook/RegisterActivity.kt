package com.kelas.balancebook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelas.balancebook.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty()) {
                binding.tilName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Kata sandi tidak boleh kosong"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Kata sandi tidak cocok"
                return@setOnClickListener
            }

            binding.tilName.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            // TODO: Implement actual registration logic
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}
