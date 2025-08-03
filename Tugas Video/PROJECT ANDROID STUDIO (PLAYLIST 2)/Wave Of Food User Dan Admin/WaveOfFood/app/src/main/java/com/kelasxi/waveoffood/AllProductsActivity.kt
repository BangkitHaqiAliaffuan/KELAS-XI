package com.kelasxi.waveoffood

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.adapter.AllProductsAdapter
import com.kelasxi.waveoffood.models.FoodItemModel
import com.kelasxi.waveoffood.utils.CartManager

/**
 * Activity untuk menampilkan semua produk dengan filter kategori
 */
class AllProductsActivity : AppCompatActivity() {
    
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var spinnerCategory: Spinner
    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    
    private lateinit var allProductsAdapter: AllProductsAdapter
    private val db = FirebaseFirestore.getInstance()
    
    private val allProducts = mutableListOf<FoodItemModel>()
    private val filteredProducts = mutableListOf<FoodItemModel>()
    private val categories = mutableListOf<String>()
    
    companion object {
        private const val TAG = "AllProductsActivity"
        private const val ALL_CATEGORIES = "All Categories"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        setupSpinner()
        setupSearch()
        loadProducts()
    }
    
    private fun initializeViews() {
        ivBack = findViewById(R.id.iv_back)
        tvTitle = findViewById(R.id.tv_title)
        spinnerCategory = findViewById(R.id.spinner_category)
        etSearch = findViewById(R.id.et_search)
        recyclerView = findViewById(R.id.rv_products)
        progressBar = findViewById(R.id.progress_bar)
        tvEmptyState = findViewById(R.id.tv_empty_state)
        
        tvTitle.text = "All Products"
    }
    
    private fun setupRecyclerView() {
        allProductsAdapter = AllProductsAdapter(filteredProducts) { foodItem ->
            // Handle product click - add to cart or show details
            addToCart(foodItem)
        }
        
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@AllProductsActivity, 2)
            adapter = allProductsAdapter
        }
    }
    
    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupSpinner() {
        // Initialize with default categories
        categories.clear()
        categories.add(ALL_CATEGORIES)
        
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                filterProducts(selectedCategory, etSearch.text.toString())
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(getSelectedCategory(), s.toString())
            }
            
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
    
    private fun loadProducts() {
        showLoading(true)
        
        db.collection("menu")
            .get()
            .addOnSuccessListener { documents ->
                showLoading(false)
                
                allProducts.clear()
                val categorySet = mutableSetOf<String>()
                
                for (document in documents) {
                    try {
                        val foodItem = document.toObject(FoodItemModel::class.java)
                        allProducts.add(foodItem)
                        
                        // Collect unique categories
                        if (foodItem.foodCategory?.isNotEmpty() == true) {
                            categorySet.add(foodItem.foodCategory)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing menu item: ${document.id}", e)
                    }
                }
                
                // Update categories spinner
                updateCategories(categorySet.sorted())
                
                // Initial filter - show all products
                filterProducts(ALL_CATEGORIES, "")
                
                Log.d(TAG, "Loaded ${allProducts.size} products")
            }
            .addOnFailureListener { error ->
                showLoading(false)
                Log.e(TAG, "Error loading products", error)
                Toast.makeText(this, "Error loading products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun updateCategories(newCategories: List<String>) {
        categories.clear()
        categories.add(ALL_CATEGORIES)
        categories.addAll(newCategories)
        
        (spinnerCategory.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }
    
    private fun filterProducts(category: String, searchQuery: String) {
        filteredProducts.clear()
        
        for (product in allProducts) {
            val matchesCategory = category == ALL_CATEGORIES || product.foodCategory == category
            val matchesSearch = searchQuery.isEmpty() || 
                               product.foodName.contains(searchQuery, ignoreCase = true) ||
                               product.foodDescription.contains(searchQuery, ignoreCase = true)
            
            if (matchesCategory && matchesSearch) {
                filteredProducts.add(product)
            }
        }
        
        allProductsAdapter.notifyDataSetChanged()
        showEmptyState(filteredProducts.isEmpty())
        
        Log.d(TAG, "Filtered products: ${filteredProducts.size} (Category: $category, Search: $searchQuery)")
    }
    
    private fun getSelectedCategory(): String {
        return if (spinnerCategory.selectedItemPosition >= 0) {
            categories[spinnerCategory.selectedItemPosition]
        } else {
            ALL_CATEGORIES
        }
    }
    
    private fun addToCart(foodItem: FoodItemModel) {
        try {
            // Convert FoodItemModel to CartItemModel
            val cartItem = com.kelasxi.waveoffood.model.CartItemModel(
                id = "",
                foodId = foodItem.id ?: "",
                name = foodItem.foodName,
                price = foodItem.foodPrice.toLongOrNull() ?: 0L,
                imageUrl = foodItem.foodImage,
                quantity = 1
            )
            CartManager.addToCart(cartItem)
            Toast.makeText(this, "${foodItem.foodName} added to cart", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to cart", e)
            Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }
    
    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        
        tvEmptyState.text = if (getSelectedCategory() != ALL_CATEGORIES || etSearch.text.isNotEmpty()) {
            "No products found for your search criteria"
        } else {
            "No products available"
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
