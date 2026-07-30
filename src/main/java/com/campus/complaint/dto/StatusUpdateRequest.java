package com.campus.complaint.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for admin status update requests.
 */
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private String status;

    private String remarks;

    // ==================== Constructors ====================

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    // ==================== Getters & Setters ====================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
