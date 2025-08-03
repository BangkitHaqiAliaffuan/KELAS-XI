package com.kelasxi.waveoffood.models

/**
 * Data class untuk merepresentasikan data pengguna
 */
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val address: String? = null,
    val profileImage: String? = null
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", null, null)
}
