package com.example.express_delivery_mobile.Model;

public class DriverDetail {

    private int driverId;
    private String status;
    private User user;
    private Vehicle vehicle;
    private String NIC;
    private String DOB;
    private String address;

    public DriverDetail(int driverId, String status, User user, Vehicle vehicle, String NIC, String DOB, String address) {
        this.driverId = driverId;
        this.status = status;
        this.user = user;
        this.vehicle = vehicle;
        this.NIC = NIC;
        this.DOB = DOB;
        this.address = address;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
