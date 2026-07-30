package com.campus.complaint.dto;

import java.util.Map;

/**
 * DTO for admin dashboard statistics.
 * Contains complaint counts and breakdowns by category and building.
 */
public class DashboardResponse {

    private long totalComplaints;
    private long openComplaints;
    private long assignedComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long closedComplaints;
    private long criticalComplaints;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByBuilding;
    private Map<String, Long> complaintsByPriority;

    // ==================== Constructors ====================

    public DashboardResponse() {
    }

    // ==================== Getters & Setters ====================

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getOpenComplaints() {
        return openComplaints;
    }

    public void setOpenComplaints(long openComplaints) {
        this.openComplaints = openComplaints;
    }

    public long getAssignedComplaints() {
        return assignedComplaints;
    }

    public void setAssignedComplaints(long assignedComplaints) {
        this.assignedComplaints = assignedComplaints;
    }

    public long getInProgressComplaints() {
        return inProgressComplaints;
    }

    public void setInProgressComplaints(long inProgressComplaints) {
        this.inProgressComplaints = inProgressComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getClosedComplaints() {
        return closedComplaints;
    }

    public void setClosedComplaints(long closedComplaints) {
        this.closedComplaints = closedComplaints;
    }

    public long getCriticalComplaints() {
        return criticalComplaints;
    }

    public void setCriticalComplaints(long criticalComplaints) {
        this.criticalComplaints = criticalComplaints;
    }

    public Map<String, Long> getComplaintsByCategory() {
        return complaintsByCategory;
    }

    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) {
        this.complaintsByCategory = complaintsByCategory;
    }

    public Map<String, Long> getComplaintsByBuilding() {
        return complaintsByBuilding;
    }

    public void setComplaintsByBuilding(Map<String, Long> complaintsByBuilding) {
        this.complaintsByBuilding = complaintsByBuilding;
    }

    public Map<String, Long> getComplaintsByPriority() {
        return complaintsByPriority;
    }

    public void setComplaintsByPriority(Map<String, Long> complaintsByPriority) {
        this.complaintsByPriority = complaintsByPriority;
    }
}
