package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.FoodModel

class FoodAdapter(
    private val foods: MutableList<FoodModel>,
    private val onFoodClick: (FoodModel) -> Unit,
    private val onAddToCart: (FoodModel) -> Unit,
    private val onToggleFavorite: (FoodModel) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int = foods.size

    fun updateFoods(newFoods: List<FoodModel>) {
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val foodDescription: TextView = itemView.findViewById(R.id.tv_food_description)
        private val foodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val rating: TextView = itemView.findViewById(R.id.tv_rating)
        private val deliveryTime: TextView = itemView.findViewById(R.id.tv_delivery_time)
        private val favoriteButton: ImageView = itemView.findViewById(R.id.iv_favorite)
        private val addToCartButton: MaterialButton = itemView.findViewById(R.id.btn_add_to_cart)

        fun bind(food: FoodModel) {
            foodName.text = food.name
            foodDescription.text = food.description
            foodPrice.text = food.getFormattedPrice()
            rating.text = food.rating.toString()
            deliveryTime.text = "${food.preparationTime} min"

            // Load food image using Glide
            Glide.with(itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.ic_category_food)
                .error(R.drawable.ic_category_food)
                .centerCrop()
                .into(foodImage)

            // Update favorite icon - TODO: Implement favorite functionality
            val favoriteIcon = R.drawable.ic_favorite_border // Default to unfavorite for now
            favoriteButton.setImageResource(favoriteIcon)

            // Set click listeners
            itemView.setOnClickListener {
                onFoodClick(food)
            }

            favoriteButton.setOnClickListener {
                onToggleFavorite(food)
                notifyItemChanged(bindingAdapterPosition)
            }

            addToCartButton.setOnClickListener {
                onAddToCart(food)
            }
        }
    }
}
