package com.kelasxi.waveoffood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.kelasxi.waveoffood.MyOrdersActivity
import com.kelasxi.waveoffood.R

/**
 * Enhanced Profile Fragment with improved UI/UX
 * Uses the enhanced profile layout
 */
class ProfileFragmentEnhanced : Fragment() {
    
    private lateinit var cvMyOrders: CardView
    private lateinit var cvFavorites: CardView
    private lateinit var cvSettings: CardView
    private lateinit var cvLogout: CardView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_enhanced, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize UI components and set up listeners
        initializeViews(view)
        setupClickListeners()
    }
    
    private fun initializeViews(view: View) {
        cvMyOrders = view.findViewById(R.id.cv_my_orders)
        cvFavorites = view.findViewById(R.id.cv_favorites)
        cvSettings = view.findViewById(R.id.cv_settings)
        cvLogout = view.findViewById(R.id.cv_logout)
    }
    
    private fun setupClickListeners() {
        cvMyOrders.setOnClickListener {
            // Navigate to My Orders Activity
            val intent = Intent(requireContext(), MyOrdersActivity::class.java)
            startActivity(intent)
        }
        
        cvFavorites.setOnClickListener {
            // TODO: Navigate to Favorites Activity
            // val intent = Intent(requireContext(), FavoritesActivity::class.java)
            // startActivity(intent)
        }
        
        cvSettings.setOnClickListener {
            // TODO: Navigate to Settings Activity
            // val intent = Intent(requireContext(), SettingsActivity::class.java)
            // startActivity(intent)
        }
        
        cvLogout.setOnClickListener {
            // TODO: Implement logout functionality
            // showLogoutDialog()
        }
    }
}
