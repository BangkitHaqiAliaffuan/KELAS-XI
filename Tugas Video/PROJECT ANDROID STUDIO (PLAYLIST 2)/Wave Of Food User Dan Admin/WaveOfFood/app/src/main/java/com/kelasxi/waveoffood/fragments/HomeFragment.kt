package com.kelasxi.waveoffood.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.DetailActivity
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.adapters.FoodAdapter
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * Fragment untuk halaman utama (Home)
 */
class HomeFragment : Fragment() {
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var popularAdapter: FoodAdapter
    private lateinit var allMenuAdapter: FoodAdapter
    private lateinit var rvPopularFood: RecyclerView
    private lateinit var rvAllMenu: RecyclerView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi views
        rvPopularFood = view.findViewById(R.id.popularRecyclerView)
        rvAllMenu = view.findViewById(R.id.allMenuRecyclerView)
        
        // Inisialisasi Firebase Firestore
        firestore = FirebaseFirestore.getInstance()
        
        // Setup RecyclerView
        setupRecyclerViews()
        
        // Fetch data dari Firestore
        fetchPopularItems()
        fetchAllMenuItems()
    }
    
    /**
     * Setup RecyclerView dan adapter-nya
     */
    private fun setupRecyclerViews() {
        // Setup Popular RecyclerView (Horizontal)
        popularAdapter = FoodAdapter { foodItem ->
            openDetailActivity(foodItem)
        }
        
        rvPopularFood.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }
        
        // Setup All Menu RecyclerView (Grid)
        allMenuAdapter = FoodAdapter { foodItem ->
            openDetailActivity(foodItem)
        }
        
        rvAllMenu.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = allMenuAdapter
        }
    }
    
    /**
     * Ambil 5 item makanan populer dari Firestore
     */
    private fun fetchPopularItems() {
        firestore.collection("menu")
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val popularItems = mutableListOf<FoodItemModel>()
                
                for (document in documents) {
                    val foodItem = document.toObject(FoodItemModel::class.java)
                    foodItem.id?.let { 
                        popularItems.add(foodItem.copy(id = document.id))
                    } ?: run {
                        popularItems.add(foodItem.copy(id = document.id))
                    }
                }
                
                popularAdapter.updateData(popularItems)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Gagal memuat menu populer: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    /**
     * Ambil semua item menu dari Firestore
     */
    private fun fetchAllMenuItems() {
        firestore.collection("menu")
            .get()
            .addOnSuccessListener { documents ->
                val allMenuItems = mutableListOf<FoodItemModel>()
                
                for (document in documents) {
                    val foodItem = document.toObject(FoodItemModel::class.java)
                    foodItem.id?.let { 
                        allMenuItems.add(foodItem.copy(id = document.id))
                    } ?: run {
                        allMenuItems.add(foodItem.copy(id = document.id))
                    }
                }
                
                allMenuAdapter.updateData(allMenuItems)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Gagal memuat semua menu: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    /**
     * Buka DetailActivity dengan mengirim ID makanan
     */
    private fun openDetailActivity(foodItem: FoodItemModel) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("FOOD_ID", foodItem.id)
        startActivity(intent)
    }
}
