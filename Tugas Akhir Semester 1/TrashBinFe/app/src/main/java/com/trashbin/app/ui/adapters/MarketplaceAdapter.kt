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
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.utils.CurrencyHelper

class MarketplaceAdapter(
    private val onItemClick: (MarketplaceListing) -> Unit
) : ListAdapter<MarketplaceListing, MarketplaceAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<MarketplaceListing>() {
        override fun areItemsTheSame(oldItem: MarketplaceListing, newItem: MarketplaceListing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarketplaceListing, newItem: MarketplaceListing): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_marketplace, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.iv_thumbnail)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(listing: MarketplaceListing) {
            // Load first image as thumbnail
            if (listing.photos.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(listing.photos[0])
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivThumbnail)
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_image_placeholder)
            }

            tvTitle.text = listing.title
            tvCategory.text = listing.category.name
            tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
            tvQuantity.text = "Tersedia: ${listing.quantity.toInt()}"
            tvLocation.text = listing.location

            // Set status badge color based on status
            when (listing.status) {
                "available" -> {
                    tvStatus.text = "Tersedia"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.green_500))
                }
                "reserved" -> {
                    tvStatus.text = "Dipesan"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.orange_500))
                }
                "sold" -> {
                    tvStatus.text = "Terjual"
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.red_500))
                }
                else -> {
                    tvStatus.text = listing.status
                    tvStatus.setBackgroundColor(itemView.context.getColor(R.color.gray_500))
                }
            }

            itemView.setOnClickListener {
                onItemClick(listing)
            }
        }
    }
}