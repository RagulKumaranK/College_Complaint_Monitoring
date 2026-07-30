package com.campus.complaint.controller;

import com.campus.complaint.dto.ApiResponse;
import com.campus.complaint.dto.ComplaintFilterRequest;
import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.dto.ComplaintResponse;
import com.campus.complaint.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for complaint operations (create, view, update, delete, search/filter).
 */
@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaints", description = "Endpoints for managing campus infrastructure complaints")
@SecurityRequirement(name = "Bearer Authentication")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    @Operation(summary = "Create complaint", description = "Report a new campus infrastructure issue.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        ComplaintResponse response = complaintService.createComplaint(request, email);
        return new ResponseEntity<>(ApiResponse.success("Complaint created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by ID", description = "Retrieve details of a specific complaint.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        ComplaintResponse response = complaintService.getComplaintById(id, email);
        return ResponseEntity.ok(ApiResponse.success("Complaint retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all complaints", description = "Fetch complaints with filtering, search, and pagination.")
    public ResponseEntity<ApiResponse<Page<ComplaintResponse>>> getAllComplaints(
            @ModelAttribute ComplaintFilterRequest filterRequest,
            Authentication authentication) {
        String email = authentication.getName();
        Page<ComplaintResponse> complaints = complaintService.getAllComplaints(filterRequest, email);
        return ResponseEntity.ok(ApiResponse.success("Complaints retrieved successfully", complaints));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complaint", description = "Update details of an existing complaint.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        ComplaintResponse response = complaintService.updateComplaint(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete complaint", description = "Soft delete a complaint by ID.")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        complaintService.deleteComplaint(id, email);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted successfully"));
    }
}
