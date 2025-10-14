package com.trashbin.app.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.trashbin.app.R
import com.trashbin.app.data.model.PickupItemRequest

class PickupItemAdapter(
    private val items: MutableList<PickupItemRequest>,
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<PickupItemAdapter.PickupItemViewHolder>() {

    class PickupItemViewHolder(itemView: View,
        private val tvCategory: TextView,
        private val tvWeight: TextView,
        private val tvPrice: TextView,
        private val btnDelete: ImageButton
    ) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = tvCategory
        val weight: TextView = tvWeight
        val price: TextView = tvPrice
        val deleteButton: ImageButton = btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupItemViewHolder {
        val cardView = MaterialCardView(parent.context).apply {
            val margin = (4 * parent.context.resources.displayMetrics.density).toInt()
            setCardBackgroundColor(parent.context.resources.getColor(R.color.material_dynamic_neutral0))
            radius = 8 * parent.context.resources.displayMetrics.density
            cardElevation = 2 * parent.context.resources.displayMetrics.density
            setPadding(margin, margin, margin, margin)
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        val linearLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding((12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt(),
                (12 * parent.context.resources.displayMetrics.density).toInt())
        }

        val textContainer = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val category = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_category
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val weight = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_weight
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (4 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 14f
        }

        val price = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_price
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (4 * parent.context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            textSize = 14f
            setTextColor(parent.context.resources.getColor(R.color.design_default_color_primary))
        }

        textContainer.addView(category)
        textContainer.addView(weight)
        textContainer.addView(price)

        val deleteButton = ImageButton(parent.context).apply {
            id = View.generateViewId() // This will be btn_delete
            // setImageResource(R.drawable.ic_delete) // Assuming this drawable exists
            setBackgroundResource(androidx.appcompat.R.drawable.abc_btn_borderless_material)
            contentDescription = "Hapus item"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
        }

        linearLayout.addView(textContainer)
        linearLayout.addView(deleteButton)
        cardView.addView(linearLayout)
        
        return PickupItemViewHolder(cardView, category, weight, price, deleteButton)
    }

    override fun onBindViewHolder(holder: PickupItemViewHolder, position: Int) {
        val item = items[position]
        holder.category.text = item.categoryId.toString()  // Placeholder - in a real app, you'd need to map category ID to name
        holder.weight.text = "Berat: ${item.estimatedWeight} kg"
        holder.price.text = "Harga: ${item.photoUrl}"  // Placeholder - price would need to be calculated based on category
        
        holder.deleteButton.setOnClickListener {
            onItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = items.size
}