package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * Activity untuk menampilkan konfirmasi pesanan setelah checkout berhasil
 */
class OrderConfirmationActivity : AppCompatActivity() {
    
    private lateinit var ivBack: ImageView
    private lateinit var tvOrderId: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvEstimatedTime: TextView
    private lateinit var btnBackToHome: MaterialButton
    private lateinit var btnTrackOrder: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)
        
        initializeViews()
        setupClickListeners()
        displayOrderInfo()
    }
    
    private fun initializeViews() {
        ivBack = findViewById(R.id.iv_back)
        tvOrderId = findViewById(R.id.tv_order_id)
        tvTotalAmount = findViewById(R.id.tv_total_amount)
        tvPaymentMethod = findViewById(R.id.tv_payment_method)
        tvOrderStatus = findViewById(R.id.tv_order_status)
        tvEstimatedTime = findViewById(R.id.tv_estimated_time)
        btnBackToHome = findViewById(R.id.btn_back_to_home)
        btnTrackOrder = findViewById(R.id.btn_track_order)
    }
    
    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            navigateToHome()
        }
        
        btnBackToHome.setOnClickListener {
            navigateToHome()
        }
        
        btnTrackOrder.setOnClickListener {
            // TODO: Implement order tracking
            android.widget.Toast.makeText(this, "Fitur tracking pesanan akan segera tersedia", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun displayOrderInfo() {
        val orderId = intent.getStringExtra("ORDER_ID") ?: ""
        val totalAmount = intent.getLongExtra("TOTAL_AMOUNT", 0L)
        val paymentMethod = intent.getStringExtra("PAYMENT_METHOD") ?: "Cash on Delivery"
        
        tvOrderId.text = orderId
        tvTotalAmount.text = "Rp ${String.format("%,d", totalAmount)}"
        tvPaymentMethod.text = paymentMethod
        tvOrderStatus.text = "Menunggu Konfirmasi"
        tvEstimatedTime.text = "30-45 menit"
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, MainActivityCompose::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToHome()
    }
}
