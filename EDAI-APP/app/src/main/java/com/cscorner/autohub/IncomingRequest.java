package com.cscorner.autohub;

import com.google.firebase.firestore.PropertyName;

public class IncomingRequest {
    private String name;

    private String requestedTimeSlot;

    private String address;

    private String documentId;
    private String userId;

    private String serviceType;

    public IncomingRequest() {
    }

    public IncomingRequest(String name, String requestedTimeSlot, String address, String documentId, String serviceType, String userId) {
        this.name = name;
        this.requestedTimeSlot = requestedTimeSlot;
        this.address = address;
        this.documentId = documentId;
        this.serviceType = serviceType;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getRequestedTimeSlot() {
        return requestedTimeSlot;
    }

    public void setRequestedTimeSlot(String requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
    }

    public String getAddress() {
        return address;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setCategory(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
