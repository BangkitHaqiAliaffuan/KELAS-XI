package com.trashbin.app.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trashbin.app.R
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.User
import com.trashbin.app.data.repository.AuthRepository
import com.trashbin.app.ui.viewmodel.AuthViewModel
import com.trashbin.app.ui.viewmodel.AuthViewModelFactory

class EditProfileActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var ivProfile: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etPhone: TextInputEditText
    private lateinit var tilAddress: TextInputLayout
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var progressBar: ProgressBar
    
    private lateinit var authViewModel: AuthViewModel
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        if (!TokenManager.isLoggedIn()) {
            finish()
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
            text = "Edit Profile"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
            gravity = android.view.Gravity.CENTER
        }
        mainLayout.addView(titleText)
        
        // Profile Picture Section
        createProfilePictureSection()
        
        // Form Fields
        createFormFields()
        
        // Action Buttons
        createActionButtons()
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createProfilePictureSection() {
        val profileLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
        }
        
        // Profile Image
        ivProfile = ImageView(this).apply {
            val size = (100 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(R.drawable.ic_person)
        }
        profileLayout.addView(ivProfile)
        
        // Select Photo Button
        btnSelectPhoto = Button(this).apply {
            text = "Change Photo"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        profileLayout.addView(btnSelectPhoto)
        
        mainLayout.addView(profileLayout)
    }
    
    private fun createFormFields() {
        // Name Field
        tilName = TextInputLayout(this).apply {
            hint = "Name"
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }
        etName = TextInputEditText(this).apply {
            id = View.generateViewId()
        }
        tilName.addView(etName)
        mainLayout.addView(tilName)
        
        // Email Field (read-only)
        tilEmail = TextInputLayout(this).apply {
            hint = "Email"
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
            isEnabled = false // Email is typically read-only for editing
        }
        etEmail = TextInputEditText(this).apply {
            id = View.generateViewId()
            isEnabled = false
        }
        tilEmail.addView(etEmail)
        mainLayout.addView(tilEmail)
        
        // Phone Field
        tilPhone = TextInputLayout(this).apply {
            hint = "Phone"
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }
        etPhone = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }
        tilPhone.addView(etPhone)
        mainLayout.addView(tilPhone)
        
        // Address Field
        tilAddress = TextInputLayout(this).apply {
            hint = "Address"
            setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
        }
        etAddress = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        tilAddress.addView(etAddress)
        mainLayout.addView(tilAddress)
    }
    
    private fun createActionButtons() {
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        
        btnCancel = Button(this).apply {
            text = "Cancel"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = (8 * resources.displayMetrics.density).toInt()
            }
        }
        buttonLayout.addView(btnCancel)
        
        btnSave = Button(this).apply {
            text = "Save Changes"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt()
            }
            setBackgroundColor(resources.getColor(R.color.primary))
            setTextColor(resources.getColor(android.R.color.white))
        }
        buttonLayout.addView(btnSave)
        
        mainLayout.addView(buttonLayout)
    }
    
    private fun setupListeners() {
        btnCancel.setOnClickListener {
            finish()
        }
        
        btnSave.setOnClickListener {
            if (validateInput()) {
                updateProfile()
            }
        }
        
        btnSelectPhoto.setOnClickListener {
            // For now, just show a toast - in real implementation, you would open gallery/camera
            Toast.makeText(this, "Photo selection feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun validateInput(): Boolean {
        var isValid = true
        
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            tilName.error = "Name is required"
            isValid = false
        } else {
            tilName.error = null
        }
        
        val phone = etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            tilPhone.error = "Phone number is required"
            isValid = false
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.error = "Invalid phone number"
            isValid = false
        } else {
            tilPhone.error = null
        }
        
        return isValid
    }
    
    private fun updateProfile() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        
        // Show loading
        showLoading(true)
        
        // Prepare user data map
        val userData = mapOf(
            "name" to name,
            "phone" to phone,
            "address" to if (address.isNotEmpty()) address else null
        ).filterValues { it != null } as Map<String, Any>
        
        authViewModel.updateProfile(userData)
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnCancel.isEnabled = !show
    }
    
    private fun observeViewModel() {
        authViewModel.profileResult.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.RepositoryResult.Success -> {
                    showLoading(false)
                    val user = result.data
                    Log.d("EditProfileActivity", "Profile updated successfully: ${user.name}")
                    
                    // Update cached user data
                    TokenManager.getInstance().saveUser(user)
                    
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is com.trashbin.app.data.repository.RepositoryResult.Error -> {
                    showLoading(false)
                    Log.e("EditProfileActivity", "Failed to update profile", Exception(result.message))
                    
                    // Check if it's an authentication error
                    val errorMessage = result.message
                    if (errorMessage?.contains("401") == true || 
                        errorMessage?.contains("Unauthorized") == true ||
                        errorMessage?.contains("Unauthenticated") == true) {
                        Toast.makeText(this, "Sesi Anda telah berakhir.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update profile: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
                is com.trashbin.app.data.repository.RepositoryResult.Loading -> {
                    showLoading(true)
                }
            }
        }
    }
    
    private fun loadUserData() {
        Log.d("EditProfileActivity", "Loading user profile for editing...")
        
        // Load from TokenManager cache first
        val cachedUser = TokenManager.getInstance().getUser()
        cachedUser?.let { user ->
            Log.d("EditProfileActivity", "Loaded cached user data: ${user.name}")
            currentUser = user
            displayUserData(user)
        }
        
        // Then fetch fresh data from API to ensure we have the latest
        authViewModel.getProfile()
    }
    
    private fun displayUserData(user: User) {
        etName.setText(user.name)
        etEmail.setText(user.email)
        etPhone.setText(user.phone)
        etAddress.setText(user.address)
        
        // Load profile picture if available
        user.avatar?.let { avatarUrl ->
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(ivProfile)
        }
    }
}