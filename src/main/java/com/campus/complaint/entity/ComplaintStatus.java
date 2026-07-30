package com.campus.complaint.entity;

/**
 * Lifecycle status of a complaint, from OPEN through CLOSED.
 */
public enum ComplaintStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
