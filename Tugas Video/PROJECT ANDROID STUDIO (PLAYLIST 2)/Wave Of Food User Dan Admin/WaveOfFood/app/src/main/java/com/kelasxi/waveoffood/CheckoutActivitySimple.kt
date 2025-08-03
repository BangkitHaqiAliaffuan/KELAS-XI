package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.model.CartItemModel
import com.kelasxi.waveoffood.utils.CartManager
import java.text.NumberFormat
import java.util.*

/**
 * Simple and stable CheckoutActivity to avoid force close issues
 */
class CheckoutActivitySimple : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    // UI Components
    private lateinit var etCustomerName: EditText
    private lateinit var etCustomerPhone: EditText
    private lateinit var etCustomerAddress: EditText
    private lateinit var spPaymentMethod: Spinner
    private lateinit var tvOrderSummary: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    private lateinit var btnBack: Button
    
    // Data
    private var cartItems = mutableListOf<CartItemModel>()
    private var total = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            createSimpleLayout()
            initializeFirebase()
            loadCartData()
            setupUI()
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSimple", "Error in onCreate", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun createSimpleLayout() {
        // Create layout programmatically to avoid XML issues
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Back button
        btnBack = Button(this).apply {
            text = "← Kembali"
            textSize = 16f
            setOnClickListener { finish() }
        }
        mainLayout.addView(btnBack)
        
        // Title
        val titleText = TextView(this).apply {
            text = "Checkout"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 24)
        }
        mainLayout.addView(titleText)
        
        // Customer Info Section
        val customerInfoTitle = TextView(this).apply {
            text = "Informasi Pemesan"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 8)
        }
        mainLayout.addView(customerInfoTitle)
        
        // Customer Name
        val nameLabel = TextView(this).apply {
            text = "Nama Lengkap *"
            textSize = 14f
            setPadding(0, 8, 0, 4)
        }
        mainLayout.addView(nameLabel)
        
        etCustomerName = EditText(this).apply {
            hint = "Masukkan nama lengkap"
            setPadding(16, 16, 16, 16)
            textSize = 16f
        }
        mainLayout.addView(etCustomerName)
        
        // Customer Phone
        val phoneLabel = TextView(this).apply {
            text = "Nomor Telepon *"
            textSize = 14f
            setPadding(0, 16, 0, 4)
        }
        mainLayout.addView(phoneLabel)
        
        etCustomerPhone = EditText(this).apply {
            hint = "Masukkan nomor telepon"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setPadding(16, 16, 16, 16)
            textSize = 16f
        }
        mainLayout.addView(etCustomerPhone)
        
        // Customer Address
        val addressLabel = TextView(this).apply {
            text = "Alamat Lengkap *"
            textSize = 14f
            setPadding(0, 16, 0, 4)
        }
        mainLayout.addView(addressLabel)
        
        etCustomerAddress = EditText(this).apply {
            hint = "Masukkan alamat lengkap"
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 2
            maxLines = 4
            setPadding(16, 16, 16, 16)
            textSize = 16f
        }
        mainLayout.addView(etCustomerAddress)
        
        // Payment Method
        val paymentLabel = TextView(this).apply {
            text = "Metode Pembayaran *"
            textSize = 14f
            setPadding(0, 16, 0, 4)
        }
        mainLayout.addView(paymentLabel)
        
        spPaymentMethod = Spinner(this).apply {
            setPadding(16, 16, 16, 16)
        }
        mainLayout.addView(spPaymentMethod)
        
        // Order Summary
        val summaryTitle = TextView(this).apply {
            text = "Ringkasan Pesanan"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        }
        mainLayout.addView(summaryTitle)
        
        tvOrderSummary = TextView(this).apply {
            text = "Loading..."
            textSize = 14f
            setPadding(0, 8, 0, 8)
        }
        mainLayout.addView(tvOrderSummary)
        
        // Total
        tvTotal = TextView(this).apply {
            text = "Total: Rp 0"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(tvTotal)
        
        // Place Order Button
        btnPlaceOrder = MaterialButton(this).apply {
            text = "Pesan Sekarang"
            textSize = 16f
            setPadding(0, 20, 0, 20)
            setOnClickListener { processOrder() }
        }
        mainLayout.addView(btnPlaceOrder)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        if (auth.currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    
    private fun loadCartData() {
        try {
            cartItems = CartManager.getCartItems().filter { 
                it.name.isNotBlank() && it.price > 0 && it.quantity > 0 
            }.toMutableList()
            
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            android.util.Log.d("CheckoutSimple", "Loaded ${cartItems.size} cart items")
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSimple", "Error loading cart data", e)
            Toast.makeText(this, "Error loading cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupUI() {
        try {
            setupPaymentSpinner()
            updateOrderSummary()
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSimple", "Error setting up UI", e)
        }
    }
    
    private fun setupPaymentSpinner() {
        val paymentMethods = arrayOf(
            "Pilih Metode Pembayaran",
            "Cash on Delivery (COD)",
            "Transfer Bank",
            "OVO",
            "GoPay",
            "DANA"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaymentMethod.adapter = adapter
    }
    
    private fun updateOrderSummary() {
        try {
            val formatter = NumberFormat.getInstance(Locale("id", "ID"))
            val summaryBuilder = StringBuilder()
            
            var subtotal = 0L
            for ((index, item) in cartItems.withIndex()) {
                val itemTotal = item.price * item.quantity
                subtotal += itemTotal
                
                summaryBuilder.append("${index + 1}. ${item.name}\n")
                summaryBuilder.append("   ${item.quantity}x @ Rp ${formatter.format(item.price)} = Rp ${formatter.format(itemTotal)}\n\n")
            }
            
            val deliveryFee = 10000L
            val serviceFee = 2000L
            total = subtotal + deliveryFee + serviceFee
            
            summaryBuilder.append("Subtotal: Rp ${formatter.format(subtotal)}\n")
            summaryBuilder.append("Biaya Antar: Rp ${formatter.format(deliveryFee)}\n")
            summaryBuilder.append("Biaya Layanan: Rp ${formatter.format(serviceFee)}\n")
            
            tvOrderSummary.text = summaryBuilder.toString()
            tvTotal.text = "Total: Rp ${formatter.format(total)}"
            
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSimple", "Error updating order summary", e)
            tvOrderSummary.text = "Error loading order summary"
            tvTotal.text = "Total: Rp 0"
        }
    }
    
    private fun processOrder() {
        try {
            // Validate form
            val name = etCustomerName.text.toString().trim()
            val phone = etCustomerPhone.text.toString().trim()
            val address = etCustomerAddress.text.toString().trim()
            val paymentIndex = spPaymentMethod.selectedItemPosition
            
            if (name.isEmpty()) {
                etCustomerName.error = "Nama wajib diisi"
                etCustomerName.requestFocus()
                return
            }
            
            if (phone.isEmpty()) {
                etCustomerPhone.error = "Nomor telepon wajib diisi"
                etCustomerPhone.requestFocus()
                return
            }
            
            if (address.isEmpty()) {
                etCustomerAddress.error = "Alamat wajib diisi"
                etCustomerAddress.requestFocus()
                return
            }
            
            if (paymentIndex == 0) {
                Toast.makeText(this, "Pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Create order
            val orderId = "ORDER_${System.currentTimeMillis()}"
            val orderData = hashMapOf(
                "orderId" to orderId,
                "userId" to auth.currentUser?.uid,
                "customerName" to name,
                "customerPhone" to phone,
                "customerAddress" to address,
                "paymentMethod" to spPaymentMethod.selectedItem.toString(),
                "items" to cartItems.map { item ->
                    hashMapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "quantity" to item.quantity,
                        "imageUrl" to item.imageUrl
                    )
                },
                "subtotal" to (total - 12000L), // minus delivery and service fee
                "deliveryFee" to 10000L,
                "serviceFee" to 2000L,
                "total" to total,
                "status" to "pending",
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            
            // Show loading
            btnPlaceOrder.isEnabled = false
            btnPlaceOrder.text = "Memproses..."
            
            // Save to Firestore
            firestore.collection("orders")
                .document(orderId)
                .set(orderData)
                .addOnSuccessListener {
                    android.util.Log.d("CheckoutSimple", "Order saved successfully")
                    
                    // Clear cart
                    CartManager.clearCart()
                    
                    // Show success message
                    Toast.makeText(this, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
                    
                    // Navigate back or to order confirmation
                    finish()
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("CheckoutSimple", "Error saving order", e)
                    Toast.makeText(this, "Gagal membuat pesanan: ${e.message}", Toast.LENGTH_LONG).show()
                    
                    // Reset button
                    btnPlaceOrder.isEnabled = true
                    btnPlaceOrder.text = "Pesan Sekarang"
                }
                
        } catch (e: Exception) {
            android.util.Log.e("CheckoutSimple", "Error processing order", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            
            // Reset button
            btnPlaceOrder.isEnabled = true
            btnPlaceOrder.text = "Pesan Sekarang"
        }
    }
}
