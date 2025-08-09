package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.CartItemModel
import java.text.NumberFormat
import java.util.Locale

class CartAdapterEnhanced(
    private val cartItems: MutableList<CartItemModel>,
    private val onQuantityChange: (CartItemModel, Int) -> Unit,
    private val onRemoveItem: (CartItemModel) -> Unit
) : RecyclerView.Adapter<CartAdapterEnhanced.CartViewHolder>() {

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

    fun updateItems(newItems: List<CartItemModel>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val foodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val quantityText: TextView = itemView.findViewById(R.id.tv_quantity)
        private val btnDecrease: ImageView = itemView.findViewById(R.id.iv_decrease_quantity)
        private val btnIncrease: ImageView = itemView.findViewById(R.id.iv_increase_quantity)
        private val btnRemove: ImageView = itemView.findViewById(R.id.iv_remove_item)

        fun bind(cartItem: CartItemModel) {
            foodName.text = cartItem.foodName
            
            // Format price
            val price = cartItem.foodPrice.replace("Rp", "").replace(",", "").replace(".", "").trim().toLongOrNull() ?: 0L
            val formatter = NumberFormat.getInstance(Locale("id", "ID"))
            foodPrice.text = "Rp ${formatter.format(price)}"
            
            quantityText.text = cartItem.quantity.toString()

            // Load image with Glide
            Glide.with(itemView.context)
                .load(cartItem.foodImage)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(foodImage)

            // Set click listeners
            btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onQuantityChange(cartItem, cartItem.quantity - 1)
                }
            }

            btnIncrease.setOnClickListener {
                onQuantityChange(cartItem, cartItem.quantity + 1)
            }

            btnRemove.setOnClickListener {
                onRemoveItem(cartItem)
            }
        }
    }
}
