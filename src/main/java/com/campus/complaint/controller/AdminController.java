package com.campus.complaint.controller;

import com.campus.complaint.dto.*;
import com.campus.complaint.service.AdminService;
import com.campus.complaint.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * Controller for Admin specific administrative operations (dashboard, assignment, status update, export).
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Operations", description = "Endpoints restricted to Admin users for complaint lifecycle management")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ExportService exportService;

    public AdminController(AdminService adminService, ExportService exportService) {
        this.adminService = adminService;
        this.exportService = exportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Fetch complaint counts, category breakdowns, and building stats.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats() {
        DashboardResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "Update complaint status", description = "Change status of a complaint and add admin remarks.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        ComplaintResponse response = adminService.updateStatus(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    @PutMapping("/assign/{id}")
    @Operation(summary = "Assign complaint", description = "Assign complaint to maintenance technician or department.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assignComplaint(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        ComplaintResponse response = adminService.assignComplaint(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Complaint assigned successfully", response));
    }

    @PutMapping("/close/{id}")
    @Operation(summary = "Close complaint", description = "Mark a complaint as closed.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> closeComplaint(
            @PathVariable Long id,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        ComplaintResponse response = adminService.closeComplaint(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Complaint closed successfully", response));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export complaints to CSV", description = "Download CSV report of all complaints.")
    public ResponseEntity<InputStreamResource> exportCsv() {
        ByteArrayInputStream in = exportService.exportComplaintsToCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=complaints.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Export complaints to Excel", description = "Download Excel report of all complaints.")
    public ResponseEntity<InputStreamResource> exportExcel() {
        ByteArrayInputStream in = exportService.exportComplaintsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=complaints.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
