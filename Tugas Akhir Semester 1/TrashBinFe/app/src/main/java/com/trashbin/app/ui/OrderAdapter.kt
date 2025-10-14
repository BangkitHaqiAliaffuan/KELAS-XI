package com.trashbin.app.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.trashbin.app.R
import com.trashbin.app.data.model.Order
import com.trashbin.app.utils.CurrencyHelper

class OrderAdapter(
    private val orders: List<Order>,
    private val onOrderAction: (Order, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View,
        private val ivListingImage: ImageView,
        private val tvListingTitle: TextView,
        private val tvListingCategory: TextView,
        private val tvOrderQuantity: TextView,
        private val tvPricePerUnit: TextView,
        private val tvTotalPrice: TextView,
        private val tvStatus: TextView,
        private val tvSellerName: TextView,
        private val tvOrderDate: TextView
    ) : RecyclerView.ViewHolder(itemView) {
        val listingImage: ImageView = ivListingImage
        val listingTitle: TextView = tvListingTitle
        val listingCategory: TextView = tvListingCategory
        val orderQuantity: TextView = tvOrderQuantity
        val pricePerUnit: TextView = tvPricePerUnit
        val totalPrice: TextView = tvTotalPrice
        val status: TextView = tvStatus
        val sellerName: TextView = tvSellerName
        val orderDate: TextView = tvOrderDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
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

        val mainLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt()
            )
        }

        // Listing Info Layout
        val listingInfoLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val imageView = ImageView(parent.context).apply {
            id = View.generateViewId() // This will be iv_listing_image
            layoutParams = LinearLayout.LayoutParams(
                (60 * parent.context.resources.displayMetrics.density).toInt(),
                (60 * parent.context.resources.displayMetrics.density).toInt()
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            // setBackgroundResource(R.color.gray_200) // Assuming this color exists
            contentDescription = "Foto barang"
        }

        val textLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins((12 * parent.context.resources.displayMetrics.density).toInt(), 0, 0, 0)
            }
        }

        val title = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_listing_title
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val category = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_listing_category
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (2 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 12f
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_secondary))
        }

        val quantity = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_order_quantity
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (2 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 12f
        }

        textLayout.addView(title)
        textLayout.addView(category)
        textLayout.addView(quantity)

        listingInfoLayout.addView(imageView)
        listingInfoLayout.addView(textLayout)

        // Price and Total Layout
        val priceLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
        }

        val pricePerUnit = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_price_per_unit
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_secondary))
            textSize = 14f
        }

        val totalPrice = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_total_price
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_primary))
            textSize = 16f
        }

        priceLayout.addView(pricePerUnit)
        priceLayout.addView(totalPrice)

        // Status and Seller Info Layout
        val statusLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
        }

        val status = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_status
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

        val sellerName = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_seller_name
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins((8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0, 0)
                gravity = android.view.Gravity.END
            }
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_secondary))
            textSize = 12f
        }

        statusLayout.addView(status)
        statusLayout.addView(sellerName)

        // Order Date
        val orderDate = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_order_date
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (8 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_secondary))
            textSize = 12f
        }

        mainLayout.addView(listingInfoLayout)
        mainLayout.addView(priceLayout)
        mainLayout.addView(statusLayout)
        mainLayout.addView(orderDate)
        
        cardView.addView(mainLayout)
        
        return OrderViewHolder(cardView, imageView, title, category, quantity, pricePerUnit, totalPrice, status, sellerName, orderDate)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.listingTitle.text = order.listing.title
        holder.listingCategory.text = order.listing.category.name
        holder.orderQuantity.text = "Jumlah: ${order.quantity}"
        holder.pricePerUnit.text = CurrencyHelper.formatRupiah(order.listing.pricePerUnit)
        holder.totalPrice.text = CurrencyHelper.formatRupiah(order.totalPrice)
        holder.status.text = order.status
        holder.sellerName.text = "Oleh: ${order.seller.name}"
        holder.orderDate.text = order.createdAt // Using createdAt as order date
        
        // Set image if available
        // if (order.listingImage != null) {
        //     holder.listingImage.setImageResource(order.listingImage)
        // } else {
        //     holder.listingImage.setImageResource(R.drawable.default_thumbnail) // Use a default image
        // }
    }

    override fun getItemCount(): Int = orders.size
}