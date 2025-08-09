package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditAdminActivity extends AppCompatActivity {
    
    private static final String TAG = "AddEditAdmin";
    
    private EditText etAdminName;
    private EditText etAdminEmail;
    private EditText etAdminPassword;
    private Spinner spinnerRole;
    private Spinner spinnerStatus;
    private CheckBox cbCanManageOrders;
    private CheckBox cbCanManageMenu;
    private CheckBox cbCanManageUsers;
    private CheckBox cbCanViewAnalytics;
    private CheckBox cbCanManageAdmins;
    private Button btnSave;
    private Button btnDelete;
    private ProgressBar progressBar;
    
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String adminId = null; // null for add, non-null for edit
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_admin);
        
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        checkIfEditMode();
        initViews();
        setupSpinners();
        setupClickListeners();
        
        if (isEditMode) {
            loadAdminData();
        }
    }
    
    private void initViews() {
        etAdminName = findViewById(R.id.et_admin_name);
        etAdminEmail = findViewById(R.id.et_admin_email);
        etAdminPassword = findViewById(R.id.et_admin_password);
        spinnerRole = findViewById(R.id.spinner_role);
        spinnerStatus = findViewById(R.id.spinner_status);
        cbCanManageOrders = findViewById(R.id.cb_can_manage_orders);
        cbCanManageMenu = findViewById(R.id.cb_can_manage_menu);
        cbCanManageUsers = findViewById(R.id.cb_can_manage_users);
        cbCanViewAnalytics = findViewById(R.id.cb_can_view_analytics);
        cbCanManageAdmins = findViewById(R.id.cb_can_manage_admins);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        progressBar = findViewById(R.id.progress_bar);
        
        // Show/hide password field and delete button based on mode
        if (isEditMode) {
            etAdminPassword.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            etAdminPassword.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        }
    }
    
    private void setupSpinners() {
        // Role spinner
        String[] roles = {"admin", "super_admin", "moderator"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        
        // Status spinner
        String[] statuses = {"active", "inactive", "suspended"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }
    
    private void checkIfEditMode() {
        Intent intent = getIntent();
        if (intent != null) {
            adminId = intent.getStringExtra("ADMIN_ID");
            isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false);
            
            if (isEditMode && adminId != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Admin");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Add New Admin");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveAdmin());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }
    
    private void loadAdminData() {
        if (adminId == null) return;
        
        showLoading(true);
        
        firestore.collection("admins").document(adminId)
                .get()
                .addOnSuccessListener(document -> {
                    showLoading(false);
                    
                    if (document.exists()) {
                        // Fill form with existing data
                        etAdminName.setText(document.getString("name"));
                        etAdminEmail.setText(document.getString("email"));
                        
                        // Set role spinner
                        String role = document.getString("role");
                        if (role != null) {
                            ArrayAdapter<String> roleAdapter = (ArrayAdapter<String>) spinnerRole.getAdapter();
                            int rolePosition = roleAdapter.getPosition(role);
                            if (rolePosition >= 0) {
                                spinnerRole.setSelection(rolePosition);
                            }
                        }
                        
                        // Set status spinner
                        String status = document.getString("status");
                        if (status != null) {
                            ArrayAdapter<String> statusAdapter = (ArrayAdapter<String>) spinnerStatus.getAdapter();
                            int statusPosition = statusAdapter.getPosition(status);
                            if (statusPosition >= 0) {
                                spinnerStatus.setSelection(statusPosition);
                            }
                        }
                        
                        // Set permissions checkboxes
                        Map<String, Object> permissions = (Map<String, Object>) document.get("permissions");
                        if (permissions != null) {
                            cbCanManageOrders.setChecked(Boolean.TRUE.equals(permissions.get("canManageOrders")));
                            cbCanManageMenu.setChecked(Boolean.TRUE.equals(permissions.get("canManageMenu")));
                            cbCanManageUsers.setChecked(Boolean.TRUE.equals(permissions.get("canManageUsers")));
                            cbCanViewAnalytics.setChecked(Boolean.TRUE.equals(permissions.get("canViewAnalytics")));
                            cbCanManageAdmins.setChecked(Boolean.TRUE.equals(permissions.get("canManageAdmins")));
                        }
                        
                    } else {
                        Toast.makeText(this, "Admin not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading admin data", e);
                    Toast.makeText(this, "Error loading admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void saveAdmin() {
        if (!validateInput()) {
            return;
        }
        
        showLoading(true);
        
        String name = etAdminName.getText().toString().trim();
        String email = etAdminEmail.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();
        
        // Create permissions map
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("canManageOrders", cbCanManageOrders.isChecked());
        permissions.put("canManageMenu", cbCanManageMenu.isChecked());
        permissions.put("canManageUsers", cbCanManageUsers.isChecked());
        permissions.put("canViewAnalytics", cbCanViewAnalytics.isChecked());
        permissions.put("canManageAdmins", cbCanManageAdmins.isChecked());
        
        if (isEditMode) {
            updateExistingAdmin(name, email, role, status, permissions);
        } else {
            createNewAdmin(name, email, password, role, status, permissions);
        }
    }
    
    private void createNewAdmin(String name, String email, String password, String role, String status, Map<String, Object> permissions) {
        // First, create the user in Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String newAdminId = authResult.getUser().getUid();
                    
                    // Create admin document in Firestore
                    Map<String, Object> adminData = new HashMap<>();
                    adminData.put("name", name);
                    adminData.put("email", email);
                    adminData.put("role", role);
                    adminData.put("status", status);
                    adminData.put("permissions", permissions);
                    adminData.put("createdAt", Timestamp.now());
                    adminData.put("updatedAt", Timestamp.now());
                    adminData.put("createdBy", auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "system");
                    
                    firestore.collection("admins").document(newAdminId)
                            .set(adminData)
                            .addOnSuccessListener(aVoid -> {
                                showLoading(false);
                                Toast.makeText(this, "Admin created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                showLoading(false);
                                Log.e(TAG, "Error creating admin document", e);
                                
                                String errorMessage = e.getMessage();
                                if (errorMessage != null && errorMessage.contains("PERMISSION_DENIED")) {
                                    // Admin user created in Authentication but failed to save to Firestore
                                    Log.w(TAG, "Admin created in Authentication but failed to save to Firestore due to permissions");
                                    Toast.makeText(this, "⚠️ Admin created in Authentication but database sync failed. Admin can still login.", Toast.LENGTH_LONG).show();
                                    finish(); // Still finish successfully as user is created
                                } else {
                                    Toast.makeText(this, "Error saving admin data: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error creating admin user", e);
                    String errorMessage = "Error creating admin user";
                    
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("email-already-in-use")) {
                            errorMessage = "Email is already registered";
                        } else if (e.getMessage().contains("weak-password")) {
                            errorMessage = "Password is too weak. Use at least 6 characters";
                        } else if (e.getMessage().contains("invalid-email")) {
                            errorMessage = "Invalid email format";
                        }
                    }
                    
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }
    
    private void updateExistingAdmin(String name, String email, String role, String status, Map<String, Object> permissions) {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("name", name);
        adminData.put("email", email);
        adminData.put("role", role);
        adminData.put("status", status);
        adminData.put("permissions", permissions);
        adminData.put("updatedAt", Timestamp.now());
        adminData.put("updatedBy", auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "system");
        
        firestore.collection("admins").document(adminId)
                .update(adminData)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(this, "Admin updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error updating admin", e);
                    Toast.makeText(this, "Error updating admin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Admin")
                .setMessage("Are you sure you want to delete this admin? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAdmin())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteAdmin() {
        if (adminId == null) return;
        
        showLoading(true);
        
        // Delete from Firestore first
        firestore.collection("admins").document(adminId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Note: In a real app, you might want to disable the user in Firebase Auth
                    // instead of deleting completely for security audit purposes
                    showLoading(false);
                    Toast.makeText(this, "Admin deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error deleting admin", e);
                    Toast.makeText(this, "Error deleting admin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    
    private boolean validateInput() {
        String name = etAdminName.getText().toString().trim();
        String email = etAdminEmail.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();
        
        if (TextUtils.isEmpty(name)) {
            etAdminName.setError("Name is required");
            etAdminName.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(email)) {
            etAdminEmail.setError("Email is required");
            etAdminEmail.requestFocus();
            return false;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etAdminEmail.setError("Invalid email format");
            etAdminEmail.requestFocus();
            return false;
        }
        
        if (!isEditMode) {
            if (TextUtils.isEmpty(password)) {
                etAdminPassword.setError("Password is required");
                etAdminPassword.requestFocus();
                return false;
            }
            
            if (password.length() < 6) {
                etAdminPassword.setError("Password must be at least 6 characters");
                etAdminPassword.requestFocus();
                return false;
            }
        }
        
        // At least one permission must be selected
        if (!cbCanManageOrders.isChecked() && !cbCanManageMenu.isChecked() && 
            !cbCanManageUsers.isChecked() && !cbCanViewAnalytics.isChecked() && 
            !cbCanManageAdmins.isChecked()) {
            Toast.makeText(this, "Please select at least one permission", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        if (isEditMode) {
            btnDelete.setEnabled(!show);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit_admin, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveAdmin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
