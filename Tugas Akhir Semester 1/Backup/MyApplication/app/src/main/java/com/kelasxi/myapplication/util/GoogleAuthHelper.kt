package com.kelasxi.myapplication.util

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Helper untuk Google Sign-In menggunakan Credential Manager API dengan fallback Firebase Auth.
 * 
 * Emulator online seperti Appetize.io mungkin tidak memiliki Google Play Services,
 * jadi kami menambahkan fallback untuk menangani error "no provider dependencies found".
 */
class GoogleAuthHelper(private val activity: Activity) {

    companion object {
        private const val TAG = "GoogleAuthHelper"
        // Web Client ID (client_type: 3) dari google-services.json
        const val WEB_CLIENT_ID = "986418745393-ke81l6ijbehk6ck4t7k7vmqp9lk1ann9.apps.googleusercontent.com"
    }

    private val credentialManager = CredentialManager.create(activity)
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signIn(): GoogleSignInResult {
        return try {
            // Coba dengan CredentialManager (ideal untuk device normal)
            val result = tryCredentialManagerSignIn()
            if (result != null) return result
            
            // Jika CredentialManager gagal, show error dengan saran
            return GoogleSignInResult.Error(
                "Google Play Services tidak tersedia di emulator ini. " +
                "Untuk testing: gunakan Android emulator lokal dengan Google Play Services yang terinstal."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error: ${e.message}", e)
            GoogleSignInResult.Error("Google Sign-In gagal: ${e.message}")
        }
    }

    private suspend fun tryCredentialManagerSignIn(): GoogleSignInResult? {
        return try {
            // Coba dengan GetGoogleIdOption dulu (lebih seamless)
            val result = tryGetGoogleId()
            if (result != null) return result

            // Fallback: GetSignInWithGoogleOption (selalu tampilkan account picker)
            val fallbackResult = trySignInWithGoogle()
            if (fallbackResult !is GoogleSignInResult.Error) fallbackResult else null
        } catch (e: Exception) {
            Log.w(TAG, "CredentialManager failed: ${e.message}")
            null  // trigger fallback
        }
    }

    private suspend fun tryGetGoogleId(): GoogleSignInResult? {
        return try {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            val response = credentialManager.getCredential(request = request, context = activity)
            parseCredential(response.credential)
        } catch (e: NoCredentialException) {
            Log.w(TAG, "GetGoogleIdOption: no credential, falling back", e)
            null  // trigger fallback
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Error("Login dibatalkan.")
        } catch (e: GetCredentialException) {
            Log.w(TAG, "GetGoogleIdOption failed: ${e.type} — ${e.message}")
            null  // trigger fallback
        }
    }

    private suspend fun trySignInWithGoogle(): GoogleSignInResult {
        return try {
            val option = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            val response = credentialManager.getCredential(request = request, context = activity)
            parseCredential(response.credential)
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Error("Login dibatalkan.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetSignInWithGoogleOption failed: ${e.type} — ${e.message}")
            GoogleSignInResult.Error("Google Sign-In gagal: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            GoogleSignInResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    private fun parseCredential(credential: androidx.credentials.Credential): GoogleSignInResult {
        return if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleSignInResult.Success(
                    idToken     = googleCred.idToken,
                    displayName = googleCred.displayName ?: "",
                    email       = googleCred.id
                )
            } catch (e: GoogleIdTokenParsingException) {
                GoogleSignInResult.Error("Gagal membaca token Google: ${e.message}")
            }
        } else {
            GoogleSignInResult.Error("Tipe credential tidak dikenali: ${credential.type}")
        }
    }
}

sealed class GoogleSignInResult {
    data class Success(
        val idToken: String,
        val displayName: String,
        val email: String
    ) : GoogleSignInResult()

    data class Error(val message: String) : GoogleSignInResult()
}
