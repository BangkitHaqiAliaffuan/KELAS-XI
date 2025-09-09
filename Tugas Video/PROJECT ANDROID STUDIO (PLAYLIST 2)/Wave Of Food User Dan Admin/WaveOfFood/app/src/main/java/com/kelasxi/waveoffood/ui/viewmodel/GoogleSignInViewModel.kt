package com.kelasxi.waveoffood.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.kelasxi.waveoffood.data.repository.AuthRepository
import kotlinx.coroutines.launch

class GoogleSignInViewModel(
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModel() {
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    private val credentialManager = CredentialManager.create(context)
    
    fun signInWithGoogle(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                // Debug: Check Web Client ID
                val webClientId = context.getString(com.kelasxi.waveoffood.R.string.default_web_client_id)
                Log.d("GoogleSignIn", "Using Web Client ID: $webClientId")
                
                if (webClientId.isEmpty() || webClientId.contains("your_web_client_id")) {
                    throw Exception("Google Web Client ID tidak dikonfigurasi dengan benar di strings.xml")
                }
                
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()
                
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                Log.d("GoogleSignIn", "Requesting credentials...")
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                
                Log.d("GoogleSignIn", "Credentials received, handling sign-in...")
                handleSignIn(result, onSuccess, onFailure)
                
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "GetCredential failed", e)
                val errorMsg = when {
                    e.message?.contains("No credentials available") == true -> {
                        "Tidak ada akun Google yang tersedia. Pastikan:\n" +
                        "1. Anda sudah login ke Google di perangkat ini\n" +
                        "2. Google Play Services terinstall dan update\n" +
                        "3. Aplikasi sudah dikonfigurasi dengan benar di Google Console"
                    }
                    e.message?.contains("Credential retrieval cancelled") == true -> {
                        "Login Google dibatalkan"
                    }
                    else -> "Gagal login dengan Google: ${e.message}"
                }
                errorMessage = errorMsg
                onFailure(errorMsg)
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Unexpected error", e)
                val errorMsg = "Error tidak terduga: ${e.message}"
                errorMessage = errorMsg
                onFailure(errorMsg)
            } finally {
                isLoading = false
            }
        }
    }
    
    private suspend fun handleSignIn(
        result: androidx.credentials.GetCredentialResponse,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        when (val credential = result.credential) {
            is androidx.credentials.CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        
                        val googleIdToken = googleIdTokenCredential.idToken
                        
                        // Use Firebase Auth with the ID token
                        val user = authRepository.signInWithGoogle(googleIdToken)
                        if (user != null) {
                            onSuccess()
                        } else {
                            onFailure("Authentication failed")
                        }
                        
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignIn", "Invalid Google ID token", e)
                        onFailure("Invalid Google ID token")
                    }
                } else {
                    onFailure("Unexpected credential type")
                }
            }
            else -> {
                onFailure("Unexpected credential type")
            }
        }
    }
    
    fun clearErrorMessage() {
        errorMessage = null
    }
}

class GoogleSignInViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoogleSignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoogleSignInViewModel(
                AuthRepository(
                    firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance(),
                    firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance(),
                    userPreferencesManager = com.kelasxi.waveoffood.data.preferences.UserPreferencesManager(context)
                ),
                context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
