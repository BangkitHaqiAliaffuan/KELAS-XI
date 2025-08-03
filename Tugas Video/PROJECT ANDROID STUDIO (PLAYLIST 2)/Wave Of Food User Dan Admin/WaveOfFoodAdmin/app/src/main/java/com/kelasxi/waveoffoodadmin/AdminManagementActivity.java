package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kelasxi.waveoffoodadmin.adapter.AdminManagementAdapter;
import com.kelasxi.waveoffoodadmin.model.AdminModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminManagementActivity extends AppCompatActivity implements AdminManagementAdapter.OnAdminClickListener {
    
    private static final String TAG = "AdminManagement";
    
    private RecyclerView recyclerView;
    private AdminManagementAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddAdmin;
    private EditText etSearch;
    
    // Statistics views
    private TextView tvTotalAdmins;
    private TextView tvActiveAdmins;
    private TextView tvNewToday;
    
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private List<AdminModel> adminsList;
    private List<AdminModel> filteredAdminsList;
    private String currentSearchQuery = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Management");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupRecyclerView();
        setupSearchBar();
        loadAdmins();
    }
    
    private void initViews() {
        try {
            recyclerView = findViewById(R.id.recycler_view_admins);
            progressBar = findViewById(R.id.progress_bar);
            tvEmptyState = findViewById(R.id.tv_empty_state);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
            fabAddAdmin = findViewById(R.id.fab_add_admin);
            etSearch = findViewById(R.id.et_search);
            
            // Statistics views
            tvTotalAdmins = findViewById(R.id.tv_total_admins);
            tvActiveAdmins = findViewById(R.id.tv_active_admins);
            tvNewToday = findViewById(R.id.tv_new_today);
            
            firestore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            adminsList = new ArrayList<>();
            filteredAdminsList = new ArrayList<>();
            
            // Setup FAB click listener
            fabAddAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddEditAdminActivity.class);
                startActivity(intent);
            });
            
            // Setup swipe refresh
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadAdmins();
                swipeRefreshLayout.setRefreshing(false);
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing admin management", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupRecyclerView() {
        adapter = new AdminManagementAdapter(filteredAdminsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                filterAdmins();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void loadAdmins() {
        showLoading(true);
        Log.d(TAG, "Loading admins from 'admins' collection...");
        
        // Log current user for debugging
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "Current user: " + auth.getCurrentUser().getEmail());
            Log.d(TAG, "User UID: " + auth.getCurrentUser().getUid());
            Log.d(TAG, "Email verified: " + auth.getCurrentUser().isEmailVerified());
        } else {
            Log.e(TAG, "No authenticated user found!");
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        firestore.collection("admins")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    showLoading(false);
                    
                    if (error != null) {
                        Log.e(TAG, "Error loading admins", error);
                        String errorMessage = error.getMessage();
                        if (errorMessage != null && errorMessage.contains("PERMISSION_DENIED")) {
                            Log.e(TAG, "PERMISSION_DENIED - Check Firestore rules and user authentication");
                            Toast.makeText(this, "Permission denied. Please ensure you're logged in as admin and Firestore rules are updated.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error loading admins: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                        showEmptyState(true);
                        return;
                    }
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        adminsList.clear();
                        
                        Log.d(TAG, "Processing " + snapshots.size() + " admin documents...");
                        
                        snapshots.forEach(document -> {
                            try {
                                Log.d(TAG, "Processing admin document: " + document.getId());
                                Log.d(TAG, "Document data: " + document.getData());
                                
                                AdminModel admin = new AdminModel();
                                
                                // Map fields according to Firestore structure
                                admin.setUid(document.getId());
                                admin.setName(document.getString("name"));
                                admin.setEmail(document.getString("email"));
                                admin.setRole(document.getString("role"));
                                admin.setStatus(document.getString("status"));
                                admin.setPermissions((Map<String, Object>) document.get("permissions"));
                                
                                // Set timestamps
                                admin.setCreatedAt(document.getTimestamp("createdAt"));
                                admin.setUpdatedAt(document.getTimestamp("updatedAt"));
                                admin.setLastLoginAt(document.getTimestamp("lastLoginAt"));
                                
                                // Set default values if needed
                                if (admin.getName() == null || admin.getName().trim().isEmpty()) {
                                    admin.setName("Admin User");
                                }
                                if (admin.getRole() == null) {
                                    admin.setRole("admin");
                                }
                                if (admin.getStatus() == null) {
                                    admin.setStatus("active");
                                }
                                
                                adminsList.add(admin);
                                
                                Log.d(TAG, "Added admin: " + admin.getName() + " - Email: " + admin.getEmail());
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing admin: " + document.getId(), e);
                            }
                        });
                        
                        Log.d(TAG, "Total admins in list: " + adminsList.size());
                        
                        filterAdmins();
                        updateStats();
                        showEmptyState(false);
                        Log.d(TAG, "Loaded " + adminsList.size() + " admins");
                        Toast.makeText(this, "✅ Loaded " + adminsList.size() + " admins", Toast.LENGTH_SHORT).show();
                    } else {
                        adminsList.clear();
                        filterAdmins();
                        updateStats();
                        showEmptyState(true);
                        Log.d(TAG, "No admins found - snapshots is null or empty");
                        Toast.makeText(this, "❌ No admins found in database", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void filterAdmins() {
        filteredAdminsList.clear();
        
        for (AdminModel admin : adminsList) {
            boolean matchesSearch = matchesSearchQuery(admin);
            
            if (matchesSearch) {
                filteredAdminsList.add(admin);
            }
        }
        
        adapter.notifyDataSetChanged();
        showEmptyState(filteredAdminsList.isEmpty());
        
        Log.d(TAG, "Filtered admins: " + filteredAdminsList.size() + " out of " + adminsList.size());
    }
    
    private boolean matchesSearchQuery(AdminModel admin) {
        if (currentSearchQuery.isEmpty()) {
            return true;
        }
        
        String query = currentSearchQuery.toLowerCase();
        String name = admin.getName() != null ? admin.getName().toLowerCase() : "";
        String email = admin.getEmail() != null ? admin.getEmail().toLowerCase() : "";
        String role = admin.getRole() != null ? admin.getRole().toLowerCase() : "";
        
        return name.contains(query) || email.contains(query) || role.contains(query);
    }
    
    private void updateStats() {
        int totalAdmins = adminsList.size();
        int activeAdmins = 0;
        int newToday = 0;
        
        long todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        
        for (AdminModel admin : adminsList) {
            // Count active admins
            if ("active".equals(admin.getStatus())) {
                activeAdmins++;
            }
            
            // Count new admins today
            if (admin.getCreatedAt() != null && admin.getCreatedAt().toDate().getTime() > todayStart) {
                newToday++;
            }
        }
        
        tvTotalAdmins.setText(String.valueOf(totalAdmins));
        tvActiveAdmins.setText(String.valueOf(activeAdmins));
        tvNewToday.setText(String.valueOf(newToday));
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void showEmptyState(boolean show) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        
        if (show) {
            if (currentSearchQuery.isEmpty()) {
                tvEmptyState.setText("No admins found\n\nAdd admin users to manage the system");
            } else {
                tvEmptyState.setText("No admins match your search\n\nTry different keywords or clear the search");
            }
        }
    }
    
    @Override
    public void onAdminClick(AdminModel admin) {
        try {
            // Validate admin data before opening details
            if (admin == null) {
                Toast.makeText(this, "Error: Admin data is null", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (admin.getUid() == null || admin.getUid().trim().isEmpty()) {
                Toast.makeText(this, "Error: Admin ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "Opening admin edit for: " + admin.getName() + " (ID: " + admin.getUid() + ")");
            Toast.makeText(this, "Opening " + (admin.getName() != null ? admin.getName() : "Admin") + "'s profile...", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, AddEditAdminActivity.class);
            intent.putExtra("ADMIN_ID", admin.getUid());
            intent.putExtra("ADMIN_NAME", admin.getName() != null ? admin.getName() : "Admin User");
            intent.putExtra("ADMIN_EMAIL", admin.getEmail() != null ? admin.getEmail() : "");
            intent.putExtra("ADMIN_ROLE", admin.getRole() != null ? admin.getRole() : "admin");
            intent.putExtra("ADMIN_STATUS", admin.getStatus() != null ? admin.getStatus() : "active");
            intent.putExtra("IS_EDIT_MODE", true);
            
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening admin edit", e);
            Toast.makeText(this, "Error opening admin details: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onAdminLongClick(AdminModel admin) {
        // Show admin actions dialog (disable, delete, etc.)
        Toast.makeText(this, "Long clicked on " + admin.getName() + "\n\nAdmin actions coming soon!", Toast.LENGTH_LONG).show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from add/edit activity
        loadAdmins();
    }
}
