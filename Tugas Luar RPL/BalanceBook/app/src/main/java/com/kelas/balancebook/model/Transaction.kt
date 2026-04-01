package com.kelas.balancebook.model

data class Transaction(
    val id: Int,
    val category: String,
    val note: String,
    val date: String,
    val amount: Long,
    val isIncome: Boolean,
    val iconRes: Int,
    val iconBgRes: Int,
    val iconTintRes: Int
)
