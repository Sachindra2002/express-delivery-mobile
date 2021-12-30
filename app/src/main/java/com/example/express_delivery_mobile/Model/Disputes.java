package com.example.express_delivery_mobile.Model;

import java.util.Date;

public class Disputes {

    private int disputeId;
    private String disputeType;
    private String description;
    private String status;
    private String response;
    private Date createdAt;
    private Mail mail;

    public Disputes() {
    }

    public Disputes(int disputeId, String disputeType, String description, String status, String response, Date createdAt, Mail mail) {
        this.disputeId = disputeId;
        this.disputeType = disputeType;
        this.description = description;
        this.status = status;
        this.response = response;
        this.createdAt = createdAt;
        this.mail = mail;
    }

    public int getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(int disputeId) {
        this.disputeId = disputeId;
    }

    public String getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(String disputeType) {
        this.disputeType = disputeType;
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

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }
}
