package com.techart.towme.model;

import com.techart.towme.enums.Unit;

public class Service {
    private String id;
    private String serviceName;
    private double fixedCharge;
    private double variableCharge;
    private Unit unitOfMeasure;

    public Service() {
    }

    public Service(String id, String serviceName, double fixedCharge, double variableCharge, Unit unitOfMeasure) {
        this.id = id;
        this.serviceName = serviceName;
        this.fixedCharge = fixedCharge;
        this.variableCharge = variableCharge;
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public void setFixedCharge(double fixedCharge) {
        this.fixedCharge = fixedCharge;
    }

    public double getVariableCharge() {
        return variableCharge;
    }

    public void setVariableCharge(double variableCharge) {
        this.variableCharge = variableCharge;
    }

    public Unit getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Unit unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
