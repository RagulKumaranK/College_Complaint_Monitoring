package com.campus.complaint.service;

import com.campus.complaint.dto.ComplaintFilterRequest;
import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.dto.ComplaintResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface for Complaint management.
 */
public interface ComplaintService {

    ComplaintResponse createComplaint(ComplaintRequest request, String userEmail);

    ComplaintResponse getComplaintById(Long id, String userEmail);

    Page<ComplaintResponse> getAllComplaints(ComplaintFilterRequest filterRequest, String userEmail);

    ComplaintResponse updateComplaint(Long id, ComplaintRequest request, String userEmail);

    void deleteComplaint(Long id, String userEmail);
}
