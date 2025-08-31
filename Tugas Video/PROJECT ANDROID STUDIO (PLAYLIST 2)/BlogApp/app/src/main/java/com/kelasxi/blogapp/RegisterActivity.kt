package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

/**
 * Activity untuk halaman pendaftaran pengguna baru
 * Menangani proses registrasi dengan validasi
 */
class RegisterActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBackToLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI components
        initViews()
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
    }

    /**
     * Setup click listeners untuk buttons
     */
    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            // TODO: Implement registration functionality
            handleRegistration()
        }

        btnBackToLogin.setOnClickListener {
            // Navigate back to login activity
            finish()
        }
    }

    /**
     * Handle registration process (placeholder for now)
     */
    private fun handleRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // TODO: Add proper validation and Firebase authentication
        // Basic validation for now
        if (validateInput(name, email, password, confirmPassword)) {
            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Save user profile to Firestore
                        val user = auth.currentUser
                        val db = Firebase.firestore
                        val userData = mapOf(
                            "uid" to (user?.uid ?: ""),
                            "name" to name,
                            "email" to email
                        )
                        user?.let {
                            db.collection("users").document(it.uid).set(userData)
                                .addOnSuccessListener {
                                    navigateToMain()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("RegisterActivity", "Error writing user profile", e)
                                    Toast.makeText(this, "Registration succeeded but failed to save profile", Toast.LENGTH_LONG).show()
                                    navigateToMain()
                                }
                        } ?: run {
                            navigateToMain()
                        }
                    } else {
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Registration failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    /**
     * Validate user input (basic validation for now)
     */
    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        when {
            name.isEmpty() -> {
                etName.error = "Name is required"
                return false
            }
            email.isEmpty() -> {
                etEmail.error = "Email is required"
                return false
            }
            password.isEmpty() -> {
                etPassword.error = "Password is required"
                return false
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Passwords do not match"
                return false
            }
            password.length < 6 -> {
                etPassword.error = "Password must be at least 6 characters"
                return false
            }
        }
        return true
    }

    /**
     * Navigate to main activity after successful registration
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close register activity
    }
}
