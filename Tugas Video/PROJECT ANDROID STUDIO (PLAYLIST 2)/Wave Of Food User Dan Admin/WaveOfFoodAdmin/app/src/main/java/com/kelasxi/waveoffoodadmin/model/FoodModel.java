package com.kelasxi.waveoffoodadmin.model;

import com.google.firebase.Timestamp;

public class FoodModel {
    private String id;
    private String name;
    private long price;
    private String description;
    private String imageUrl;
    private String categoryId;
    private boolean isPopular;
    private double rating;
    private int preparationTime;
    private boolean isAvailable;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public FoodModel() {}

    public FoodModel(String id, String name, long price, String description, String imageUrl,
                    String categoryId, boolean isPopular, double rating, int preparationTime,
                    boolean isAvailable, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.isPopular = isPopular;
        this.rating = rating;
        this.preparationTime = preparationTime;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("Rp %,d", price);
    }
}
