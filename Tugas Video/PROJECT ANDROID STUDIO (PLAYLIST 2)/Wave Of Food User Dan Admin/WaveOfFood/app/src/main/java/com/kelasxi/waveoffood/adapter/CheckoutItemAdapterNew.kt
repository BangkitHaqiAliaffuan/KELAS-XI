package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.CartItemModel

class CheckoutItemAdapterNew(
    private val cartItems: List<CartItemModel>
) : RecyclerView.Adapter<CheckoutItemAdapterNew.CheckoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_simple, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        try {
            val cartItem = cartItems[position]
            holder.bind(cartItem)
        } catch (e: Exception) {
            android.util.Log.e("CheckoutAdapter", "Error binding item at position $position", e)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView? = itemView.findViewById(R.id.iv_food_image)
        private val foodName: TextView? = itemView.findViewById(R.id.tv_food_name)
        private val foodPrice: TextView? = itemView.findViewById(R.id.tv_food_price)
        private val quantity: TextView? = itemView.findViewById(R.id.tv_quantity)
        private val totalPrice: TextView? = itemView.findViewById(R.id.tv_total_price)

        fun bind(cartItem: CartItemModel) {
            try {
                foodName?.text = cartItem.foodName
                foodPrice?.text = cartItem.foodPrice
                quantity?.text = "x${cartItem.quantity}"
                
                // Calculate and display total for this item
                try {
                    val cleanPrice = cartItem.foodPrice
                        .replace("Rp", "")
                        .replace(".", "")
                        .replace(",", "")
                        .replace(" ", "")
                        .trim()
                    
                    val price = cleanPrice.toDoubleOrNull() ?: 0.0
                    val itemTotal = price * cartItem.quantity
                    totalPrice?.text = "Rp ${String.format("%,.0f", itemTotal)}"
                } catch (e: Exception) {
                    totalPrice?.text = cartItem.foodPrice
                }
                
                // Load image
                foodImage?.let { imageView ->
                    if (cartItem.foodImage.isNotEmpty()) {
                        Glide.with(itemView.context)
                            .load(cartItem.foodImage)
                            .placeholder(R.drawable.ic_food_placeholder)
                            .error(R.drawable.ic_food_placeholder)
                            .into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.ic_food_placeholder)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CheckoutAdapter", "Error in bind", e)
                // Set fallback values
                foodName?.text = "Unknown Item"
                foodPrice?.text = "Rp 0"
                quantity?.text = "x1"
                totalPrice?.text = "Rp 0"
            }
        }
    }
}
