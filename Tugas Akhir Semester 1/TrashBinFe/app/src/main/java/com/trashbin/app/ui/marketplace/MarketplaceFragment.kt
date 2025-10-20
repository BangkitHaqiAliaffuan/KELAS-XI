package com.trashbin.app.ui.marketplace

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trashbin.app.R
import com.trashbin.app.ui.adapters.MarketplaceAdapter
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel
import com.trashbin.app.data.repository.Result

class MarketplaceFragment : Fragment() {
    private val viewModel: MarketplaceViewModel by activityViewModels()
    private lateinit var adapter: MarketplaceAdapter
    
    // UI Components
    private lateinit var coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout
    private lateinit var appBarLayout: com.google.android.material.appbar.AppBarLayout
    private lateinit var searchView: SearchView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCreate: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create CoordinatorLayout as root
        coordinatorLayout = androidx.coordinatorlayout.widget.CoordinatorLayout(requireContext())

        // AppBarLayout with SearchView
        appBarLayout = com.google.android.material.appbar.AppBarLayout(requireContext()).apply {
            layoutParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.MATCH_PARENT,
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
        }

        searchView = SearchView(requireContext()).apply {
            layoutParams = com.google.android.material.appbar.AppBarLayout.LayoutParams(
                com.google.android.material.appbar.AppBarLayout.LayoutParams.MATCH_PARENT,
                com.google.android.material.appbar.AppBarLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Add hint for search
                queryHint = "Cari barang bekas..."
            }
        }

        appBarLayout.addView(searchView)

        // Create SwipeRefreshLayout
        swipeRefresh = SwipeRefreshLayout(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.MATCH_PARENT,
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.MATCH_PARENT
            ).apply {
                // Remove the behavior line for the abstract class
            }
        }

        // Create RecyclerView
        recyclerView = RecyclerView(requireContext()).apply {
            id = View.generateViewId()
            layoutManager = GridLayoutManager(requireContext(), 2)
            setPadding((8 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt())
        }

        swipeRefresh.addView(recyclerView)

        // Create FAB
        fabCreate = FloatingActionButton(requireContext()).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.ic_add)
            contentDescription = "Buat listing baru"
            val fabParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END or Gravity.BOTTOM
                setMargins(0, 0, (16 * resources.displayMetrics.density).toInt(), 
                    (16 * resources.displayMetrics.density).toInt())
            }
            layoutParams = fabParams
        }

        coordinatorLayout.addView(appBarLayout)
        coordinatorLayout.addView(swipeRefresh)
        coordinatorLayout.addView(fabCreate)

        return coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        setupListeners()
        
        // Load initial listings
        viewModel.loadListings()
    }
    
    private fun setupRecyclerView() {
        adapter = MarketplaceAdapter { listing ->
            // Navigate to listing detail
            val intent = android.content.Intent(requireContext(), ListingDetailActivity::class.java)
            intent.putExtra("listing_id", listing.id)
            requireContext().startActivity(intent)
        }
        
        recyclerView.adapter = adapter
    }
    
    private fun observeViewModel() {
        viewModel.listings.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is Result.Success -> {
                    swipeRefresh.isRefreshing = false
                    val listings = result.data.data
                    android.util.Log.d("MarketplaceFragment", "Data received: ${listings.size} items")
                    adapter.submitList(listings)
                    if (listings.isEmpty()) {
                        Toast.makeText(requireContext(), "Tidak ada data listing", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Berhasil memuat ${listings.size} listing", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    swipeRefresh.isRefreshing = false
                    android.util.Log.e("MarketplaceFragment", "Error: ${result.message}")
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.loadListings(search = it)
                    } else {
                        viewModel.loadListings()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optional: Implement real-time search with debounce
                return false
            }
        })
        
        fabCreate.setOnClickListener {
            val intent = android.content.Intent(requireContext(), CreateListingActivity::class.java)
            requireContext().startActivity(intent)
        }
        
        swipeRefresh.setOnRefreshListener {
            viewModel.loadListings()
        }
    }
}