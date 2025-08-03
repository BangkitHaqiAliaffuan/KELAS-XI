package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;
import com.kelasxi.waveoffoodadmin.adapter.AdminUserAdapter;
import com.kelasxi.waveoffoodadmin.model.UserModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity implements AdminUserAdapter.OnUserClickListener {
    
    private static final String TAG = "UserManagement";
    
    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearchUsers;
    private ImageView ivClearSearch;
    
    // Filter buttons
    private Button btnFilterAll, btnFilterNew, btnFilterRegular, btnFilterLoyal, btnFilterVip;
    private Button currentSelectedFilter;
    
    // Stats TextViews
    private TextView tvTotalUsers, tvActiveUsers, tvNewToday;
    
    private FirebaseFirestore firestore;
    private List<UserModel> usersList;
    private List<UserModel> filteredUsersList;
    private String currentFilter = "all";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "UserManagementActivity onCreate started");
            setContentView(R.layout.activity_user_management);
            
            // Setup toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("User Management");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            initViews();
            setupRecyclerView();
            setupFilterButtons();
            setupSearchBar();
            loadUsers();
            
            Log.d(TAG, "UserManagementActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in UserManagementActivity onCreate", e);
            Toast.makeText(this, "Error initializing User Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initViews() {
        Log.d(TAG, "Initializing views...");
        try {
            recyclerView = findViewById(R.id.recycler_view_users);
            progressBar = findViewById(R.id.progress_bar);
            tvEmptyState = findViewById(R.id.tv_empty_state);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
            etSearchUsers = findViewById(R.id.et_search_users);
            ivClearSearch = findViewById(R.id.iv_clear_search);
            
            // Filter buttons
            btnFilterAll = findViewById(R.id.btn_filter_all);
            btnFilterNew = findViewById(R.id.btn_filter_new);
            btnFilterRegular = findViewById(R.id.btn_filter_regular);
            btnFilterLoyal = findViewById(R.id.btn_filter_loyal);
            btnFilterVip = findViewById(R.id.btn_filter_vip);
            
            // Stats TextViews
            tvTotalUsers = findViewById(R.id.tv_total_users);
            tvActiveUsers = findViewById(R.id.tv_active_users);
            tvNewToday = findViewById(R.id.tv_new_today);
            
            currentSelectedFilter = btnFilterAll;
            
            firestore = FirebaseFirestore.getInstance();
            usersList = new ArrayList<>();
            filteredUsersList = new ArrayList<>();
            
            swipeRefreshLayout.setOnRefreshListener(this::loadUsers);
            
            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }
    
    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView...");
        try {
            adapter = new AdminUserAdapter(filteredUsersList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
            throw e;
        }
    }
    
    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> setFilter("all", btnFilterAll));
        btnFilterNew.setOnClickListener(v -> setFilter("new", btnFilterNew));
        btnFilterRegular.setOnClickListener(v -> setFilter("regular", btnFilterRegular));
        btnFilterLoyal.setOnClickListener(v -> setFilter("loyal", btnFilterLoyal));
        btnFilterVip.setOnClickListener(v -> setFilter("vip", btnFilterVip));
    }
    
    private void setupSearchBar() {
        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                ivClearSearch.setVisibility(currentSearchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                filterUsers();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        ivClearSearch.setOnClickListener(v -> {
            etSearchUsers.setText("");
            ivClearSearch.setVisibility(View.GONE);
        });
    }
    
    private void setFilter(String filter, Button selectedButton) {
        if (currentSelectedFilter != null) {
            currentSelectedFilter.setBackgroundResource(R.drawable.bg_filter_unselected);
            currentSelectedFilter.setTextColor(getResources().getColor(android.R.color.white));
        }
        
        selectedButton.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedButton.setTextColor(getResources().getColor(R.color.primary_color));
        currentSelectedFilter = selectedButton;
        
        currentFilter = filter;
        filterUsers();
        
        String filterText = getFilterDisplayText(filter);
        Toast.makeText(this, "Showing " + filterText, Toast.LENGTH_SHORT).show();
    }
    
    private String getFilterDisplayText(String filter) {
        switch (filter) {
            case "new": return "new users (0-4 orders)";
            case "regular": return "regular users (5-9 orders)";
            case "loyal": return "loyal users (10-19 orders)";
            case "vip": return "VIP users (20+ orders)";
            default: return "all users";
        }
    }
    
    private void loadUsers() {
        Log.d(TAG, "Loading users...");
        showLoading(true);
        
        if (!swipeRefreshLayout.isRefreshing()) {
            Toast.makeText(this, "Loading users...", Toast.LENGTH_SHORT).show();
        }
        
        // Load users without ordering by createdAt first, since it might not exist
        firestore.collection("users")
                .addSnapshotListener((snapshots, error) -> {
                    showLoading(false);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    if (error != null) {
                        Log.e(TAG, "Error loading users", error);
                        Toast.makeText(this, "Error loading users: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        return;
                    }
                    
                    Log.d(TAG, "Firestore snapshots: " + (snapshots != null ? snapshots.size() : "null"));
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        usersList.clear();
                        
                        Log.d(TAG, "Processing " + snapshots.size() + " documents...");
                        
                        snapshots.forEach(document -> {
                            try {
                                Log.d(TAG, "Processing document: " + document.getId());
                                Log.d(TAG, "Document data: " + document.getData());
                                
                                UserModel user = new UserModel();
                                
                                // Map fields according to actual Firestore structure
                                user.setUid(document.getId());
                                user.setName(document.getString("name"));
                                user.setEmail(document.getString("email"));
                                user.setAddress(document.getString("address"));
                                
                                // Map profileImage to profileImageUrl
                                String profileImage = document.getString("profileImage");
                                user.setProfileImageUrl(profileImage);
                                
                                // Set default values for fields that might not exist
                                user.setPhone(""); // Phone might not be in the structure
                                user.setTotalOrders(0); // Will be calculated
                                user.setLoyaltyPoints(0); // Will be calculated
                                user.setCreatedAt(document.getTimestamp("createdAt")); // Might be null
                                user.setUpdatedAt(document.getTimestamp("updatedAt")); // Might be null
                                
                                // Calculate total orders from orders collection
                                calculateUserOrderStats(user);
                                
                                usersList.add(user);
                                
                                Log.d(TAG, "Added user: " + user.getName() + " - Email: " + user.getEmail());
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing user: " + document.getId(), e);
                                // Still add the user with basic info
                                UserModel user = new UserModel();
                                user.setUid(document.getId());
                                user.setName("Unknown User");
                                user.setEmail(document.getString("email"));
                                usersList.add(user);
                            }
                        });
                        
                        Log.d(TAG, "Total users in list: " + usersList.size());
                        
                        filterUsers();
                        updateStats();
                        showEmptyState(false);
                        Log.d(TAG, "Loaded " + usersList.size() + " users");
                        Toast.makeText(this, "✅ Loaded " + usersList.size() + " users", Toast.LENGTH_SHORT).show();
                    } else {
                        usersList.clear();
                        filterUsers();
                        updateStats();
                        showEmptyState(true);
                        Log.d(TAG, "No users found - snapshots is null or empty");
                        Toast.makeText(this, "❌ No users found in database", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void calculateUserOrderStats(UserModel user) {
        // This is a simplified version - in production you'd batch these queries
        firestore.collection("orders")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalOrders = queryDocumentSnapshots.size();
                    user.setTotalOrders(totalOrders);
                    
                    // Calculate loyalty points (10 points per order)
                    user.setLoyaltyPoints(totalOrders * 10);
                    
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error calculating user stats for: " + user.getUid(), e);
                });
    }
    
    private void filterUsers() {
        filteredUsersList.clear();
        
        for (UserModel user : usersList) {
            boolean matchesFilter = matchesCurrentFilter(user);
            boolean matchesSearch = matchesSearchQuery(user);
            
            if (matchesFilter && matchesSearch) {
                filteredUsersList.add(user);
            }
        }
        
        adapter.notifyDataSetChanged();
        showEmptyState(filteredUsersList.isEmpty());
        
        Log.d(TAG, "Filtered users: " + filteredUsersList.size() + " out of " + usersList.size());
    }
    
    private boolean matchesCurrentFilter(UserModel user) {
        switch (currentFilter) {
            case "new":
                return user.getTotalOrders() >= 0 && user.getTotalOrders() <= 4;
            case "regular":
                return user.getTotalOrders() >= 5 && user.getTotalOrders() <= 9;
            case "loyal":
                return user.getTotalOrders() >= 10 && user.getTotalOrders() <= 19;
            case "vip":
                return user.getTotalOrders() >= 20;
            case "all":
            default:
                return true;
        }
    }
    
    private boolean matchesSearchQuery(UserModel user) {
        if (currentSearchQuery.isEmpty()) {
            return true;
        }
        
        String name = user.getName() != null ? user.getName().toLowerCase() : "";
        String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
        String phone = user.getPhone() != null ? user.getPhone().toLowerCase() : "";
        
        return name.contains(currentSearchQuery) || 
               email.contains(currentSearchQuery) || 
               phone.contains(currentSearchQuery);
    }
    
    private void updateStats() {
        int totalUsers = usersList.size();
        int activeUsers = 0;
        int newToday = 0;
        
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayStart = today.getTime();
        
        for (UserModel user : usersList) {
            // Count active users (users with at least 1 order)
            if (user.getTotalOrders() > 0) {
                activeUsers++;
            }
            
            // Count new users today (only if createdAt exists)
            if (user.getCreatedAt() != null) {
                Date userCreatedDate = user.getCreatedAt().toDate();
                if (userCreatedDate.after(todayStart)) {
                    newToday++;
                }
            }
        }
        
        // Update UI
        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvActiveUsers.setText(String.valueOf(activeUsers));
        tvNewToday.setText(String.valueOf(newToday));
        
        Log.d(TAG, "Stats updated - Total: " + totalUsers + ", Active: " + activeUsers + ", New Today: " + newToday);
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void showEmptyState(boolean show) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        
        if (show) {
            if (currentSearchQuery.isEmpty()) {
                tvEmptyState.setText("No users found\n\nUsers will appear here once they register on the app");
            } else {
                tvEmptyState.setText("No users match your search\n\nTry different keywords or clear the search");
            }
        }
    }
    
    @Override
    public void onUserClick(UserModel user) {
        try {
            // Validate user data before opening details
            if (user == null) {
                Toast.makeText(this, "Error: User data is null", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (user.getUid() == null || user.getUid().trim().isEmpty()) {
                Toast.makeText(this, "Error: User ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "Opening user details for: " + user.getName() + " (ID: " + user.getUid() + ")");
            Toast.makeText(this, "Opening " + (user.getName() != null ? user.getName() : "User") + "'s profile...", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra("USER_ID", user.getUid());
            intent.putExtra("USER_NAME", user.getName() != null ? user.getName() : "Unknown User");
            intent.putExtra("USER_EMAIL", user.getEmail() != null ? user.getEmail() : "No email");
            intent.putExtra("USER_PHONE", user.getPhone() != null ? user.getPhone() : "");
            intent.putExtra("USER_TOTAL_ORDERS", user.getTotalOrders());
            intent.putExtra("USER_LOYALTY_POINTS", user.getLoyaltyPoints());
            
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening user details", e);
            Toast.makeText(this, "Error opening user details: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onUserLongClick(UserModel user) {
        // Show user actions dialog (ban, delete, send notification, etc.)
        Toast.makeText(this, "Long clicked on " + user.getName() + "\n\nUser actions coming soon!", Toast.LENGTH_LONG).show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from user detail
        loadUsers();
    }
}
