package com.kelasxi.waveoffood.data.repository

import android.util.Patterns
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.data.preferences.UserPreferencesManager
import com.kelasxi.waveoffood.data.preferences.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val userPreferencesManager: UserPreferencesManager
) {
    
    // Flow untuk mengamati status login
    val isLoggedIn: Flow<Boolean> = userPreferencesManager.isLoggedIn
    
    // Flow untuk mengamati profil user
    val userProfile: Flow<UserProfile> = userPreferencesManager.userProfile
    
    // Cek apakah user sudah login (dari local storage)
    suspend fun checkUserLoggedIn(): Boolean {
        return userPreferencesManager.isLoggedIn.first()
    }
    
    // Login dengan email dan password
    suspend fun signInWithEmailAndPassword(
        email: String, 
        password: String,
        rememberLogin: Boolean = true
    ): Result<FirebaseUser> {
        return try {
            // Validasi input
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Email and password cannot be empty"))
            }
            
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.failure(Exception("Please enter a valid email address"))
            }
            
            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }
            
            // Authenticate dengan Firebase
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Verifikasi user aktif
                if (!user.isEmailVerified) {
                    // Optional: Uncomment jika ingin memaksa email verification
                    // firebaseAuth.signOut()
                    // return Result.failure(Exception("Please verify your email address"))
                }
                
                // Ambil data user dari Firestore
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                
                if (userDoc.exists()) {
                    // User ada di database, simpan ke local storage
                    userPreferencesManager.saveUserLogin(
                        userId = user.uid,
                        email = user.email ?: email,
                        name = userDoc.getString("name") ?: user.displayName ?: "",
                        phone = userDoc.getString("phone") ?: "",
                        address = userDoc.getString("address") ?: "",
                        avatarUrl = userDoc.getString("avatarUrl") ?: user.photoUrl?.toString() ?: "",
                        rememberLogin = rememberLogin
                    )
                } else {
                    // User tidak ada di Firestore, buat dokumen baru
                    val userData = hashMapOf(
                        "name" to (user.displayName ?: ""),
                        "email" to email,
                        "phone" to "",
                        "address" to "",
                        "avatarUrl" to (user.photoUrl?.toString() ?: ""),
                        "createdAt" to System.currentTimeMillis(),
                        "isActive" to true
                    )
                    
                    firestore.collection("users").document(user.uid).set(userData).await()
                    
                    userPreferencesManager.saveUserLogin(
                        userId = user.uid,
                        email = email,
                        name = user.displayName ?: "",
                        phone = "",
                        address = "",
                        avatarUrl = user.photoUrl?.toString() ?: "",
                        rememberLogin = rememberLogin
                    )
                }
                
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: User authentication returned null"))
            }
        } catch (e: Exception) {
            // Handle specific Firebase Auth exceptions
            val errorMessage = when {
                e.message?.contains("invalid-email") == true -> "Invalid email address format"
                e.message?.contains("user-disabled") == true -> "This account has been disabled"
                e.message?.contains("user-not-found") == true -> "No account found with this email address"
                e.message?.contains("wrong-password") == true -> "Incorrect password"
                e.message?.contains("invalid-credential") == true -> "Invalid email or password"
                e.message?.contains("too-many-requests") == true -> "Too many login attempts. Please try again later"
                e.message?.contains("network-request-failed") == true -> "Network error. Please check your connection"
                else -> "Login failed: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    // Register dengan email dan password
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        phone: String = "",
        rememberLogin: Boolean = true
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Simpan data user ke Firestore
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "address" to "",
                    "avatarUrl" to "",
                    "createdAt" to System.currentTimeMillis(),
                    "isActive" to true
                )
                
                firestore.collection("users").document(user.uid).set(userData).await()
                
                // Simpan data user ke local storage
                userPreferencesManager.saveUserLogin(
                    userId = user.uid,
                    email = email,
                    name = name,
                    phone = phone,
                    rememberLogin = rememberLogin
                )
                
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Auto login jika user sudah tersimpan di local storage
    suspend fun autoLogin(): Result<UserProfile> {
        return try {
            val isLoggedIn = userPreferencesManager.isLoggedIn.first()
            val rememberLogin = userPreferencesManager.rememberLogin.first()
            
            if (isLoggedIn && rememberLogin) {
                val userProfile = userPreferencesManager.userProfile.first()
                
                // Cek apakah user profile valid
                if (userProfile.userId.isNotEmpty() && userProfile.email.isNotEmpty()) {
                    // Try to restore Firebase Auth session
                    val currentUser = firebaseAuth.currentUser
                    
                    if (currentUser != null && currentUser.uid == userProfile.userId) {
                        // Firebase session masih aktif
                        Result.success(userProfile)
                    } else {
                        // Firebase session expired, tapi local data masih valid
                        // Restore login dengan data local
                        try {
                            // Optional: Bisa try silent refresh atau gunakan stored credentials
                            // Untuk sekarang, trust local data jika valid
                            Result.success(userProfile)
                        } catch (e: Exception) {
                            // Jika ada masalah, clear data dan minta login ulang
                            signOut()
                            Result.failure(Exception("Auto login failed: ${e.message}"))
                        }
                    }
                } else {
                    // Data user tidak valid
                    signOut()
                    Result.failure(Exception("Invalid user data"))
                }
            } else {
                Result.failure(Exception("User not logged in or not remembered"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update profil user
    suspend fun updateUserProfile(
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        avatarUrl: String? = null
    ): Result<Unit> {
        return try {
            val userId = userPreferencesManager.getUserId()
            if (userId.isNotEmpty()) {
                // Update di Firestore
                val updates = mutableMapOf<String, Any>()
                name?.let { updates["name"] = it }
                phone?.let { updates["phone"] = it }
                address?.let { updates["address"] = it }
                avatarUrl?.let { updates["avatarUrl"] = it }
                
                if (updates.isNotEmpty()) {
                    firestore.collection("users").document(userId).update(updates).await()
                    
                    // Update di local storage
                    userPreferencesManager.updateUserProfile(name, phone, address, avatarUrl)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Logout
    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            userPreferencesManager.clearUserLogin()
        } catch (e: Exception) {
            // Log error tapi tetap clear local data
            userPreferencesManager.clearUserLogin()
        }
    }
    
    // Reset password
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get current user dari local storage
    suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            if (userPreferencesManager.isLoggedIn.first()) {
                userPreferencesManager.userProfile.first()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Sinkronisasi data user dari Firestore ke local storage
    suspend fun syncUserData(): Result<UserProfile> {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                
                if (userDoc.exists()) {
                    val name = userDoc.getString("name") ?: ""
                    val phone = userDoc.getString("phone") ?: ""
                    val address = userDoc.getString("address") ?: ""
                    val avatarUrl = userDoc.getString("avatarUrl") ?: ""
                    
                    // Update local storage dengan data terbaru dari Firestore
                    userPreferencesManager.updateUserProfile(name, phone, address, avatarUrl)
                    
                    val updatedProfile = userPreferencesManager.userProfile.first()
                    Result.success(updatedProfile)
                } else {
                    Result.failure(Exception("User document not found"))
                }
            } else {
                Result.failure(Exception("No authenticated user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Google Sign-In
    suspend fun signInWithGoogle(googleToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            
            result.user?.let { user ->
                // Cek apakah user sudah ada di Firestore
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                
                if (userDoc.exists()) {
                    // User sudah ada, update data login
                    userPreferencesManager.saveUserLogin(
                        userId = user.uid,
                        email = user.email ?: "",
                        name = userDoc.getString("name") ?: user.displayName ?: "",
                        phone = userDoc.getString("phone") ?: "",
                        address = userDoc.getString("address") ?: "",
                        avatarUrl = userDoc.getString("avatarUrl") ?: user.photoUrl?.toString() ?: "",
                        rememberLogin = true
                    )
                } else {
                    // User baru, buat dokumen baru di Firestore
                    val userData = hashMapOf(
                        "name" to (user.displayName ?: ""),
                        "email" to (user.email ?: ""),
                        "phone" to "",
                        "address" to "",
                        "avatarUrl" to (user.photoUrl?.toString() ?: ""),
                        "createdAt" to System.currentTimeMillis(),
                        "isActive" to true
                    )
                    
                    firestore.collection("users").document(user.uid).set(userData).await()
                    
                    userPreferencesManager.saveUserLogin(
                        userId = user.uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        phone = "",
                        address = "",
                        avatarUrl = user.photoUrl?.toString() ?: "",
                        rememberLogin = true
                    )
                }
            }
            
            result.user
        } catch (e: Exception) {
            throw Exception("Google Sign-In failed: ${e.message}")
        }
    }

    // Get current Firebase user
    suspend fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}
