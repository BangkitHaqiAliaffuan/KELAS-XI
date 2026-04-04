package com.kelas.balancebook.util

import com.kelas.balancebook.R
import com.kelas.balancebook.data.remote.TopCategoryDto
import com.kelas.balancebook.data.remote.TransactionDto
import com.kelas.balancebook.model.Transaction

object TransactionUiMapper {
    fun map(dto: TransactionDto): Transaction {
        val lowerCategory = dto.category.lowercase()
        val iconRes = when {
            lowerCategory.contains("food") || lowerCategory.contains("makan") -> R.drawable.ic_restaurant
            lowerCategory.contains("transport") || lowerCategory.contains("grab") || lowerCategory.contains("bus") -> R.drawable.ic_directions_car
            lowerCategory.contains("shop") || lowerCategory.contains("belanja") -> R.drawable.ic_shopping_cart
            lowerCategory.contains("entertain") || lowerCategory.contains("hiburan") || lowerCategory.contains("netflix") -> R.drawable.ic_theater
            lowerCategory.contains("coffee") || lowerCategory.contains("kopi") -> R.drawable.ic_coffee
            dto.type == "income" -> R.drawable.ic_payments
            else -> R.drawable.ic_receipt_long
        }

        val (iconBgRes, iconTintRes) = when {
            dto.type == "income" -> R.drawable.bg_rounded_emerald to R.color.colorEmerald
            lowerCategory.contains("food") || lowerCategory.contains("makan") -> R.drawable.bg_rounded_orange to R.color.colorOrange
            lowerCategory.contains("transport") || lowerCategory.contains("grab") || lowerCategory.contains("bus") -> R.drawable.bg_rounded_blue to R.color.colorBlue
            lowerCategory.contains("shop") || lowerCategory.contains("belanja") -> R.drawable.bg_rounded_indigo to R.color.colorIndigo
            lowerCategory.contains("entertain") || lowerCategory.contains("hiburan") || lowerCategory.contains("netflix") -> R.drawable.bg_rounded_purple to R.color.colorPurple
            else -> R.drawable.bg_rounded_primary_light to R.color.colorPrimary
        }

        return Transaction(
            id = dto.id.toInt(),
            category = dto.category,
            note = dto.note ?: "-",
            date = dto.transactionDate,
            amount = dto.amount.toLong(),
            isIncome = dto.type == "income",
            iconRes = iconRes,
            iconBgRes = iconBgRes,
            iconTintRes = iconTintRes
        )
    }

    fun asMapByCategory(categories: List<TopCategoryDto>): Map<String, TopCategoryDto> {
        return categories.associateBy { it.category.lowercase() }
    }
}
