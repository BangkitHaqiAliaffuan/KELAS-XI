package com.kelasxi.waveoffoodadmin.model;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Data models compatible with WaveOfFood User app
 */

public class OrderModel {
    private String orderId;
    private String userId;
    private String userName;
    private String userPhone;
    private DeliveryAddress deliveryAddress;
    private List<CartItemModel> items;
    private long subtotal;
    private long deliveryFee;
    private long serviceFee;
    private long discount;
    private long totalAmount;
    private String paymentMethod;
    private String orderStatus; // pending, confirmed, preparing, delivering, completed, cancelled
    private Timestamp estimatedDelivery;
    private Timestamp actualDelivery;
    private double rating;
    private String review;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public OrderModel() {
        this.items = new ArrayList<>();
        this.deliveryAddress = new DeliveryAddress();
    }

    public OrderModel(String orderId, String userId, String userName, String userPhone,
                     DeliveryAddress deliveryAddress, List<CartItemModel> items,
                     long subtotal, long deliveryFee, long serviceFee, long discount,
                     long totalAmount, String paymentMethod, String orderStatus,
                     Timestamp estimatedDelivery, Timestamp actualDelivery,
                     double rating, String review, Timestamp createdAt, Timestamp updatedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.deliveryAddress = deliveryAddress;
        this.items = items != null ? items : new ArrayList<>();
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.serviceFee = serviceFee;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
        this.estimatedDelivery = estimatedDelivery;
        this.actualDelivery = actualDelivery;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public List<CartItemModel> getItems() { return items; }
    public void setItems(List<CartItemModel> items) { this.items = items; }

    public long getSubtotal() { return subtotal; }
    public void setSubtotal(long subtotal) { this.subtotal = subtotal; }

    public long getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(long deliveryFee) { this.deliveryFee = deliveryFee; }

    public long getServiceFee() { return serviceFee; }
    public void setServiceFee(long serviceFee) { this.serviceFee = serviceFee; }

    public long getDiscount() { return discount; }
    public void setDiscount(long discount) { this.discount = discount; }

    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Timestamp getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(Timestamp estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public Timestamp getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(Timestamp actualDelivery) { this.actualDelivery = actualDelivery; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFormattedTotal() {
        return String.format("Rp %,d", totalAmount);
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
