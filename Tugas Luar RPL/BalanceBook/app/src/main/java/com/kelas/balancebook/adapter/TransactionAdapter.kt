package com.kelas.balancebook.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kelas.balancebook.R
import com.kelas.balancebook.databinding.ItemTransactionBinding
import com.kelas.balancebook.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private var items: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            val ctx = binding.root.context

            binding.llCategoryIcon.setBackgroundResource(transaction.iconBgRes)
            binding.ivCategoryIcon.setImageResource(transaction.iconRes)
            binding.ivCategoryIcon.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, transaction.iconTintRes))

            binding.tvCategory.text = transaction.category
            binding.tvNote.text = "${transaction.note} • ${transaction.date}"

            val formatted = NumberFormat.getInstance(Locale("id", "ID")).format(transaction.amount)
            val sign = if (transaction.isIncome) "+" else "-"
            binding.tvAmount.text = "$sign Rp $formatted"

            val amountColor = ContextCompat.getColor(
                ctx,
                if (transaction.isIncome) R.color.colorIncome else R.color.colorExpense
            )
            binding.tvAmount.setTextColor(amountColor)
            // Update the amount badge background
            val badgeBg = if (transaction.isIncome) R.drawable.bg_rounded_income else R.drawable.bg_rounded_expense
            (binding.tvAmount.parent as? android.view.ViewGroup)?.setBackgroundResource(badgeBg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Transaction>) {
        items = newItems
        notifyDataSetChanged()
    }
}
