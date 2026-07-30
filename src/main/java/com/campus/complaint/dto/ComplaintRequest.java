package com.campus.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a complaint.
 * All required fields have Jakarta validation constraints.
 */
public class ComplaintRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private String category;

    @NotBlank(message = "Building is required")
    @Size(max = 100, message = "Building name must not exceed 100 characters")
    private String building;

    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;

    @NotNull(message = "Priority is required")
    private String priority;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    // ==================== Constructors ====================

    public ComplaintRequest() {
    }

    public ComplaintRequest(String title, String description, String category,
                            String building, String roomNumber, String priority,
                            String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.building = building;
        this.roomNumber = roomNumber;
        this.priority = priority;
        this.imageUrl = imageUrl;
    }

    // ==================== Getters & Setters ====================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
