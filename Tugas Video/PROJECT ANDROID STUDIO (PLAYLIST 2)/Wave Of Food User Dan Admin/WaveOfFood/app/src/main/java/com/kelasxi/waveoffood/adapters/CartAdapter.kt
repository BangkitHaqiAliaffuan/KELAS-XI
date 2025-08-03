package com.kelasxi.waveoffood.adapters

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

/**
 * Adapter untuk RecyclerView di keranjang
 */
class CartAdapter(
    private var cartItems: List<CartItemModel> = emptyList(),
    private val onDeleteClick: (CartItemModel, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    
    /**
     * ViewHolder untuk item keranjang
     */
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCartFoodName: TextView = itemView.findViewById(R.id.tvCartFoodName)
        private val tvCartFoodPrice: TextView = itemView.findViewById(R.id.tvCartFoodPrice)
        private val tvCartQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        private val ivCartFoodImage: ImageView = itemView.findViewById(R.id.ivCartFoodImage)
        private val btnDeleteItem: Button = itemView.findViewById(R.id.btnDeleteItem)
        
        fun bind(cartItem: CartItemModel, onDeleteClick: (CartItemModel, Int) -> Unit, position: Int) {
            tvCartFoodName.text = cartItem.foodName
            tvCartFoodPrice.text = "Rp ${cartItem.foodPrice}"
            tvCartQuantity.text = "Jumlah: ${cartItem.quantity}"
            
            // Load gambar menggunakan Glide
            Glide.with(itemView.context)
                .load(cartItem.foodImage)
                .into(ivCartFoodImage)
            
            // Set click listener untuk tombol hapus
            btnDeleteItem.setOnClickListener {
                onDeleteClick(cartItem, position)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_cart, parent, false)
        return CartViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], onDeleteClick, position)
    }
    
    override fun getItemCount(): Int = cartItems.size
    
    /**
     * Update data pada adapter
     */
    fun updateData(newCartItems: List<CartItemModel>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}
