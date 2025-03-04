package com.cscorner.autohub;

public class WashingCenterOwner {
    private String documentId; // Add this field
    private String name;
    private String username;
    private String mobileNo;
    private String address;
    private int doorStepWashingPrice;
    private int pickupReturnWashingPrice;
    private int normalWashingPrice;

    // Constructor
    public WashingCenterOwner(String documentId, String name, String username, String mobileNo, String address,
                              int doorStepWashingPrice, int pickupReturnWashingPrice, int normalWashingPrice) {
        this.documentId = documentId; // Initialize documentId
        this.name = name;
        this.username = username;
        this.mobileNo = mobileNo;
        this.address = address;
        this.doorStepWashingPrice = doorStepWashingPrice;
        this.pickupReturnWashingPrice = pickupReturnWashingPrice;
        this.normalWashingPrice = normalWashingPrice;
    }

    // Getters
    public String getDocumentId() { return documentId; } // Add getter for documentId
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getMobileNo() { return mobileNo; }
    public String getAddress() { return address; }
    public int getDoorStepWashing() { return doorStepWashingPrice; }
    public int getPickupReturnWashing() { return pickupReturnWashingPrice; }
    public int getNormalWashing() { return normalWashingPrice; }
}
