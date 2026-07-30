package com.campus.complaint.util;

import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.dto.ComplaintResponse;
import com.campus.complaint.entity.Complaint;
import com.campus.complaint.entity.ComplaintCategory;
import com.campus.complaint.entity.ComplaintPriority;
import com.campus.complaint.entity.User;

/**
 * Manual mapper utility for converting between Complaint entities and DTOs.
 */
public final class ComplaintMapper {

    private ComplaintMapper() {
        // Utility class — prevent instantiation
    }

    /**
     * Convert a Complaint entity to a ComplaintResponse DTO.
     */
    public static ComplaintResponse toResponse(Complaint complaint) {
        if (complaint == null) return null;

        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setTitle(complaint.getTitle());
        response.setDescription(complaint.getDescription());
        response.setCategory(complaint.getCategory() != null ? complaint.getCategory().name() : null);
        response.setBuilding(complaint.getBuilding());
        response.setRoomNumber(complaint.getRoomNumber());
        response.setPriority(complaint.getPriority() != null ? complaint.getPriority().name() : null);
        response.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : null);
        response.setImageUrl(complaint.getImageUrl());
        response.setRemarks(complaint.getRemarks());
        response.setAssignedTo(complaint.getAssignedTo());
        response.setCreatedBy(complaint.getCreatedBy());
        response.setUpdatedBy(complaint.getUpdatedBy());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setUpdatedAt(complaint.getUpdatedAt());

        // Map reporter details
        User reporter = complaint.getReportedBy();
        if (reporter != null) {
            response.setReportedByName(reporter.getFullName());
            response.setReportedByEmail(reporter.getEmail());
        }

        return response;
    }

    /**
     * Convert a ComplaintRequest DTO to a new Complaint entity.
     * Status and timestamps are set by JPA lifecycle callbacks.
     */
    public static Complaint toEntity(ComplaintRequest request, User reportedBy) {
        if (request == null) return null;

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(ComplaintCategory.valueOf(request.getCategory().toUpperCase()));
        complaint.setBuilding(request.getBuilding());
        complaint.setRoomNumber(request.getRoomNumber());
        complaint.setPriority(ComplaintPriority.valueOf(request.getPriority().toUpperCase()));
        complaint.setImageUrl(request.getImageUrl());
        complaint.setReportedBy(reportedBy);
        complaint.setCreatedBy(reportedBy.getEmail());

        return complaint;
    }

    /**
     * Update an existing Complaint entity from a ComplaintRequest DTO.
     * Only updates mutable fields (not ID, reportedBy, status, etc.).
     */
    public static void updateEntity(Complaint complaint, ComplaintRequest request, String updatedByEmail) {
        if (request.getTitle() != null) {
            complaint.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            complaint.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            complaint.setCategory(ComplaintCategory.valueOf(request.getCategory().toUpperCase()));
        }
        if (request.getBuilding() != null) {
            complaint.setBuilding(request.getBuilding());
        }
        if (request.getRoomNumber() != null) {
            complaint.setRoomNumber(request.getRoomNumber());
        }
        if (request.getPriority() != null) {
            complaint.setPriority(ComplaintPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getImageUrl() != null) {
            complaint.setImageUrl(request.getImageUrl());
        }
        complaint.setUpdatedBy(updatedByEmail);
    }
}
