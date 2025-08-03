package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.adapter.CheckoutItemAdapter
import com.kelasxi.waveoffood.model.*
import com.kelasxi.waveoffood.utils.CartManager
import java.util.*

/**
 * Activity untuk proses checkout yang lengkap
 */
class CheckoutActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    // UI Components
    private lateinit var ivBack: ImageView
    private lateinit var tilCustomerName: TextInputLayout
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var tilCustomerPhone: TextInputLayout
    private lateinit var etCustomerPhone: TextInputEditText
    private lateinit var tilCustomerAddress: TextInputLayout
    private lateinit var etCustomerAddress: TextInputEditText
    private lateinit var spPaymentMethod: Spinner
    private lateinit var rvCheckoutItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    
    private lateinit var checkoutAdapter: CheckoutItemAdapter
    
    // Loading indicator
    private var loadingOverlay: View? = null
    
    // Order data
    private var cartItems = mutableListOf<CartItemModel>()
    private val deliveryFee = 10000L // Rp 10,000
    private var subtotal = 0L
    private var total = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        android.util.Log.d("CheckoutActivity", "Starting CheckoutActivity")
        
        try {
            setContentView(R.layout.activity_checkout)
            android.util.Log.d("CheckoutActivity", "Layout set successfully")
            
            // Initialize Firebase
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            android.util.Log.d("CheckoutActivity", "Firebase initialized")
            
            // Check if user is logged in
            if (auth.currentUser == null) {
                android.util.Log.w("CheckoutActivity", "User not logged in")
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            android.util.Log.d("CheckoutActivity", "User is logged in: ${auth.currentUser?.uid}")
            
            // Load cart items
            loadCartItems()
            
            // Check if cart is empty
            android.util.Log.d("CheckoutActivity", "Cart items count: ${cartItems.size}")
            if (cartItems.isEmpty()) {
                android.util.Log.w("CheckoutActivity", "Cart is empty")
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            initializeViews()
            android.util.Log.d("CheckoutActivity", "Views initialized")
            
            setupRecyclerView()
            android.util.Log.d("CheckoutActivity", "RecyclerView setup complete")
            
            setupClickListeners()
            android.util.Log.d("CheckoutActivity", "Click listeners setup complete")
            
            loadUserData()
            android.util.Log.d("CheckoutActivity", "User data loading started")
            
            calculateOrderSummary()
            android.util.Log.d("CheckoutActivity", "Order summary calculated")
            
            updateUI()
            android.util.Log.d("CheckoutActivity", "UI updated successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error in onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan saat membuka checkout: ${e.message}", Toast.LENGTH_LONG).show()
            // Try to show minimal UI instead of closing
            showMinimalErrorUI()
        }
    }
    
    private fun loadCartItems() {
        try {
            val items = CartManager.getCartItems().filter { it.name.isNotBlank() && it.price > 0 }
            cartItems.clear()
            cartItems.addAll(items)
            android.util.Log.d("CheckoutActivity", "Loaded ${cartItems.size} cart items")
            
            // Log each item for debugging
            cartItems.forEachIndexed { index, item ->
                android.util.Log.d("CheckoutActivity", "Item $index: ${item.name} - Rp ${item.price} x ${item.quantity}")
            }
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error loading cart items", e)
            cartItems.clear()
        }
    }
    
    private fun initializeViews() {
        try {
            // Initialize views with null checks
            ivBack = findViewById(R.id.iv_back)
            tilCustomerName = findViewById(R.id.til_customer_name)
            etCustomerName = findViewById(R.id.et_customer_name)
            tilCustomerPhone = findViewById(R.id.til_customer_phone)
            etCustomerPhone = findViewById(R.id.et_customer_phone)
            tilCustomerAddress = findViewById(R.id.til_customer_address)
            etCustomerAddress = findViewById(R.id.et_customer_address)
            spPaymentMethod = findViewById(R.id.sp_payment_method)
            rvCheckoutItems = findViewById(R.id.rv_checkout_items)
            tvSubtotal = findViewById(R.id.tv_subtotal)
            tvDeliveryFee = findViewById(R.id.tv_delivery_fee)
            tvTotal = findViewById(R.id.tv_total_payment)
            btnPlaceOrder = findViewById(R.id.btn_place_order)
            
            // Verify all views are found
            if (ivBack == null || etCustomerName == null || rvCheckoutItems == null || btnPlaceOrder == null) {
                throw IllegalStateException("Failed to find required views")
            }
            
            setupPaymentSpinner()
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error initializing views", e)
            throw e // Re-throw to be caught in onCreate
        }
    }
    
    private fun showMinimalErrorUI() {
        try {
            // Find the back button and make sure it works
            val backButton = findViewById<ImageView>(R.id.iv_back)
            backButton?.setOnClickListener { finish() }
            
            // Show error message to user
            Toast.makeText(this, "Gagal memuat halaman checkout. Silakan coba lagi.", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error showing minimal UI", e)
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        try {
            if (rvCheckoutItems == null) {
                android.util.Log.e("CheckoutActivity", "RecyclerView is null")
                return
            }
            
            checkoutAdapter = CheckoutItemAdapter(cartItems)
            rvCheckoutItems.apply {
                layoutManager = LinearLayoutManager(this@CheckoutActivity)
                adapter = checkoutAdapter
                isNestedScrollingEnabled = false
            }
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error setting up RecyclerView", e)
            Toast.makeText(this, "Error loading items", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupPaymentSpinner() {
        try {
            if (spPaymentMethod == null) {
                android.util.Log.e("CheckoutActivity", "Payment spinner is null")
                return
            }
            
            val paymentMethods = arrayOf(
                "Pilih Metode Pembayaran",
                "Cash on Delivery (COD)",
                "Transfer Bank",
                "OVO",
                "GoPay",
                "DANA",
                "ShopeePay"
            )
            
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPaymentMethod.adapter = adapter
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error setting up payment spinner", e)
        }
    }
    
    private fun setupClickListeners() {
        try {
            ivBack?.setOnClickListener {
                finish()
            }
            
            btnPlaceOrder?.setOnClickListener {
                if (validateForm()) {
                    placeOrder()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error setting up click listeners", e)
        }
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        // Validate customer name
        val customerName = etCustomerName.text.toString().trim()
        if (customerName.isEmpty()) {
            tilCustomerName.error = "Nama wajib diisi"
            etCustomerName.requestFocus()
            isValid = false
        } else {
            tilCustomerName.error = null
        }
        
        // Validate customer phone
        val customerPhone = etCustomerPhone.text.toString().trim()
        if (customerPhone.isEmpty()) {
            tilCustomerPhone.error = "Nomor telepon wajib diisi"
            if (isValid) etCustomerPhone.requestFocus()
            isValid = false
        } else if (customerPhone.length < 10) {
            tilCustomerPhone.error = "Nomor telepon minimal 10 digit"
            if (isValid) etCustomerPhone.requestFocus()
            isValid = false
        } else {
            tilCustomerPhone.error = null
        }
        
        // Validate customer address
        val customerAddress = etCustomerAddress.text.toString().trim()
        if (customerAddress.isEmpty()) {
            tilCustomerAddress.error = "Alamat wajib diisi"
            if (isValid) etCustomerAddress.requestFocus()
            isValid = false
        } else if (customerAddress.length < 10) {
            tilCustomerAddress.error = "Alamat minimal 10 karakter"
            if (isValid) etCustomerAddress.requestFocus()
            isValid = false
        } else {
            tilCustomerAddress.error = null
        }
        
        // Validate payment method
        if (spPaymentMethod.selectedItemPosition == 0) {
            Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        if (!isValid) {
            Toast.makeText(this, "Mohon lengkapi semua data yang diperlukan", Toast.LENGTH_SHORT).show()
        }
        
        return isValid
    }
    
    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userModel = document.toObject(UserModel::class.java)
                    userModel?.let { user ->
                        // Pre-fill form with user data if available
                        if (user.name.isNotEmpty()) {
                            etCustomerName.setText(user.name)
                        }
                        if (user.phone.isNotEmpty()) {
                            etCustomerPhone.setText(user.phone)
                        }
                        if (user.address.isNotEmpty()) {
                            etCustomerAddress.setText(user.address)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memuat data user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun calculateOrderSummary() {
        try {
            subtotal = cartItems.sumOf { 
                try {
                    it.calculateSubtotal()
                } catch (e: Exception) {
                    android.util.Log.e("CheckoutActivity", "Error calculating subtotal for item: ${it.name}", e)
                    0L
                }
            }
            total = subtotal + deliveryFee
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error calculating order summary", e)
            subtotal = 0L
            total = deliveryFee
        }
    }
    
    private fun updateUI() {
        try {
            // Update price summary
            tvSubtotal.text = "Rp ${String.format("%,d", subtotal)}"
            tvDeliveryFee.text = "Rp ${String.format("%,d", deliveryFee)}"
            tvTotal.text = "Rp ${String.format("%,d", total)}"
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error updating UI", e)
            // Set fallback values
            tvSubtotal.text = "Rp 0"
            tvDeliveryFee.text = "Rp 0"
            tvTotal.text = "Rp 0"
        }
    }
    
    private fun placeOrder() {
        android.util.Log.d("CheckoutActivity", "placeOrder() called")
        val currentUser = auth.currentUser ?: return
        
        // Get form data
        val customerName = etCustomerName.text.toString().trim()
        val customerPhone = etCustomerPhone.text.toString().trim()
        val customerAddress = etCustomerAddress.text.toString().trim()
        val paymentMethod = spPaymentMethod.selectedItem.toString()
        
        android.util.Log.d("CheckoutActivity", "Form data - Name: $customerName, Phone: $customerPhone, Address: $customerAddress, Payment: $paymentMethod")
        
        // Double check validation before proceeding
        if (customerName.isEmpty() || customerPhone.isEmpty() || customerAddress.isEmpty() || spPaymentMethod.selectedItemPosition == 0) {
            android.util.Log.w("CheckoutActivity", "Validation failed in placeOrder")
            Toast.makeText(this, "Mohon lengkapi semua data yang diperlukan", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading
        showLoading(true)
        btnPlaceOrder.isEnabled = false
        
        // Create order
        val orderId = "ORD-${System.currentTimeMillis()}"
        val deliveryAddress = DeliveryAddress(
            address = customerAddress,
            instructions = ""
        )
        
        val order = OrderModel(
            orderId = orderId,
            userId = currentUser.uid,
            userName = customerName,
            userPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            items = cartItems,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            serviceFee = 0L,
            totalAmount = total,
            paymentMethod = paymentMethod,
            orderStatus = "pending",
            estimatedDelivery = Timestamp(Date(System.currentTimeMillis() + 45 * 60 * 1000)), // 45 minutes from now
            createdAt = Timestamp.now()
        )
        
        // Save order to Firestore
        firestore.collection("orders")
            .document(orderId)
            .set(order)
            .addOnSuccessListener {
                // Clear cart after successful order
                CartManager.clearCart()
                
                showLoading(false)
                
                Toast.makeText(this, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
                
                // Navigate to order confirmation or back to main
                finish()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                btnPlaceOrder.isEnabled = true
                Toast.makeText(this, "Gagal membuat pesanan: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
    
    private fun showLoading(show: Boolean) {
        if (show) {
            btnPlaceOrder.text = "Memproses..."
        } else {
            btnPlaceOrder.text = "Pesan Sekarang"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
}
