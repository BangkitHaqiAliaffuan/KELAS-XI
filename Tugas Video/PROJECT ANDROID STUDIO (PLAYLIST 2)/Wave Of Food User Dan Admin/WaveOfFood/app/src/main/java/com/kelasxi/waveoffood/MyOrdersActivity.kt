package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelasxi.waveoffood.adapter.OrderAdapter
import com.kelasxi.waveoffood.models.OrderModel
import java.util.Date

/**
 * Activity untuk menampilkan riwayat pesanan user
 * Updated untuk menggunakan enhanced layout dengan Material Design 3
 */
class MyOrdersActivity : AppCompatActivity() {
    
    // Updated view references untuk layout baru
    private lateinit var btnBack: MaterialButton
    private lateinit var btnFilter: MaterialButton
    private lateinit var tvTitle: TextView
    private lateinit var rvOrders: RecyclerView
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutEmptyOrders: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var tvEmptyMessage: TextView
    private lateinit var tvErrorMessage: TextView
    private lateinit var btnStartOrdering: MaterialButton
    private lateinit var btnRetry: MaterialButton
    
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
        // Initialize semua views dengan ID yang baru
        btnBack = findViewById(R.id.btnBack)
        btnFilter = findViewById(R.id.btnFilter)
        tvTitle = findViewById(R.id.tvTitle)
        rvOrders = findViewById(R.id.rvOrders)
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders)
        layoutError = findViewById(R.id.layoutError)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        btnStartOrdering = findViewById(R.id.btnStartOrdering)
        btnRetry = findViewById(R.id.btnRetry)
        
        tvTitle.text = "Riwayat Pesanan"
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(ordersList) { order ->
            // Handle order item click - bisa untuk detail order
            showOrderDetail(order)
        }
        
        rvOrders.apply {
            layoutManager = LinearLayoutManager(this@MyOrdersActivity)
            adapter = orderAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        
        btnFilter.setOnClickListener {
            // TODO: Implement filter functionality
            showFilterDialog()
        }
        
        btnStartOrdering.setOnClickListener {
            // Navigate back to main activity or menu
            finish()
        }
        
        btnRetry.setOnClickListener {
            loadUserOrders()
        }
    }
    
    private fun showFilterDialog() {
        // TODO: Implement filter dialog
        // Bisa filter berdasarkan status, tanggal, dll
        Toast.makeText(this, "Filter akan ditambahkan", Toast.LENGTH_SHORT).show()
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
                            "Struktur data berubah. Silakan coba lagi."
                        error.message?.contains("permission", ignoreCase = true) == true -> 
                            "Akses ditolak. Silakan periksa login Anda."
                        error.message?.contains("index", ignoreCase = true) == true -> 
                            "Indeks database hilang. Memuat tanpa pengurutan..."
                        else -> "Error memuat pesanan: ${error.message}"
                    }
                    
                    showErrorState(errorMessage)
                    
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
                    
                    if (ordersList.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContentState()
                    }
                } else {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    showEmptyState()
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
                    
                    if (ordersList.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContentState()
                    }
                } else {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    showEmptyState()
                    Log.d(TAG, "No orders found for user")
                }
            }
    }
    
    private fun showLoading(show: Boolean) {
        if (show) {
            showLoadingState()
        }
        // Content state akan diatur di callback
    }
    
    private fun showLoadingState() {
        layoutLoading.visibility = View.VISIBLE
        rvOrders.visibility = View.GONE
        layoutEmptyOrders.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    private fun showContentState() {
        layoutLoading.visibility = View.GONE
        rvOrders.visibility = View.VISIBLE
        layoutEmptyOrders.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    private fun showEmptyState() {
        layoutLoading.visibility = View.GONE
        rvOrders.visibility = View.GONE
        layoutEmptyOrders.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
        
        tvEmptyMessage.text = "Mulai pesan makanan favorit Anda\ndan riwayat pesanan akan muncul di sini"
    }

    private fun showErrorState(message: String) {
        layoutLoading.visibility = View.GONE
        rvOrders.visibility = View.GONE
        layoutEmptyOrders.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        
        tvErrorMessage.text = message
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
