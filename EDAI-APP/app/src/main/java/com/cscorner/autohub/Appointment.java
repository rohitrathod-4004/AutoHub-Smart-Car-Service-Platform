package com.cscorner.autohub;

public class Appointment {
    private String requestedTimeSlot;
    private String status;
    private String washingCenterName;
    private String serviceType;

    // Empty constructor for Firestore
    public Appointment() {}

    public Appointment(String requestedTimeSlot, String washingCenterName, String status,String serviceType) {
        this.requestedTimeSlot = requestedTimeSlot;
        this.washingCenterName = washingCenterName;;
        this.status = status;
        this.serviceType = serviceType;
    }

    public String getRequestedTimeSlot() {
        return requestedTimeSlot;
    }

    public void setSelectedTimeslot(String requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getWashingCenterName() {
        return washingCenterName;
    }

    public void setWashingCenterName(String washingCenterName) {
        this.washingCenterName = washingCenterName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    }
