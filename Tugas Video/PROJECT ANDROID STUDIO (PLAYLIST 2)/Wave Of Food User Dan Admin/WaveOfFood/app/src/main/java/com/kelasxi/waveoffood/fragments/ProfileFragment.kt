package com.kelasxi.waveoffood.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.LoginActivity
import com.kelasxi.waveoffood.MyOrdersActivity
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.UserModel

/**
 * Fragment untuk halaman profil pengguna
 */
class ProfileFragment : Fragment() {
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnMyOrders: Button
    private lateinit var btnLogout: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi views
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        btnMyOrders = view.findViewById(R.id.btnMyOrders)
        btnLogout = view.findViewById(R.id.btnLogout)
        
        // Inisialisasi Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Setup click listeners
        setupClickListeners()
        
        // Load user data
        loadUserData()
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        btnMyOrders.setOnClickListener {
            openMyOrders()
        }
        
        btnLogout.setOnClickListener {
            performLogout()
        }
    }
    
    /**
     * Load data pengguna dari Firestore
     */
    private fun loadUserData() {
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            Toast.makeText(context, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        
        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userModel = document.toObject(UserModel::class.java)
                    userModel?.let { user ->
                        tvUserName.text = user.name
                        tvUserEmail.text = user.email
                    }
                } else {
                    // Jika dokumen tidak ada, gunakan data dari Auth
                    tvUserName.text = currentUser.displayName ?: "Tidak tersedia"
                    tvUserEmail.text = currentUser.email ?: "Tidak tersedia"
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
                
                // Fallback ke data Auth
                tvUserName.text = currentUser.displayName ?: "Tidak tersedia"
                tvUserEmail.text = currentUser.email ?: "Tidak tersedia"
            }
    }
    
    /**
     * Buka halaman My Orders
     */
    private fun openMyOrders() {
        val intent = Intent(context, MyOrdersActivity::class.java)
        startActivity(intent)
    }
    
    /**
     * Logout pengguna
     */
    private fun performLogout() {
        auth.signOut()
        
        // Arahkan ke LoginActivity
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        
        // Tutup activity saat ini
        activity?.finish()
    }
}
