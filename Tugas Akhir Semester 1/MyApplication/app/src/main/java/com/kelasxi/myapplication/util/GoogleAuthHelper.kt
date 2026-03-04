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

/**
 * Helper untuk Google Sign-In menggunakan Credential Manager API.
 * Mencoba GetGoogleIdOption terlebih dahulu; jika gagal, fallback ke GetSignInWithGoogleOption
 * (yang menampilkan bottomsheet Google Sign-In standar).
 */
class GoogleAuthHelper(private val activity: Activity) {

    companion object {
        private const val TAG = "GoogleAuthHelper"
        // Web Client ID (client_type: 3) dari google-services.json
        const val WEB_CLIENT_ID = "986418745393-ke81l6ijbehk6ck4t7k7vmqp9lk1ann9.apps.googleusercontent.com"
    }

    private val credentialManager = CredentialManager.create(activity)

    suspend fun signIn(): GoogleSignInResult {
        // Coba dengan GetGoogleIdOption dulu (lebih seamless)
        val result = tryGetGoogleId()
        if (result != null) return result

        // Fallback: GetSignInWithGoogleOption (selalu tampilkan account picker)
        return trySignInWithGoogle()
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
            Log.w(TAG, "GetGoogleIdOption failed: ${e.type} — ${e.message}, falling back")
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
