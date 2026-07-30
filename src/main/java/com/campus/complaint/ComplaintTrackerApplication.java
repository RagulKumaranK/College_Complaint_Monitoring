package com.campus.complaint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Campus Complaint Tracker application.
 * Bootstraps the Spring Boot application context.
 */
@SpringBootApplication
public class ComplaintTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplaintTrackerApplication.class, args);
    }
}
