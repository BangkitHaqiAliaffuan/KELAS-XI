package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.CategoryModel

class CategoryAdapter(
    private val categories: List<CategoryModel>,
    private val onCategoryClick: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        private val categoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: CategoryModel) {
            categoryName.text = category.name
            
            // Load category image using Glide
            if (category.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(category.imageUrl)
                    .placeholder(R.drawable.ic_category_food)
                    .error(R.drawable.ic_category_food)
                    .into(categoryIcon)
            } else {
                // Set default icon based on category type
                val iconResource = when (category.id.lowercase()) {
                    "pizza" -> R.drawable.ic_category_food
                    "burger" -> R.drawable.ic_category_food
                    "indonesian" -> R.drawable.ic_category_food
                    "dessert" -> R.drawable.ic_category_food
                    "drinks" -> R.drawable.ic_category_food
                    else -> R.drawable.ic_category_food
                }
                categoryIcon.setImageResource(iconResource)
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }
}
