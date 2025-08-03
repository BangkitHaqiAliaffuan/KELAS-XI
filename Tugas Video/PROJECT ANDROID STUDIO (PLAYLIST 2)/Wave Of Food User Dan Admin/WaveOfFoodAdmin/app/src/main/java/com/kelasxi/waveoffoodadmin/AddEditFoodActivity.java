package com.kelasxi.waveoffoodadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kelasxi.waveoffoodadmin.model.FoodModel;

import java.util.HashMap;
import java.util.Map;

public class AddEditFoodActivity extends AppCompatActivity {
    
    private static final String TAG = "AddEditFood";
    
    private EditText etFoodName;
    private EditText etFoodPrice;
    private EditText etFoodDescription;
    private EditText etImageUrl;
    private Spinner spinnerCategory;
    private CheckBox cbIsPopular;
    private CheckBox cbIsAvailable;
    private EditText etRating;
    private EditText etPreparationTime;
    private ImageView ivPreview;
    private Button btnSave;
    private Button btnPreviewImage;
    private ProgressBar progressBar;
    
    private FirebaseFirestore firestore;
    private String foodId = null; // null for add, non-null for edit
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_food);
        
        firestore = FirebaseFirestore.getInstance();
        
        initViews();
        setupSpinner();
        checkIfEditMode();
        setupClickListeners();
    }
    
    private void initViews() {
        etFoodName = findViewById(R.id.et_food_name);
        etFoodPrice = findViewById(R.id.et_food_price);
        etFoodDescription = findViewById(R.id.et_food_description);
        etImageUrl = findViewById(R.id.et_image_url);
        spinnerCategory = findViewById(R.id.spinner_category);
        cbIsPopular = findViewById(R.id.cb_is_popular);
        cbIsAvailable = findViewById(R.id.cb_is_available);
        etRating = findViewById(R.id.et_rating);
        etPreparationTime = findViewById(R.id.et_preparation_time);
        ivPreview = findViewById(R.id.iv_preview);
        btnSave = findViewById(R.id.btn_save);
        btnPreviewImage = findViewById(R.id.btn_preview_image);
        progressBar = findViewById(R.id.progress_bar);
        
        // Set default values
        cbIsAvailable.setChecked(true);
        etRating.setText("4.0");
        etPreparationTime.setText("15");
    }
    
    private void setupSpinner() {
        String[] categories = {
            "Indonesian Food",
            "Chinese Food", 
            "Western Food",
            "Japanese Food",
            "Fast Food",
            "Beverages",
            "Desserts",
            "Snacks"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }
    
    private void checkIfEditMode() {
        Intent intent = getIntent();
        foodId = intent.getStringExtra("FOOD_ID");
        
        if (foodId != null) {
            isEditMode = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Food Item");
            }
            
            // Populate fields with existing data
            etFoodName.setText(intent.getStringExtra("FOOD_NAME"));
            etFoodPrice.setText(String.valueOf(intent.getLongExtra("FOOD_PRICE", 0)));
            etFoodDescription.setText(intent.getStringExtra("FOOD_DESCRIPTION"));
            etImageUrl.setText(intent.getStringExtra("FOOD_IMAGE_URL"));
            cbIsPopular.setChecked(intent.getBooleanExtra("FOOD_IS_POPULAR", false));
            cbIsAvailable.setChecked(intent.getBooleanExtra("FOOD_IS_AVAILABLE", true));
            etRating.setText(String.valueOf(intent.getDoubleExtra("FOOD_RATING", 4.0)));
            
            // Set category spinner
            String category = intent.getStringExtra("FOOD_CATEGORY");
            if (category != null) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
                int position = adapter.getPosition(category);
                if (position >= 0) {
                    spinnerCategory.setSelection(position);
                }
            }
            
            // Load image preview
            String imageUrl = intent.getStringExtra("FOOD_IMAGE_URL");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                loadImagePreview(imageUrl);
            }
            
            btnSave.setText("UPDATE FOOD");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Food Item");
            }
            btnSave.setText("ADD FOOD");
        }
    }
    
    private void setupClickListeners() {
        btnPreviewImage.setOnClickListener(v -> {
            String imageUrl = etImageUrl.getText().toString().trim();
            if (!imageUrl.isEmpty()) {
                loadImagePreview(imageUrl);
            } else {
                Toast.makeText(this, "Please enter an image URL first", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnSave.setOnClickListener(v -> saveFoodItem());
    }
    
    private void loadImagePreview(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(ivPreview);
        
        ivPreview.setVisibility(View.VISIBLE);
    }
    
    private void saveFoodItem() {
        if (!validateInput()) {
            return;
        }
        
        showLoading(true);
        
        // Create food model
        FoodModel food = createFoodModel();
        
        if (isEditMode) {
            updateFoodItem(food);
        } else {
            addNewFoodItem(food);
        }
    }
    
    private boolean validateInput() {
        if (etFoodName.getText().toString().trim().isEmpty()) {
            etFoodName.setError("Food name is required");
            etFoodName.requestFocus();
            return false;
        }
        
        if (etFoodPrice.getText().toString().trim().isEmpty()) {
            etFoodPrice.setError("Price is required");
            etFoodPrice.requestFocus();
            return false;
        }
        
        try {
            Long.parseLong(etFoodPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            etFoodPrice.setError("Invalid price format");
            etFoodPrice.requestFocus();
            return false;
        }
        
        if (etFoodDescription.getText().toString().trim().isEmpty()) {
            etFoodDescription.setError("Description is required");
            etFoodDescription.requestFocus();
            return false;
        }
        
        try {
            double rating = Double.parseDouble(etRating.getText().toString().trim());
            if (rating < 0 || rating > 5) {
                etRating.setError("Rating must be between 0 and 5");
                etRating.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etRating.setError("Invalid rating format");
            etRating.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private FoodModel createFoodModel() {
        FoodModel food = new FoodModel();
        
        if (isEditMode) {
            food.setId(foodId);
        }
        
        food.setName(etFoodName.getText().toString().trim());
        food.setPrice(Long.parseLong(etFoodPrice.getText().toString().trim()));
        food.setDescription(etFoodDescription.getText().toString().trim());
        food.setImageUrl(etImageUrl.getText().toString().trim());
        food.setCategoryId(spinnerCategory.getSelectedItem().toString());
        food.setPopular(cbIsPopular.isChecked());
        food.setAvailable(cbIsAvailable.isChecked());
        food.setRating(Double.parseDouble(etRating.getText().toString().trim()));
        
        try {
            food.setPreparationTime(Integer.parseInt(etPreparationTime.getText().toString().trim()));
        } catch (NumberFormatException e) {
            food.setPreparationTime(15); // Default
        }
        
        if (isEditMode) {
            food.setUpdatedAt(Timestamp.now());
        } else {
            food.setCreatedAt(Timestamp.now());
            food.setUpdatedAt(Timestamp.now());
        }
        
        return food;
    }
    
    private void addNewFoodItem(FoodModel food) {
        // Add to 'foods' collection (enhanced structure)
        Map<String, Object> foodData = createFoodMap(food);
        
        firestore.collection("foods")
                .add(foodData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Food added to 'foods' collection: " + documentReference.getId());
                    
                    // Also add to 'menu' collection for backward compatibility
                    addToMenuCollection(food, documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding food to 'foods' collection", e);
                    showLoading(false);
                    Toast.makeText(this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void addToMenuCollection(FoodModel food, String documentId) {
        // Create backward compatible data for menu collection
        Map<String, Object> menuData = new HashMap<>();
        menuData.put("foodName", food.getName());
        menuData.put("foodPrice", String.valueOf(food.getPrice()));
        menuData.put("foodDescription", food.getDescription());
        menuData.put("foodImage", food.getImageUrl());
        menuData.put("foodCategory", food.getCategoryId());
        menuData.put("isPopular", food.isPopular());
        menuData.put("rating", food.getRating());
        
        firestore.collection("menu")
                .document(documentId)
                .set(menuData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Food also added to 'menu' collection");
                    showLoading(false);
                    Toast.makeText(this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to add to 'menu' collection (non-critical)", e);
                    showLoading(false);
                    Toast.makeText(this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    
    private void updateFoodItem(FoodModel food) {
        Map<String, Object> foodData = createFoodMap(food);
        
        firestore.collection("foods")
                .document(foodId)
                .update(foodData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Food updated successfully in 'foods' collection");
                    
                    // Also update in 'menu' collection
                    updateMenuCollection(food);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating food in 'foods' collection", e);
                    showLoading(false);
                    Toast.makeText(this, "Failed to update food item", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void updateMenuCollection(FoodModel food) {
        Map<String, Object> menuData = new HashMap<>();
        menuData.put("foodName", food.getName());
        menuData.put("foodPrice", String.valueOf(food.getPrice()));
        menuData.put("foodDescription", food.getDescription());
        menuData.put("foodImage", food.getImageUrl());
        menuData.put("foodCategory", food.getCategoryId());
        menuData.put("isPopular", food.isPopular());
        menuData.put("rating", food.getRating());
        
        firestore.collection("menu")
                .document(foodId)
                .update(menuData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Food also updated in 'menu' collection");
                    showLoading(false);
                    Toast.makeText(this, "Food item updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to update in 'menu' collection (non-critical)", e);
                    showLoading(false);
                    Toast.makeText(this, "Food item updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    
    private Map<String, Object> createFoodMap(FoodModel food) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", food.getName());
        data.put("price", food.getPrice());
        data.put("description", food.getDescription());
        data.put("imageUrl", food.getImageUrl());
        data.put("categoryId", food.getCategoryId());
        data.put("isPopular", food.isPopular());
        data.put("rating", food.getRating());
        data.put("preparationTime", food.getPreparationTime());
        data.put("isAvailable", food.isAvailable());
        
        if (food.getUpdatedAt() != null) {
            data.put("updatedAt", food.getUpdatedAt());
        }
        if (food.getCreatedAt() != null) {
            data.put("createdAt", food.getCreatedAt());
        }
        
        return data;
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_food, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_save) {
            saveFoodItem();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
