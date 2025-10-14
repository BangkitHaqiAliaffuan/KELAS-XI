package com.trashbin.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.trashbin.app.R
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.User
import com.trashbin.app.ui.auth.LoginActivity

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
    private lateinit var btnMyListings: MaterialButton
    private lateinit var btnMyOrders: MaterialButton
    private lateinit var btnMyPickups: MaterialButton
    private lateinit var btnLogout: MaterialButton
    
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        loadUserData()
        setupListeners()
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
            setImageResource(R.drawable.ic_user) // Assuming this drawable exists
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
        
        btnMyListings = MaterialButton(this).apply {
            text = "My Listings"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            ).apply {
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
        }
        mainLayout.addView(btnMyListings)
        
        btnMyOrders = MaterialButton(this).apply {
            text = "My Orders"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50 * resources.displayMetrics.density).toInt()
            ).apply {
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
        }
        mainLayout.addView(btnMyOrders)
        
        btnMyPickups = MaterialButton(this).apply {
            text = "My Pickups"
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
    
    private fun loadUserData() {
        // Get user from TokenManager (assuming it stores user data)
        currentUser = TokenManager.getInstance().getUser()
        
        currentUser?.let { user ->
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
                    .placeholder(R.drawable.ic_user)
                    .into(ivProfile)
            }
        } ?: run {
            // If no user data, redirect to login
            logout()
        }
    }
    
    private fun setupListeners() {
        btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Edit profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        btnMyListings.setOnClickListener {
            Toast.makeText(this, "My listings feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        btnMyOrders.setOnClickListener {
            Toast.makeText(this, "My orders feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        btnMyPickups.setOnClickListener {
            Toast.makeText(this, "My pickups feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        TokenManager.getInstance().clearToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}