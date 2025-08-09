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
import java.text.NumberFormat
import java.util.*

/**
 * Simple adapter for checkout - read-only display only
 */
class CheckoutItemAdapter(
    private val cartItems: List<CartItemModel>
) : RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_simple, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val foodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val quantityText: TextView = itemView.findViewById(R.id.tv_quantity)

        fun bind(cartItem: CartItemModel) {
            foodName.text = cartItem.foodName
            
            // Format price
            val price = cartItem.foodPrice.replace("Rp", "").replace(",", "").replace(".", "").trim().toLongOrNull() ?: 0L
            val formatter = NumberFormat.getInstance(Locale("id", "ID"))
            foodPrice.text = "Rp ${formatter.format(price)}"
            
            quantityText.text = "x${cartItem.quantity}"

            // Load image with Glide
            Glide.with(itemView.context)
                .load(cartItem.foodImage)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(foodImage)
        }
    }
}
