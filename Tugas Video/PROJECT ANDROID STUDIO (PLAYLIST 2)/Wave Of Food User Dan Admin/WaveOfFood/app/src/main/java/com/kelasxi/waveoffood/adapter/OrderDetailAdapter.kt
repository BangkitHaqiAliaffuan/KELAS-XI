package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.OrderItemModel

/**
 * Adapter untuk RecyclerView item detail order
 */
class OrderDetailAdapter(
    private var orderItems: List<OrderItemModel>
) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    
    class OrderDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoodImage: ImageView = view.findViewById(R.id.iv_food_image)
        val tvFoodName: TextView = view.findViewById(R.id.tv_food_name)
        val tvFoodPrice: TextView = view.findViewById(R.id.tv_food_price)
        val tvQuantity: TextView = view.findViewById(R.id.tv_quantity)
        val tvTotalPrice: TextView = view.findViewById(R.id.tv_total_price)
        val tvSpecialInstructions: TextView = view.findViewById(R.id.tv_special_instructions)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val item = orderItems[position]
        
        with(holder) {
            tvFoodName.text = item.foodName
            tvFoodPrice.text = "Rp ${String.format("%,d", item.price)}"
            tvQuantity.text = "x${item.quantity}"
            
            val totalPrice = item.price * item.quantity
            tvTotalPrice.text = "Rp ${String.format("%,d", totalPrice)}"
            
            // Special instructions
            if (item.specialInstructions.isNotEmpty()) {
                tvSpecialInstructions.text = item.specialInstructions
                tvSpecialInstructions.visibility = View.VISIBLE
            } else {
                tvSpecialInstructions.visibility = View.GONE
            }
            
            // Load food image
            Glide.with(holder.itemView.context)
                .load(item.foodImage)
                .placeholder(R.drawable.ic_category_food)
                .error(R.drawable.ic_category_food)
                .centerCrop()
                .into(ivFoodImage)
        }
    }
    
    override fun getItemCount() = orderItems.size
    
    fun updateItems(newItems: List<OrderItemModel>) {
        orderItems = newItems
        notifyDataSetChanged()
    }
}
