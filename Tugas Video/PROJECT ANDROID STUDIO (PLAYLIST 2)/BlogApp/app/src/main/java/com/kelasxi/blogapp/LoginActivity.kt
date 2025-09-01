package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity untuk halaman login pengguna
 * Menangani proses autentikasi pengguna
 */
class LoginActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is already logged in
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to main activity
            navigateToMain()
            return
        }
        
        setContentView(R.layout.activity_login)

        // Initialize UI components
        initViews()
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
    }

    /**
     * Setup click listeners untuk buttons
     */
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            // TODO: Implement login functionality
            handleLogin()
        }

        btnRegister.setOnClickListener {
            // Navigate to register activity
            navigateToRegister()
        }
    }

    /**
     * Handle login process (placeholder for now)
     */
    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // TODO: Add validation and Firebase authentication
        // For now, just navigate to main activity
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentication
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, load user profile data
                    loadUserProfileAndNavigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Navigate to register activity
     */
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigate to main activity after successful login
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close login activity
    }

    /**
     * Load user profile data dari Firestore setelah login
     */
    private fun loadUserProfileAndNavigate() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            navigateToMain()
            return
        }

        val firestore = Firebase.firestore
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User profile exists, navigate to main
                    Log.d("LoginActivity", "User profile loaded successfully")
                    navigateToMain()
                } else {
                    // User profile doesn't exist, create default profile
                    Log.d("LoginActivity", "Creating default user profile")
                    createDefaultUserProfile(currentUser.uid, currentUser.email ?: "")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error loading user profile", e)
                // Even if profile loading fails, navigate to main
                navigateToMain()
            }
    }

    /**
     * Create default user profile jika belum ada
     */
    private fun createDefaultUserProfile(userId: String, email: String) {
        val firestore = Firebase.firestore
        val profileImageUrl = ProfileImageHelper.getProfileImageForUser(email)
        
        val userData = mapOf(
            "uid" to userId,
            "name" to email.substringBefore("@"), // Use email prefix as default name
            "email" to email,
            "profileUrl" to profileImageUrl,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                Log.d("LoginActivity", "Default user profile created")
                navigateToMain()
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error creating default user profile", e)
                navigateToMain() // Navigate anyway
            }
    }
}
