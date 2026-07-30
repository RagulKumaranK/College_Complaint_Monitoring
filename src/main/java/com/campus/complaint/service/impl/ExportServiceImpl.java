package com.campus.complaint.service.impl;

import com.campus.complaint.entity.Complaint;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.service.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Implementation of ExportService generating CSV and Excel files.
 */
@Service
public class ExportServiceImpl implements ExportService {

    private final ComplaintRepository complaintRepository;

    public ExportServiceImpl(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public ByteArrayInputStream exportComplaintsToCsv() {
        List<Complaint> complaints = complaintRepository.findByDeletedFalse();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.println("ID,Title,Category,Building,Room Number,Priority,Status,Reported By,Assigned To,Created At");
            for (Complaint c : complaints) {
                pw.printf("%d,\"%s\",%s,\"%s\",\"%s\",%s,%s,\"%s\",\"%s\",%s%n",
                        c.getId(),
                        escapeCsv(c.getTitle()),
                        c.getCategory(),
                        escapeCsv(c.getBuilding()),
                        c.getRoomNumber() != null ? escapeCsv(c.getRoomNumber()) : "",
                        c.getPriority(),
                        c.getStatus(),
                        c.getReportedBy() != null ? escapeCsv(c.getReportedBy().getFullName()) : "",
                        c.getAssignedTo() != null ? escapeCsv(c.getAssignedTo()) : "",
                        c.getCreatedAt());
            }
            pw.flush();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportComplaintsToExcel() {
        List<Complaint> complaints = complaintRepository.findByDeletedFalse();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Complaints");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Title", "Category", "Building", "Room", "Priority", "Status", "Reported By", "Assigned To", "Created At"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Complaint c : complaints) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getTitle());
                row.createCell(2).setCellValue(c.getCategory().name());
                row.createCell(3).setCellValue(c.getBuilding());
                row.createCell(4).setCellValue(c.getRoomNumber() != null ? c.getRoomNumber() : "");
                row.createCell(5).setCellValue(c.getPriority().name());
                row.createCell(6).setCellValue(c.getStatus().name());
                row.createCell(7).setCellValue(c.getReportedBy() != null ? c.getReportedBy().getFullName() : "");
                row.createCell(8).setCellValue(c.getAssignedTo() != null ? c.getAssignedTo() : "");
                row.createCell(9).setCellValue(c.getCreatedAt().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file", e);
        }
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }
}
