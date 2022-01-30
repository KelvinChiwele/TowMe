package com.techart.towmekiz.model;

import com.techart.towmekiz.enums.Unit;

import java.io.Serializable;

public class Order implements Serializable {
    private String id;
    private String service;
    private String fixedChargeNarration;
    private String userUrl;
    private String garageUrl;
    private String needsQuantity;
    private String additionalInformation;
    private double fixedCharge;
    private double unitCharge;
    private double quantity;
    private double amountToPay;
    private String status;
    private String totalUnitChargeNarration;
    private double latitude;
    private double longitude;
    private Unit unitOfMeasure;
    private Long timeCreated;

    public Order() {
    }

    public Order(String id, String service, double fixedCharge, double unitCharge, Unit unitOfMeasure) {
        this.id = id;
        this.service = service;
        this.fixedCharge = fixedCharge;
        this.unitCharge = unitCharge;
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getGarageUrl() {
        return garageUrl;
    }

    public void setGarageUrl(String garageUrl) {
        this.garageUrl = garageUrl;
    }

    public String getNeedsQuantity() {
        return needsQuantity;
    }

    public void setNeedsQuantity(String needsQuantity) {
        this.needsQuantity = needsQuantity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public void setFixedCharge(double fixedCharge) {
        this.fixedCharge = fixedCharge;
    }

    public double getUnitCharge() {
        return unitCharge;
    }

    public void setUnitCharge(double unitCharge) {
        this.unitCharge = unitCharge;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Unit getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Unit unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
