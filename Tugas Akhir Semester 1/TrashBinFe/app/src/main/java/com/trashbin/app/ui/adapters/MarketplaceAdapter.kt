package com.trashbin.app.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
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
        val cardView = MaterialCardView(parent.context).apply {
            val margin = (8 * parent.context.resources.displayMetrics.density).toInt()
            setCardBackgroundColor(parent.context.resources.getColor(R.color.material_dynamic_neutral0))
            radius = 8 * parent.context.resources.displayMetrics.density
            cardElevation = 4 * parent.context.resources.displayMetrics.density
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
        }

        val linearLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt()
            )
        }

        val imageView = ImageView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (150 * parent.context.resources.displayMetrics.density).toInt()
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            // setBackgroundResource(R.color.gray_200) // Assuming this color exists
            contentDescription = "Foto barang"
        }

        val title = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val category = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (4 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 14f
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_secondary))
        }

        val price = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (4 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_primary))
        }

        val infoLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
        }

        val quantity = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            textSize = 12f
        }

        val location = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                gravity = android.view.Gravity.END
            }
            textSize = 12f
        }

        infoLayout.addView(quantity)
        infoLayout.addView(location)

        val status = TextView(parent.context).apply {
            id = View.generateViewId() // Add generated ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            // setBackgroundResource(R.drawable.bg_status_badge) // Assuming this drawable exists
            setPadding(
                (8 * parent.context.resources.displayMetrics.density).toInt(),
                (4 * parent.context.resources.displayMetrics.density).toInt(),
                (8 * parent.context.resources.displayMetrics.density).toInt(),
                (4 * parent.context.resources.displayMetrics.density).toInt()
            )
            textSize = 12f
            setTextColor(parent.context.resources.getColor(R.color.white))
        }

        linearLayout.addView(imageView)
        linearLayout.addView(title)
        linearLayout.addView(category)
        linearLayout.addView(price)
        linearLayout.addView(infoLayout)
        linearLayout.addView(status)
        cardView.addView(linearLayout)

        return ViewHolder(cardView, imageView, title, category, price, quantity, location, status)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        itemView: View,
        private val ivThumbnail: ImageView,
        private val tvTitle: TextView,
        private val tvCategory: TextView,
        private val tvPrice: TextView,
        private val tvQuantity: TextView,
        private val tvLocation: TextView,
        private val tvStatus: TextView
    ) : RecyclerView.ViewHolder(itemView) {

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
            // Use total price from API response
            tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
            tvQuantity.text = "Tersedia: ${listing.quantity} ${listing.unit}"
            tvLocation.text = listing.location

            // Set status badge color based on condition
            when (listing.condition) {
                "clean" -> {
                    tvStatus.text = "Bersih"
                    // tvStatus.setBackgroundColor(itemView.context.getColor(R.color.green_500))
                }
                "needs_cleaning" -> {
                    tvStatus.text = "Perlu Dibersihkan"
                    // tvStatus.setBackgroundColor(itemView.context.getColor(R.color.orange_500))
                }
                "mixed" -> {
                    tvStatus.text = "Campur"
                    // tvStatus.setBackgroundColor(itemView.context.getColor(R.color.gray_500))
                }
                else -> {
                    tvStatus.text = listing.condition
                    // tvStatus.setBackgroundColor(itemView.context.getColor(R.color.gray_500))
                }
            }

            itemView.setOnClickListener {
                onItemClick(listing)
            }
        }
    }
}