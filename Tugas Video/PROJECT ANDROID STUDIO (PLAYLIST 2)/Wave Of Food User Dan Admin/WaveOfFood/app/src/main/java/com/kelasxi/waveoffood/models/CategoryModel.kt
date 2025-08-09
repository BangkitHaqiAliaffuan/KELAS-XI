package com.kelasxi.waveoffood.models

/**
 * Data class untuk kategori makanan
 */
data class CategoryModel(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", true)
}
