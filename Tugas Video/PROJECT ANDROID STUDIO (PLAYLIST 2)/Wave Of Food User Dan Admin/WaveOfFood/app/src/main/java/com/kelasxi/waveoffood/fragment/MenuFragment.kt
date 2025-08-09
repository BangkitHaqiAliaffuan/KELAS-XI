package com.kelasxi.waveoffood.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.DetailActivity
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.adapter.AllProductsAdapter
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * Fragment untuk menampilkan semua menu dengan fitur filter dan search
 */
class MenuFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var menuAdapter: AllProductsAdapter
    private lateinit var rvAllProducts: RecyclerView
    private lateinit var spinnerCategory: Spinner
    private lateinit var etSearch: EditText
    
    private var allProducts = mutableListOf<FoodItemModel>()
    private var filteredProducts = mutableListOf<FoodItemModel>()
    
    companion object {
        private const val TAG = "MenuFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initViews(view)
        
        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup search functionality
        setupSearch()
        
        // Setup category filter
        setupCategoryFilter()
        
        // Load all products
        loadAllProducts()
    }

    private fun initViews(view: View) {
        rvAllProducts = view.findViewById(R.id.rvAllProducts)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etSearch = view.findViewById(R.id.etSearch)
    }

    private fun setupRecyclerView() {
        menuAdapter = AllProductsAdapter(
            products = filteredProducts,
            onProductClick = { foodItem: FoodItemModel ->
                openDetailActivity(foodItem)
            }
        )
        
        rvAllProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuAdapter
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts()
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategoryFilter() {
        Log.d(TAG, "Setting up category filter...")
        // Get categories from Firebase foods collection
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Foods collection query successful. Document count: ${documents.size()}")
                val categories = mutableSetOf<String>()
                categories.add("Semua Kategori") // Default option
                
                for (document in documents) {
                    val categoryId = document.getString("categoryId")
                    Log.d(TAG, "Found categoryId in foods: $categoryId")
                    if (!categoryId.isNullOrBlank()) {
                        categories.add(categoryId)
                    }
                }
                
                // If no categories found in foods, try categories collection
                if (categories.size <= 1) {
                    Log.d(TAG, "No categories found in foods collection, trying categories collection...")
                    setupCategoryFromCategoriesCollection(categories)
                } else {
                    setupSpinner(categories)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading categories from foods collection", exception)
                // Try categories collection as fallback
                setupCategoryFromCategoriesCollection(mutableSetOf("Semua Kategori"))
            }
    }

    private fun setupCategoryFromCategoriesCollection(existingCategories: MutableSet<String>) {
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Categories collection query successful. Document count: ${documents.size()}")
                val categories = existingCategories
                 
                for (document in documents) {
                    val categoryName = document.getString("name") ?: document.id
                    Log.d(TAG, "Found category in categories collection: $categoryName")
                    if (categoryName.isNotBlank()) {
                        categories.add(categoryName)
                    }
                }
                
                setupSpinner(categories)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading categories from categories collection", exception)
                Toast.makeText(context, "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                setupSpinner(existingCategories)
            }
    }

    private fun setupSpinner(categories: MutableSet<String>) {
        // Setup spinner
        val categoryList = categories.toList().sorted()
        Log.d(TAG, "Setting up spinner with categories: $categoryList")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        
        // Set listener
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterProducts()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadAllProducts() {
        Log.d(TAG, "Starting to load products from Firestore foods collection...")
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Firestore query successful. Document count: ${documents.size()}")
                allProducts.clear()
                
                if (documents.isEmpty()) {
                    Log.w(TAG, "No documents found in foods collection")
                    Toast.makeText(context, "Tidak ada menu tersedia", Toast.LENGTH_SHORT).show()
                    filterProducts()
                    return@addOnSuccessListener
                }
                
                var successCount = 0
                var errorCount = 0
                
                for (document in documents) {
                    Log.d(TAG, "Processing document: ${document.id}")
                    try {
                        // Log raw document data for debugging
                        Log.d(TAG, "Document data: ${document.data}")
                        
                        // Try automatic parsing first
                        val foodItem = try {
                            document.toObject(FoodItemModel::class.java)
                        } catch (e: Exception) {
                            Log.w(TAG, "Automatic parsing failed for ${document.id}, trying manual parsing: ${e.message}")
                            // Manual parsing as fallback
                            createFoodItemManually(document)
                        }
                        
                        // Pastikan ID ter-set dengan benar
                        foodItem.id = document.id
                        
                        // Log detail item untuk debugging
                        Log.d(TAG, "Successfully parsed: ${foodItem.name} - ID: ${foodItem.id} - Price: ${foodItem.price} - Category: ${foodItem.categoryId} - Available: ${foodItem.isAvailable}")
                        
                        // Hanya tambahkan item yang tersedia dan memiliki nama
                        if (foodItem.name.isNotEmpty() && foodItem.isAvailable) {
                            allProducts.add(foodItem)
                            successCount++
                        } else {
                            Log.d(TAG, "Skipping item: name='${foodItem.name}', available=${foodItem.isAvailable}")
                        }
                    } catch (e: Exception) {
                        errorCount++
                        Log.e(TAG, "Error parsing document ${document.id}: ${e.message}", e)
                        Log.e(TAG, "Document data that failed: ${document.data}")
                    }
                }
                
                Log.d(TAG, "Loading completed - Success: $successCount, Errors: $errorCount, Total loaded: ${allProducts.size}")
                
                if (allProducts.isEmpty()) {
                    Toast.makeText(context, "Tidak ada menu tersedia saat ini", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Berhasil memuat ${allProducts.size} menu", Toast.LENGTH_SHORT).show()
                }
                
                filterProducts() // Apply current filters
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading products from foods collection", exception)
                Toast.makeText(context, "Gagal memuat produk: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun filterProducts() {
        Log.d(TAG, "Starting to filter products. Total products: ${allProducts.size}")
        val searchQuery = etSearch.text.toString().lowercase().trim()
        val selectedCategory = spinnerCategory.selectedItem?.toString() ?: "Semua Kategori"
        
        Log.d(TAG, "Search query: '$searchQuery', Selected category: '$selectedCategory'")
        
        filteredProducts.clear()
        
        for (product in allProducts) {
            val matchesSearch = searchQuery.isEmpty() || 
                product.name.lowercase().contains(searchQuery) ||
                product.description.lowercase().contains(searchQuery)
            val matchesCategory = selectedCategory == "Semua Kategori" || 
                (product.categoryId.equals(selectedCategory, ignoreCase = true))
            
            Log.d(TAG, "Product: ${product.name}, matchesSearch: $matchesSearch, matchesCategory: $matchesCategory")
            
            if (matchesSearch && matchesCategory) {
                filteredProducts.add(product)
            }
        }
        
        Log.d(TAG, "Filtered products: ${filteredProducts.size}")
        menuAdapter.notifyDataSetChanged()
    }

    private fun openDetailActivity(foodItem: FoodItemModel) {
        Log.d(TAG, "Opening detail activity for food: ${foodItem.name} with ID: ${foodItem.id}")
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("FOOD_ID", foodItem.id)
        startActivity(intent)
    }
    
    /**
     * Manual parsing untuk mengatasi masalah tipe data yang tidak kompatibel
     */
    private fun createFoodItemManually(document: com.google.firebase.firestore.DocumentSnapshot): FoodItemModel {
        return FoodItemModel(
            id = document.id,
            name = document.getString("name") ?: document.getString("foodName") ?: "",
            description = document.getString("description") ?: document.getString("foodDescription") ?: "",
            imageUrl = document.getString("imageUrl") ?: document.getString("foodImage") ?: "",
            categoryId = document.getString("categoryId") ?: document.getString("foodCategory") ?: "",
            price = when (val priceValue = document.get("price")) {
                is Long -> priceValue
                is String -> priceValue.toLongOrNull() ?: 0L
                is Double -> priceValue.toLong()
                else -> document.getString("foodPrice")?.toLongOrNull() ?: 0L
            },
            rating = document.getDouble("rating") ?: 0.0,
            isPopular = document.getBoolean("isPopular") ?: false,
            isAvailable = document.getBoolean("isAvailable") ?: true,
            preparationTime = document.getLong("preparationTime") ?: 0L,
            ingredients = document.get("ingredients") as? List<String> ?: emptyList(),
            createdAt = document.getString("createdAt"),
            updatedAt = document.getString("updatedAt")
        )
    }
}
