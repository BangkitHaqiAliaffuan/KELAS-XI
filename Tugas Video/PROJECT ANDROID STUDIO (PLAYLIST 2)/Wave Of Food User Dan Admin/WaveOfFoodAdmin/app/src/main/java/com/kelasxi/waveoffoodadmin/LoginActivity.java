package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    
    // Admin credentials - dalam implementasi nyata, gunakan Firebase Authentication rules
    private static final String ADMIN_EMAIL = "admin@waveoffood.com";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        
        // Check if user is already logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && isAdminUser(currentUser.getEmail())) {
            navigateToMainActivity();
            return;
        }
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginAdmin());
    }
    
    private void loginAdmin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }
        
        // Check if email is admin email
        if (!isAdminUser(email)) {
            Toast.makeText(this, "Akses ditolak. Hanya admin yang dapat masuk.", Toast.LENGTH_LONG).show();
            return;
        }
        
        showLoading(true);
        
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && isAdminUser(user.getEmail())) {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        } else {
                            auth.signOut();
                            Toast.makeText(this, "Akses ditolak", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ? 
                                task.getException().getMessage() : "Login gagal";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private boolean isAdminUser(String email) {
        // Dalam implementasi nyata, bisa menggunakan custom claims atau role-based auth
        return email != null && (email.equals(ADMIN_EMAIL) || email.contains("admin"));
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
