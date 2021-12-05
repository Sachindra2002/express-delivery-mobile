package com.example.express_delivery_mobile.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class Mail implements Serializable {
    private int mailId;
    private String pickupAddress,
            receiverAddress,
            receiverFirstName,
            receiverLastName,
            receiverPhoneNumber,
            receiverEmail,
            receiverCity,
            parcelType,
            weight,
            pieces,
            paymentMethod,
            date,
            time,
            totalCost,
            status,
            description;
    private User user;
    private MailTracking mailTracking;
    private DriverDetail driverDetail;
    private String transportationStatus;
    private ServiceCentre serviceCentre;
    private String dropOffDate;
    private Date createdAt;

    public Mail(int mailId, String pickupAddress, String receiverAddress, String receiverFirstName, String receiverLastName, String receiverPhoneNumber, String receiverEmail, String receiverCity, String parcelType, String weight, String pieces, String paymentMethod, String date, String time, String totalCost, String status, String description, User user, MailTracking mailTracking, DriverDetail driverDetail, String transportationStatus, ServiceCentre serviceCentre, String dropOffDate, Date createdAt) {
        this.mailId = mailId;
        this.pickupAddress = pickupAddress;
        this.receiverAddress = receiverAddress;
        this.receiverFirstName = receiverFirstName;
        this.receiverLastName = receiverLastName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.receiverEmail = receiverEmail;
        this.receiverCity = receiverCity;
        this.parcelType = parcelType;
        this.weight = weight;
        this.pieces = pieces;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.time = time;
        this.totalCost = totalCost;
        this.status = status;
        this.description = description;
        this.user = user;
        this.mailTracking = mailTracking;
        this.driverDetail = driverDetail;
        this.transportationStatus = transportationStatus;
        this.serviceCentre = serviceCentre;
        this.dropOffDate = dropOffDate;
        this.createdAt = createdAt;
    }

    public int getMailId() {
        return mailId;
    }

    public void setMailId(int mailId) {
        this.mailId = mailId;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public void setReceiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getParcelType() {
        return parcelType;
    }

    public void setParcelType(String parcelType) {
        this.parcelType = parcelType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MailTracking getMailTracking() {
        return mailTracking;
    }

    public void setMailTracking(MailTracking mailTracking) {
        this.mailTracking = mailTracking;
    }

    public DriverDetail getDriverDetail() {
        return driverDetail;
    }

    public void setDriverDetail(DriverDetail driverDetail) {
        this.driverDetail = driverDetail;
    }

    public String getTransportationStatus() {
        return transportationStatus;
    }

    public void setTransportationStatus(String transportationStatus) {
        this.transportationStatus = transportationStatus;
    }

    public ServiceCentre getServiceCentre() {
        return serviceCentre;
    }

    public void setServiceCentre(ServiceCentre serviceCentre) {
        this.serviceCentre = serviceCentre;
    }

    public String getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(String dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
