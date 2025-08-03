package com.kelasxi.waveoffoodadmin.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kelasxi.waveoffoodadmin.R;
import com.kelasxi.waveoffoodadmin.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    
    private List<OrderModel> orders;
    private OnOrderClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnOrderClickListener {
        void onOrderClick(OrderModel order);
        void onStatusChange(OrderModel order, String newStatus);
    }
    
    public AdminOrderAdapter(List<OrderModel> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);
        holder.bind(order);
    }
    
    @Override
    public int getItemCount() {
        return orders.size();
    }
    
    class OrderViewHolder extends RecyclerView.ViewHolder {
        private CardView cardOrder;
        private TextView tvOrderId, tvCustomerName, tvOrderDate, tvOrderStatus, 
                tvTotalAmount, tvItemCount;
        private ImageView ivFirstItem;
        private Button btnUpdateStatus;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardOrder = itemView.findViewById(R.id.card_order);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            ivFirstItem = itemView.findViewById(R.id.iv_first_item);
            btnUpdateStatus = itemView.findViewById(R.id.btn_update_status);
        }
        
        public void bind(OrderModel order) {
            // Display order ID (last 8 characters)
            String orderId = order.getOrderId();
            if (orderId != null && orderId.length() > 8) {
                tvOrderId.setText("#" + orderId.substring(orderId.length() - 8));
            } else {
                tvOrderId.setText("#" + orderId);
            }
            
            // Customer name
            tvCustomerName.setText(order.getUserName() != null ? order.getUserName() : "Unknown Customer");
            
            // Order date
            if (order.getCreatedAt() != null) {
                Date date = order.getCreatedAt().toDate();
                tvOrderDate.setText(dateFormat.format(date));
            } else {
                tvOrderDate.setText("Unknown Date");
            }
            
            // Order status with color coding
            String status = order.getOrderStatus() != null ? order.getOrderStatus() : "pending";
            tvOrderStatus.setText(status.toUpperCase());
            setStatusColor(tvOrderStatus, status);
            
            // Total amount
            tvTotalAmount.setText(order.getFormattedTotal());
            
            // Item count
            tvItemCount.setText(order.getItemCount() + " items");
            
            // Load first item image if available
            if (order.getItems() != null && !order.getItems().isEmpty() && 
                order.getItems().get(0).getImageUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(order.getItems().get(0).getImageUrl())
                        .placeholder(R.drawable.ic_food_placeholder)
                        .error(R.drawable.ic_food_placeholder)
                        .into(ivFirstItem);
            } else {
                ivFirstItem.setImageResource(R.drawable.ic_food_placeholder);
            }
            
            // Update status button
            btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) {
                    String newStatus = getNextStatus(status);
                    listener.onStatusChange(order, newStatus);
                }
            });
            
            // Set button text based on current status
            btnUpdateStatus.setText(getButtonText(status));
            
            // Hide button if order is completed or cancelled
            if ("completed".equals(status.toLowerCase()) || "cancelled".equals(status.toLowerCase())) {
                btnUpdateStatus.setVisibility(View.GONE);
            } else {
                btnUpdateStatus.setVisibility(View.VISIBLE);
            }
            
            // Card click for details
            cardOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }
        
        private void setStatusColor(TextView textView, String status) {
            switch (status.toLowerCase()) {
                case "pending":
                    textView.setTextColor(Color.parseColor("#FF9800")); // Orange
                    textView.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "confirmed":
                    textView.setTextColor(Color.parseColor("#2196F3")); // Blue
                    textView.setBackgroundResource(R.drawable.bg_status_confirmed);
                    break;
                case "preparing":
                    textView.setTextColor(Color.parseColor("#9C27B0")); // Purple
                    textView.setBackgroundResource(R.drawable.bg_status_preparing);
                    break;
                case "delivering":
                    textView.setTextColor(Color.parseColor("#607D8B")); // Blue Grey
                    textView.setBackgroundResource(R.drawable.bg_status_delivering);
                    break;
                case "completed":
                    textView.setTextColor(Color.parseColor("#4CAF50")); // Green
                    textView.setBackgroundResource(R.drawable.bg_status_completed);
                    break;
                case "cancelled":
                    textView.setTextColor(Color.parseColor("#F44336")); // Red
                    textView.setBackgroundResource(R.drawable.bg_status_cancelled);
                    break;
                default:
                    textView.setTextColor(Color.parseColor("#757575")); // Grey
                    textView.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
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
                    return "completed"; // Already completed
                default:
                    return "confirmed";
            }
        }
        
        private String getButtonText(String currentStatus) {
            switch (currentStatus.toLowerCase()) {
                case "pending":
                    return "CONFIRM ORDER";
                case "confirmed":
                    return "START PREPARING";
                case "preparing":
                    return "OUT FOR DELIVERY";
                case "delivering":
                    return "MARK COMPLETED";
                case "completed":
                    return "COMPLETED";
                case "cancelled":
                    return "CANCELLED";
                default:
                    return "UPDATE STATUS";
            }
        }
    }
}
