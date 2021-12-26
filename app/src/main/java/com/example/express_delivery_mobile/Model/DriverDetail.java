package com.example.express_delivery_mobile.Model;

public class DriverDetail {

    private int driverId;
    private String status;
    private User user;
    private Vehicle vehicle;
    private String nic;
    private String dob;
    private String address;

    public DriverDetail(int driverId, String status, User user, Vehicle vehicle, String nic, String dob, String address) {
        this.driverId = driverId;
        this.status = status;
        this.user = user;
        this.vehicle = vehicle;
        this.nic = nic;
        this.dob = dob;
        this.address = address;
    }

    public DriverDetail() {

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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
