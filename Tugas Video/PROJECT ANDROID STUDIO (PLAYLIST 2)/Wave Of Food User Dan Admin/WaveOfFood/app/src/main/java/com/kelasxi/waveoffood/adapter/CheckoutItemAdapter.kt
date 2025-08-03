package com.kelasxi.waveoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.model.CartItemModel
import java.text.NumberFormat
import java.util.*

class CheckoutItemAdapter(
    private val items: List<CartItemModel>
) : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder>() {

    class CheckoutItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivItemImage: ImageView = view.findViewById(R.id.ivItemImage)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvItemQuantity: TextView = view.findViewById(R.id.tvItemQuantity)
        val tvItemSubtotal: TextView = view.findViewById(R.id.tvItemSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_simple, parent, false)
        return CheckoutItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutItemViewHolder, position: Int) {
        val item = items[position]
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        
        holder.tvItemName.text = item.name
        holder.tvItemPrice.text = "Rp ${formatter.format(item.price)}"
        holder.tvItemQuantity.text = "${item.quantity}x"
        holder.tvItemSubtotal.text = "Rp ${formatter.format(item.calculateSubtotal())}"

        // Load image
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_category_food)
            .error(R.drawable.ic_category_food)
            .centerCrop()
            .into(holder.ivItemImage)
    }

    override fun getItemCount() = items.size
}
