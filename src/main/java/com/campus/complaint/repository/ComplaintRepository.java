package com.campus.complaint.repository;

import com.campus.complaint.entity.Complaint;
import com.campus.complaint.entity.ComplaintCategory;
import com.campus.complaint.entity.ComplaintPriority;
import com.campus.complaint.entity.ComplaintStatus;
import com.campus.complaint.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Complaint entity database operations.
 * Extends JpaSpecificationExecutor for dynamic filtering via Specifications.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long>,
        JpaSpecificationExecutor<Complaint> {

    /**
     * Find a non-deleted complaint by ID.
     */
    Optional<Complaint> findByIdAndDeletedFalse(Long id);

    /**
     * Find all non-deleted complaints with pagination.
     */
    Page<Complaint> findByDeletedFalse(Pageable pageable);

    /**
     * Find all non-deleted complaints reported by a specific user.
     */
    Page<Complaint> findByReportedByAndDeletedFalse(User reportedBy, Pageable pageable);

    /**
     * Find complaints by status (non-deleted).
     */
    Page<Complaint> findByStatusAndDeletedFalse(ComplaintStatus status, Pageable pageable);

    /**
     * Find complaints by category (non-deleted).
     */
    Page<Complaint> findByCategoryAndDeletedFalse(ComplaintCategory category, Pageable pageable);

    /**
     * Find complaints by building (non-deleted).
     */
    Page<Complaint> findByBuildingAndDeletedFalse(String building, Pageable pageable);

    /**
     * Find complaints by priority (non-deleted).
     */
    Page<Complaint> findByPriorityAndDeletedFalse(ComplaintPriority priority, Pageable pageable);

    // ==================== Dashboard Queries ====================

    /**
     * Count all non-deleted complaints.
     */
    long countByDeletedFalse();

    /**
     * Count non-deleted complaints by status.
     */
    long countByStatusAndDeletedFalse(ComplaintStatus status);

    /**
     * Count non-deleted complaints by priority.
     */
    long countByPriorityAndDeletedFalse(ComplaintPriority priority);

    /**
     * Get complaint counts grouped by category (non-deleted).
     */
    @Query("SELECT c.category, COUNT(c) FROM Complaint c WHERE c.deleted = false GROUP BY c.category")
    List<Object[]> countByCategory();

    /**
     * Get complaint counts grouped by building (non-deleted).
     */
    @Query("SELECT c.building, COUNT(c) FROM Complaint c WHERE c.deleted = false GROUP BY c.building")
    List<Object[]> countByBuilding();

    /**
     * Get complaint counts grouped by priority (non-deleted).
     */
    @Query("SELECT c.priority, COUNT(c) FROM Complaint c WHERE c.deleted = false GROUP BY c.priority")
    List<Object[]> countByPriorityGrouped();

    /**
     * Find all non-deleted complaints (for export).
     */
    List<Complaint> findByDeletedFalse();

    /**
     * Search complaints by keyword in title or description (non-deleted).
     */
    @Query("SELECT c FROM Complaint c WHERE c.deleted = false AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Complaint> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
