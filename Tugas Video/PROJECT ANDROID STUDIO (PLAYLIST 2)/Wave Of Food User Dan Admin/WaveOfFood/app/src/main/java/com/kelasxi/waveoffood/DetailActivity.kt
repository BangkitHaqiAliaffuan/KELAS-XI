package com.kelasxi.waveoffood

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.models.CartItemModel
import com.kelasxi.waveoffood.models.FoodItemModel
import com.kelasxi.waveoffood.utils.CartManager
import java.util.UUID

/**
 * Activity untuk menampilkan detail makanan
 */
class DetailActivity : AppCompatActivity() {
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    // Views
    private lateinit var ivFoodDetail: ImageView
    private lateinit var tvFoodName: TextView
    private lateinit var tvFoodPrice: TextView
    private lateinit var tvFoodDescription: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var btnMinus: Button
    private lateinit var btnPlus: Button
    private lateinit var btnAddToCart: Button
    
    private var currentFoodItem: FoodItemModel? = null
    private var quantity = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        
        // Inisialisasi Views
        initViews()
        
        // Inisialisasi Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Check authentication status
        val currentUser = auth.currentUser
        android.util.Log.d("DetailActivity", "Current user: ${currentUser?.uid}")
        android.util.Log.d("DetailActivity", "User email: ${currentUser?.email}")
        
        // Ambil ID makanan dari intent
        val foodId = intent.getStringExtra("FOOD_ID")
        android.util.Log.d("DetailActivity", "Received FOOD_ID: $foodId")
        
        if (foodId != null) {
            fetchFoodDetails(foodId)
        } else {
            android.util.Log.e("DetailActivity", "FOOD_ID is null")
            Toast.makeText(this, "ID makanan tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        setupClickListeners()
    }
    
    /**
     * Inisialisasi semua views
     */
    private fun initViews() {
        ivFoodDetail = findViewById(R.id.ivFoodImage)
        tvFoodName = findViewById(R.id.tvFoodName)
        tvFoodPrice = findViewById(R.id.tvFoodPrice)
        tvFoodDescription = findViewById(R.id.tvFoodDescription)
        tvQuantity = findViewById(R.id.tvQuantity)
        btnMinus = findViewById(R.id.btnDecrease)
        btnPlus = findViewById(R.id.btnIncrease)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        // btnBack = findViewById(R.id.btnBack) // Remove since no back button in current layout
    }
    
    /**
     * Setup click listeners untuk tombol-tombol
     */
    private fun setupClickListeners() {
        // Tombol decrease quantity
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityDisplay()
            }
        }
        
        // Tombol increase quantity
        btnPlus.setOnClickListener {
            quantity++
            updateQuantityDisplay()
        }
        
        // Tombol tambah ke keranjang
        btnAddToCart.setOnClickListener {
            addToCart()
        }
    }
    
    /**
     * Ambil detail makanan dari Firestore berdasarkan ID
     */
    private fun fetchFoodDetails(foodId: String) {
        android.util.Log.d("DetailActivity", "Fetching food details for ID: $foodId")
        firestore.collection("foods").document(foodId)
            .get()
            .addOnSuccessListener { document ->
                android.util.Log.d("DetailActivity", "Document exists: ${document.exists()}")
                if (document.exists()) {
                    android.util.Log.d("DetailActivity", "Document data: ${document.data}")
                    currentFoodItem = document.toObject(FoodItemModel::class.java)?.copy(id = document.id)
                    currentFoodItem?.let { foodItem ->
                        android.util.Log.d("DetailActivity", "Parsed food item: ${foodItem.name}")
                        displayFoodDetails(foodItem)
                    }
                } else {
                    android.util.Log.e("DetailActivity", "Document does not exist for ID: $foodId")
                    Toast.makeText(this, "Makanan tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("DetailActivity", "Error fetching food details: ${exception.message}")
                Toast.makeText(this, "Gagal memuat detail: ${exception.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
    
    /**
     * Tampilkan detail makanan di UI
     */
    private fun displayFoodDetails(foodItem: FoodItemModel) {
        tvFoodName.text = foodItem.name
        tvFoodPrice.text = "Rp ${foodItem.price}"
        tvFoodDescription.text = foodItem.description.ifEmpty { "Deskripsi tidak tersedia" }
        
        // Load gambar menggunakan Glide
        Glide.with(this)
            .load(foodItem.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(ivFoodDetail)
    }
    
    /**
     * Update tampilan quantity
     */
    private fun updateQuantityDisplay() {
        tvQuantity.text = quantity.toString()
    }
    
    /**
     * Tambahkan item ke keranjang
     */
    private fun addToCart() {
        android.util.Log.d("DetailActivity", "addToCart called")
        proceedWithAddToCart()
    }
    
    private fun proceedWithAddToCart() {
        currentFoodItem?.let { foodItem ->
            android.util.Log.d("DetailActivity", "Adding to cart: ${foodItem.name}, quantity: $quantity")
            
            try {
                // Buat CartItemModel dengan struktur yang sama seperti HomeFragment
                val cartItem = com.kelasxi.waveoffood.model.CartItemModel(
                    id = UUID.randomUUID().toString(),
                    foodId = foodItem.id,
                    name = foodItem.name,
                    price = foodItem.price,
                    imageUrl = foodItem.imageUrl,
                    quantity = quantity,
                    selectedSize = "Regular",
                    selectedExtras = emptyList(),
                    subtotal = foodItem.price * quantity,
                    addedAt = null
                )
                
                // Disable tombol saat proses
                btnAddToCart.isEnabled = false
                btnAddToCart.text = "Menambahkan..."
                
                // Add to cart using CartManager (same as HomeFragment)
                CartManager.addToCart(cartItem)
                
                android.util.Log.d("DetailActivity", "Successfully added to cart using CartManager")
                
                // Berhasil ditambahkan
                btnAddToCart.isEnabled = true
                btnAddToCart.text = "Tambah ke Keranjang"
                
                Toast.makeText(this, "✅ ${foodItem.name} berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                
                // Reset quantity
                quantity = 1
                updateQuantityDisplay()
                
            } catch (e: Exception) {
                android.util.Log.e("DetailActivity", "Error adding to cart: ${e.message}")
                btnAddToCart.isEnabled = true
                btnAddToCart.text = "Tambah ke Keranjang"
                Toast.makeText(this, "Gagal menambahkan ke keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
