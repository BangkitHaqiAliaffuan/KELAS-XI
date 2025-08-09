package com.kelasxi.waveoffood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * RecyclerView Adapter untuk menampilkan item makanan
 */
class FoodAdapter(
    private var foodList: List<FoodItemModel> = emptyList(),
    private val onItemClick: (FoodItemModel) -> Unit,
    private val onAddToCartClick: ((FoodItemModel) -> Unit)? = null
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    
    /**
     * ViewHolder untuk item makanan
     */
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val tvFoodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val btnAddToCart: Button = itemView.findViewById(R.id.btn_add_to_cart)
        
        fun bind(
            foodItem: FoodItemModel, 
            onItemClick: (FoodItemModel) -> Unit,
            onAddToCartClick: ((FoodItemModel) -> Unit)?
        ) {
            tvFoodName.text = foodItem.foodName
            tvFoodPrice.text = "Rp ${foodItem.foodPrice}"
            tvRating.text = foodItem.rating.toString()
            
            // Load gambar menggunakan Glide
            Glide.with(itemView.context)
                .load(foodItem.foodImage)
                .into(ivFoodImage)
            
            // Set click listener
            itemView.setOnClickListener {
                onItemClick(foodItem)
            }
            
            // Set add to cart click listener
            btnAddToCart.setOnClickListener {
                onAddToCartClick?.invoke(foodItem)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position], onItemClick, onAddToCartClick)
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
