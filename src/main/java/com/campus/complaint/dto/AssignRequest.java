package com.campus.complaint.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for admin complaint assignment requests.
 */
public class AssignRequest {

    @NotBlank(message = "Assigned to field is required")
    private String assignedTo;

    // ==================== Constructors ====================

    public AssignRequest() {
    }

    public AssignRequest(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    // ==================== Getters & Setters ====================

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
