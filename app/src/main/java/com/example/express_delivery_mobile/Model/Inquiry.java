package com.example.express_delivery_mobile.Model;

import java.util.Date;

public class Inquiry {

    private int inquiryId;
    private String inquiryType;
    private String description;
    private String status;
    private String response;
    private Date createdAt;
    private User user;

    public Inquiry() {
    }

    public Inquiry(int inquiryId, String inquiryType, String description, String status, String response, Date createdAt, User user) {
        this.inquiryId = inquiryId;
        this.inquiryType = inquiryType;
        this.description = description;
        this.status = status;
        this.response = response;
        this.createdAt = createdAt;
        this.user = user;
    }

    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(String inquiryType) {
        this.inquiryType = inquiryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
