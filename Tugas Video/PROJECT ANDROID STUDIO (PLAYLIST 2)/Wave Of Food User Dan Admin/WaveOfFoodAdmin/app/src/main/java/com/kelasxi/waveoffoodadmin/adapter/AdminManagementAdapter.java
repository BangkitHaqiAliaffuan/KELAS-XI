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

import com.google.android.material.chip.Chip;
import com.kelasxi.waveoffoodadmin.R;
import com.kelasxi.waveoffoodadmin.model.AdminModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminManagementAdapter extends RecyclerView.Adapter<AdminManagementAdapter.AdminViewHolder> {
    
    private List<AdminModel> admins;
    private OnAdminClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnAdminClickListener {
        void onAdminClick(AdminModel admin);
        void onAdminLongClick(AdminModel admin);
        void onEditAdmin(AdminModel admin);
        void onDeleteAdmin(AdminModel admin);
    }
    
    public AdminManagementAdapter(List<AdminModel> admins, OnAdminClickListener listener) {
        this.admins = admins;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_management, parent, false);
        return new AdminViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminModel admin = admins.get(position);
        holder.bind(admin);
    }
    
    @Override
    public int getItemCount() {
        return admins.size();
    }
    
    class AdminViewHolder extends RecyclerView.ViewHolder {
        private CardView cardAdmin;
        private ImageView ivAdminProfile;
        private TextView tvAdminName, tvAdminEmail, tvAdminStatus,
                tvCreatedDate, tvLastLogin, tvPermissions;
        private Chip chipAdminRole;
        private Button btnEditAdmin, btnDeleteAdmin;
        
        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardAdmin = itemView.findViewById(R.id.card_admin);
            ivAdminProfile = itemView.findViewById(R.id.iv_admin_profile);
            tvAdminName = itemView.findViewById(R.id.tv_admin_name);
            tvAdminEmail = itemView.findViewById(R.id.tv_admin_email);
            chipAdminRole = itemView.findViewById(R.id.chip_admin_role);
            tvAdminStatus = itemView.findViewById(R.id.tv_admin_status);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            tvLastLogin = itemView.findViewById(R.id.tv_last_login);
            tvPermissions = itemView.findViewById(R.id.tv_permissions);
            btnEditAdmin = itemView.findViewById(R.id.btnEditAdmin);
            btnDeleteAdmin = itemView.findViewById(R.id.btnDeleteAdmin);
        }
        
        public void bind(AdminModel admin) {
            try {
                // Validate admin object
                if (admin == null) {
                    tvAdminName.setText("Error: Admin data is null");
                    tvAdminEmail.setText("");
                    return;
                }
                
                // Admin name with null safety
                tvAdminName.setText(admin.getName() != null && !admin.getName().trim().isEmpty() ? 
                    admin.getName() : "Unknown Admin");
                
                // Admin email with null safety
                tvAdminEmail.setText(admin.getEmail() != null && !admin.getEmail().trim().isEmpty() ? 
                    admin.getEmail() : "No email");
                
                // Admin role with styling
                String role = admin.getRole() != null ? admin.getRoleDisplayName() : "Admin";
                chipAdminRole.setText(role);
                setRoleStyle(admin.getRole());
                
                // Admin status with styling
                String status = admin.getStatus() != null ? admin.getStatusDisplayName() : "Unknown";
                tvAdminStatus.setText(status);
                setStatusStyle(admin.getStatus());
                
                // Created date
                if (admin.getCreatedAt() != null) {
                    try {
                        Date date = admin.getCreatedAt().toDate();
                        tvCreatedDate.setText("Created: " + dateFormat.format(date));
                    } catch (Exception e) {
                        tvCreatedDate.setText("Recently created");
                    }
                } else {
                    tvCreatedDate.setText("Recently created");
                }
                
                // Last login
                if (admin.getLastLoginAt() != null) {
                    try {
                        Date date = admin.getLastLoginAt().toDate();
                        tvLastLogin.setText("Last login: " + dateFormat.format(date));
                    } catch (Exception e) {
                        tvLastLogin.setText("Never logged in");
                    }
                } else {
                    tvLastLogin.setText("Never logged in");
                }
                
                // Permissions summary
                tvPermissions.setText(getPermissionsSummary(admin));
                
                // Profile image (placeholder for now)
                ivAdminProfile.setImageResource(R.drawable.ic_admin_placeholder);
                
                // Click listeners with null safety
                cardAdmin.setOnClickListener(v -> {
                    if (listener != null && admin != null) {
                        listener.onAdminClick(admin);
                    }
                });
                
                cardAdmin.setOnLongClickListener(v -> {
                    if (listener != null && admin != null) {
                        listener.onAdminLongClick(admin);
                    }
                    return true;
                });
                
                // Button click listeners
                btnEditAdmin.setOnClickListener(v -> {
                    if (listener != null && admin != null) {
                        listener.onEditAdmin(admin);
                    }
                });
                
                btnDeleteAdmin.setOnClickListener(v -> {
                    if (listener != null && admin != null) {
                        listener.onDeleteAdmin(admin);
                    }
                });
                
            } catch (Exception e) {
                // Log error and show fallback UI
                android.util.Log.e("AdminManagementAdapter", "Error binding admin data", e);
                tvAdminName.setText("Error loading admin");
                tvAdminEmail.setText("Please try again");
                ivAdminProfile.setImageResource(R.drawable.ic_admin_placeholder);
            }
        }
        
        private void setRoleStyle(String role) {
            if (role == null) {
                chipAdminRole.setTextColor(Color.parseColor("#FFFFFF"));
                chipAdminRole.setChipBackgroundColorResource(R.color.gray_600);
                return;
            }
            
            switch (role) {
                case "super_admin":
                    chipAdminRole.setTextColor(Color.parseColor("#FFFFFF"));
                    chipAdminRole.setChipBackgroundColorResource(R.color.error);
                    break;
                case "admin":
                    chipAdminRole.setTextColor(Color.parseColor("#FFFFFF"));
                    chipAdminRole.setChipBackgroundColorResource(R.color.primary_green);
                    break;
                case "moderator":
                    chipAdminRole.setTextColor(Color.parseColor("#FFFFFF"));
                    chipAdminRole.setChipBackgroundColorResource(R.color.warning);
                    break;
                default:
                    chipAdminRole.setTextColor(Color.parseColor("#FFFFFF"));
                    chipAdminRole.setChipBackgroundColorResource(R.color.gray_600);
                    break;
            }
        }
        
        private void setStatusStyle(String status) {
            if (status == null) {
                tvAdminStatus.setTextColor(Color.parseColor("#666666"));
                tvAdminStatus.setBackgroundResource(R.drawable.bg_status_pending);
                return;
            }
            
            switch (status) {
                case "active":
                    tvAdminStatus.setTextColor(Color.parseColor("#4CAF50"));
                    tvAdminStatus.setBackgroundResource(R.drawable.bg_status_available);
                    break;
                case "inactive":
                    tvAdminStatus.setTextColor(Color.parseColor("#FF9800"));
                    tvAdminStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "suspended":
                    tvAdminStatus.setTextColor(Color.parseColor("#F44336"));
                    tvAdminStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                    break;
                default:
                    tvAdminStatus.setTextColor(Color.parseColor("#666666"));
                    tvAdminStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
            }
        }
        
        private String getPermissionsSummary(AdminModel admin) {
            if (admin.getPermissions() == null) {
                return "No permissions";
            }
            
            int permissionCount = 0;
            StringBuilder summary = new StringBuilder();
            
            if (admin.hasPermission("canManageOrders")) {
                summary.append("Orders");
                permissionCount++;
            }
            
            if (admin.hasPermission("canManageMenu")) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Menu");
                permissionCount++;
            }
            
            if (admin.hasPermission("canManageUsers")) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Users");
                permissionCount++;
            }
            
            if (admin.hasPermission("canViewAnalytics")) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Analytics");
                permissionCount++;
            }
            
            if (admin.hasPermission("canManageAdmins")) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Admins");
                permissionCount++;
            }
            
            if (permissionCount == 0) {
                return "No permissions";
            } else if (permissionCount <= 2) {
                return summary.toString();
            } else {
                return permissionCount + " permissions";
            }
        }
    }
}
