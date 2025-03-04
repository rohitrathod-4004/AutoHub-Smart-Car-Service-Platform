package com.cscorner.autohub;

import android.widget.Button;
import android.widget.EditText;

public class User {
    private String name;
    private String email;
    private String username;
    private String mobileNo;
    private String drivingLicenseImage;
    private String drivingLicenseNumber;
    private String aadhaarImage;
    private String aadhaarNumber;
    private String panCardImage;
    private String panCardNumber;
    private String profilePhoto;
    private String address;
    private Integer fuelExpense;
    private Integer tollFineExpense;
    private Integer maintenanceExpense;
    private Integer miscellaneousExpense;
    private Integer totalCurrentMonthExpense;
    private Integer totalExpense;
    private String role;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String username, String mobileNo, String email, String address,String role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.mobileNo = mobileNo;
        // Initialize other fields to null or empty
        this.drivingLicenseImage = null;
        this.drivingLicenseNumber = null;
        this.aadhaarImage = null;
        this.aadhaarNumber = null;
        this.panCardImage = null;
        this.panCardNumber = null;
        this.profilePhoto = null;
        this.address = address;
        this.fuelExpense = 0;
        this.tollFineExpense = 0;
        this.maintenanceExpense = 0;
        this.miscellaneousExpense = 0;
        this.totalCurrentMonthExpense = 0;
        this.totalExpense = 0;
        this.role = "user";
    }

    // Getters and Setters for all fields
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.username = username;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDrivingLicenseImage() {
        return drivingLicenseImage;
    }

    public void setDrivingLicenseImage(String drivingLicenseImage) {
        this.drivingLicenseImage = drivingLicenseImage;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getAadhaarImage() {
        return aadhaarImage;
    }

    public void setAadhaarImage(String aadhaarImage) {
        this.aadhaarImage = aadhaarImage;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanCardImage() {
        return panCardImage;
    }

    public void setPanCardImage(String panCardImage) {
        this.panCardImage = panCardImage;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    public Integer getFuelExpense() {
        return fuelExpense;
    }

    public void setFuelExpense(Integer fuelExpense) {
        this.fuelExpense += fuelExpense;
    }
    public Integer getTollFineExpense() {
        return tollFineExpense;
    }

    public void setTollFineExpense(Integer tollFineExpense) {
        this.tollFineExpense += tollFineExpense;
    }
    public Integer getMaintenanceExpense() {
        return maintenanceExpense;
    }

    public void setMaintenanceExpense(Integer maintenanceExpense) {
        this.maintenanceExpense += maintenanceExpense;
    }
    public Integer getMiscellaneousExpense() {
        return miscellaneousExpense;
    }

    public void setMiscellaneousExpense(Integer miscellaneousExpense) {
        this.miscellaneousExpense += miscellaneousExpense;
    }
    public Integer getTotalCurrentMonthExpense() {
        return totalCurrentMonthExpense;
    }
    public void setTotalCurrentMonthExpense(Integer totalCurrentMonthExpense) {
        this.totalCurrentMonthExpense += totalCurrentMonthExpense;
    }
    public Integer getTotalExpense() {
        return totalExpense;
    }
    public void setTotalExpense(Integer totalExpense) {
        this.totalExpense += totalExpense;
    }
    public void setRole(String role) {
        if (role.equals("user") || role.equals("owner")) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role. Allowed values are 'user' or 'owner'.");
        }
    }
    public String getRole() {
        return role;
    }

}