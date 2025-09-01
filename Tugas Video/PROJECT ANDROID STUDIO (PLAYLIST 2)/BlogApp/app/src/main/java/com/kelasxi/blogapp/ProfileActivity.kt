package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Activity untuk menampilkan profile pengguna
 * Berisi menu navigasi ke berbagai fitur user
 */
class ProfileActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var ivProfilePicture: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var llAddNewArticle: LinearLayout
    private lateinit var llYourArticles: LinearLayout
    private lateinit var llLogout: LinearLayout

    // Firebase
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Load user data
        loadUserData()
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        llAddNewArticle = findViewById(R.id.llAddNewArticle)
        llYourArticles = findViewById(R.id.llYourArticles)
        llLogout = findViewById(R.id.llLogout)
    }

    /**
     * Setup toolbar dengan navigation
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Load user data dari Firebase
     */
    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set email langsung dari FirebaseUser
        tvUserEmail.text = currentUser.email

        // Load user profile dari Firestore
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("name") ?: currentUser.email ?: "User"
                    val profileUrl = document.getString("profileUrl") ?: ""
                    
                    tvUserName.text = userName
                    
                    // Load profile picture with ProfileImageHelper
                    ProfileImageHelper.loadProfileImage(
                        ivProfilePicture,
                        profileUrl,
                        R.drawable.ic_launcher_foreground
                    )
                } else {
                    // Document doesn't exist, use email as name
                    tvUserName.text = currentUser.email ?: "User"
                    ProfileImageHelper.loadProfileImage(
                        ivProfilePicture,
                        null,
                        R.drawable.ic_launcher_foreground
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Error loading user profile", e)
                tvUserName.text = currentUser.email ?: "User"
                ProfileImageHelper.loadProfileImage(
                    ivProfilePicture,
                    null,
                    R.drawable.ic_launcher_foreground
                )
            }
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        llAddNewArticle.setOnClickListener {
            // Navigate to write blog activity
            val intent = Intent(this, WriteBlogActivity::class.java)
            startActivity(intent)
        }

        llYourArticles.setOnClickListener {
            // Navigate to your articles activity
            val intent = Intent(this, YourArticlesActivity::class.java)
            startActivity(intent)
        }

        llLogout.setOnClickListener {
            handleLogout()
        }
    }

    /**
     * Handle logout
     */
    private fun handleLogout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to login activity and clear task stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
