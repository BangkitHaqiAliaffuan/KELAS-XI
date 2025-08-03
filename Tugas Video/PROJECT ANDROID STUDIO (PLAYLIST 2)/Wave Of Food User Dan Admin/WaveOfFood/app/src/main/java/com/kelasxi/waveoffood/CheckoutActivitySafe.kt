package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.model.*
import com.kelasxi.waveoffood.utils.CartManager
import java.util.*

/**
 * Simplified CheckoutActivity with better error handling
 */
class CheckoutActivitySafe : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    // UI Components - will be initialized safely
    private var tvCartItems: TextView? = null
    private var tvTotal: TextView? = null
    private var btnPlaceOrder: MaterialButton? = null
    private var btnBack: Button? = null
    
    // Order data with safety checks
    private var cartItems = emptyList<CartItemModel>()
    private val deliveryFee = 10000L
    private val serviceFee = 2000L
    private var total = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        android.util.Log.d("CheckoutSafe", "Starting safe checkout")
        
        try {
            // Create simple layout programmatically to avoid resource issues
            createSimpleLayout()
            
            // Initialize Firebase
            initializeFirebase()
            
            // Load and validate cart data
            loadCartData()
            
            // Setup UI
            updateUI()
            
            android.util.Log.d("CheckoutSafe", "Checkout initialized successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSafe", "Error in onCreate", e)
            showError("Error initializing checkout: ${e.message}")
        }
    }
    
    private fun createSimpleLayout() {
        // Create layout programmatically to avoid XML resource issues
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Back button
        btnBack = Button(this).apply {
            text = "← Back"
            textSize = 16f
            setOnClickListener { finish() }
        }
        mainLayout.addView(btnBack)
        
        // Title
        val titleText = TextView(this).apply {
            text = "Checkout"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(titleText)
        
        // Cart items display
        tvCartItems = TextView(this).apply {
            text = "Loading cart items..."
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        mainLayout.addView(tvCartItems)
        
        // Total display
        tvTotal = TextView(this).apply {
            text = "Total: Calculating..."
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(tvTotal)
        
        // Place order button
        btnPlaceOrder = MaterialButton(this).apply {
            text = "Place Order"
            textSize = 16f
            setPadding(0, 16, 0, 16)
            setOnClickListener { placeOrder() }
        }
        mainLayout.addView(btnPlaceOrder)
        
        setContentView(mainLayout)
    }
    
    private fun initializeFirebase() {
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            
            // Check authentication
            if (auth.currentUser == null) {
                showError("Please login first")
                return
            }
            
            android.util.Log.d("CheckoutSafe", "Firebase initialized, user: ${auth.currentUser?.uid}")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSafe", "Firebase initialization error", e)
            showError("Firebase error: ${e.message}")
        }
    }
    
    private fun loadCartData() {
        try {
            cartItems = CartManager.getCartItems().filter { item ->
                item.name.isNotBlank() && item.price > 0 && item.quantity > 0
            }
            
            android.util.Log.d("CheckoutSafe", "Loaded ${cartItems.size} valid cart items")
            
            if (cartItems.isEmpty()) {
                showError("Cart is empty")
                return
            }
            
            // Calculate total safely
            var subtotal = 0L
            for (item in cartItems) {
                try {
                    subtotal += item.calculateSubtotal()
                } catch (e: Exception) {
                    android.util.Log.e("CheckoutSafe", "Error calculating subtotal for ${item.name}", e)
                    subtotal += (item.price * item.quantity)
                }
            }
            
            total = subtotal + deliveryFee + serviceFee
            android.util.Log.d("CheckoutSafe", "Total calculated: $total")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSafe", "Error loading cart data", e)
            showError("Error loading cart: ${e.message}")
        }
    }
    
    private fun updateUI() {
        try {
            // Update cart items display
            val itemsText = buildString {
                appendLine("Items in cart:")
                cartItems.forEach { item ->
                    appendLine("• ${item.name} x${item.quantity} - Rp ${String.format("%,d", item.price)}")
                }
                appendLine()
                appendLine("Subtotal: Rp ${String.format("%,d", total - deliveryFee - serviceFee)}")
                appendLine("Delivery: Rp ${String.format("%,d", deliveryFee)}")
                appendLine("Service: Rp ${String.format("%,d", serviceFee)}")
            }
            
            tvCartItems?.text = itemsText
            tvTotal?.text = "TOTAL: Rp ${String.format("%,d", total)}"
            
            android.util.Log.d("CheckoutSafe", "UI updated successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSafe", "Error updating UI", e)
            tvCartItems?.text = "Error displaying items"
            tvTotal?.text = "Error calculating total"
        }
    }
    
    private fun placeOrder() {
        try {
            android.util.Log.d("CheckoutSafe", "Placing order...")
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                showError("Please login first")
                return
            }
            
            btnPlaceOrder?.apply {
                isEnabled = false
                text = "Processing..."
            }
            
            // Create order ID
            val orderId = "ORD-${System.currentTimeMillis()}"
            
            // Get user input - simple address for safe version
            val deliveryAddressString = "Default Address - Please update in profile"
            
            // Create delivery address object
            val deliveryAddress = DeliveryAddress(
                address = deliveryAddressString,
                coordinates = GeoPoint(0.0, 0.0),
                instructions = ""
            )
            
            // Calculate totals
            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val orderTotal = subtotal + deliveryFee + serviceFee
            
            // Create order using correct OrderModel structure
            val order = OrderModel(
                orderId = orderId,
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Customer",
                userPhone = currentUser.phoneNumber ?: "",
                deliveryAddress = deliveryAddress,
                items = cartItems, // Use CartItemModel directly as expected
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                serviceFee = serviceFee,
                discount = 0L,
                totalAmount = orderTotal,
                paymentMethod = "Cash on Delivery",
                orderStatus = "pending",
                estimatedDelivery = Timestamp(Date(System.currentTimeMillis() + 45 * 60 * 1000)), // 45 minutes
                actualDelivery = null,
                driverInfo = null,
                rating = 0.0,
                review = "",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            // Save to Firestore
            firestore.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener {
                    android.util.Log.d("CheckoutSafe", "Order saved successfully")
                    
                    // Clear cart
                    try {
                        CartManager.clearCart()
                    } catch (e: Exception) {
                        android.util.Log.e("CheckoutSafe", "Error clearing cart", e)
                    }
                    
                    // Navigate to order confirmation
                    try {
                        val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                            putExtra("ORDER_ID", orderId)
                            putExtra("TOTAL_AMOUNT", total)
                            putExtra("PAYMENT_METHOD", "Cash on Delivery")
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        android.util.Log.e("CheckoutSafe", "Error navigating to confirmation", e)
                        Toast.makeText(this, "Order placed successfully! ID: $orderId", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("CheckoutSafe", "Error saving order", e)
                    showError("Failed to place order: ${e.message}")
                    
                    btnPlaceOrder?.apply {
                        isEnabled = true
                        text = "Place Order"
                    }
                }
                
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSafe", "Error in placeOrder", e)
            showError("Error placing order: ${e.message}")
            
            btnPlaceOrder?.apply {
                isEnabled = true
                text = "Place Order"
            }
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        android.util.Log.e("CheckoutSafe", message)
        
        // Auto finish on critical errors
        if (message.contains("empty") || message.contains("login")) {
            finish()
        }
    }
}
