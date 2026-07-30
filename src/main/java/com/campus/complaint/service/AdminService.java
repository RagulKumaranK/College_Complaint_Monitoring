package com.campus.complaint.service;

import com.campus.complaint.dto.AssignRequest;
import com.campus.complaint.dto.ComplaintResponse;
import com.campus.complaint.dto.DashboardResponse;
import com.campus.complaint.dto.StatusUpdateRequest;

/**
 * Service interface for Admin specific operations.
 */
public interface AdminService {

    ComplaintResponse updateStatus(Long id, StatusUpdateRequest request, String adminEmail);

    ComplaintResponse assignComplaint(Long id, AssignRequest request, String adminEmail);

    ComplaintResponse closeComplaint(Long id, String adminEmail);

    DashboardResponse getDashboardStats();
}
