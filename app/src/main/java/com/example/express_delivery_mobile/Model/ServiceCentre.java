package com.example.express_delivery_mobile.Model;

public class ServiceCentre {
    private int centreId;
    private String city;
    private String center;
    private String address;

    public ServiceCentre(int centreId, String city, String center, String address) {
        this.centreId = centreId;
        this.city = city;
        this.center = center;
        this.address = address;
    }

    public ServiceCentre() {
    }

    public int getCentreId() {
        return centreId;
    }

    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCentre() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
