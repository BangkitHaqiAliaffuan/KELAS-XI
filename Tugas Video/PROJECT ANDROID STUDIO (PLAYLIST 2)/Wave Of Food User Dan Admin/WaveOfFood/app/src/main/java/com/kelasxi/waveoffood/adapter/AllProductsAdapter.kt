package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * Adapter untuk RecyclerView semua produk
 */
class AllProductsAdapter(
    private val products: List<FoodItemModel>,
    private val onProductClick: (FoodItemModel) -> Unit
) : RecyclerView.Adapter<AllProductsAdapter.ProductViewHolder>() {
    
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProductImage: ImageView = view.findViewById(R.id.iv_product_image)
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
        val tvProductCategory: TextView = view.findViewById(R.id.tv_product_category)
        val tvProductDescription: TextView = view.findViewById(R.id.tv_product_description)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_products, parent, false)
        return ProductViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        with(holder) {
            tvProductName.text = product.foodName
            // Convert price string to formatted price if it's numeric
            tvProductPrice.text = try {
                val price = product.foodPrice.toLongOrNull() ?: 0L
                "Rp ${String.format("%,d", price)}"
            } catch (e: Exception) {
                product.foodPrice
            }
            tvProductCategory.text = product.foodCategory ?: "No Category"
            tvProductDescription.text = if (product.foodDescription.length > 60) {
                "${product.foodDescription.take(60)}..."
            } else {
                product.foodDescription
            }
            
            // Load product image
            Glide.with(holder.itemView.context)
                .load(product.foodImage)
                .placeholder(R.drawable.ic_category_food)
                .error(R.drawable.ic_category_food)
                .centerCrop()
                .into(ivProductImage)
            
            // Set click listener
            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }
    
    override fun getItemCount(): Int = products.size
}
