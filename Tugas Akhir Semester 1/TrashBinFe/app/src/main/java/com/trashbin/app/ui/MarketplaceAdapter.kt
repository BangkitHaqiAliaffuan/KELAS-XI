package com.trashbin.app.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.trashbin.app.R
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.utils.CurrencyHelper

class MarketplaceAdapter(
    private val listings: List<MarketplaceListing>,
    private val onItemClick: (MarketplaceListing) -> Unit
) : RecyclerView.Adapter<MarketplaceAdapter.MarketplaceViewHolder>() {

    class MarketplaceViewHolder(itemView: View, 
        private val ivThumbnail: ImageView, 
        private val tvTitle: TextView, 
        private val tvCategory: TextView, 
        private val tvPrice: TextView, 
        private val tvQuantity: TextView, 
        private val tvLocation: TextView, 
        private val tvStatus: TextView
    ) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = ivThumbnail
        val title: TextView = tvTitle
        val category: TextView = tvCategory
        val price: TextView = tvPrice
        val quantity: TextView = tvQuantity
        val location: TextView = tvLocation
        val status: TextView = tvStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketplaceViewHolder {
        val cardView = MaterialCardView(parent.context).apply {
            val margin = (4 * parent.context.resources.displayMetrics.density).toInt()
            setCardBackgroundColor(parent.context.resources.getColor(R.color.material_dynamic_neutral0))
            radius = 8 * parent.context.resources.displayMetrics.density
            cardElevation = 4 * parent.context.resources.displayMetrics.density
            setPadding(margin, margin, margin, margin)
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        val linearLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt())
        }

        val imageView = ImageView(parent.context).apply {
            id = View.generateViewId() // This will be iv_thumbnail
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (150 * parent.context.resources.displayMetrics.density).toInt()
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            // setBackgroundResource(R.color.gray_200) // Assuming this color exists
            contentDescription = "Foto barang"
        }

        val title = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_title
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
            id = View.generateViewId() // This will be tv_category
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
            id = View.generateViewId() // This will be tv_price
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
            id = View.generateViewId() // This will be tv_quantity
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            textSize = 12f
        }

        val location = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_location
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                gravity = android.view.Gravity.END
            }
            textSize = 12f
        }

        infoLayout.addView(quantity)
        infoLayout.addView(location)

        val status = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_status
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            // setBackgroundResource(R.drawable.bg_status_badge) // Assuming this drawable exists
            setPadding((8 * parent.context.resources.displayMetrics.density).toInt(),
                (4 * parent.context.resources.displayMetrics.density).toInt(),
                (8 * parent.context.resources.displayMetrics.density).toInt(),
                (4 * parent.context.resources.displayMetrics.density).toInt())
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
        
        return MarketplaceViewHolder(cardView, imageView, title, category, price, quantity, location, status)
    }

    override fun onBindViewHolder(holder: MarketplaceViewHolder, position: Int) {
        val listing = listings[position]
        holder.title.text = listing.title
        holder.category.text = listing.category.name
        holder.price.text = CurrencyHelper.formatRupiah(listing.totalPrice)
        holder.quantity.text = "Tersedia: ${listing.quantity} ${listing.unit}"
        holder.location.text = listing.location
        holder.status.text = when (listing.condition) {
            "clean" -> "Bersih"
            "needs_cleaning" -> "Perlu Dibersihkan"
            "mixed" -> "Campur"
            else -> listing.condition
        }
        
        // Set image if available
        // if (listing.thumbnail != null) {
        //     holder.thumbnail.setImageResource(listing.thumbnail)
        // } else {
        //     holder.thumbnail.setImageResource(R.drawable.default_thumbnail) // Use a default image
        // }
        
        holder.itemView.setOnClickListener {
            onItemClick(listing)
        }
    }

    override fun getItemCount(): Int = listings.size
}