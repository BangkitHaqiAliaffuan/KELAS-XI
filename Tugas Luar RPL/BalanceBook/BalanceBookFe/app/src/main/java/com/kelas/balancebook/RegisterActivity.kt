package com.kelas.balancebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kelas.balancebook.data.local.SessionManager
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.RegisterRequest
import com.kelas.balancebook.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

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

            register(name, email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun register(name: String, email: String, password: String, confirmPassword: String) {
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            runCatching {
                ApiClient.service(this@RegisterActivity).register(
                    RegisterRequest(
                        name = name,
                        email = email,
                        password = password,
                        passwordConfirmation = confirmPassword
                    )
                )
            }.onSuccess { response ->
                SessionManager.saveSession(this@RegisterActivity, response.token, response.user)
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }.onFailure {
                Toast.makeText(
                    this@RegisterActivity,
                    it.message ?: "Registrasi gagal, silakan coba lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.btnRegister.isEnabled = true
        }
    }
}
