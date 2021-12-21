package com.techart.towme.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String service;
    private String fixedChargeNarration;
    private double fixedCharge;
    private double totalUnitCharge;
    private String totalUnitChargeNarration;
    private double latitude;
    private double longitude;
    private Long timeCreated;

    public Order() {
    }

    public Order(String service, String fixedChargeNarration, double fixedCharge, double totalUnitCharge, String totalUnitChargeNarration, double latitude, double longitude) {
        this.service = service;
        this.fixedChargeNarration = fixedChargeNarration;
        this.fixedCharge = fixedCharge;
        this.totalUnitCharge = totalUnitCharge;
        this.totalUnitChargeNarration = totalUnitChargeNarration;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFixedChargeNarration() {
        return fixedChargeNarration;
    }

    public void setFixedChargeNarration(String fixedChargeNarration) {
        this.fixedChargeNarration = fixedChargeNarration;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public void setFixedCharge(double fixedCharge) {
        this.fixedCharge = fixedCharge;
    }

    public double getTotalUnitCharge() {
        return totalUnitCharge;
    }

    public void setTotalUnitCharge(double totalUnitCharge) {
        this.totalUnitCharge = totalUnitCharge;
    }

    public String getTotalUnitChargeNarration() {
        return totalUnitChargeNarration;
    }

    public void setTotalUnitChargeNarration(String totalUnitChargeNarration) {
        this.totalUnitChargeNarration = totalUnitChargeNarration;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
