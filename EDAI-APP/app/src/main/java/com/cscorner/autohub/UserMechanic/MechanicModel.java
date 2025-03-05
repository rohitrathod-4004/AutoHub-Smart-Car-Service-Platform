package com.cscorner.autohub.UserMechanic;

public class MechanicModel {
    private String name, mobile;
    private double distance;

    public MechanicModel(String name, String mobile, double distance) {
        this.name = name;
        this.mobile = mobile;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public double getDistance() {
        return distance;
    }
}
