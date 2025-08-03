package com.kelasxi.waveoffoodadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kelasxi.waveoffoodadmin.R;
import com.kelasxi.waveoffoodadmin.model.FoodModel;

import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.FoodViewHolder> {
    
    private List<FoodModel> foodList;
    private OnFoodActionListener listener;
    
    public interface OnFoodActionListener {
        void onEditFood(FoodModel food);
        void onToggleAvailability(FoodModel food);
        void onDeleteFood(FoodModel food);
    }
    
    public AdminFoodAdapter(List<FoodModel> foodList, OnFoodActionListener listener) {
        if (foodList == null) {
            throw new IllegalArgumentException("Food list cannot be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("OnFoodActionListener cannot be null");
        }
        this.foodList = foodList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_food, parent, false);
        return new FoodViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel food = foodList.get(position);
        holder.bind(food);
    }
    
    @Override
    public int getItemCount() {
        return foodList.size();
    }
    
    class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFoodImage;
        private TextView tvFoodName;
        private TextView tvFoodPrice;
        private TextView tvFoodDescription;
        private TextView tvRating;
        private TextView tvPopularBadge;
        private TextView tvAvailabilityStatus;
        private SwitchMaterial switchAvailable;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;
        private LinearLayout layoutAvailability;
        
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvFoodDescription = itemView.findViewById(R.id.tv_food_description);
            tvPopularBadge = itemView.findViewById(R.id.tv_popular_badge);
            tvAvailabilityStatus = itemView.findViewById(R.id.tv_availability_status);
            switchAvailable = itemView.findViewById(R.id.switch_available);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            layoutAvailability = itemView.findViewById(R.id.layout_availability);
        }
        
        public void bind(FoodModel food) {
            // Set food details
            tvFoodName.setText(food.getName() != null ? food.getName() : "Unknown Food");
            tvFoodPrice.setText(food.getFormattedPrice());
            tvFoodDescription.setText(food.getDescription() != null ? food.getDescription() : "No description available");
            
            // Set popular badge
            if (food.isPopular()) {
                tvPopularBadge.setVisibility(View.VISIBLE);
            } else {
                tvPopularBadge.setVisibility(View.GONE);
            }
            
            // Set availability
            switchAvailable.setChecked(food.isAvailable());
            updateAvailabilityStatus(food.isAvailable());
            
            // Load image with Glide and enhanced styling
            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .transform(new RoundedCorners(24)) // Larger radius for modern look
                    .into(ivFoodImage);
            
            // Set click listeners with animations
            switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    // Add haptic feedback
                    buttonView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                    updateAvailabilityStatus(isChecked);
                    listener.onToggleAvailability(food);
                }
            });
            
            btnEdit.setOnClickListener(v -> {
                // Add button animation
                animateButton(v);
                if (listener != null) {
                    listener.onEditFood(food);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                // Add button animation
                animateButton(v);
                if (listener != null) {
                    listener.onDeleteFood(food);
                }
            });
            
            // Apply visual state based on availability
            applyAvailabilityVisualState(food.isAvailable());
        }
        
        private void updateAvailabilityStatus(boolean isAvailable) {
            if (isAvailable) {
                tvAvailabilityStatus.setText("AVAILABLE");
                tvAvailabilityStatus.setBackgroundResource(R.drawable.bg_status_available);
                tvAvailabilityStatus.setTextColor(itemView.getContext().getColor(R.color.success));
            } else {
                tvAvailabilityStatus.setText("UNAVAILABLE");
                tvAvailabilityStatus.setBackgroundResource(R.drawable.bg_status_unavailable);
                tvAvailabilityStatus.setTextColor(itemView.getContext().getColor(R.color.error));
            }
        }
        
        private void applyAvailabilityVisualState(boolean isAvailable) {
            // Apply alpha to the entire card based on availability
            float alpha = isAvailable ? 1.0f : 0.7f;
            itemView.setAlpha(alpha);
            
            // Apply scaling effect
            float scale = isAvailable ? 1.0f : 0.98f;
            itemView.setScaleX(scale);
            itemView.setScaleY(scale);
        }
        
        private void animateButton(View button) {
            button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100);
                });
        }
    }
}
