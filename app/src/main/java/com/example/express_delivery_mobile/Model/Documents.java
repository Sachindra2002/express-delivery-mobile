package com.example.express_delivery_mobile.Model;

public class Documents {
    private int documentId;
    private String description;
    private String fileName;
    private Long fileSize;
    private User user;

    public Documents(int documentId, String description, String fileName, Long fileSize, User user) {
        this.documentId = documentId;
        this.description = description;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.user = user;
    }

    public Documents() {
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
