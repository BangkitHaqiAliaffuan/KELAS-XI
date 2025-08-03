package com.kelasxi.waveoffood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.OrderModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk RecyclerView daftar order user
 */
class OrderAdapter(
    private val orders: List<OrderModel>,
    private val onOrderClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardOrder: CardView = view.findViewById(R.id.card_order)
        val tvOrderId: TextView = view.findViewById(R.id.tv_order_id)
        val tvOrderDate: TextView = view.findViewById(R.id.tv_order_date)
        val tvOrderStatus: TextView = view.findViewById(R.id.tv_order_status)
        val tvTotalAmount: TextView = view.findViewById(R.id.tv_total_amount)
        val tvItemCount: TextView = view.findViewById(R.id.tv_item_count)
        val ivFirstItem: ImageView = view.findViewById(R.id.iv_first_item)
        val tvFirstItemName: TextView = view.findViewById(R.id.tv_first_item_name)
        val tvMoreItems: TextView = view.findViewById(R.id.tv_more_items)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        
        with(holder) {
            // Order ID
            tvOrderId.text = "#${order.orderId.takeLast(8).uppercase()}"
            
            // Order Date
            val orderDate = order.createdAt?.toDate() ?: Date()
            tvOrderDate.text = dateFormat.format(orderDate)
            
            // Total Amount
            tvTotalAmount.text = "Rp ${String.format("%,d", order.totalAmount)}"
            
            // Item Count
            val itemCount = order.items.sumOf { it.quantity }
            tvItemCount.text = "$itemCount item${if (itemCount > 1) "s" else ""}"
            
            // Order Status with color coding
            tvOrderStatus.text = order.getOrderStatusDisplay()
            val statusColor = when (order.orderStatus.lowercase()) {
                "pending" -> Color.parseColor("#FF9800") // Orange
                "confirmed" -> Color.parseColor("#2196F3") // Blue
                "preparing" -> Color.parseColor("#9C27B0") // Purple
                "delivering" -> Color.parseColor("#4CAF50") // Green
                "completed" -> Color.parseColor("#4CAF50") // Green
                "cancelled" -> Color.parseColor("#F44336") // Red
                else -> Color.parseColor("#757575") // Gray
            }
            tvOrderStatus.setTextColor(statusColor)
            
            // First item details
            if (order.items.isNotEmpty()) {
                val firstItem = order.items[0]
                tvFirstItemName.text = firstItem.name
                
                // Load first item image
                Glide.with(holder.itemView.context)
                    .load(firstItem.imageUrl)
                    .placeholder(R.drawable.ic_category_food)
                    .error(R.drawable.ic_category_food)
                    .centerCrop()
                    .into(ivFirstItem)
                
                // Show more items if available
                if (order.items.size > 1) {
                    val moreCount = order.items.size - 1
                    tvMoreItems.text = "+$moreCount more"
                    tvMoreItems.visibility = View.VISIBLE
                } else {
                    tvMoreItems.visibility = View.GONE
                }
            } else {
                tvFirstItemName.text = "No items"
                tvMoreItems.visibility = View.GONE
                ivFirstItem.setImageResource(R.drawable.ic_category_food)
            }
            
            // Click listener
            cardOrder.setOnClickListener { 
                onOrderClick(order) 
            }
        }
    }
    
    override fun getItemCount() = orders.size
}
