package com.example.express_delivery_mobile.Model;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String location;
    private String phoneNumber;
    private String password;
    private String userRole;
    private String token;
    private String confirmPassword;
    private DriverDetail driverDetail;
    private ServiceCentre serviceCentre;
    private boolean isBanned;

    public User(String email, String firstName, String lastName, String location, String phoneNumber, String password, String userRole, String token, String confirmPassword, DriverDetail driverDetail, ServiceCentre serviceCentre) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.userRole = userRole;
        this.token = token;
        this.confirmPassword = confirmPassword;
        this.driverDetail = driverDetail;
        this.serviceCentre = serviceCentre;
    }

    public User() {
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public DriverDetail getDriverDetail() {
        return driverDetail;
    }

    public void setDriverDetail(DriverDetail driverDetail) {
        this.driverDetail = driverDetail;
    }

    public ServiceCentre getServiceCentre() {
        return serviceCentre;
    }

    public void setServiceCentre(ServiceCentre serviceCentre) {
        this.serviceCentre = serviceCentre;
    }
}
