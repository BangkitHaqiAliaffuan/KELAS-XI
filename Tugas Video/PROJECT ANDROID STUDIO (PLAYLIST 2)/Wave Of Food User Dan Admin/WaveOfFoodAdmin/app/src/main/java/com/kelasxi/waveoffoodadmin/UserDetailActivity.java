package com.kelasxi.waveoffoodadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kelasxi.waveoffoodadmin.adapter.AdminOrderAdapter;
import com.kelasxi.waveoffoodadmin.model.OrderModel;
import com.kelasxi.waveoffoodadmin.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserDetailActivity extends AppCompatActivity implements AdminOrderAdapter.OnOrderClickListener {
    
    private static final String TAG = "UserDetail";
    
    private ImageView ivUserProfile;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvJoinDate, 
            tvTotalOrders, tvLoyaltyPoints, tvUserStatus, tvTotalSpent;
    private RecyclerView recyclerViewOrders;
    private ProgressBar progressBar;
    private TextView tvEmptyOrders;
    
    private FirebaseFirestore firestore;
    private AdminOrderAdapter orderAdapter;
    private List<OrderModel> userOrdersList;
    private SimpleDateFormat dateFormat;
    
    private String userId;
    private UserModel currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_user_detail);
            
            // Setup toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("User Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            // Validate intent data
            if (!validateIntentData()) {
                Toast.makeText(this, "Invalid user data. Cannot open user details.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            initViews();
            setupData();
            loadUserOrders();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error opening user details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private boolean validateIntentData() {
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.trim().isEmpty()) {
            Log.e(TAG, "USER_ID is null or empty");
            return false;
        }
        return true;
    }
    
    private void initViews() {
        try {
            ivUserProfile = findViewById(R.id.iv_user_profile);
            tvUserName = findViewById(R.id.tv_user_name);
            tvUserEmail = findViewById(R.id.tv_user_email);
            tvUserPhone = findViewById(R.id.tv_user_phone);
            tvJoinDate = findViewById(R.id.tv_join_date);
            tvTotalOrders = findViewById(R.id.tv_total_orders);
            tvLoyaltyPoints = findViewById(R.id.tv_loyalty_points);
            tvUserStatus = findViewById(R.id.tv_user_status);
            tvTotalSpent = findViewById(R.id.tv_total_spent);
            recyclerViewOrders = findViewById(R.id.recycler_view_orders);
            progressBar = findViewById(R.id.progress_bar);
            tvEmptyOrders = findViewById(R.id.tv_empty_orders);
            
            // Validate critical views
            if (tvUserName == null || tvUserEmail == null || recyclerViewOrders == null) {
                throw new IllegalStateException("Critical views not found in layout");
            }
            
            firestore = FirebaseFirestore.getInstance();
            userOrdersList = new ArrayList<>();
            dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            
            // Setup RecyclerView
            orderAdapter = new AdminOrderAdapter(userOrdersList, this);
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewOrders.setAdapter(orderAdapter);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in initViews", e);
            throw new RuntimeException("Failed to initialize views: " + e.getMessage(), e);
        }
    }
    
    private void setupData() {
        try {
            // Get user data from intent
            userId = getIntent().getStringExtra("USER_ID");
            String userName = getIntent().getStringExtra("USER_NAME");
            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            String userPhone = getIntent().getStringExtra("USER_PHONE");
            int totalOrders = getIntent().getIntExtra("USER_TOTAL_ORDERS", 0);
            int loyaltyPoints = getIntent().getIntExtra("USER_LOYALTY_POINTS", 0);
            
            // Validate required data
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("USER_ID cannot be null or empty");
            }
            
            // Create user model with null safety
            currentUser = new UserModel();
            currentUser.setUid(userId);
            currentUser.setName(userName != null ? userName : "Unknown User");
            currentUser.setEmail(userEmail != null ? userEmail : "No email");
            currentUser.setPhone(userPhone != null ? userPhone : "");
            currentUser.setTotalOrders(totalOrders);
            currentUser.setLoyaltyPoints(loyaltyPoints);
            
            // Display user info with null safety
            tvUserName.setText(currentUser.getName());
            tvUserEmail.setText(currentUser.getEmail());
            
            if (userPhone != null && !userPhone.trim().isEmpty()) {
                tvUserPhone.setText(userPhone);
                tvUserPhone.setVisibility(View.VISIBLE);
            } else {
                tvUserPhone.setVisibility(View.GONE);
            }
            
            tvTotalOrders.setText(String.valueOf(totalOrders));
            tvLoyaltyPoints.setText(String.valueOf(loyaltyPoints));
            
            // Set user status
            setUserStatus(totalOrders);
            
            // Load complete user data from Firebase
            loadCompleteUserData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in setupData", e);
            Toast.makeText(this, "Error setting up user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void loadCompleteUserData() {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            // Map fields according to actual Firestore structure
                            currentUser.setName(documentSnapshot.getString("name"));
                            currentUser.setEmail(documentSnapshot.getString("email"));
                            currentUser.setAddress(documentSnapshot.getString("address"));
                            
                            // Map profileImage to profileImageUrl
                            String profileImage = documentSnapshot.getString("profileImage");
                            currentUser.setProfileImageUrl(profileImage);
                            
                            // Update UI with complete data
                            tvUserName.setText(currentUser.getName() != null ? currentUser.getName() : "Unknown User");
                            tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No email");
                            
                            // Update join date if available
                            if (documentSnapshot.getTimestamp("createdAt") != null) {
                                Date date = documentSnapshot.getTimestamp("createdAt").toDate();
                                tvJoinDate.setText("Joined: " + dateFormat.format(date));
                            } else {
                                tvJoinDate.setText("Recently joined");
                            }
                            
                            // Load profile image
                            if (profileImage != null && !profileImage.isEmpty()) {
                                Glide.with(this)
                                        .load(profileImage)
                                        .placeholder(R.drawable.ic_user_placeholder)
                                        .error(R.drawable.ic_user_placeholder)
                                        .circleCrop()
                                        .into(ivUserProfile);
                            } else {
                                ivUserProfile.setImageResource(R.drawable.ic_user_placeholder);
                            }
                            
                            Log.d(TAG, "User data loaded successfully for: " + currentUser.getName());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing user data", e);
                        }
                    } else {
                        Log.w(TAG, "User document does not exist: " + userId);
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user data", e);
                    Toast.makeText(this, "Error loading user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void loadUserOrders() {
        progressBar.setVisibility(View.VISIBLE);
        
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (error != null) {
                        Log.e(TAG, "Error loading user orders", error);
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                        showEmptyOrders(true);
                        return;
                    }
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        userOrdersList.clear();
                        final long[] totalSpent = {0}; // Use array to make it effectively final
                        
                        snapshots.forEach(document -> {
                            try {
                                OrderModel order = document.toObject(OrderModel.class);
                                if (order != null) {
                                    order.setOrderId(document.getId());
                                    userOrdersList.add(order);
                                    totalSpent[0] += order.getTotalAmount();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing order: " + document.getId(), e);
                            }
                        });
                        
                        orderAdapter.notifyDataSetChanged();
                        showEmptyOrders(false);
                        
                        // Update total spent
                        tvTotalSpent.setText(String.format("Rp %,d", totalSpent[0]));
                        
                        Log.d(TAG, "Loaded " + userOrdersList.size() + " orders for user: " + userId);
                    } else {
                        userOrdersList.clear();
                        orderAdapter.notifyDataSetChanged();
                        showEmptyOrders(true);
                        tvTotalSpent.setText("Rp 0");
                        Log.d(TAG, "No orders found for user: " + userId);
                    }
                });
    }
    
    private void setUserStatus(int totalOrders) {
        if (totalOrders >= 20) {
            tvUserStatus.setText("VIP CUSTOMER");
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvUserStatus.setBackgroundResource(R.drawable.bg_status_vip);
        } else if (totalOrders >= 10) {
            tvUserStatus.setText("LOYAL CUSTOMER");
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvUserStatus.setBackgroundResource(R.drawable.bg_status_loyal);
        } else if (totalOrders >= 5) {
            tvUserStatus.setText("REGULAR CUSTOMER");
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvUserStatus.setBackgroundResource(R.drawable.bg_status_regular);
        } else {
            tvUserStatus.setText("NEW CUSTOMER");
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_purple));
            tvUserStatus.setBackgroundResource(R.drawable.bg_status_new);
        }
    }
    
    private void showEmptyOrders(boolean show) {
        tvEmptyOrders.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewOrders.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public void onOrderClick(OrderModel order) {
        Toast.makeText(this, "Opening order details...", Toast.LENGTH_SHORT).show();
        // Navigate to OrderDetailActivity
        // Intent intent = new Intent(this, OrderDetailActivity.class);
        // intent.putExtra("ORDER_ID", order.getOrderId());
        // startActivity(intent);
    }
    
    @Override
    public void onStatusChange(OrderModel order, String newStatus) {
        // Handle status change from user detail view
        firestore.collection("orders")
                .document(order.getOrderId())
                .update("orderStatus", newStatus, "updatedAt", com.google.firebase.Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating order status", e);
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
