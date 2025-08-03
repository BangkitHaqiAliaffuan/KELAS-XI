package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.CartItemModel
import com.kelasxi.waveoffood.model.FoodModel

class CartAdapter(
    private val cartItems: MutableList<CartItemModel>,
    private val onQuantityChange: (CartItemModel, Int) -> Unit,
    private val onRemoveItem: (CartItemModel) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val foodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val quantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val removeButton: ImageView = itemView.findViewById(R.id.iv_remove_item)
        private val decreaseButton: ImageView = itemView.findViewById(R.id.iv_decrease_quantity)
        private val increaseButton: ImageView = itemView.findViewById(R.id.iv_increase_quantity)

        fun bind(cartItem: CartItemModel) {
            foodName.text = cartItem.name
            foodPrice.text = String.format("$%.2f", cartItem.price / 100.0) // Convert from cents to dollars
            quantity.text = cartItem.quantity.toString()

            // Load food image using Glide
            Glide.with(itemView.context)
                .load(cartItem.imageUrl)
                .placeholder(R.drawable.ic_category_food)
                .error(R.drawable.ic_category_food)
                .centerCrop()
                .into(foodImage)

            // Set click listeners
            removeButton.setOnClickListener {
                onRemoveItem(cartItem)
            }

            decreaseButton.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                onQuantityChange(cartItem, newQuantity)
            }

            increaseButton.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChange(cartItem, newQuantity)
            }
            
            // Disable decrease button if quantity is 1
            decreaseButton.alpha = if (cartItem.quantity <= 1) 0.5f else 1.0f
            decreaseButton.isEnabled = cartItem.quantity > 1
        }
    }
}
