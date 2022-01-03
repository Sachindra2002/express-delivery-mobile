package com.example.express_delivery_mobile.Model;

public class Vehicle {
    private int vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private String status;
    private DriverDetail driverDetail;

    public Vehicle() {
    }

    public Vehicle(int vehicleId, String vehicleNumber, String vehicleType, String status, DriverDetail driverDetail) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.status = status;
        this.driverDetail = driverDetail;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DriverDetail getDriverDetail() {
        return driverDetail;
    }

    public void setDriverDetail(DriverDetail driverDetail) {
        this.driverDetail = driverDetail;
    }
}
