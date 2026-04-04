package com.kelas.balancebook.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun format(amount: Double, currencyCode: String?): String {
        val code = (currencyCode ?: "IDR").uppercase(Locale.US)

        val locale = when (code) {
            "USD" -> Locale.US
            "EUR" -> Locale.GERMANY
            else -> Locale.forLanguageTag("id-ID")
        }

        val prefix = when (code) {
            "USD" -> "$"
            "EUR" -> "€"
            else -> "Rp"
        }

        val formatter = NumberFormat.getInstance(locale)
        return "$prefix ${formatter.format(amount.toLong())}"
    }

    fun formatIdr(amount: Double): String {
        return format(amount, "IDR")
    }
}
