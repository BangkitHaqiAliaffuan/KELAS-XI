package com.trashbin.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trashbin.app.R
import com.trashbin.app.data.model.Order
import com.trashbin.app.utils.CurrencyHelper
import com.trashbin.app.utils.DateHelper

class OrderAdapter(
    private val onItemClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivListingImage: ImageView = itemView.findViewById(R.id.iv_listing_image)
        private val tvListingTitle: TextView = itemView.findViewById(R.id.tv_listing_title)
        private val tvListingCategory: TextView = itemView.findViewById(R.id.tv_listing_category)
        private val tvOrderQuantity: TextView = itemView.findViewById(R.id.tv_order_quantity)
        private val tvPricePerUnit: TextView = itemView.findViewById(R.id.tv_price_per_unit)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tv_total_price)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        private val tvSellerName: TextView = itemView.findViewById(R.id.tv_seller_name)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tv_order_date)

        fun bind(order: Order) {
            // Load first image of the listing
            if (order.listing.photos.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(order.listing.photos[0])
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivListingImage)
            } else {
                ivListingImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            tvListingTitle.text = order.listing.title
            tvListingCategory.text = order.listing.category.name
            tvOrderQuantity.text = "Jumlah: ${order.quantity.toInt()}"
            tvPricePerUnit.text = "Rp ${CurrencyHelper.formatRupiah(order.listing.pricePerUnit)}/unit"
            tvTotalPrice.text = CurrencyHelper.formatRupiah(order.totalPrice)

            // Set status badge
            when (order.status) {
                "pending" -> {
                    tvStatus.text = "Menunggu"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.orange_500))
                }
                "confirmed" -> {
                    tvStatus.text = "Dikonfirmasi"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.blue_500))  // We need to add blue color
                }
                "shipping" -> {
                    tvStatus.text = "Dikirim"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.blue_500))
                }
                "completed" -> {
                    tvStatus.text = "Selesai"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.green_500))
                }
                "cancelled" -> {
                    tvStatus.text = "Dibatalkan"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.red_500))
                }
                else -> {
                    tvStatus.text = order.status
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.gray_500))
                }
            }

            // Show seller name
            tvSellerName.text = "Oleh: ${order.seller.name}"

            // Format date
            tvOrderDate.text = "Dipesan: ${DateHelper.formatDate(order.createdAt)}"

            itemView.setOnClickListener {
                onItemClick(order)
            }
        }
    }
}