package com.kelasxi.waveoffoodadmin.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String uid;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String profileImageUrl;
    private int totalOrders;
    private int loyaltyPoints;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserModel() {}

    public UserModel(String uid, String name, String email, String address, String phone,
                    String profileImageUrl, int totalOrders, int loyaltyPoints,
                    Timestamp createdAt, Timestamp updatedAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.totalOrders = totalOrders;
        this.loyaltyPoints = loyaltyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
