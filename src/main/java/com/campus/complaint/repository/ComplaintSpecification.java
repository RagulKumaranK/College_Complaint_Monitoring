package com.campus.complaint.repository;

import com.campus.complaint.entity.Complaint;
import com.campus.complaint.entity.ComplaintCategory;
import com.campus.complaint.entity.ComplaintPriority;
import com.campus.complaint.entity.ComplaintStatus;
import com.campus.complaint.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Specification builder for dynamic complaint filtering.
 * Each method returns a composable Specification predicate.
 * Combine them with Specification.where(...).and(...).and(...)
 */
public final class ComplaintSpecification {

    private ComplaintSpecification() {
        // Utility class — prevent instantiation
    }

    /**
     * Filter: only non-deleted complaints.
     */
    public static Specification<Complaint> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /**
     * Filter by complaint status.
     */
    public static Specification<Complaint> hasStatus(ComplaintStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filter by complaint category.
     */
    public static Specification<Complaint> hasCategory(ComplaintCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }

    /**
     * Filter by building name (case-insensitive).
     */
    public static Specification<Complaint> hasBuilding(String building) {
        return (root, query, cb) -> {
            if (building == null || building.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("building")), building.toLowerCase());
        };
    }

    /**
     * Filter by complaint priority.
     */
    public static Specification<Complaint> hasPriority(ComplaintPriority priority) {
        return (root, query, cb) -> {
            if (priority == null) return cb.conjunction();
            return cb.equal(root.get("priority"), priority);
        };
    }

    /**
     * Search by keyword in title or description (case-insensitive).
     */
    public static Specification<Complaint> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    /**
     * Filter by reported user.
     */
    public static Specification<Complaint> reportedBy(User user) {
        return (root, query, cb) -> {
            if (user == null) return cb.conjunction();
            return cb.equal(root.get("reportedBy"), user);
        };
    }

    /**
     * Filter: created on or after the given date.
     */
    public static Specification<Complaint> createdAfter(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return cb.conjunction();
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    /**
     * Filter: created on or before the end of the given date.
     */
    public static Specification<Complaint> createdBefore(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return cb.conjunction();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            return cb.lessThan(root.get("createdAt"), endDateTime);
        };
    }
}
