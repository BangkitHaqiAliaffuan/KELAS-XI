package com.trashbin.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trashbin.app.R
import com.trashbin.app.data.model.PickupItemRequest
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.utils.CurrencyHelper

class PickupItemAdapter(
    private val categories: List<WasteCategory>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PickupItemAdapter.ViewHolder>() {

    private val items = mutableListOf<PickupItemRequest>()

    fun submitList(newItems: List<PickupItemRequest>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pickup, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvWeight: TextView = itemView.findViewById(R.id.tv_weight)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(item: PickupItemRequest, position: Int) {
            // Find category name by ID
            val category = categories.find { it.id == item.categoryId }
            tvCategory.text = category?.name ?: "Kategori Tidak Dikenal"
            tvWeight.text = "Berat: ${item.estimatedWeight} kg"
            
            // Calculate price based on category base price
            val totalPrice = (category?.basePricePerUnit ?: 0.0) * item.estimatedWeight
            tvPrice.text = "Harga: ${CurrencyHelper.formatRupiah(totalPrice)}"
            
            btnDelete.setOnClickListener { 
                onDelete(position) 
            }
        }
    }
}