package com.kelasxi.waveoffoodadmin;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.kelasxi.waveoffoodadmin.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "OrderDetail";
    
    private TextView tvOrderId, tvCustomerName, tvCustomerPhone, tvDeliveryAddress;
    private TextView tvOrderDate, tvOrderStatus, tvSubtotal, tvDeliveryFee, tvTotalAmount;
    private TextView tvPaymentMethod;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private Button btnUpdateStatus;
    
    private FirebaseFirestore firestore;
    private String orderId;
    private OrderModel currentOrder;
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupData();
        loadOrderDetails();
    }
    
    private void initViews() {
        tvOrderId = findViewById(R.id.tv_order_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerPhone = findViewById(R.id.tv_customer_phone);
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        recyclerViewItems = findViewById(R.id.recycler_view_items);
        progressBar = findViewById(R.id.progress_bar);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
        
        firestore = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }
    
    private void setupData() {
        orderId = getIntent().getStringExtra("ORDER_ID");
        
        if (orderId == null) {
            Toast.makeText(this, "Error: Order ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        btnUpdateStatus.setOnClickListener(v -> updateOrderStatus());
    }
    
    private void loadOrderDetails() {
        showLoading(true);
        
        firestore.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);
                    
                    if (documentSnapshot.exists()) {
                        currentOrder = documentSnapshot.toObject(OrderModel.class);
                        if (currentOrder != null) {
                            currentOrder.setOrderId(documentSnapshot.getId());
                            displayOrderDetails(currentOrder);
                        } else {
                            showError("Failed to parse order data");
                        }
                    } else {
                        showError("Order not found");
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading order details", e);
                    showError("Error loading order: " + e.getMessage());
                });
    }
    
    private void displayOrderDetails(OrderModel order) {
        // Order ID
        String orderIdDisplay = order.getOrderId();
        if (orderIdDisplay != null && orderIdDisplay.length() > 8) {
            orderIdDisplay = "#" + orderIdDisplay.substring(orderIdDisplay.length() - 8);
        } else {
            orderIdDisplay = "#" + orderIdDisplay;
        }
        tvOrderId.setText(orderIdDisplay);
        
        // Customer Information
        tvCustomerName.setText(order.getUserName() != null ? order.getUserName() : "Unknown Customer");
        tvCustomerPhone.setText(order.getUserPhone() != null ? order.getUserPhone() : "No phone");
        
        // Delivery Address
        if (order.getDeliveryAddress() != null && order.getDeliveryAddress().getAddress() != null) {
            tvDeliveryAddress.setText(order.getDeliveryAddress().getAddress());
        } else {
            tvDeliveryAddress.setText("No address provided");
        }
        
        // Order Date
        if (order.getCreatedAt() != null) {
            Date date = order.getCreatedAt().toDate();
            tvOrderDate.setText(dateFormat.format(date));
        } else {
            tvOrderDate.setText("Unknown Date");
        }
        
        // Order Status
        String status = order.getOrderStatus() != null ? order.getOrderStatus() : "pending";
        tvOrderStatus.setText(status.toUpperCase());
        setStatusColor(status);
        
        // Financial Information
        tvSubtotal.setText(String.format("Rp %,d", order.getSubtotal()));
        tvDeliveryFee.setText(String.format("Rp %,d", order.getDeliveryFee()));
        tvTotalAmount.setText(String.format("Rp %,d", order.getTotalAmount()));
        
        // Payment Method
        tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "Cash on Delivery");
        
        // Update button text based on status
        updateStatusButtonText(status);
        
        // Setup items list (if needed)
        setupItemsList(order);
    }
    
    private void setStatusColor(String status) {
        int color;
        switch (status.toLowerCase()) {
            case "pending":
                color = getResources().getColor(R.color.warning_color, null);
                break;
            case "confirmed":
                color = getResources().getColor(R.color.accent_color, null);
                break;
            case "preparing":
                color = getResources().getColor(android.R.color.holo_purple, null);
                break;
            case "delivering":
                color = getResources().getColor(android.R.color.holo_blue_dark, null);
                break;
            case "completed":
                color = getResources().getColor(R.color.success_color, null);
                break;
            case "cancelled":
                color = getResources().getColor(R.color.error_color, null);
                break;
            default:
                color = getResources().getColor(R.color.text_secondary, null);
                break;
        }
        tvOrderStatus.setTextColor(color);
    }
    
    private void updateStatusButtonText(String currentStatus) {
        String nextStatus = getNextStatus(currentStatus);
        btnUpdateStatus.setText("Update to " + nextStatus.toUpperCase());
        
        if ("completed".equals(currentStatus.toLowerCase())) {
            btnUpdateStatus.setEnabled(false);
            btnUpdateStatus.setText("ORDER COMPLETED");
        }
    }
    
    private String getNextStatus(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "pending":
                return "confirmed";
            case "confirmed":
                return "preparing";
            case "preparing":
                return "delivering";
            case "delivering":
                return "completed";
            case "completed":
                return "completed";
            default:
                return "confirmed";
        }
    }
    
    private void setupItemsList(OrderModel order) {
        // Simple display of item count for now
        // In full implementation, you would create an adapter for order items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Log.d(TAG, "Order has " + order.getItems().size() + " items");
            // TODO: Implement OrderItemsAdapter if needed
        }
    }
    
    private void updateOrderStatus() {
        if (currentOrder == null) return;
        
        String currentStatus = currentOrder.getOrderStatus() != null ? currentOrder.getOrderStatus() : "pending";
        String newStatus = getNextStatus(currentStatus);
        
        firestore.collection("orders")
                .document(orderId)
                .update("orderStatus", newStatus, "updatedAt", com.google.firebase.Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                    currentOrder.setOrderStatus(newStatus);
                    displayOrderDetails(currentOrder);
                    Log.d(TAG, "Order status updated: " + orderId + " -> " + newStatus);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating order status", e);
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
