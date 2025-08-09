package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "AdminDashboard";
    
    private TextView tvTotalOrders, tvTotalUsers, tvTotalMenuItems, tvTotalRevenue, tvAdminEmail;
    private LinearLayout cardOrders, cardMenu, cardUsers, cardAdminManagement;
    private Button btnLogout;
    
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    
    private boolean isLoadingData = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "MainActivity onCreate started");
            setContentView(R.layout.activity_main);
            
            // Initialize Firebase
            Log.d(TAG, "Initializing Firebase components...");
            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            
            // Check Firebase initialization
            if (auth == null) {
                Log.e(TAG, "Firebase Auth is null!");
                Toast.makeText(this, "Firebase Authentication failed to initialize", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            if (firestore == null) {
                Log.e(TAG, "Firebase Firestore is null!");
                Toast.makeText(this, "Firebase Firestore failed to initialize", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            Log.d(TAG, "Firebase components initialized successfully");
            
            // Check authentication
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Log.w(TAG, "No authenticated user found, redirecting to login");
                redirectToLogin();
                return;
            } else {
                Log.d(TAG, "Current user: " + currentUser.getEmail());
                Log.d(TAG, "User UID: " + currentUser.getUid());
            }
            
            // Test Firestore connectivity
            Log.d(TAG, "Testing Firestore connectivity...");
            firestore.collection("test")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Log.d(TAG, "Firestore connectivity test successful");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Firestore connectivity test failed", e);
                        Log.e(TAG, "Connection error type: " + e.getClass().getSimpleName());
                        Log.e(TAG, "Connection error message: " + e.getMessage());
                        Toast.makeText(this, "Firebase connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            
            setupToolbar();
            initViews();
            setupClickListeners();
            loadDashboardData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Try to redirect to login as fallback
            try {
                redirectToLogin();
            } catch (Exception e2) {
                Log.e(TAG, "Failed to redirect to login", e2);
                finish(); // Close app if we can't even redirect
            }
        }
    }
    
    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("WaveOfFood Admin");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }
    
    private void initViews() {
        Log.d(TAG, "Initializing views...");
        try {
            // Header views
            tvAdminEmail = findViewById(R.id.tv_admin_email);
            btnLogout = findViewById(R.id.btn_logout);
            
            // Statistics TextViews
            tvTotalOrders = findViewById(R.id.tv_total_orders);
            tvTotalUsers = findViewById(R.id.tv_total_users);
            tvTotalMenuItems = findViewById(R.id.tv_total_menu_items);
            tvTotalRevenue = findViewById(R.id.tv_total_revenue);
            
            // CardViews for navigation
            cardOrders = findViewById(R.id.card_orders);
            cardMenu = findViewById(R.id.card_menu);
            cardUsers = findViewById(R.id.card_users);
            cardAdminManagement = findViewById(R.id.card_admin_management);
            
            // Log which views were found
            Log.d(TAG, "tvAdminEmail: " + (tvAdminEmail != null ? "Found" : "NULL"));
            Log.d(TAG, "btnLogout: " + (btnLogout != null ? "Found" : "NULL"));
            Log.d(TAG, "tvTotalOrders: " + (tvTotalOrders != null ? "Found" : "NULL"));
            Log.d(TAG, "tvTotalUsers: " + (tvTotalUsers != null ? "Found" : "NULL"));
            Log.d(TAG, "tvTotalMenuItems: " + (tvTotalMenuItems != null ? "Found" : "NULL"));
            Log.d(TAG, "tvTotalRevenue: " + (tvTotalRevenue != null ? "Found" : "NULL"));
            Log.d(TAG, "cardOrders: " + (cardOrders != null ? "Found" : "NULL"));
            Log.d(TAG, "cardMenu: " + (cardMenu != null ? "Found" : "NULL"));
            Log.d(TAG, "cardUsers: " + (cardUsers != null ? "Found" : "NULL"));
            Log.d(TAG, "cardAdminManagement: " + (cardAdminManagement != null ? "Found" : "NULL"));
            
            // Check if all views are found
            if (tvTotalOrders == null || tvTotalUsers == null || 
                tvTotalMenuItems == null || tvTotalRevenue == null ||
                cardOrders == null || cardMenu == null || 
                cardUsers == null || cardAdminManagement == null) {
                
                Log.e(TAG, "Some views not found in layout");
                Toast.makeText(this, "Layout error detected - some buttons may not work", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "All views initialized successfully");
                
                // Test click properties
                Log.d(TAG, "cardOrders clickable: " + cardOrders.isClickable());
                Log.d(TAG, "cardOrders focusable: " + cardOrders.isFocusable());
                Log.d(TAG, "cardMenu clickable: " + cardMenu.isClickable());
                Log.d(TAG, "cardMenu focusable: " + cardMenu.isFocusable());
            }
            
            // Setup admin info
            setupAdminInfo();
            
            // Setup logout button
            setupLogoutButton();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing dashboard", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners...");
        try {
            if (cardOrders != null) {
                Log.d(TAG, "Setting up Orders card click listener");
                cardOrders.setOnClickListener(v -> {
                    Log.d(TAG, "Orders card clicked!");
                    Log.d(TAG, "View ID: " + v.getId());
                    Log.d(TAG, "Expected ID: " + R.id.card_orders);
                    Toast.makeText(this, "Opening Order Management...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(this, OrderManagementActivity.class);
                        Log.d(TAG, "Starting OrderManagementActivity...");
                        startActivity(intent);
                        Log.d(TAG, "OrderManagementActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening OrderManagementActivity", e);
                        Toast.makeText(this, "Error opening Order Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG, "Orders card click listener set successfully");
            } else {
                Log.e(TAG, "cardOrders is null!");
            }
            
            if (cardMenu != null) {
                Log.d(TAG, "Setting up Menu card click listener");
                cardMenu.setOnClickListener(v -> {
                    Log.d(TAG, "Menu card clicked!");
                    Log.d(TAG, "View ID: " + v.getId());
                    Log.d(TAG, "Expected ID: " + R.id.card_menu);
                    Toast.makeText(this, "Opening Menu Management...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(this, MenuManagementActivity.class);
                        Log.d(TAG, "Starting MenuManagementActivity...");
                        startActivity(intent);
                        Log.d(TAG, "MenuManagementActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening MenuManagementActivity", e);
                        Toast.makeText(this, "Error opening Menu Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG, "Menu card click listener set successfully");
            } else {
                Log.e(TAG, "cardMenu is null!");
            }
            
            if (cardUsers != null) {
                Log.d(TAG, "Setting up Users card click listener");
                cardUsers.setOnClickListener(v -> {
                    Log.d(TAG, "Users card clicked!");
                    Toast.makeText(this, "Opening User Management...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(this, UserManagementActivity.class);
                        Log.d(TAG, "Starting UserManagementActivity...");
                        startActivity(intent);
                        Log.d(TAG, "UserManagementActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening UserManagementActivity", e);
                        Toast.makeText(this, "Error opening User Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.e(TAG, "cardUsers is null!");
            }
            
            if (cardAdminManagement != null) {
                Log.d(TAG, "Setting up Admin Management card click listener");
                cardAdminManagement.setOnClickListener(v -> {
                    Log.d(TAG, "Admin Management card clicked!");
                    Toast.makeText(this, "Opening Admin Management...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(this, AdminManagementActivity.class);
                        Log.d(TAG, "Starting AdminManagementActivity...");
                        startActivity(intent);
                        Log.d(TAG, "AdminManagementActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening AdminManagementActivity", e);
                        Toast.makeText(this, "Error opening Admin Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.e(TAG, "cardAdminManagement is null!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
            Toast.makeText(this, "Error setting up navigation", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadDashboardData() {
        // Prevent multiple simultaneous loading
        if (isLoadingData) {
            Log.d(TAG, "Dashboard data loading already in progress, skipping...");
            return;
        }
        
        isLoadingData = true;
        Log.d(TAG, "Starting to load dashboard data...");
        
        // Check authentication
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            isLoadingData = false;
            redirectToLogin();
            return;
        }
        
        Log.d(TAG, "User authenticated: " + currentUser.getEmail());
        Log.d(TAG, "Loading dashboard data for authenticated admin user...");
        
        // Load data directly for authenticated user
        try {
            loadOrdersCount();
            loadUsersCount();
            loadFoodsCount();
            calculateTotalRevenue();
            Log.d(TAG, "Dashboard data loading initiated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data", e);
            Toast.makeText(this, "Error loading dashboard data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Reset loading flag after a delay to allow async operations to complete
            new android.os.Handler().postDelayed(() -> {
                isLoadingData = false;
                Log.d(TAG, "Loading flag reset");
            }, 3000);
        }
    }
    
    private void loadOrdersCount() {
        Log.d(TAG, "Loading orders count from 'orders' collection...");
        firestore.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    Log.d(TAG, "Orders loaded successfully: " + count);
                    if (tvTotalOrders != null) {
                        tvTotalOrders.setText(String.valueOf(count));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load orders", e);
                    if (tvTotalOrders != null) {
                        tvTotalOrders.setText("0");
                    }
                    Toast.makeText(this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void loadUsersCount() {
        Log.d(TAG, "Loading users count from 'users' collection...");
        
        // Check authentication status first
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated - cannot load users");
            if (tvTotalUsers != null) {
                tvTotalUsers.setText("0");
            }
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }
        
        Log.d(TAG, "Authenticated user: " + currentUser.getEmail());
        Log.d(TAG, "User UID: " + currentUser.getUid());
        
        // Get ID token to check custom claims
        currentUser.getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    Log.d(TAG, "ID Token obtained successfully");
                    Object adminClaim = getTokenResult.getClaims().get("admin");
                    Log.d(TAG, "Admin claim: " + adminClaim);
                    
                    // Now try to load users
                    firestore.collection("users")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                int count = queryDocumentSnapshots.size();
                                Log.d(TAG, "Users loaded successfully: " + count);
                                if (tvTotalUsers != null) {
                                    tvTotalUsers.setText(String.valueOf(count));
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to load users", e);
                                if (tvTotalUsers != null) {
                                    tvTotalUsers.setText("0");
                                }
                                // Show detailed error message
                                String errorMsg = e.getMessage();
                                if (errorMsg != null && errorMsg.contains("PERMISSION_DENIED")) {
                                    Toast.makeText(this, "Permission denied. Please ensure you're logged in as admin.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Failed to load users: " + errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get ID token", e);
                    if (tvTotalUsers != null) {
                        tvTotalUsers.setText("0");
                    }
                    Toast.makeText(this, "Authentication error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void loadFoodsCount() {
        Log.d(TAG, "Loading foods count from 'foods' collection...");
        firestore.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    Log.d(TAG, "Foods loaded successfully: " + count);
                    if (tvTotalMenuItems != null) {
                        tvTotalMenuItems.setText(String.valueOf(count));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load foods", e);
                    if (tvTotalMenuItems != null) {
                        tvTotalMenuItems.setText("0");
                    }
                    Toast.makeText(this, "Failed to load foods: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void calculateTotalRevenue() {
        Log.d(TAG, "Calculating total revenue from orders...");
        firestore.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long totalRevenue = 0;
                    int orderCount = queryDocumentSnapshots.size();
                    Log.d(TAG, "Processing " + orderCount + " orders for revenue calculation");
                    
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        try {
                            // Based on user app structure, orders have totalAmount field
                            Long totalAmount = document.getLong("totalAmount");
                            if (totalAmount != null && totalAmount > 0) {
                                totalRevenue += totalAmount;
                            } else {
                                // Fallback to other possible field names
                                Long total = document.getLong("total");
                                if (total != null && total > 0) {
                                    totalRevenue += total;
                                }
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Error processing order revenue: " + document.getId(), e);
                        }
                    }
                    
                    Log.d(TAG, "Total revenue calculated: " + totalRevenue);
                    if (tvTotalRevenue != null) {
                        tvTotalRevenue.setText(String.format("Rp %,d", totalRevenue));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to calculate revenue", e);
                    if (tvTotalRevenue != null) {
                        tvTotalRevenue.setText("Rp 0");
                    }
                    Toast.makeText(this, "Failed to calculate revenue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating options menu", e);
            return false;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_admin_management) {
            try {
                Log.d(TAG, "Admin Management menu clicked");
                Intent intent = new Intent(this, AdminManagementActivity.class);
                startActivity(intent);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error opening Admin Management", e);
                Toast.makeText(this, "Error opening Admin Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return true;
            }
        } else if (id == R.id.action_refresh) {
            loadDashboardData();
            Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void redirectToLogin() {
        try {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to login", e);
            Toast.makeText(this, "Error: Please restart the app", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called");
        
        // Only refresh data if not already loading and user is authenticated
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !isLoadingData) {
            Log.d(TAG, "Refreshing dashboard data on resume...");
            loadDashboardData();
        } else if (currentUser == null) {
            Log.d(TAG, "User not authenticated on resume, redirecting to login");
            redirectToLogin();
        } else {
            Log.d(TAG, "Data loading already in progress, skipping refresh");
        }
    }
    
    private void setupAdminInfo() {
        try {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null && tvAdminEmail != null) {
                String email = currentUser.getEmail();
                if (email != null && !email.isEmpty()) {
                    tvAdminEmail.setText("Welcome, " + email);
                    Log.d(TAG, "Admin info set: " + email);
                } else {
                    tvAdminEmail.setText("Welcome, Admin User");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up admin info", e);
        }
    }
    
    private void setupLogoutButton() {
        try {
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> showLogoutConfirmation());
                Log.d(TAG, "Logout button click listener set");
            } else {
                Log.e(TAG, "Logout button is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up logout button", e);
        }
    }
    
    private void showLogoutConfirmation() {
        try {
            new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout from WaveOfFood Admin?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Logout", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing logout confirmation", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void performLogout() {
        try {
            Log.d(TAG, "Performing logout...");
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            
            // Sign out from Firebase
            auth.signOut();
            
            // Clear any local data/preferences if needed
            clearLocalData();
            
            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Close current activity
            finish();
            
            Log.d(TAG, "Logout completed successfully");
            Toast.makeText(this, "✅ Logged out successfully", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void clearLocalData() {
        try {
            // Clear any cached data
            isLoadingData = false;
            
            // You can add more data clearing logic here if needed
            // For example: SharedPreferences, local database, etc.
            
            Log.d(TAG, "Local data cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing local data", e);
        }
    }
}