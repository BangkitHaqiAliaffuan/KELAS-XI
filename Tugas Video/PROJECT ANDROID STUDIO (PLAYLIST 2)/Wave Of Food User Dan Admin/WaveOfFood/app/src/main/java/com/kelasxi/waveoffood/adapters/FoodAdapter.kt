package com.kelasxi.waveoffood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * RecyclerView Adapter untuk menampilkan item makanan
 */
class FoodAdapter(
    private var foodList: List<FoodItemModel> = emptyList(),
    private val onItemClick: (FoodItemModel) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    
    /**
     * ViewHolder untuk item makanan
     */
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val tvFoodPrice: TextView = itemView.findViewById(R.id.tvFoodPrice)
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.ivFoodImage)
        
        fun bind(foodItem: FoodItemModel, onItemClick: (FoodItemModel) -> Unit) {
            tvFoodName.text = foodItem.foodName
            tvFoodPrice.text = "Rp ${foodItem.foodPrice}"
            
            // Load gambar menggunakan Glide
            Glide.with(itemView.context)
                .load(foodItem.foodImage)
                .into(ivFoodImage)
            
            // Set click listener
            itemView.setOnClickListener {
                onItemClick(foodItem)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_food, parent, false)
        return FoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position], onItemClick)
    }
    
    override fun getItemCount(): Int = foodList.size
    
    /**
     * Update data pada adapter
     */
    fun updateData(newFoodList: List<FoodItemModel>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}
