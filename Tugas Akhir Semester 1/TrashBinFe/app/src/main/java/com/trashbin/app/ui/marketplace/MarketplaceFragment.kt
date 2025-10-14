package com.trashbin.app.ui.marketplace

import android.os.Bundle
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
import com.trashbin.app.utils.Result

class MarketplaceFragment : Fragment() {
    private val viewModel: MarketplaceViewModel by activityViewModels()
    private lateinit var adapter: MarketplaceAdapter
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var fabCreate: FloatingActionButton
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_marketplace, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        observeViewModel()
        setupListeners()
        
        // Load initial listings
        viewModel.loadListings()
    }
    
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        searchView = view.findViewById(R.id.search_view)
        fabCreate = view.findViewById(R.id.fab_create)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
    }
    
    private fun setupRecyclerView() {
        adapter = MarketplaceAdapter { listing ->
            // Navigate to listing detail
            val intent = android.content.Intent(requireContext(), ListingDetailActivity::class.java)
            intent.putExtra("listing_id", listing.id)
            requireContext().startActivity(intent)
        }
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
    
    private fun observeViewModel() {
        viewModel.listings.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> swipeRefresh.isRefreshing = true
                is Result.Success -> {
                    swipeRefresh.isRefreshing = false
                    adapter.submitList(result.data.listings)
                }
                is Result.Error -> {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
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