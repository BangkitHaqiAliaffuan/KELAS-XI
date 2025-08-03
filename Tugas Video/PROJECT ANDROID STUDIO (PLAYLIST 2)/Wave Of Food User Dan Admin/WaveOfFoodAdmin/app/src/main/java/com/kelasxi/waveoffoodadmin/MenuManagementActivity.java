package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kelasxi.waveoffoodadmin.adapter.AdminFoodAdapter;
import com.kelasxi.waveoffoodadmin.model.FoodModel;

import java.util.ArrayList;
import java.util.List;

public class MenuManagementActivity extends AppCompatActivity implements AdminFoodAdapter.OnFoodActionListener {
    
    private static final String TAG = "MenuManagement";
    
    private RecyclerView recyclerView;
    private AdminFoodAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddFood;
    
    // Statistics views
    private TextView tvTotalItems;
    private TextView tvAvailableItems;
    private TextView tvPopularItems;
    private LinearLayout layoutEmptyState;
    
    private FirebaseFirestore firestore;
    private List<FoodModel> foodsList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "MenuManagementActivity onCreate started");
            setContentView(R.layout.activity_menu_management);
            
            // Setup toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Menu Management");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            initViews();
            setupRecyclerView();
            loadFoodItems();
            
            Log.d(TAG, "MenuManagementActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in MenuManagementActivity onCreate", e);
            Toast.makeText(this, "Error initializing Menu Management: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Finish activity if initialization fails
            finish();
        }
    }
    
    private void initViews() {
        Log.d(TAG, "Initializing views...");
        try {
            recyclerView = findViewById(R.id.rv_food_items);
            progressBar = findViewById(R.id.progress_bar);
            tvEmptyState = findViewById(R.id.tv_empty_state);
            layoutEmptyState = findViewById(R.id.layout_empty_state);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
            fabAddFood = findViewById(R.id.fab_add_food);
            
            // Statistics views
            tvTotalItems = findViewById(R.id.tv_total_items);
            tvAvailableItems = findViewById(R.id.tv_available_items);
            tvPopularItems = findViewById(R.id.tv_popular_items);
            
            // Check if all views are found
            if (recyclerView == null) {
                throw new RuntimeException("RecyclerView not found in layout");
            }
            if (progressBar == null) {
                throw new RuntimeException("ProgressBar not found in layout");
            }
            if (tvEmptyState == null) {
                throw new RuntimeException("Empty state TextView not found in layout");
            }
            if (layoutEmptyState == null) {
                throw new RuntimeException("Empty state layout not found in layout");
            }
            if (swipeRefreshLayout == null) {
                throw new RuntimeException("SwipeRefreshLayout not found in layout");
            }
            if (fabAddFood == null) {
                throw new RuntimeException("FloatingActionButton not found in layout");
            }
            
            firestore = FirebaseFirestore.getInstance();
            if (firestore == null) {
                throw new RuntimeException("Failed to initialize Firebase Firestore");
            }
            
            foodsList = new ArrayList<>();
            
            // Setup swipe refresh with green color scheme
            swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_green,
                R.color.primary_green_light,
                R.color.accent_orange
            );
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Toast.makeText(this, "Refreshing menu...", Toast.LENGTH_SHORT).show();
                loadFoodItems();
            });
            
            // Setup FAB click with animation
            fabAddFood.setOnClickListener(v -> {
                // Add scale animation
                v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100);
                    });
                
                Toast.makeText(this, "Opening Add Food Form...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AddEditFoodActivity.class);
                startActivity(intent);
            });
            
            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e; // Re-throw to be caught by onCreate
        }
    }
    
    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView...");
        try {
            adapter = new AdminFoodAdapter(foodsList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
            throw e; // Re-throw to be caught by onCreate
        }
    }
    
    private void loadFoodItems() {
        Log.d(TAG, "Loading food items...");
        showLoading(true);
        
        // Show loading toast only if not refreshing via swipe
        if (!swipeRefreshLayout.isRefreshing()) {
            Toast.makeText(this, "Loading menu items...", Toast.LENGTH_SHORT).show();
        }
        
        // Try 'foods' collection first (enhanced structure)
        firestore.collection("foods")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Successfully loaded from 'foods' collection");
                    foodsList.clear();
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "'foods' collection is empty, trying 'menu' collection...");
                        loadFromMenuCollection();
                        return;
                    }
                    
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            FoodModel food = new FoodModel();
                            food.setId(document.getId());
                            food.setName(document.getString("name") != null ? document.getString("name") : document.getString("foodName"));
                            
                            // Handle price field (Long vs String)
                            Object priceObj = document.get("price");
                            if (priceObj instanceof Long) {
                                food.setPrice((Long) priceObj);
                            } else if (priceObj instanceof String) {
                                try {
                                    food.setPrice(Long.parseLong((String) priceObj));
                                } catch (NumberFormatException e) {
                                    food.setPrice(0);
                                }
                            } else {
                                food.setPrice(0);
                            }
                            
                            food.setDescription(document.getString("description") != null ? document.getString("description") : document.getString("foodDescription"));
                            food.setImageUrl(document.getString("imageUrl") != null ? document.getString("imageUrl") : document.getString("foodImage"));
                            food.setCategoryId(document.getString("categoryId") != null ? document.getString("categoryId") : document.getString("foodCategory"));
                            food.setPopular(document.getBoolean("isPopular") != null ? document.getBoolean("isPopular") : false);
                            food.setRating(document.getDouble("rating") != null ? document.getDouble("rating") : 0.0);
                            food.setAvailable(document.getBoolean("isAvailable") != null ? document.getBoolean("isAvailable") : true);
                            
                            foodsList.add(food);
                            Log.d(TAG, "Added food: " + food.getName() + " - Price: " + food.getPrice());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing food document: " + document.getId(), e);
                        }
                    }
                    
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading from 'foods' collection", e);
                    loadFromMenuCollection();
                });
    }
    
    private void loadFromMenuCollection() {
        Log.d(TAG, "Loading from 'menu' collection as fallback...");
        
        firestore.collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Successfully loaded from 'menu' collection");
                    foodsList.clear();
                    
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            FoodModel food = new FoodModel();
                            food.setId(document.getId());
                            food.setName(document.getString("foodName"));
                            
                            // Convert string price to long
                            String priceStr = document.getString("foodPrice");
                            if (priceStr != null) {
                                try {
                                    food.setPrice(Long.parseLong(priceStr));
                                } catch (NumberFormatException e) {
                                    food.setPrice(0);
                                }
                            }
                            
                            food.setDescription(document.getString("foodDescription"));
                            food.setImageUrl(document.getString("foodImage"));
                            food.setCategoryId(document.getString("foodCategory"));
                            food.setPopular(document.getBoolean("isPopular") != null ? document.getBoolean("isPopular") : false);
                            food.setRating(document.getDouble("rating") != null ? document.getDouble("rating") : 0.0);
                            food.setAvailable(true); // Default to available for menu collection
                            
                            foodsList.add(food);
                            Log.d(TAG, "Added food from menu: " + food.getName());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing menu document: " + document.getId(), e);
                        }
                    }
                    
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading from 'menu' collection", e);
                    showLoading(false);
                    Toast.makeText(this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void updateUI() {
        showLoading(false);
        swipeRefreshLayout.setRefreshing(false);
        
        // Update statistics
        updateStatistics();
        
        if (foodsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("No menu items found\n\nTap the + button to add your first food item");
            Toast.makeText(this, "No menu items found", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Loaded " + foodsList.size() + " menu items", Toast.LENGTH_SHORT).show();
        }
        
        Log.d(TAG, "UI updated. Total food items: " + foodsList.size());
    }
    
    private void updateStatistics() {
        int totalItems = foodsList.size();
        int availableItems = 0;
        int popularItems = 0;
        
        for (FoodModel food : foodsList) {
            if (food.isAvailable()) {
                availableItems++;
            }
            if (food.isPopular()) {
                popularItems++;
            }
        }
        
        // Update statistics with animation
        animateTextChange(tvTotalItems, String.valueOf(totalItems));
        animateTextChange(tvAvailableItems, String.valueOf(availableItems));
        animateTextChange(tvPopularItems, String.valueOf(popularItems));
        
        Log.d(TAG, "Statistics updated - Total: " + totalItems + ", Available: " + availableItems + ", Popular: " + popularItems);
    }
    
    private void animateTextChange(TextView textView, String newText) {
        textView.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction(() -> {
                textView.setText(newText);
                textView.animate()
                    .alpha(1f)
                    .setDuration(150);
            });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onEditFood(FoodModel food) {
        Toast.makeText(this, "Editing " + food.getName() + "...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AddEditFoodActivity.class);
        intent.putExtra("FOOD_ID", food.getId());
        intent.putExtra("FOOD_NAME", food.getName());
        intent.putExtra("FOOD_PRICE", food.getPrice());
        intent.putExtra("FOOD_DESCRIPTION", food.getDescription());
        intent.putExtra("FOOD_IMAGE_URL", food.getImageUrl());
        intent.putExtra("FOOD_CATEGORY", food.getCategoryId());
        intent.putExtra("FOOD_IS_POPULAR", food.isPopular());
        intent.putExtra("FOOD_RATING", food.getRating());
        intent.putExtra("FOOD_IS_AVAILABLE", food.isAvailable());
        startActivity(intent);
    }
    
    @Override
    public void onToggleAvailability(FoodModel food) {
        // Toggle availability and update in Firebase
        boolean newAvailability = !food.isAvailable();
        String actionMessage = newAvailability ? "Making " + food.getName() + " available..." : "Making " + food.getName() + " unavailable...";
        Toast.makeText(this, actionMessage, Toast.LENGTH_SHORT).show();
        
        // Update in 'foods' collection first
        firestore.collection("foods")
                .document(food.getId())
                .update("isAvailable", newAvailability)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Updated availability in 'foods' collection");
                    food.setAvailable(newAvailability);
                    adapter.notifyDataSetChanged();
                    
                    String status = newAvailability ? "available" : "unavailable";
                    Toast.makeText(this, food.getName() + " is now " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update availability", e);
                    Toast.makeText(this, "Failed to update availability", Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public void onDeleteFood(FoodModel food) {
        // Show confirmation dialog would be better, but for now direct delete
        Toast.makeText(this, "Deleting " + food.getName() + "...", Toast.LENGTH_SHORT).show();
        
        firestore.collection("foods")
                .document(food.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Food item deleted from 'foods' collection");
                    foodsList.remove(food);
                    adapter.notifyDataSetChanged();
                    updateUI();
                    Toast.makeText(this, food.getName() + " deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Try to delete from 'menu' collection as fallback
                    firestore.collection("menu")
                            .document(food.getId())
                            .delete()
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "Food item deleted from 'menu' collection");
                                foodsList.remove(food);
                                adapter.notifyDataSetChanged();
                                updateUI();
                                Toast.makeText(this, food.getName() + " deleted successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e2 -> {
                                Log.e(TAG, "Failed to delete food item", e2);
                                Toast.makeText(this, "Failed to delete food item", Toast.LENGTH_SHORT).show();
                            });
                });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refreshing menu data...", Toast.LENGTH_SHORT).show();
            loadFoodItems();
            return true;
        } else if (id == R.id.action_add_food) {
            Toast.makeText(this, "Opening Add Food Form...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddEditFoodActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from add/edit
        loadFoodItems();
    }
}
