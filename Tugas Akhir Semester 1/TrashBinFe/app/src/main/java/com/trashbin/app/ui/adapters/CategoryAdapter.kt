package com.trashbin.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory

class CategoryAdapter(
    private val categories: List<WasteCategory>,
    private val onCategorySelected: (WasteCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val btnSelect: ImageButton = itemView.findViewById(R.id.btn_select)

        fun bind(category: WasteCategory) {
            tvCategoryName.text = category.name
            btnSelect.setOnClickListener {
                onCategorySelected(category)
            }
        }
    }
}