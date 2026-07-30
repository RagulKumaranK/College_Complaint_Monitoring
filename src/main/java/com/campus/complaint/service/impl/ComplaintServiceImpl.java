package com.campus.complaint.service.impl;

import com.campus.complaint.dto.ComplaintFilterRequest;
import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.dto.ComplaintResponse;
import com.campus.complaint.entity.*;
import com.campus.complaint.exception.ResourceNotFoundException;
import com.campus.complaint.exception.UnauthorizedException;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.repository.ComplaintSpecification;
import com.campus.complaint.service.ComplaintService;
import com.campus.complaint.service.UserService;
import com.campus.complaint.util.ComplaintMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ComplaintService.
 */
@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserService userService;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserService userService) {
        this.complaintRepository = complaintRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ComplaintResponse createComplaint(ComplaintRequest request, String userEmail) {
        User reportedBy = userService.getUserByEmail(userEmail);
        Complaint complaint = ComplaintMapper.toEntity(request, reportedBy);
        Complaint savedComplaint = complaintRepository.save(complaint);
        return ComplaintMapper.toResponse(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        if (user.getRole() != Role.ADMIN && !complaint.getReportedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to view this complaint");
        }

        return ComplaintMapper.toResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getAllComplaints(ComplaintFilterRequest filter, String userEmail) {
        User user = userService.getUserByEmail(userEmail);

        Sort sort = filter.getSortDir().equalsIgnoreCase("asc") ?
                Sort.by(filter.getSortBy()).ascending() :
                Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Complaint> spec = ComplaintSpecification.isNotDeleted();

        if (user.getRole() != Role.ADMIN) {
            spec = spec.and(ComplaintSpecification.reportedBy(user));
        }

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            spec = spec.and(ComplaintSpecification.hasStatus(ComplaintStatus.valueOf(filter.getStatus().toUpperCase())));
        }
        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            spec = spec.and(ComplaintSpecification.hasCategory(ComplaintCategory.valueOf(filter.getCategory().toUpperCase())));
        }
        if (filter.getBuilding() != null && !filter.getBuilding().isBlank()) {
            spec = spec.and(ComplaintSpecification.hasBuilding(filter.getBuilding()));
        }
        if (filter.getPriority() != null && !filter.getPriority().isBlank()) {
            spec = spec.and(ComplaintSpecification.hasPriority(ComplaintPriority.valueOf(filter.getPriority().toUpperCase())));
        }
        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            spec = spec.and(ComplaintSpecification.containsKeyword(filter.getKeyword()));
        }
        if (filter.getStartDate() != null) {
            spec = spec.and(ComplaintSpecification.createdAfter(filter.getStartDate()));
        }
        if (filter.getEndDate() != null) {
            spec = spec.and(ComplaintSpecification.createdBefore(filter.getEndDate()));
        }

        return complaintRepository.findAll(spec, pageable).map(ComplaintMapper::toResponse);
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaint(Long id, ComplaintRequest request, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        if (user.getRole() != Role.ADMIN && !complaint.getReportedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to update this complaint");
        }

        ComplaintMapper.updateEntity(complaint, request, userEmail);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        return ComplaintMapper.toResponse(updatedComplaint);
    }

    @Override
    @Transactional
    public void deleteComplaint(Long id, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        Complaint complaint = complaintRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        if (user.getRole() != Role.ADMIN && !complaint.getReportedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to delete this complaint");
        }

        complaint.setDeleted(true);
        complaint.setUpdatedBy(userEmail);
        complaintRepository.save(complaint);
    }
}
