package com.kelasxi.waveoffood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.OrderModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk menampilkan riwayat pesanan
 */
class OrderHistoryAdapter(
    private var orderList: List<OrderModel> = emptyList(),
    private val onItemClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRestaurantLogo: ImageView = itemView.findViewById(R.id.ivRestaurantLogo)
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvItemName1: TextView = itemView.findViewById(R.id.tvItemName1)
        val tvItemPrice1: TextView = itemView.findViewById(R.id.tvItemPrice1)
        val tvMoreItems: TextView = itemView.findViewById(R.id.tvMoreItems)
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        val btnRate: Button = itemView.findViewById(R.id.btnRate)
        val btnReorder: Button = itemView.findViewById(R.id.btnReorder)
        val btnTrackOrder: Button = itemView.findViewById(R.id.btnTrackOrder)
        
        fun bind(
            order: OrderModel, 
            onItemClick: (OrderModel) -> Unit,
            dateFormat: SimpleDateFormat,
            currencyFormat: NumberFormat
        ) {
            // Restaurant info - using userName since restaurantName not available
            tvRestaurantName.text = order.userName.ifEmpty { "Restaurant" }
            
            // Order date
            try {
                val date = Date(order.orderDate)
                tvOrderDate.text = dateFormat.format(date)
            } catch (e: Exception) {
                tvOrderDate.text = "Date unavailable"
            }
            
            // Order status
            tvOrderStatus.text = getStatusText(order.status)
            updateStatusColor(tvOrderStatus, order.status)
            
            // Order items
            if (order.items.isNotEmpty()) {
                val firstItem = order.items.first()
                tvItemName1.text = if (firstItem.foodName.isNotEmpty()) firstItem.foodName else firstItem.name
                tvItemPrice1.text = "Rp ${String.format("%,.0f", firstItem.price)}"
                
                // Show more items indicator
                if (order.items.size > 1) {
                    tvMoreItems.visibility = View.VISIBLE
                    tvMoreItems.text = "+ ${order.items.size - 1} more items"
                } else {
                    tvMoreItems.visibility = View.GONE
                }
            }
            
            // Order summary
            tvOrderId.text = "Order #${order.id.take(8)}"
            tvTotalAmount.text = "Total: Rp ${order.totalAmount}"
            
            // Button visibility based on status
            when (order.status) {
                "delivered" -> {
                    btnRate.visibility = View.VISIBLE
                    btnTrackOrder.visibility = View.GONE
                }
                "pending", "preparing", "on_the_way" -> {
                    btnRate.visibility = View.GONE
                    btnTrackOrder.visibility = View.VISIBLE
                }
                else -> {
                    btnRate.visibility = View.GONE
                    btnTrackOrder.visibility = View.GONE
                }
            }
            
            // Click listeners
            itemView.setOnClickListener {
                onItemClick(order)
            }
            
            btnReorder.setOnClickListener {
                // TODO: Implement reorder functionality
            }
            
            btnRate.setOnClickListener {
                // TODO: Implement rating functionality
            }
            
            btnTrackOrder.setOnClickListener {
                // TODO: Implement order tracking
            }
        }
        
        private fun getStatusText(status: String): String {
            return when (status) {
                "pending" -> "Pending"
                "preparing" -> "Preparing"
                "on_the_way" -> "On the Way"
                "delivered" -> "Delivered"
                "cancelled" -> "Cancelled"
                else -> "Unknown"
            }
        }
        
        private fun updateStatusColor(textView: TextView, status: String) {
            val context = textView.context
            val colorRes = when (status) {
                "delivered" -> R.color.success_main
                "pending", "preparing", "on_the_way" -> R.color.warning_main
                "cancelled" -> R.color.error_main
                else -> R.color.text_secondary
            }
            textView.setTextColor(context.getColor(colorRes))
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_enhanced, parent, false)
        return OrderViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position], onItemClick, dateFormat, currencyFormat)
    }
    
    override fun getItemCount(): Int = orderList.size
    
    fun updateData(newOrderList: List<OrderModel>) {
        orderList = newOrderList
        notifyDataSetChanged()
    }
}
