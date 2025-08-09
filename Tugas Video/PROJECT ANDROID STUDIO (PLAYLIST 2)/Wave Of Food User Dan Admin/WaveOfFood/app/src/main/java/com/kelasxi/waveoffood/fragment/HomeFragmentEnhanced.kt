package com.kelasxi.waveoffood.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.adapter.CategoryAdapter
import com.kelasxi.waveoffood.adapter.FoodAdapter
import com.kelasxi.waveoffood.models.CategoryModel
import com.kelasxi.waveoffood.models.FoodModel
import com.kelasxi.waveoffood.models.CartItemModel
import com.kelasxi.waveoffood.repository.FirebaseRepository
import com.kelasxi.waveoffood.utils.CartManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentEnhanced : Fragment() {
    
    // private lateinit var greetingText: TextView  // Not available in current layout
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var popularFoodsRecyclerView: RecyclerView
    private lateinit var recommendedFoodsRecyclerView: RecyclerView
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularFoodAdapter: FoodAdapter
    private lateinit var recommendedFoodAdapter: FoodAdapter
    
    private val firebaseRepository = FirebaseRepository()
    private val categories = mutableListOf<CategoryModel>()
    private val popularFoods = mutableListOf<FoodModel>()
    private val recommendedFoods = mutableListOf<FoodModel>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_enhanced, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerViews()
        setupGreeting()
        loadData()
    }
    
    private fun initializeViews(view: View) {
        // Note: tv_greeting is not in the current layout, so we'll skip it for now
        // greetingText = view.findViewById(R.id.tv_greeting)
        categoriesRecyclerView = view.findViewById(R.id.rvCategories)
        popularFoodsRecyclerView = view.findViewById(R.id.rvPopularFoods)
        recommendedFoodsRecyclerView = view.findViewById(R.id.rvRecommendedFoods)
    }
    
    private fun setupRecyclerViews() {
        // Categories RecyclerView
        categoryAdapter = CategoryAdapter(categories) { category ->
            onCategoryClick(category)
        }
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Popular Foods RecyclerView
        popularFoodAdapter = FoodAdapter(
            popularFoods,
            onFoodClick = { food -> onFoodClick(food) },
            onAddToCart = { food -> onAddToCart(food) },
            onToggleFavorite = { food -> onToggleFavorite(food) }
        )
        popularFoodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularFoodAdapter
        }
        
        // Recommended Foods RecyclerView
        recommendedFoodAdapter = FoodAdapter(
            recommendedFoods,
            onFoodClick = { food -> onFoodClick(food) },
            onAddToCart = { food -> onAddToCart(food) },
            onToggleFavorite = { food -> onToggleFavorite(food) }
        )
        recommendedFoodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendedFoodAdapter
        }
    }
    
    private fun setupGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (hour) {
            in 0..11 -> getString(R.string.good_morning)
            in 12..17 -> getString(R.string.good_afternoon)
            else -> getString(R.string.good_evening)
        }
        
        // Note: Greeting text view is not available in current layout
        // greetingText.text = greeting
        Log.d("HomeFragment", "Greeting: $greeting")
    }
    
    private fun loadData() {
        loadCategories()
        loadPopularFoods()
        loadRecommendedFoods()
    }
    
    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseRepository.getCategories()
                .onSuccess { categoriesList ->
                    withContext(Dispatchers.Main) {
                        categories.clear()
                        categories.addAll(categoriesList)
                        categoryAdapter.notifyDataSetChanged()
                        Log.d("HomeFragment", "✅ Categories loaded: ${categoriesList.size}")
                    }
                }
                .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        Log.e("HomeFragment", "❌ Failed to load categories", exception)
                        // Load sample data as fallback
                        loadSampleCategories()
                    }
                }
        }
    }
    
    private fun loadSampleCategories() {
        val sampleCategories = listOf(
            CategoryModel("pizza", "Pizza", "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400", true),
            CategoryModel("burger", "Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400", true),
            CategoryModel("indonesian", "Indonesian Food", "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400", true),
            CategoryModel("dessert", "Dessert", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400", true),
            CategoryModel("drinks", "Drinks", "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400", true)
        )
        
        categories.clear()
        categories.addAll(sampleCategories)
        categoryAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "📦 Sample categories loaded: ${sampleCategories.size}")
    }
    
    private fun loadPopularFoods() {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseRepository.getPopularFoods()
                .onSuccess { foodsList ->
                    withContext(Dispatchers.Main) {
                        popularFoods.clear()
                        popularFoods.addAll(foodsList)
                        popularFoodAdapter.notifyDataSetChanged()
                        Log.d("HomeFragment", "✅ Popular foods loaded: ${foodsList.size}")
                    }
                }
                .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        Log.e("HomeFragment", "❌ Failed to load popular foods", exception)
                        // Load sample data as fallback
                        loadSamplePopularFoods()
                    }
                }
        }
    }
    
    private fun loadSamplePopularFoods() {
        val sampleFoods = listOf(
            FoodModel("nasi-gudeg", "Nasi Gudeg", "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis", 
                "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400", 
                25000L, "indonesian", true, 4.5, true, 15),
            FoodModel("margherita-pizza", "Margherita Pizza", "Pizza klasik dengan saus tomat, mozzarella segar, dan daun basil yang harum", 
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400", 
                45000L, "pizza", true, 4.6, true, 25),
            FoodModel("cheeseburger", "Classic Cheeseburger", "Burger daging sapi juicy dengan keju cheddar, lettuce, tomat, dan saus spesial", 
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400", 
                38000L, "burger", true, 4.4, true, 18)
        )
        
        popularFoods.clear()
        popularFoods.addAll(sampleFoods)
        popularFoodAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "📦 Sample popular foods loaded: ${sampleFoods.size}")
    }
    
    private fun loadRecommendedFoods() {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseRepository.getAllFoods()
                .onSuccess { foodsList ->
                    withContext(Dispatchers.Main) {
                        recommendedFoods.clear()
                        recommendedFoods.addAll(foodsList.take(20)) // Limit to 20 items
                        recommendedFoodAdapter.notifyDataSetChanged()
                        Log.d("HomeFragment", "✅ Recommended foods loaded: ${foodsList.size}")
                    }
                }
                .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        Log.e("HomeFragment", "❌ Failed to load recommended foods", exception)
                        // Load sample data as fallback
                        loadSampleRecommendedFoods()
                    }
                }
        }
    }
    
    private fun loadSampleRecommendedFoods() {
        val sampleFoods = listOf(
            FoodModel("rendang-daging", "Rendang Daging", "Rendang daging sapi autentik Padang dengan bumbu rempah yang kaya dan santan yang gurih", 
                "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400", 
                35000L, "indonesian", true, 4.8, true, 20),
            FoodModel("chocolate-cake", "Chocolate Fudge Cake", "Kue cokelat lembut dengan lapisan fudge yang kaya dan topping whipped cream", 
                "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400", 
                22000L, "dessert", false, 4.7, true, 10),
            FoodModel("iced-coffee", "Iced Coffee Latte", "Kopi susu dingin dengan espresso premium dan susu segar yang creamy", 
                "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400", 
                15000L, "drinks", false, 4.3, true, 5)
        )
        
        recommendedFoods.clear()
        recommendedFoods.addAll(sampleFoods)
        recommendedFoodAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "📦 Sample recommended foods loaded: ${sampleFoods.size}")
    }
    
    private fun onCategoryClick(category: CategoryModel) {
        Log.d("HomeFragment", "Category clicked: ${category.name}")
        // TODO: Navigate to category detail or filter foods by category
        // You can implement navigation to a category detail fragment here
    }
    
    private fun onFoodClick(food: FoodModel) {
        Log.d("HomeFragment", "Food clicked: ${food.name}")
        // TODO: Navigate to food detail activity
        // Intent to detail activity can be implemented here
    }
    
    private fun onAddToCart(food: FoodModel) {
        try {
            // Create CartItemModel from FoodModel
            val cartItem = CartItemModel(
                id = food.id,
                foodName = food.name,
                foodPrice = food.price.toString(),
                foodDescription = food.description,
                foodImage = food.imageUrl,
                foodCategory = food.categoryId,
                quantity = 1
            )
            
            // Add to cart using CartManager
            CartManager.addToCart(cartItem)
            
            // Show success message
            android.widget.Toast.makeText(
                context, 
                "✅ ${food.name} added to cart!", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
            
            Log.d("HomeFragment", "✅ Added to cart: ${food.name}")
        } catch (e: Exception) {
            Log.e("HomeFragment", "❌ Failed to add to cart: ${food.name}", e)
            android.widget.Toast.makeText(
                context, 
                "❌ Failed to add ${food.name} to cart", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun onToggleFavorite(food: FoodModel) {
        Log.d("HomeFragment", "Toggle favorite for: ${food.name}")
        // TODO: Implement favorite functionality with FirebaseRepository
        // firebaseRepository.addToFavorites(userId, food.id) or removeFromFavorites
    }
}
