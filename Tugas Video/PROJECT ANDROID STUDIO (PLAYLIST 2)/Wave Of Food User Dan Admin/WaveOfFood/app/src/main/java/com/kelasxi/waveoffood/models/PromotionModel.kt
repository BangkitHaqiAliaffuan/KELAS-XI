package com.kelasxi.waveoffood.models

import com.google.firebase.Timestamp

/**
 * Data class untuk promosi dan diskon
 */
data class PromotionModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val discountPercent: Int = 0,
    val discountAmount: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDiscountAmount: Double = 0.0,
    val promoCode: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val usageLimit: Int = -1, // -1 untuk unlimited
    val usageCount: Int = 0
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", 0, 0.0, 0.0, 0.0, "", Timestamp.now(), Timestamp.now(), true, -1, 0)
    
    fun isValid(): Boolean {
        val currentTime = Timestamp.now()
        return isActive && 
               currentTime.seconds >= startDate.seconds && 
               currentTime.seconds <= endDate.seconds &&
               (usageLimit == -1 || usageCount < usageLimit)
    }
    
    fun getDiscountDisplay(): String {
        return if (discountPercent > 0) {
            "${discountPercent}%"
        } else {
            "Rp ${discountAmount.toInt()}"
        }
    }
}
