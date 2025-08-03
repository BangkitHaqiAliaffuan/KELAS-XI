package com.kelasxi.waveoffoodadmin.model;

public class DeliveryAddress {
    private String address;
    private String city;
    private String postalCode;
    private String recipientName;
    private String recipientPhone;

    public DeliveryAddress() {}

    public DeliveryAddress(String address, String city, String postalCode, String recipientName, String recipientPhone) {
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
}
