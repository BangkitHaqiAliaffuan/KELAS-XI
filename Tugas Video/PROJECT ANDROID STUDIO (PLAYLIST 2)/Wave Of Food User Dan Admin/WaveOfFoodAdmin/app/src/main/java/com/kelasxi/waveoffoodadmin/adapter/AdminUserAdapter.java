package com.kelasxi.waveoffoodadmin.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kelasxi.waveoffoodadmin.R;
import com.kelasxi.waveoffoodadmin.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {
    
    private List<UserModel> users;
    private OnUserClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnUserClickListener {
        void onUserClick(UserModel user);
        void onUserLongClick(UserModel user);
    }
    
    public AdminUserAdapter(List<UserModel> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.bind(user);
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    class UserViewHolder extends RecyclerView.ViewHolder {
        private CardView cardUser;
        private ImageView ivUserProfile;
        private TextView tvUserName, tvUserEmail, tvUserPhone, tvJoinDate, 
                tvTotalOrders, tvLoyaltyPoints, tvUserStatus;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardUser = itemView.findViewById(R.id.card_user);
            ivUserProfile = itemView.findViewById(R.id.iv_user_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPhone = itemView.findViewById(R.id.tv_user_phone);
            tvJoinDate = itemView.findViewById(R.id.tv_join_date);
            tvTotalOrders = itemView.findViewById(R.id.tv_total_orders);
            tvLoyaltyPoints = itemView.findViewById(R.id.tv_loyalty_points);
            tvUserStatus = itemView.findViewById(R.id.tv_user_status);
        }
        
        public void bind(UserModel user) {
            try {
                // Validate user object
                if (user == null) {
                    tvUserName.setText("Error: User data is null");
                    tvUserEmail.setText("");
                    return;
                }
                
                // User name with null safety
                tvUserName.setText(user.getName() != null && !user.getName().trim().isEmpty() ? user.getName() : "Unknown User");
                
                // User email with null safety
                tvUserEmail.setText(user.getEmail() != null && !user.getEmail().trim().isEmpty() ? user.getEmail() : "No email");
                
                // User phone - hide if not available
                if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                    tvUserPhone.setText(user.getPhone());
                    tvUserPhone.setVisibility(View.VISIBLE);
                } else {
                    tvUserPhone.setVisibility(View.GONE);
                }
                
                // Join date - show "Recently joined" if createdAt is null
                if (user.getCreatedAt() != null) {
                    try {
                        Date date = user.getCreatedAt().toDate();
                        tvJoinDate.setText("Joined: " + dateFormat.format(date));
                    } catch (Exception e) {
                        tvJoinDate.setText("Recently joined");
                    }
                } else {
                    tvJoinDate.setText("Recently joined");
                }
                
                // Total orders with safety
                int totalOrders = user.getTotalOrders();
                tvTotalOrders.setText(totalOrders + " orders");
                
                // Loyalty points with safety
                int loyaltyPoints = user.getLoyaltyPoints();
                tvLoyaltyPoints.setText(loyaltyPoints + " pts");
                
                // User status based on total orders
                setUserStatus(totalOrders);
                
                // Load profile image with error handling
                try {
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().trim().isEmpty()) {
                        Glide.with(itemView.getContext())
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.ic_user_placeholder)
                                .error(R.drawable.ic_user_placeholder)
                                .circleCrop()
                                .into(ivUserProfile);
                    } else {
                        ivUserProfile.setImageResource(R.drawable.ic_user_placeholder);
                    }
                } catch (Exception e) {
                    ivUserProfile.setImageResource(R.drawable.ic_user_placeholder);
                }
                
                // Click listeners with null safety
                cardUser.setOnClickListener(v -> {
                    if (listener != null && user != null) {
                        listener.onUserClick(user);
                    }
                });
                
                cardUser.setOnLongClickListener(v -> {
                    if (listener != null && user != null) {
                        listener.onUserLongClick(user);
                    }
                    return true;
                });
                
            } catch (Exception e) {
                // Log error and show fallback UI
                android.util.Log.e("AdminUserAdapter", "Error binding user data", e);
                tvUserName.setText("Error loading user");
                tvUserEmail.setText("Please try again");
                ivUserProfile.setImageResource(R.drawable.ic_user_placeholder);
            }
        }
        
        private void setUserStatus(int totalOrders) {
            if (totalOrders >= 20) {
                tvUserStatus.setText("VIP");
                tvUserStatus.setTextColor(Color.parseColor("#FF9800"));
                tvUserStatus.setBackgroundResource(R.drawable.bg_status_vip);
            } else if (totalOrders >= 10) {
                tvUserStatus.setText("LOYAL");
                tvUserStatus.setTextColor(Color.parseColor("#4CAF50"));
                tvUserStatus.setBackgroundResource(R.drawable.bg_status_loyal);
            } else if (totalOrders >= 5) {
                tvUserStatus.setText("REGULAR");
                tvUserStatus.setTextColor(Color.parseColor("#2196F3"));
                tvUserStatus.setBackgroundResource(R.drawable.bg_status_regular);
            } else {
                tvUserStatus.setText("NEW");
                tvUserStatus.setTextColor(Color.parseColor("#9C27B0"));
                tvUserStatus.setBackgroundResource(R.drawable.bg_status_new);
            }
        }
    }
}
