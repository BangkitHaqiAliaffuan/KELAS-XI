package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.adapter.OrderDetailAdapter
import com.kelasxi.waveoffood.models.OrderModel
import com.kelasxi.waveoffood.models.OrderItemModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity untuk menampilkan detail order
 */
class OrderDetailActivity : AppCompatActivity() {
    
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvOrderIdDetail: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvEstimatedDelivery: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private val db = FirebaseFirestore.getInstance()
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    companion object {
        private const val TAG = "OrderDetailActivity"
        const val EXTRA_ORDER_ID = "order_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        
        val orderId = intent.getStringExtra(EXTRA_ORDER_ID)
        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadOrderDetail(orderId)
    }
    
    private fun initializeViews() {
        ivBack = findViewById(R.id.iv_back)
        tvTitle = findViewById(R.id.tv_title)
        tvOrderIdDetail = findViewById(R.id.tv_order_id_detail)
        tvOrderDate = findViewById(R.id.tv_order_date)
        tvOrderStatus = findViewById(R.id.tv_order_status)
        tvCustomerName = findViewById(R.id.tv_customer_name)
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address)
        tvPaymentMethod = findViewById(R.id.tv_payment_method)
        tvEstimatedDelivery = findViewById(R.id.tv_estimated_delivery)
        recyclerView = findViewById(R.id.rv_order_items)
        tvSubtotal = findViewById(R.id.tv_subtotal)
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee)
        tvTotalAmount = findViewById(R.id.tv_total_amount)
        progressBar = findViewById(R.id.progress_bar)
        
        tvTitle.text = "Order Details"
    }
    
    private fun setupRecyclerView() {
        orderDetailAdapter = OrderDetailAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = orderDetailAdapter
        }
    }
    
    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun loadOrderDetail(orderId: String) {
        showLoading(true)
        
        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                showLoading(false)
                
                if (document.exists()) {
                    try {
                        Log.d(TAG, "Order document data: ${document.data}")
                        
                        val order = document.toObject(OrderModel::class.java)
                        if (order != null) {
                            Log.d(TAG, "Successfully parsed order: ${order.orderId}")
                            Log.d(TAG, "Order total: ${order.totalAmount}")
                            displayOrderDetail(order)
                        } else {
                            Log.e(TAG, "Failed to parse order object from document")
                            Toast.makeText(this, "Error parsing order data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing order detail", e)
                        Toast.makeText(this, "Error loading order details: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { error ->
                showLoading(false)
                Log.e(TAG, "Error loading order detail", error)
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun displayOrderDetail(order: OrderModel) {
        // Order basic info
        tvOrderIdDetail.text = "#${order.orderId.takeLast(8).uppercase()}"
        val orderDate = order.createdAt.toDate()
        tvOrderDate.text = "Ordered on ${dateFormat.format(orderDate)}"
        tvOrderStatus.text = order.getOrderStatusDisplay()
        
        // Customer info
        tvCustomerName.text = order.userName.ifEmpty { "Customer" }
        tvDeliveryAddress.text = order.deliveryAddress.ifEmpty { "No address provided" }
        tvPaymentMethod.text = order.paymentMethod
        tvEstimatedDelivery.text = order.estimatedDelivery.ifEmpty { "TBD" }
        
        // Order items - directly use order.items since they are OrderItemModel
        orderDetailAdapter.updateItems(order.items)
        
        // Price breakdown
        tvSubtotal.text = "Rp ${String.format("%,.0f", order.subtotal)}"
        tvDeliveryFee.text = "Rp ${String.format("%,.0f", order.deliveryFee)}"
        tvTotalAmount.text = "Rp ${String.format("%,.0f", order.totalAmount)}"
        
        // Status color
        val statusColor = when (order.orderStatus.lowercase()) {
            "pending" -> android.graphics.Color.parseColor("#FF9800")
            "confirmed" -> android.graphics.Color.parseColor("#2196F3")
            "preparing" -> android.graphics.Color.parseColor("#9C27B0")
            "delivering" -> android.graphics.Color.parseColor("#4CAF50")
            "completed" -> android.graphics.Color.parseColor("#4CAF50")
            "cancelled" -> android.graphics.Color.parseColor("#F44336")
            else -> android.graphics.Color.parseColor("#757575")
        }
        tvOrderStatus.setTextColor(statusColor)
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
