 package com.trashbin.app.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trashbin.app.R
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.repository.AuthRepository
import com.trashbin.app.ui.main.MainActivity
import com.trashbin.app.ui.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    
    // Views
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRegister: TextView
    
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createLayout()
        val apiService = RetrofitClient.apiService
        val tokenManager = TokenManager.getInstance()
        val repository = AuthRepository(apiService, tokenManager)
        val factory = com.trashbin.app.ui.viewmodel.AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupObservers()
        setupClickListeners()
    }
    
    private fun createLayout() {
        // Main container
        val mainLayout = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
        
        val contentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(32), dpToPx(48), dpToPx(32), dpToPx(48))
        }
        
        // Logo/Icon placeholder
        val logoContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.VERTICAL
        }
        
        val logo = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(80), dpToPx(80)
            ).apply {
                gravity = Gravity.CENTER
            }
            text = "垃圾桶"
            textSize = 32f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.purple_500))
        }
        
        // Title
        val title = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dpToPx(8)
                bottomMargin = dpToPx(8)
            }
            text = "Welcome Back"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        
        val subtitle = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dpToPx(32)
            }
            text = "Sign in to continue"
            textSize = 16f
            setTextColor(Color.GRAY)
        }
        
        // Email Input
        tilEmail = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            hint = "Email"
            boxStrokeColor = ContextCompat.getColor(this@LoginActivity, R.color.purple_500)
        }
        
        etEmail = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        tilEmail.addView(etEmail)
        
        // Password Input
        tilPassword = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }
            hint = "Password"
            isPasswordVisibilityToggleEnabled = true
            boxStrokeColor = ContextCompat.getColor(this@LoginActivity, R.color.purple_500)
        }
        
        etPassword = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        tilPassword.addView(etPassword)
        
        // Login Button
        btnLogin = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            ).apply {
                bottomMargin = dpToPx(16)
                topMargin = dpToPx(8)
            }
            text = "Sign In"
            textSize = 16f
            isEnabled = true
            setTextColor(Color.WHITE) // Ensure text is white for better contrast
        }
        
        // Set background color using Material Design approach
        val colorStateList = android.content.res.ColorStateList.valueOf(
            ContextCompat.getColor(this@LoginActivity, R.color.purple_500)
        )
        btnLogin.backgroundTintList = colorStateList
        
        // Progress Bar
        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                bottomMargin = dpToPx(16)
            }
            visibility = View.GONE
        }
        
        // Register Link
        val registerContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
                gravity = Gravity.CENTER
            }
            orientation = LinearLayout.HORIZONTAL
        }
        
        val registerText1 = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Don't have an account? "
            textSize = 14f
            setTextColor(Color.GRAY)
        }
        
        tvRegister = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Sign Up"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.purple_500))
            setTypeface(null, Typeface.BOLD)
            isClickable = true
            isFocusable = true
        }
        
        // Add views to containers
        logoContainer.addView(logo)
        registerContainer.addView(registerText1)
        registerContainer.addView(tvRegister)
        
        contentLayout.addView(logoContainer)
        contentLayout.addView(title)
        contentLayout.addView(subtitle)
        contentLayout.addView(tilEmail)
        contentLayout.addView(tilPassword)
        contentLayout.addView(btnLogin)
        contentLayout.addView(progressBar)
        contentLayout.addView(registerContainer)
        
        mainLayout.addView(contentLayout)
        setContentView(mainLayout)
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }



    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            Log.d("LoginActivity", "Loading state changed: $isLoading")
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.RepositoryResult.Success -> {
                    Log.d("LoginActivity", "Login successful, starting redirect")
                    // Login berhasil, redirect ke MainActivity
                    showSuccessMessage("Login successful!") {
                        Log.d("LoginActivity", "Success dialog callback triggered")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                is com.trashbin.app.data.repository.RepositoryResult.Error -> {
                    Log.d("LoginActivity", "Login failed: ${result.message}")
                    // Login gagal, tampilkan error
                    showErrorMessage(result.message ?: "Login failed")
                }
                is com.trashbin.app.data.repository.RepositoryResult.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        Log.d("LoginActivity", "performLogin() called")
        // Clear previous errors
        tilEmail.error = null
        tilPassword.error = null
        
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        Log.d("LoginActivity", "Email: $email, Password length: ${password.length}")

        // Validate inputs
        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            return
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email"
            return
        }

        Log.d("LoginActivity", "Calling viewModel.login()")
        viewModel.login(email, password)
    }

    private fun showSuccessMessage(message: String, onSuccess: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Login Successful")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> 
                dialog.dismiss()
                onSuccess()
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Login Failed")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}