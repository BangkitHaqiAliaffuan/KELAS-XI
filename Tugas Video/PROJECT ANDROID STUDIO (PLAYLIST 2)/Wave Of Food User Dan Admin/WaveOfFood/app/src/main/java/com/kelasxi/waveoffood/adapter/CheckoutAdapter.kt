package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.CartItemModel

/**
 * Adapter untuk menampilkan item cart dalam checkout
 * Optimized dengan error handling dan performance improvements
 */
class CheckoutAdapter(
    private val cartItems: List<CartItemModel>
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        if (position < cartItems.size) {
            val cartItem = cartItems[position]
            holder.bind(cartItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.iv_checkout_food_image)
        private val foodName: TextView = itemView.findViewById(R.id.tv_checkout_food_name)
        private val foodPrice: TextView = itemView.findViewById(R.id.tv_checkout_food_price)
        private val quantity: TextView = itemView.findViewById(R.id.tv_checkout_quantity)
        private val subtotal: TextView = itemView.findViewById(R.id.tv_checkout_subtotal)
        private val extras: TextView = itemView.findViewById(R.id.tv_checkout_extras)

        fun bind(cartItem: CartItemModel) {
            try {
                // Set food name with null check
                foodName.text = cartItem.name.takeIf { it.isNotBlank() } ?: "Unnamed Item"
                
                // Format price with proper handling
                val formattedPrice = formatPrice(cartItem.price)
                foodPrice.text = formattedPrice
                
                // Set quantity
                quantity.text = "x${cartItem.quantity}"
                
                // Calculate and format subtotal
                val subtotalAmount = cartItem.calculateSubtotal()
                subtotal.text = formatPrice(subtotalAmount)

                // Load food image with enhanced Glide configuration
                loadFoodImage(cartItem.imageUrl)

                // Handle extras display
                handleExtrasDisplay(cartItem)
                
            } catch (e: Exception) {
                // Fallback values in case of any error
                foodName.text = "Error loading item"
                foodPrice.text = "Rp 0"
                quantity.text = "x0"
                subtotal.text = "Rp 0"
                extras.visibility = View.GONE
                
                // Log error for debugging
                android.util.Log.e("CheckoutAdapter", "Error binding item at position $adapterPosition", e)
            }
        }
        
        /**
         * Format price to Rupiah format with proper thousand separators
         */
        private fun formatPrice(price: Long): String {
            return try {
                "Rp ${String.format("%,d", price)}"
            } catch (e: Exception) {
                "Rp 0"
            }
        }
        
        /**
         * Load food image with optimized Glide configuration
         */
        private fun loadFoodImage(imageUrl: String?) {
            try {
                val context = itemView.context
                val placeholderDrawable = try {
                    context.getDrawable(R.drawable.ic_launcher_foreground)
                } catch (e: Exception) {
                    // Fallback to system drawable if custom drawable not found
                    context.getDrawable(android.R.drawable.ic_menu_gallery)
                }

                val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(placeholderDrawable)
                    .error(placeholderDrawable)
                    .fallback(placeholderDrawable)

                Glide.with(context)
                    .load(imageUrl?.takeIf { it.isNotBlank() })
                    .apply(requestOptions)
                    .into(foodImage)
                    
            } catch (e: Exception) {
                // Ultimate fallback - set system drawable
                try {
                    foodImage.setImageResource(android.R.drawable.ic_menu_gallery)
                } catch (e2: Exception) {
                    android.util.Log.e("CheckoutAdapter", "Critical error loading image", e2)
                }
                android.util.Log.e("CheckoutAdapter", "Error loading image", e)
            }
        }
        
        /**
         * Handle extras display with proper error handling
         */
        private fun handleExtrasDisplay(cartItem: CartItemModel) {
            try {
                if (cartItem.selectedExtras.isNotEmpty()) {
                    val extrasText = cartItem.selectedExtras
                        .filter { it.name.isNotBlank() }
                        .joinToString(", ") { it.name }
                        
                    if (extrasText.isNotBlank()) {
                        extras.text = "Extra: $extrasText"
                        extras.visibility = View.VISIBLE
                    } else {
                        extras.visibility = View.GONE
                    }
                } else {
                    extras.visibility = View.GONE
                }
            } catch (e: Exception) {
                extras.visibility = View.GONE
                android.util.Log.e("CheckoutAdapter", "Error handling extras", e)
            }
        }
    }
}
