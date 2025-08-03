package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kelasxi.waveoffoodadmin.adapter.AdminOrderAdapter;
import com.kelasxi.waveoffoodadmin.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity implements AdminOrderAdapter.OnOrderClickListener {
    
    private static final String TAG = "OrderManagement";
    
    private RecyclerView recyclerView;
    private AdminOrderAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    // Filter buttons
    private Button btnFilterAll, btnFilterPending, btnFilterConfirmed, 
                   btnFilterPreparing, btnFilterDelivering, btnFilterCompleted;
    private Button currentSelectedFilter;
    
    private FirebaseFirestore firestore;
    private List<OrderModel> ordersList;
    private List<OrderModel> filteredOrdersList;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order Management");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupRecyclerView();
        setupFilterButtons();
        loadOrders();
    }    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_orders);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        
        // Filter buttons
        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterPending = findViewById(R.id.btn_filter_pending);
        btnFilterConfirmed = findViewById(R.id.btn_filter_confirmed);
        btnFilterPreparing = findViewById(R.id.btn_filter_preparing);
        btnFilterDelivering = findViewById(R.id.btn_filter_delivering);
        btnFilterCompleted = findViewById(R.id.btn_filter_completed);
        
        currentSelectedFilter = btnFilterAll;
        
        firestore = FirebaseFirestore.getInstance();
        ordersList = new ArrayList<>();
        filteredOrdersList = new ArrayList<>();
        
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
    }
    
    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(filteredOrdersList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> setFilter("all", btnFilterAll));
        btnFilterPending.setOnClickListener(v -> setFilter("pending", btnFilterPending));
        btnFilterConfirmed.setOnClickListener(v -> setFilter("confirmed", btnFilterConfirmed));
        btnFilterPreparing.setOnClickListener(v -> setFilter("preparing", btnFilterPreparing));
        btnFilterDelivering.setOnClickListener(v -> setFilter("delivering", btnFilterDelivering));
        btnFilterCompleted.setOnClickListener(v -> setFilter("completed", btnFilterCompleted));
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
        filterOrders();
        
        Toast.makeText(this, "Showing " + filter + " orders", Toast.LENGTH_SHORT).show();
    }
    
    private void filterOrders() {
        filteredOrdersList.clear();
        
        for (OrderModel order : ordersList) {
            if (currentFilter.equals("all") || 
                (order.getOrderStatus() != null && order.getOrderStatus().toLowerCase().equals(currentFilter))) {
                filteredOrdersList.add(order);
            }
        }
        
        adapter.notifyDataSetChanged();
        showEmptyState(filteredOrdersList.isEmpty());
        
        Log.d(TAG, "Filtered orders: " + filteredOrdersList.size() + " out of " + ordersList.size());
    }
    
    private void loadOrders() {
        showLoading(true);
        swipeRefreshLayout.setRefreshing(false);
        
        firestore.collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    showLoading(false);
                    
                    if (error != null) {
                        Log.e(TAG, "Error loading orders", error);
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        return;
                    }
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        ordersList.clear();
                        
                        snapshots.forEach(document -> {
                            try {
                                OrderModel order = document.toObject(OrderModel.class);
                                if (order != null) {
                                    order.setOrderId(document.getId()); // Ensure ID is set
                                    ordersList.add(order);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing order: " + document.getId(), e);
                            }
                        });
                        
                        filterOrders(); // Apply current filter
                        showEmptyState(false);
                        Log.d(TAG, "Loaded " + ordersList.size() + " orders");
                    } else {
                        ordersList.clear();
                        filterOrders();
                        showEmptyState(true);
                        Log.d(TAG, "No orders found");
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public void onOrderClick(OrderModel order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", order.getOrderId());
        startActivity(intent);
    }
    
    @Override
    public void onStatusChange(OrderModel order, String newStatus) {
        updateOrderStatus(order, newStatus);
    }
    
    private void updateOrderStatus(OrderModel order, String newStatus) {
        if (order.getOrderId() == null) {
            Toast.makeText(this, "Error: Order ID is null", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        firestore.collection("orders")
                .document(order.getOrderId())
                .update("orderStatus", newStatus, "updatedAt", com.google.firebase.Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(this, "✅ Order #" + order.getOrderId().substring(order.getOrderId().length() - 8) + 
                                 " updated to: " + newStatus.toUpperCase(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Order status updated: " + order.getOrderId() + " -> " + newStatus);
                    
                    // Play notification sound
                    try {
                        android.media.MediaPlayer mediaPlayer = android.media.MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(android.media.MediaPlayer::release);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error playing notification sound", e);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error updating order status", e);
                    Toast.makeText(this, "❌ Failed to update status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
