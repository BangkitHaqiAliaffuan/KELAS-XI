package com.kelasxi.waveoffoodadmin.model;

import com.google.firebase.Timestamp;
import java.util.Map;

public class AdminModel {
    private String uid;
    private String name;
    private String email;
    private String role;
    private String status;
    private Map<String, Object> permissions;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLoginAt;
    private String createdBy;
    private String updatedBy;

    // Default constructor required for Firestore
    public AdminModel() {}

    public AdminModel(String uid, String name, String email, String role, String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Object> permissions) {
        this.permissions = permissions;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Helper methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean hasPermission(String permissionKey) {
        if (permissions == null) return false;
        Object permission = permissions.get(permissionKey);
        return Boolean.TRUE.equals(permission);
    }

    public String getRoleDisplayName() {
        if (role == null) return "Admin";
        
        switch (role) {
            case "super_admin":
                return "Super Admin";
            case "moderator":
                return "Moderator";
            case "admin":
            default:
                return "Admin";
        }
    }

    public String getStatusDisplayName() {
        if (status == null) return "Unknown";
        
        switch (status) {
            case "active":
                return "Active";
            case "inactive":
                return "Inactive";
            case "suspended":
                return "Suspended";
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return "AdminModel{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", permissions=" + permissions +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
