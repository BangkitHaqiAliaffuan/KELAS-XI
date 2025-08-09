package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.adapter.CheckoutItemAdapter
import com.kelasxi.waveoffood.models.CartItemModel
import com.kelasxi.waveoffood.utils.CartManager
import java.text.NumberFormat
import java.util.*

/**
 * Enhanced CheckoutActivity using activity_checkout_new.xml layout
 */
class CheckoutActivityNew : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    // UI Components
    private lateinit var ivBack: ImageButton
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var etCustomerPhone: TextInputEditText
    private lateinit var etCustomerAddress: TextInputEditText
    private lateinit var spPaymentMethod: Spinner
    private lateinit var rvCheckoutItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    
    // Data
    private var cartItems = mutableListOf<CartItemModel>()
    private var subtotal = 0.0
    private val deliveryFee = 5000.0
    private var total = 0.0
    
    private lateinit var checkoutAdapter: CheckoutItemAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_checkout_new)
            
            initializeFirebase()
            initializeViews()
            loadCartData()
            setupRecyclerView()
            setupPaymentSpinner()
            setupClickListeners()
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error in onCreate", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }
    
    private fun initializeViews() {
        try {
            ivBack = findViewById(R.id.iv_back)
            etCustomerName = findViewById(R.id.et_customer_name)
            etCustomerPhone = findViewById(R.id.et_customer_phone)
            etCustomerAddress = findViewById(R.id.et_customer_address)
            spPaymentMethod = findViewById(R.id.sp_payment_method)
            rvCheckoutItems = findViewById(R.id.rv_checkout_items)
            tvSubtotal = findViewById(R.id.tvSubtotal)
            tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
            tvTotal = findViewById(R.id.tvTotal)
            btnPlaceOrder = findViewById(R.id.btn_place_order)
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error initializing views", e)
            throw e
        }
    }
    
    private fun loadCartData() {
        try {
            val items = CartManager.getCartItems()
            cartItems.clear()
            cartItems.addAll(items)
            
            android.util.Log.d("CheckoutNew", "Loaded ${cartItems.size} items to checkout")
            cartItems.forEachIndexed { index, item ->
                android.util.Log.d("CheckoutNew", "Item $index: ${item.foodName} - ${item.foodPrice} x ${item.quantity}")
            }
            
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            calculateTotal()
            updatePriceDisplay()
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error loading cart data", e)
            Toast.makeText(this, "Error loading cart: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        try {
            // Use simple adapter for read-only checkout display
            checkoutAdapter = CheckoutItemAdapter(cartItems)
            rvCheckoutItems.apply {
                layoutManager = LinearLayoutManager(this@CheckoutActivityNew)
                adapter = checkoutAdapter
                isNestedScrollingEnabled = false
            }
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error setting up RecyclerView", e)
            Toast.makeText(this, "Error loading items", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupPaymentSpinner() {
        val paymentMethods = arrayOf("Cash on Delivery", "Transfer Bank", "E-Wallet")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaymentMethod.adapter = adapter
    }
    
    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            finish()
        }
        
        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }
    
    private fun calculateTotal() {
        try {
            subtotal = cartItems.sumOf { item ->
                try {
                    // Clean price string more thoroughly
                    val cleanPrice = item.foodPrice
                        .replace("Rp", "")
                        .replace(".", "")  
                        .replace(",", "")
                        .replace(" ", "")
                        .trim()
                    
                    val price = cleanPrice.toDoubleOrNull() ?: 0.0
                    price * item.quantity
                } catch (e: Exception) {
                    android.util.Log.e("CheckoutNew", "Error parsing price for item: ${item.foodName}, price: ${item.foodPrice}", e)
                    0.0
                }
            }
            total = subtotal + deliveryFee
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error calculating total", e)
            subtotal = 0.0
            total = deliveryFee
        }
    }
    
    private fun updatePriceDisplay() {
        try {
            tvSubtotal.text = formatToRupiah(subtotal.toLong())
            tvDeliveryFee.text = formatToRupiah(deliveryFee.toLong())
            tvTotal.text = formatToRupiah(total.toLong())
        } catch (e: Exception) {
            android.util.Log.e("CheckoutNew", "Error updating price display", e)
            // Set fallback values
            tvSubtotal.text = "Rp 0"
            tvDeliveryFee.text = "Rp 5.000"
            tvTotal.text = "Rp 5.000"
        }
    }
    
    private fun formatToRupiah(amount: Long): String {
        val numberFormat = NumberFormat.getInstance(Locale("id", "ID"))
        return "Rp ${numberFormat.format(amount)}"
    }
    
    private fun placeOrder() {
        val customerName = etCustomerName.text.toString().trim()
        val customerPhone = etCustomerPhone.text.toString().trim()
        val customerAddress = etCustomerAddress.text.toString().trim()
        val paymentMethod = spPaymentMethod.selectedItem.toString()
        
        // Validation
        if (customerName.isEmpty()) {
            etCustomerName.error = "Nama tidak boleh kosong"
            etCustomerName.requestFocus()
            return
        }
        
        if (customerPhone.isEmpty()) {
            etCustomerPhone.error = "Nomor telepon tidak boleh kosong"
            etCustomerPhone.requestFocus()
            return
        }
        
        if (customerAddress.isEmpty()) {
            etCustomerAddress.error = "Alamat tidak boleh kosong"
            etCustomerAddress.requestFocus()
            return
        }
        
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading
        btnPlaceOrder.isEnabled = false
        btnPlaceOrder.text = "Memproses..."
        
        // Create order
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            btnPlaceOrder.isEnabled = true
            btnPlaceOrder.text = "Pesan Sekarang"
            return
        }
        
        val orderId = "ORD-${System.currentTimeMillis()}"
        val orderData = hashMapOf(
            "orderId" to orderId,
            "userId" to currentUser.uid,
            "customerName" to customerName,
            "customerPhone" to customerPhone,
            "customerAddress" to customerAddress,
            "paymentMethod" to paymentMethod,
            "items" to cartItems.map { item ->
                hashMapOf(
                    "name" to item.foodName,
                    "price" to item.foodPrice,
                    "quantity" to item.quantity,
                    "image" to item.foodImage
                )
            },
            "subtotal" to subtotal,
            "deliveryFee" to deliveryFee,
            "total" to total,
            "status" to "pending",
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        
        firestore.collection("orders")
            .document(orderId)
            .set(orderData)
            .addOnSuccessListener {
                // Clear cart
                CartManager.clearCart()
                
                // Navigate to confirmation
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.putExtra("orderId", orderId)
                intent.putExtra("total", total)
                intent.putExtra("paymentMethod", paymentMethod)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("CheckoutNew", "Error placing order", e)
                Toast.makeText(this, "Gagal membuat pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                btnPlaceOrder.isEnabled = true
                btnPlaceOrder.text = "Pesan Sekarang"
            }
    }
}
