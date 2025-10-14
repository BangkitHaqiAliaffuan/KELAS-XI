package com.trashbin.app.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
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
import com.trashbin.app.ui.main.MainActivity
import com.trashbin.app.ui.viewmodel.AuthViewModel
import org.json.JSONObject
import org.json.JSONException

class RegisterActivity : AppCompatActivity() {
    
    // Views
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLogin: TextView
    
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createLayout()
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

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
            setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.purple_500))
        }
        
        logoContainer.addView(logo)
        
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
            text = "Create Account"
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
            text = "Sign up to continue"
            textSize = 16f
            setTextColor(Color.GRAY)
        }
        
        // Name Input
        tilName = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            hint = "Full Name"
            boxStrokeColor = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        }
        
        etName = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        }
        tilName.addView(etName)
        
        // Email Input
        tilEmail = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            hint = "Email"
            boxStrokeColor = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        }
        
        etEmail = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        tilEmail.addView(etEmail)
        
        // Phone Input
        tilPhone = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            hint = "Phone Number (+62)"
            boxStrokeColor = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        }
        
        etPhone = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_CLASS_PHONE
        }
        tilPhone.addView(etPhone)
        
        // Password Input
        tilPassword = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            hint = "Password"
            isPasswordVisibilityToggleEnabled = true
            boxStrokeColor = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        }
        
        etPassword = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        tilPassword.addView(etPassword)
        
        // Confirm Password Input
        tilConfirmPassword = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }
            hint = "Confirm Password"
            isPasswordVisibilityToggleEnabled = true
            boxStrokeColor = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        }
        
        etConfirmPassword = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        tilConfirmPassword.addView(etConfirmPassword)
        
        // Register Button
        btnRegister = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            ).apply {
                bottomMargin = dpToPx(16)
                topMargin = dpToPx(8)
            }
            text = "Create Account"
            textSize = 16f
            isEnabled = true
            setTextColor(Color.WHITE) // Ensure text is white for better contrast
        }
        
        // Set background color using Material Design approach
        val colorStateList = android.content.res.ColorStateList.valueOf(
            ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
        )
        btnRegister.backgroundTintList = colorStateList
        
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
        
        // Login Link
        val loginContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
                gravity = Gravity.CENTER
            }
            orientation = LinearLayout.HORIZONTAL
        }
        
        val loginText1 = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Already have an account? "
            textSize = 14f
            setTextColor(Color.GRAY)
        }
        
        tvLogin = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Sign In"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.purple_500))
            setTypeface(null, Typeface.BOLD)
            isClickable = true
            isFocusable = true
        }
        
        // Add views to containers
        loginContainer.addView(loginText1)
        loginContainer.addView(tvLogin)
        
        contentLayout.addView(logoContainer)
        contentLayout.addView(title)
        contentLayout.addView(subtitle)
        contentLayout.addView(tilName)
        contentLayout.addView(tilEmail)
        contentLayout.addView(tilPhone)
        contentLayout.addView(tilPassword)
        contentLayout.addView(tilConfirmPassword)
        contentLayout.addView(btnRegister)
        contentLayout.addView(progressBar)
        contentLayout.addView(loginContainer)
        
        mainLayout.addView(contentLayout)
        setContentView(mainLayout)
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setupObservers() {
        viewModel.registerState.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.Result.Loading -> {
                    btnRegister.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                is com.trashbin.app.data.repository.Result.Success -> {
                    btnRegister.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // After successful registration, user is already authenticated
                    // Redirect to main activity instead of login
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is com.trashbin.app.data.repository.Result.Error -> {
                    btnRegister.isEnabled = true
                    progressBar.visibility = View.GONE
                    
                    // Parse validation errors dari Laravel
                    parseAndShowValidationErrors(result.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            performRegister()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegister() {
        // Clear previous errors
        tilName.error = null
        tilEmail.error = null
        tilPhone.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validate inputs sesuai dengan backend Laravel
        var hasError = false

        if (name.isEmpty()) {
            tilName.error = "Name is required"
            hasError = true
        }

        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email"
            hasError = true
        }

        if (phone.isEmpty()) {
            tilPhone.error = "Phone number is required"
            hasError = true
        } else if (!isValidPhoneNumber(phone)) {
            tilPhone.error = "Please enter a valid phone number"
            hasError = true
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            hasError = true
        } else if (password.length < 8) {
            tilPassword.error = "Password must be at least 8 characters"
            hasError = true
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Password confirmation is required"
            hasError = true
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Password confirmation does not match"
            hasError = true
        }

        if (hasError) {
            return
        }

        // Format phone number untuk Indonesia
        val formattedPhone = formatPhoneNumber(phone)
        
        viewModel.register(name, email, formattedPhone, password, confirmPassword, "user", 0.0, 0.0)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Validasi nomor telepon Indonesia
        val phonePattern = "^(\\+?62|0)[0-9]{8,13}$"
        return phone.matches(phonePattern.toRegex())
    }

    private fun formatPhoneNumber(phone: String): String {
        // Format nomor telepon ke format internasional (+62)
        return when {
            phone.startsWith("0") -> "+62${phone.substring(1)}"
            phone.startsWith("62") && !phone.startsWith("+62") -> "+$phone"
            phone.startsWith("+62") -> phone
            else -> "+62$phone"
        }
    }

    private fun parseAndShowValidationErrors(errorMessage: String) {
        try {
            // Coba parse error JSON dari Laravel
            val jsonObject = JSONObject(errorMessage)
            
            if (jsonObject.has("errors")) {
                val errors = jsonObject.getJSONObject("errors")
                
                // Clear previous errors
                tilName.error = null
                tilEmail.error = null
                tilPhone.error = null
                tilPassword.error = null
                tilConfirmPassword.error = null
                
                // Show field-specific errors
                if (errors.has("name")) {
                    val nameErrors = errors.getJSONArray("name")
                    tilName.error = nameErrors.getString(0)
                }
                
                if (errors.has("email")) {
                    val emailErrors = errors.getJSONArray("email")
                    tilEmail.error = emailErrors.getString(0)
                }
                
                if (errors.has("phone")) {
                    val phoneErrors = errors.getJSONArray("phone")
                    tilPhone.error = phoneErrors.getString(0)
                }
                
                if (errors.has("password")) {
                    val passwordErrors = errors.getJSONArray("password")
                    tilPassword.error = passwordErrors.getString(0)
                }
                
                // Show general error message
                val message = jsonObject.optString("message", "Validation failed")
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                
            } else {
                // Fallback ke error message biasa
                showErrorMessage(errorMessage)
            }
            
        } catch (e: JSONException) {
            // Jika bukan JSON, tampilkan error message biasa
            showErrorMessage(errorMessage)
        }
    }
    
    private fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Registration Failed")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}