package com.trashbin.app.ui.marketplace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trashbin.app.R
import com.trashbin.app.data.repository.Result
import com.trashbin.app.ui.adapters.MarketplaceAdapter
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel

class MarketplaceActivity : AppCompatActivity() {
    private val viewModel: MarketplaceViewModel by viewModels()
    
    // UI Components
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var fabCreate: FloatingActionButton
    private lateinit var adapter: MarketplaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Load initial data
        viewModel.loadListings()
    }
    
    private fun setupUI() {
        // Main layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "Marketplace"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt()
            )
        }
        mainLayout.addView(titleText)
        
        // SwipeRefreshLayout with RecyclerView
        swipeRefresh = SwipeRefreshLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(
                (8 * resources.displayMetrics.density).toInt(),
                0,
                (8 * resources.displayMetrics.density).toInt(),
                0
            )
        }
        
        swipeRefresh.addView(recyclerView)
        mainLayout.addView(swipeRefresh)
        
        // Empty view
        emptyView = TextView(this).apply {
            text = "Belum ada listing tersedia"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        
        // Create listing FAB
        val fabLayout = androidx.coordinatorlayout.widget.CoordinatorLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        
        fabCreate = FloatingActionButton(this).apply {
            setImageResource(R.drawable.ic_add) // Assuming this drawable exists
            contentDescription = "Buat listing baru"
            layoutParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.END or android.view.Gravity.BOTTOM
                setMargins(0, 0, (16 * resources.displayMetrics.density).toInt(), 
                    (16 * resources.displayMetrics.density).toInt())
            }
        }
        
        fabLayout.addView(mainLayout)
        fabLayout.addView(emptyView)
        fabLayout.addView(fabCreate)
        
        setContentView(fabLayout)
    }
    
    private fun setupRecyclerView() {
        adapter = MarketplaceAdapter { listing ->
            // Navigate to listing detail
            val intent = Intent(this, ListingDetailActivity::class.java)
            intent.putExtra("listing_id", listing.id)
            startActivity(intent)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.listings.observe(this) { result ->
            when (result) {
                is Result.Loading -> swipeRefresh.isRefreshing = true
                is Result.Success -> {
                    swipeRefresh.isRefreshing = false
                    val listings = result.data.data
                    adapter.submitList(listings)
                    
                    // Show/hide empty view
                    if (listings.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadListings()
        }
        
        fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateListingActivity::class.java))
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        viewModel.loadListings()
    }
}