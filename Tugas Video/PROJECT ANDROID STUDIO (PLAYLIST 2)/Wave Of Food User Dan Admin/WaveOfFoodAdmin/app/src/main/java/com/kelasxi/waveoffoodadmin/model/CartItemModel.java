package com.kelasxi.waveoffoodadmin.model;

public class CartItemModel {
    private String name;
    private String imageUrl;
    private int quantity;
    private double price;
    private String description;

    public CartItemModel() {}

    public CartItemModel(String name, String imageUrl, int quantity, double price, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
