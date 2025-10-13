package com.trashbin.app.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    init {
        formatter.minimumFractionDigits = 0
        formatter.currency = java.util.Currency.getInstance("IDR")
    }

    fun formatRupiah(amount: Double): String {
        return formatter.format(amount)
    }

    fun formatRupiahWithoutPrefix(amount: Double): String {
        return formatter.format(amount).replace("Rp", "").trim()
    }

    fun parseRupiah(text: String): Double {
        // Remove all non-digit characters except decimal separator
        val numericValue = text.replace(Regex("[^0-9,.]"), "")
        return try {
            numericValue.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    fun formatNumber(number: Long): String {
        return NumberFormat.getNumberInstance(Locale.US).format(number)
    }
}