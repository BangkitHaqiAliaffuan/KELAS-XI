package com.kelasxi.waveoffood.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.MyOrdersActivity
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.UserModel

class ProfileFragmentEnhanced : Fragment() {
    
    private lateinit var profilePicture: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var myOrdersCard: CardView
    private lateinit var favoritesCard: CardView
    private lateinit var settingsCard: CardView
    private lateinit var helpCard: CardView
    private lateinit var logoutCard: CardView
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_enhanced, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupClickListeners()
        loadUserProfile()
    }
    
    private fun initializeViews(view: View) {
        profilePicture = view.findViewById(R.id.iv_profile_picture)
        userName = view.findViewById(R.id.tv_user_name)
        userEmail = view.findViewById(R.id.tv_user_email)
        myOrdersCard = view.findViewById(R.id.cv_my_orders)
        favoritesCard = view.findViewById(R.id.cv_favorites)
        settingsCard = view.findViewById(R.id.cv_settings)
        helpCard = view.findViewById(R.id.cv_help)
        logoutCard = view.findViewById(R.id.cv_logout)
    }
    
    private fun setupClickListeners() {
        myOrdersCard.setOnClickListener {
            Log.d("ProfileFragment", "My Orders clicked")
            val intent = Intent(context, MyOrdersActivity::class.java)
            startActivity(intent)
        }
        
        favoritesCard.setOnClickListener {
            Log.d("ProfileFragment", "Favorites clicked")
            Toast.makeText(context, "Favorites - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        settingsCard.setOnClickListener {
            Log.d("ProfileFragment", "Settings clicked")
            Toast.makeText(context, "Settings - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        helpCard.setOnClickListener {
            Log.d("ProfileFragment", "Help clicked")
            Toast.makeText(context, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        logoutCard.setOnClickListener {
            performLogout()
        }
    }
    
    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // Load user data from Firebase Auth
            userName.text = currentUser.displayName ?: "User"
            userEmail.text = currentUser.email ?: "user@example.com"
            
            // Load additional user data from Firestore
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserModel::class.java)
                        user?.let { userModel ->
                            userName.text = userModel.name.ifEmpty { currentUser.displayName ?: "User" }
                            userEmail.text = userModel.email.ifEmpty { currentUser.email ?: "user@example.com" }
                            
                            // Load profile picture if available
                            if (userModel.profileImageUrl.isNotEmpty()) {
                                Glide.with(this@ProfileFragmentEnhanced)
                                    .load(userModel.profileImageUrl)
                                    .placeholder(R.drawable.ic_category_food) // Use existing placeholder
                                    .error(R.drawable.ic_category_food)
                                    .circleCrop()
                                    .into(profilePicture)
                                Log.d("ProfileFragment", "Profile image loaded: ${userModel.profileImageUrl}")
                            }
                        }
                    }
                    Log.d("ProfileFragment", "User profile loaded successfully")
                }
                .addOnFailureListener { exception ->
                    Log.w("ProfileFragment", "Error loading user profile", exception)
                }
        } else {
            // Show default values for non-authenticated users
            userName.text = "Guest User"
            userEmail.text = "guest@example.com"
        }
    }
    
    private fun performLogout() {
        try {
            auth.signOut()
            Log.d("ProfileFragment", "User logged out successfully")
            
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            
            // TODO: Navigate to login screen or update UI accordingly
            // For now, just reload the profile to show guest state
            loadUserProfile()
            
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error during logout", e)
            Toast.makeText(context, "Error during logout", Toast.LENGTH_SHORT).show()
        }
    }
}
