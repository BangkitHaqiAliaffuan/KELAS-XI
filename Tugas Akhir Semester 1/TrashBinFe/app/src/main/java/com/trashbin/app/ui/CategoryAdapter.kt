package com.trashbin.app.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory

class CategoryAdapter(
    private val categories: List<WasteCategory>,
    private val onCategorySelected: (WasteCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View, private val tvCategoryName: TextView, private val btnSelect: ImageButton) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = tvCategoryName
        val selectButton: ImageButton = btnSelect
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
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
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val textView = TextView(parent.context).apply {
            id = View.generateViewId() // This will be tv_category_name
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            textSize = 16f
        }

        val selectButton = ImageButton(parent.context).apply {
            id = View.generateViewId() // This will be btn_select
            // setImageResource(R.drawable.ic_check) // Assuming this drawable exists
            setBackgroundResource(androidx.appcompat.R.drawable.abc_btn_borderless_material)
            contentDescription = "Pilih kategori"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
        }

        linearLayout.addView(textView)
        linearLayout.addView(selectButton)
        cardView.addView(linearLayout)
        
        return CategoryViewHolder(cardView, textView, selectButton)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name
        
        holder.selectButton.setOnClickListener {
            onCategorySelected(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}