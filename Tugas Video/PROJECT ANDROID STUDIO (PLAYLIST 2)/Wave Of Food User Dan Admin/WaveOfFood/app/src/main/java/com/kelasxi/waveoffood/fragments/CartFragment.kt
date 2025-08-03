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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kelasxi.waveoffood.CheckoutActivity
import com.kelasxi.waveoffood.CheckoutActivitySafe
import com.kelasxi.waveoffood.CheckoutActivitySimple
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.adapters.CartAdapter
import com.kelasxi.waveoffood.models.CartItemModel

/**
 * Fragment untuk halaman keranjang belanja
 */
class CartFragment : Fragment() {
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    
    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnCheckout: Button
    
    private var cartListener: ListenerRegistration? = null
    private val cartItems = mutableListOf<CartItemModel>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi views
        rvCart = view.findViewById(R.id.cartRecyclerView)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        
        // Inisialisasi Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup click listeners
        setupClickListeners()
        
        // Fetch cart data
        fetchCartData()
    }
    
    /**
     * Setup RecyclerView dan adapter-nya
     */
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { cartItem, position ->
            deleteCartItem(cartItem, position)
        }
        
        rvCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        btnCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                try {
                    // Navigate to simple CheckoutActivity that won't crash
                    val intent = Intent(context, CheckoutActivitySimple::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening checkout: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Keranjang masih kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Ambil data keranjang secara real-time dari Firestore
     */
    private fun fetchCartData() {
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Setup real-time listener
        cartListener = firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(context, "Gagal memuat keranjang: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                
                cartItems.clear()
                
                snapshots?.let { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val cartItem = document.toObject(CartItemModel::class.java)
                        cartItem?.let {
                            cartItems.add(it.copy(id = document.id))
                        }
                    }
                }
                
                // Update adapter dan total harga
                cartAdapter.updateData(cartItems)
                calculateTotalPrice()
            }
    }
    
    /**
     * Hitung dan tampilkan total harga
     */
    private fun calculateTotalPrice() {
        var totalPrice = 0
        
        for (item in cartItems) {
            val price = item.foodPrice.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
            totalPrice += price * item.quantity
        }
        
        tvTotalPrice.text = "Total Harga: Rp ${String.format("%,d", totalPrice)}"
    }
    
    /**
     * Hapus item dari keranjang
     */
    private fun deleteCartItem(cartItem: CartItemModel, position: Int) {
        val currentUser = auth.currentUser ?: return
        
        cartItem.id?.let { itemId ->
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("cart")
                .document(itemId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Gagal menghapus item: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        cartListener?.remove()
    }
}
