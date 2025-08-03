package com.kelasxi.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.models.UserModel

/**
 * Activity untuk halaman registrasi pengguna
 */
class RegisterActivity : AppCompatActivity() {
    
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Inisialisasi views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        
        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        setupClickListeners()
    }
    
    /**
     * Setup click listeners untuk button dan text view
     */
    private fun setupClickListeners() {
        // OnClickListener untuk tombol Register
        btnRegister.setOnClickListener {
            performRegister()
        }
        
        // OnClickListener untuk TextView Login
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    /**
     * Proses registrasi pengguna baru
     */
    private fun performRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validasi input
        if (name.isEmpty()) {
            etName.error = "Nama tidak boleh kosong"
            etName.requestFocus()
            return
        }
        
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }
        
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return
        }
        
        // Disable tombol register saat proses
        btnRegister.isEnabled = false
        btnRegister.text = "Loading..."
        
        // Proses registrasi menggunakan Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registrasi berhasil, simpan data user ke Firestore
                    val currentUser = auth.currentUser
                    currentUser?.let { user ->
                        val userModel = UserModel(
                            uid = user.uid,
                            name = name,
                            email = email
                        )
                        
                        // Simpan ke Firestore dengan UID sebagai ID dokumen
                        firestore.collection("users").document(user.uid)
                            .set(userModel)
                            .addOnSuccessListener {
                                // Data berhasil disimpan
                                btnRegister.isEnabled = true
                                btnRegister.text = "Daftar"
                                
                                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                
                                // Arahkan ke LoginActivity
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                // Gagal menyimpan data
                                btnRegister.isEnabled = true
                                btnRegister.text = "Daftar"
                                
                                Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    // Registrasi gagal
                    btnRegister.isEnabled = true
                    btnRegister.text = "Daftar"
                    
                    val errorMessage = task.exception?.message ?: "Registrasi gagal"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}
