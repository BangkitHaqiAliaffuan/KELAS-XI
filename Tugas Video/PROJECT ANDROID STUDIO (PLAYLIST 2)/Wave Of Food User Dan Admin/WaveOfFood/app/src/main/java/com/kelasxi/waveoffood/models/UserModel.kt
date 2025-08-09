package com.kelasxi.waveoffood.models

/**
 * Data class untuk merepresentasikan data pengguna
 */
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val profileImage: String = "",
    val profileImageUrl: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", "", "", "", System.currentTimeMillis(), System.currentTimeMillis())
}
