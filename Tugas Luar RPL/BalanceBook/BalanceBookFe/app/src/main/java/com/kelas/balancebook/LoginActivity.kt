package com.kelas.balancebook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kelas.balancebook.data.local.SessionManager
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.GoogleLoginRequest
import com.kelas.balancebook.data.remote.LoginRequest
import com.kelas.balancebook.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Toast.makeText(this, "Login Google dibatalkan", Toast.LENGTH_SHORT).show()
                binding.btnGoogle.isEnabled = true
                return@registerForActivityResult
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken.isNullOrBlank()) {
                    Toast.makeText(this, "Google token tidak tersedia", Toast.LENGTH_SHORT).show()
                    binding.btnGoogle.isEnabled = true
                    return@registerForActivityResult
                }

                authenticateWithFirebase(idToken)
            } catch (exception: ApiException) {
                val statusLabel = GoogleSignInStatusCodes.getStatusCodeString(exception.statusCode)
                Log.e(TAG, "Google sign-in gagal, code=${exception.statusCode}", exception)
                Toast.makeText(
                    this,
                    "Login Google gagal (${exception.statusCode}: $statusLabel)",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnGoogle.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        setupGoogleSignIn()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Kata sandi tidak boleh kosong"
                return@setOnClickListener
            }

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            authenticate(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            // TODO: Implement forgot password flow
            Toast.makeText(this, "Fitur lupa kata sandi segera hadir", Toast.LENGTH_SHORT).show()
        }

        binding.btnGoogle.setOnClickListener {
            startGoogleLogin()
        }
    }

    private fun setupGoogleSignIn() {
        val webClientId = resolveWebClientId()
        if (webClientId.isBlank()) {
            Log.e(TAG, "Web client ID kosong. Cek google-services.json dan strings.xml")
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun resolveWebClientId(): String {
        val fromFirebase = runCatching { getString(R.string.default_web_client_id) }.getOrNull()
        if (!fromFirebase.isNullOrBlank()) {
            return fromFirebase
        }

        val fromManual = getString(R.string.google_web_client_id)
        if (fromManual.startsWith("YOUR_WEB_CLIENT_ID")) {
            return ""
        }

        return fromManual
    }

    private fun startGoogleLogin() {
        if (!::googleSignInClient.isInitialized) {
            Toast.makeText(
                this,
                "Google Sign-In belum dikonfigurasi",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding.btnGoogle.isEnabled = false
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun authenticate(email: String, password: String) {
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            runCatching {
                ApiClient.service(this@LoginActivity).login(
                    LoginRequest(email = email, password = password)
                )
            }.onSuccess { response ->
                SessionManager.saveSession(this@LoginActivity, response.token, response.user)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }.onFailure {
                Toast.makeText(
                    this@LoginActivity,
                    it.message ?: "Login gagal, silakan coba lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.btnLogin.isEnabled = true
        }
    }

    private fun loginWithGoogleToken(idToken: String) {
        lifecycleScope.launch {
            runCatching {
                ApiClient.service(this@LoginActivity).googleLogin(
                    GoogleLoginRequest(idToken = idToken)
                )
            }.onSuccess { response ->
                SessionManager.saveSession(this@LoginActivity, response.token, response.user)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }.onFailure {
                Toast.makeText(
                    this@LoginActivity,
                    it.message ?: "Login Google gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.btnGoogle.isEnabled = true
        }
    }

    private fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                loginWithGoogleToken(idToken)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Firebase signInWithCredential gagal", exception)
                Toast.makeText(
                    this,
                    exception.message ?: "Autentikasi Firebase gagal",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnGoogle.isEnabled = true
            }
    }
}
