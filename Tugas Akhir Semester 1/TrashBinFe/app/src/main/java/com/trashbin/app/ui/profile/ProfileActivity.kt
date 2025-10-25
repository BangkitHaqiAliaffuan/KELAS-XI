package com.trashbin.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.trashbin.app.R
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.User
import com.trashbin.app.data.repository.AuthRepository
import com.trashbin.app.ui.auth.LoginActivity
import com.trashbin.app.ui.viewmodel.AuthViewModel
import com.trashbin.app.ui.viewmodel.AuthViewModelFactory

class ProfileActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var ivProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvRating: TextView
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnMyOrders: MaterialButton
    private lateinit var btnMyPickups: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var progressBar: ProgressBar
    
    private lateinit var authViewModel: AuthViewModel
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        if (!TokenManager.isLoggedIn()) {
            redirectToLogin()
            return
        }
        
        setupViewModel()
        setupUI()
        setupListeners()
        observeViewModel()
        loadUserData()
    }
    
    private fun setupViewModel() {
        val apiService = RetrofitClient.apiService
        val tokenManager = TokenManager.getInstance()
        val repository = AuthRepository(apiService, tokenManager)
        val factory = AuthViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }
    
    private fun setupUI() {
        scrollView = ScrollView(this)
        
        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt()
            )
        }
        
        // Progress Bar
        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = (16 * resources.displayMetrics.density).toInt()
            }
            visibility = View.GONE
        }
        mainLayout.addView(progressBar)
        
        // Title
        val titleText = TextView(this).apply {
            text = "Profile"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
            gravity = android.view.Gravity.CENTER
        }
        mainLayout.addView(titleText)
        
        // Profile Picture
        ivProfile = ImageView(this).apply {
            val size = (100 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = (16 * resources.displayMetrics.density).toInt()
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            // Add circular background or use placeholder
            setImageResource(R.drawable.ic_person) // Updated to use correct drawable
        }
        mainLayout.addView(ivProfile)
        
        // User Information
        createInfoSection()
        
        // Menu Buttons
        createMenuButtons()
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createInfoSection() {
        // Name
        tvName = TextView(this).apply {
            text = "Loading..."
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(tvName)
        
        // Email
        tvEmail = TextView(this).apply {
            text = "Loading..."
            textSize = 14f
            setTextColor(resources.getColor(R.color.gray_600))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (4 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(tvEmail)
        
        // Phone
        tvPhone = TextView(this).apply {
            text = "Loading..."
            textSize = 14f
            setTextColor(resources.getColor(R.color.gray_600))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (4 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(tvPhone)
        
        // Address
        tvAddress = TextView(this).apply {
            text = "Loading..."
            textSize = 14f
            setTextColor(resources.getColor(R.color.gray_600))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(tvAddress)
        
        // Stats Layout
        val statsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, (24 * resources.displayMetrics.density).toInt())
        }
        
        // Points
        val pointsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val pointsLabel = TextView(this).apply {
            text = "Points"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
            gravity = android.view.Gravity.CENTER
        }
        pointsLayout.addView(pointsLabel)
        
        tvPoints = TextView(this).apply {
            text = "0"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }
        pointsLayout.addView(tvPoints)
        
        statsLayout.addView(pointsLayout)
        
        // Rating
        val ratingLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val ratingLabel = TextView(this).apply {
            text = "Rating"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
            gravity = android.view.Gravity.CENTER
        }
        ratingLayout.addView(ratingLabel)
        
        tvRating = TextView(this).apply {
            text = "⭐ 0.0"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }
        ratingLayout.addView(tvRating)
        
        statsLayout.addView(ratingLayout)
        mainLayout.addView(statsLayout)
    }
    
    private fun createMenuButtons() {
        btnEditProfile = MaterialButton(this).apply {
            text = "Edit Profile"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            ).apply {
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
        }
        mainLayout.addView(btnEditProfile)
        
        btnMyOrders = MaterialButton(this).apply {
            text = "📦 My Orders"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            ).apply {
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
        }
        mainLayout.addView(btnMyOrders)
        
        btnMyPickups = MaterialButton(this).apply {
            text = "🗑️ My Pickups"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            ).apply {
                bottomMargin = (24 * resources.displayMetrics.density).toInt()
            }
        }
        mainLayout.addView(btnMyPickups)
        
        btnLogout = MaterialButton(this).apply {
            text = "Logout"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            )
            setBackgroundColor(resources.getColor(R.color.red_500))
        }
        mainLayout.addView(btnLogout)
    }
    
    private fun observeViewModel() {
        authViewModel.profileResult.observe(this) { result ->
            progressBar.visibility = View.GONE
            
            when (result) {
                is com.trashbin.app.data.repository.RepositoryResult.Success -> {
                    val user = result.data
                    Log.d("ProfileActivity", "Profile loaded successfully: ${user.name}")
                    currentUser = user
                    displayUserData(user)
                }
                is com.trashbin.app.data.repository.RepositoryResult.Error -> {
                    Log.e("ProfileActivity", "Failed to load profile", Exception(result.message))
                    
                    // Check if it's an authentication error
                    val errorMessage = result.message
                    if (errorMessage?.contains("401") == true || 
                        errorMessage?.contains("Unauthorized") == true ||
                        errorMessage?.contains("Unauthenticated") == true) {
                        Toast.makeText(this, "Sesi Anda telah berakhir. Silakan login kembali.", Toast.LENGTH_LONG).show()
                        redirectToLogin()
                    } else {
                        Toast.makeText(this, "Gagal memuat profil: $errorMessage", Toast.LENGTH_LONG).show()
                        // Try to load from cache
                        loadCachedUserData()
                    }
                }
                is com.trashbin.app.data.repository.RepositoryResult.Loading -> {
                    // Handle loading state if needed
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun loadUserData() {
        Log.d("ProfileActivity", "Loading user profile from API...")
        progressBar.visibility = View.VISIBLE
        
        // Try to load from cache first
        loadCachedUserData()
        
        // Then fetch fresh data from API
        authViewModel.getProfile()
    }
    
    private fun loadCachedUserData() {
        val cachedUser = TokenManager.getInstance().getUser()
        cachedUser?.let { user ->
            Log.d("ProfileActivity", "Loaded cached user data: ${user.name}")
            currentUser = user
            displayUserData(user)
        }
    }
    
    private fun displayUserData(user: User) {
        tvName.text = user.name
        tvEmail.text = user.email
        tvPhone.text = user.phone ?: "No phone number"
        tvAddress.text = user.address ?: "No address"
        tvPoints.text = user.points.toString()
        tvRating.text = "⭐ ${user.rating ?: 0.0}"
        
        // Load profile picture if available
        user.avatar?.let { avatarUrl ->
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(ivProfile)
        }
    }
    
    private fun setupListeners() {
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        
        btnMyOrders.setOnClickListener {
            Log.d("ProfileActivity", "Opening My Orders")
            startActivity(Intent(this, com.trashbin.app.ui.orders.MyOrdersActivity::class.java))
        }
        
        btnMyPickups.setOnClickListener {
            Log.d("ProfileActivity", "Opening My Pickups")
            startActivity(Intent(this, com.trashbin.app.ui.pickups.MyPickupsActivity::class.java))
        }
        
        btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        Log.d("ProfileActivity", "Logging out user...")
        TokenManager.getInstance().clearToken()
        redirectToLogin()
    }
    
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}