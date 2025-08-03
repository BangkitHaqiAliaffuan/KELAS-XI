package com.kelasxi.waveoffood

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelasxi.waveoffood.adapter.OrderAdapter
import com.kelasxi.waveoffood.model.OrderModel
import java.util.Date

/**
 * Activity untuk menampilkan riwayat pesanan user
 */
class MyOrdersActivity : AppCompatActivity() {
    
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyOrdersLayout: View
    private lateinit var tvEmptyMessage: TextView
    
    private lateinit var orderAdapter: OrderAdapter
    private val ordersList = mutableListOf<OrderModel>()
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "MyOrdersActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadUserOrders()
    }
    
    private fun initializeViews() {
        ivBack = findViewById(R.id.iv_back)
        tvTitle = findViewById(R.id.tv_title)
        recyclerView = findViewById(R.id.rv_orders)
        progressBar = findViewById(R.id.progress_bar)
        emptyOrdersLayout = findViewById(R.id.layout_empty_orders)
        tvEmptyMessage = findViewById(R.id.tv_empty_message)
        
        tvTitle.text = "My Orders"
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(ordersList) { order ->
            // Handle order item click - bisa untuk detail order
            showOrderDetail(order)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyOrdersActivity)
            adapter = orderAdapter
        }
    }
    
    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun loadUserOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view orders", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        showLoading(true)
        
        db.collection("orders")
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                showLoading(false)
                
                if (error != null) {
                    Log.e(TAG, "Error loading orders", error)
                    
                    // Show more specific error message
                    val errorMessage = when {
                        error.message?.contains("precondition", ignoreCase = true) == true -> 
                            "Data structure error. Please try again."
                        error.message?.contains("permission", ignoreCase = true) == true -> 
                            "Permission denied. Please check your login."
                        error.message?.contains("index", ignoreCase = true) == true -> 
                            "Database index missing. Loading without sorting..."
                        else -> "Error loading orders: ${error.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    
                    // If it's an index error, try without orderBy
                    if (error.message?.contains("index", ignoreCase = true) == true) {
                        loadOrdersWithoutSorting()
                    }
                    return@addSnapshotListener
                }
                
                if (snapshot != null && !snapshot.isEmpty) {
                    ordersList.clear()
                    
                    for (document in snapshot.documents) {
                        try {
                            Log.d(TAG, "Processing document: ${document.id}")
                            Log.d(TAG, "Document data: ${document.data}")
                            
                            // Try to parse with better error handling
                            val orderData = document.data
                            if (orderData != null) {
                                val order = document.toObject(OrderModel::class.java)
                                if (order != null) {
                                    Log.d(TAG, "Parsed order: ${order.orderId}, total: ${order.totalAmount}")
                                    ordersList.add(order)
                                } else {
                                    Log.w(TAG, "Failed to parse order from document: ${document.id}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing order: ${document.id}", e)
                            // Continue with other orders even if one fails
                        }
                    }
                    
                    Log.d(TAG, "Loaded ${ordersList.size} orders")
                    orderAdapter.notifyDataSetChanged()
                    
                    showEmptyState(ordersList.isEmpty())
                } else {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    showEmptyState(true)
                    Log.d(TAG, "No orders found for user")
                }
            }
    }
    
    /**
     * Load orders without sorting (fallback for index errors)
     */
    private fun loadOrdersWithoutSorting() {
        val currentUser = auth.currentUser ?: return
        
        showLoading(true)
        
        db.collection("orders")
            .whereEqualTo("userId", currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                showLoading(false)
                
                if (error != null) {
                    Log.e(TAG, "Error loading orders without sorting", error)
                    Toast.makeText(this, "Error loading orders: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                
                if (snapshot != null && !snapshot.isEmpty) {
                    ordersList.clear()
                    
                    for (document in snapshot.documents) {
                        try {
                            Log.d(TAG, "Processing document: ${document.id}")
                            
                            val order = document.toObject(OrderModel::class.java)
                            if (order != null) {
                                Log.d(TAG, "Parsed order: ${order.orderId}, total: ${order.totalAmount}")
                                ordersList.add(order)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing order: ${document.id}", e)
                        }
                    }
                    
                    // Sort manually by createdAt if available
                    ordersList.sortByDescending { it.createdAt?.toDate() ?: Date(0) }
                    
                    Log.d(TAG, "Loaded ${ordersList.size} orders without sorting")
                    orderAdapter.notifyDataSetChanged()
                    
                    showEmptyState(ordersList.isEmpty())
                } else {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    showEmptyState(true)
                    Log.d(TAG, "No orders found for user")
                }
            }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }
    
    private fun showEmptyState(isEmpty: Boolean) {
        emptyOrdersLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        
        if (isEmpty) {
            tvEmptyMessage.text = "No orders yet!\nStart ordering some delicious food."
        }
    }
    
    private fun showOrderDetail(order: OrderModel) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.orderId)
        startActivity(intent)
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
