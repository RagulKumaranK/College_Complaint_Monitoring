package com.campus.complaint.service.impl;

import com.campus.complaint.dto.AssignRequest;
import com.campus.complaint.dto.ComplaintResponse;
import com.campus.complaint.dto.DashboardResponse;
import com.campus.complaint.dto.StatusUpdateRequest;
import com.campus.complaint.entity.Complaint;
import com.campus.complaint.entity.ComplaintPriority;
import com.campus.complaint.entity.ComplaintStatus;
import com.campus.complaint.exception.ResourceNotFoundException;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.service.AdminService;
import com.campus.complaint.util.ComplaintMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AdminService.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final ComplaintRepository complaintRepository;

    public AdminServiceImpl(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    @Transactional
    public ComplaintResponse updateStatus(Long id, StatusUpdateRequest request, String adminEmail) {
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        complaint.setStatus(ComplaintStatus.valueOf(request.getStatus().toUpperCase()));
        if (request.getRemarks() != null) {
            complaint.setRemarks(request.getRemarks());
        }
        complaint.setUpdatedBy(adminEmail);

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ComplaintResponse assignComplaint(Long id, AssignRequest request, String adminEmail) {
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        complaint.setAssignedTo(request.getAssignedTo());
        if (complaint.getStatus() == ComplaintStatus.OPEN) {
            complaint.setStatus(ComplaintStatus.ASSIGNED);
        }
        complaint.setUpdatedBy(adminEmail);

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ComplaintResponse closeComplaint(Long id, String adminEmail) {
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        complaint.setStatus(ComplaintStatus.CLOSED);
        complaint.setUpdatedBy(adminEmail);

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintMapper.toResponse(updated);
    }

    @Override
    public DashboardResponse getDashboardStats() {
        DashboardResponse stats = new DashboardResponse();

        stats.setTotalComplaints(complaintRepository.countByDeletedFalse());
        stats.setOpenComplaints(complaintRepository.countByStatusAndDeletedFalse(ComplaintStatus.OPEN));
        stats.setAssignedComplaints(complaintRepository.countByStatusAndDeletedFalse(ComplaintStatus.ASSIGNED));
        stats.setInProgressComplaints(complaintRepository.countByStatusAndDeletedFalse(ComplaintStatus.IN_PROGRESS));
        stats.setResolvedComplaints(complaintRepository.countByStatusAndDeletedFalse(ComplaintStatus.RESOLVED));
        stats.setClosedComplaints(complaintRepository.countByStatusAndDeletedFalse(ComplaintStatus.CLOSED));
        stats.setCriticalComplaints(complaintRepository.countByPriorityAndDeletedFalse(ComplaintPriority.CRITICAL));

        // Grouped by Category
        Map<String, Long> byCategory = new HashMap<>();
        List<Object[]> categoryResults = complaintRepository.countByCategory();
        for (Object[] row : categoryResults) {
            byCategory.put(row[0].toString(), (Long) row[1]);
        }
        stats.setComplaintsByCategory(byCategory);

        // Grouped by Building
        Map<String, Long> byBuilding = new HashMap<>();
        List<Object[]> buildingResults = complaintRepository.countByBuilding();
        for (Object[] row : buildingResults) {
            byBuilding.put((String) row[0], (Long) row[1]);
        }
        stats.setComplaintsByBuilding(byBuilding);

        // Grouped by Priority
        Map<String, Long> byPriority = new HashMap<>();
        List<Object[]> priorityResults = complaintRepository.countByPriorityGrouped();
        for (Object[] row : priorityResults) {
            byPriority.put(row[0].toString(), (Long) row[1]);
        }
        stats.setComplaintsByPriority(byPriority);

        return stats;
    }
}
