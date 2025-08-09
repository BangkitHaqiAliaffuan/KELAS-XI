package com.kelasxi.waveoffood

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kelasxi.waveoffood.adapters.OrderHistoryAdapter
import com.kelasxi.waveoffood.models.OrderModel

/**
 * Activity untuk menampilkan riwayat pesanan dengan design enhanced
 */
class OrderHistoryActivity : AppCompatActivity() {
    
    private lateinit var btnBack: ImageButton
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var chipAll: Chip
    private lateinit var chipDelivered: Chip
    private lateinit var chipInProgress: Chip
    private lateinit var chipCancelled: Chip
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    
    private lateinit var orderAdapter: OrderHistoryAdapter
    private val orderList = mutableListOf<OrderModel>()
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history_enhanced)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        setupSwipeRefresh()
        loadOrderHistory()
    }
    
    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        chipGroupFilter = findViewById(R.id.chipGroupFilter)
        chipAll = findViewById(R.id.chipAll)
        chipDelivered = findViewById(R.id.chipDelivered)
        chipInProgress = findViewById(R.id.chipInProgress)
        chipCancelled = findViewById(R.id.chipCancelled)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvOrderHistory = findViewById(R.id.rvOrderHistory)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderHistoryAdapter(orderList) { order ->
            // Handle order item click
            showOrderDetail(order)
        }
        
        rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = orderAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        
        // Filter chip listeners
        chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedChip = findViewById<Chip>(checkedIds.firstOrNull() ?: R.id.chipAll)
            filterOrders(selectedChip.text.toString())
        }
    }
    
    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            loadOrderHistory()
        }
    }
    
    private fun loadOrderHistory() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showEmptyState()
            return
        }
        
        swipeRefresh.isRefreshing = true
        
        firestore.collection("orders")
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("orderDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                orderList.clear()
                for (document in documents) {
                    try {
                        val order = document.toObject(OrderModel::class.java)
                        val orderWithId = order.copy(id = document.id)
                        orderList.add(orderWithId)
                    } catch (e: Exception) {
                        android.util.Log.e("OrderHistory", "Error parsing order", e)
                    }
                }
                
                updateUI()
                swipeRefresh.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("OrderHistory", "Error loading orders", exception)
                showEmptyState()
                swipeRefresh.isRefreshing = false
            }
    }
    
    private fun filterOrders(filter: String) {
        val filteredList = when (filter) {
            "Delivered" -> orderList.filter { it.status == "delivered" }
            "In Progress" -> orderList.filter { it.status in listOf("pending", "preparing", "on_the_way") }
            "Cancelled" -> orderList.filter { it.status == "cancelled" }
            else -> orderList
        }
        
        orderAdapter.updateData(filteredList)
        updateEmptyState(filteredList.isEmpty())
    }
    
    private fun updateUI() {
        orderAdapter.notifyDataSetChanged()
        updateEmptyState(orderList.isEmpty())
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }
    
    private fun showEmptyState() {
        rvOrderHistory.visibility = View.GONE
        layoutEmptyState.visibility = View.VISIBLE
    }
    
    private fun hideEmptyState() {
        rvOrderHistory.visibility = View.VISIBLE
        layoutEmptyState.visibility = View.GONE
    }
    
    private fun showOrderDetail(order: OrderModel) {
        // TODO: Implement order detail view
        android.util.Log.d("OrderHistory", "Show detail for order: ${order.id}")
    }
}
