package com.cscorner.autohub.mechanic;

public class EmergencyRequest {
    private String requestId;
    private String userId;
    private String status;
    private long timestamp;
    private long expiryTimestamp;

    private String mechanicId;

    public EmergencyRequest() {
        // Empty constructor for Firestore
    }

    public EmergencyRequest(String requestId, String userId, String status, long timestamp , String mechanicId) {
        this.requestId = requestId;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
        this.mechanicId = mechanicId;

    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public long getExpiryTimestamp() { return expiryTimestamp; }

    public String getMechanicId() {return mechanicId ; }

    // Setters
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setExpiryTimestamp(long expiryTimestamp) { this.expiryTimestamp = expiryTimestamp; }

    public void setMechanicId(String mechanicId) { this.mechanicId = mechanicId ; }
}
