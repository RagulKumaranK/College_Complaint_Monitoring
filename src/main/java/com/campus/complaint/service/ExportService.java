package com.campus.complaint.service;

import java.io.ByteArrayInputStream;

/**
 * Export service for CSV and Excel format generation.
 */
public interface ExportService {

    ByteArrayInputStream exportComplaintsToCsv();

    ByteArrayInputStream exportComplaintsToExcel();
}
