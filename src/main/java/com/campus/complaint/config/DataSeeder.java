package com.campus.complaint.config;

import com.campus.complaint.entity.*;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataSeeder component running at application startup to populate
 * sample users and complaints if seed is enabled.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    public DataSeeder(UserRepository userRepository,
                      ComplaintRepository complaintRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            logger.info("Data seeding skipped (already seeded or disabled).");
            return;
        }

        logger.info("Seeding initial data...");

        // 1. Create Admin User
        User admin = new User("Admin User", "admin@campus.edu", passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(admin);

        // 2. Create Sample Students / Faculty
        User student = new User("John Doe", "john.doe@student.campus.edu", passwordEncoder.encode("student123"), Role.USER);
        userRepository.save(student);

        User faculty = new User("Dr. Sarah Smith", "sarah.smith@faculty.campus.edu", passwordEncoder.encode("faculty123"), Role.USER);
        userRepository.save(faculty);

        // 3. Create Sample Complaints
        Complaint c1 = new Complaint(
                "Ceiling Fan Noise and Low Speed",
                "The ceiling fan in Room 302 is making loud screeching noises and running very slow.",
                ComplaintCategory.FAN,
                "Academic Block A",
                "Room 302",
                ComplaintPriority.MEDIUM,
                student
        );
        complaintRepository.save(c1);

        Complaint c2 = new Complaint(
                "Projector Display Flickering",
                "HDMI display port on the projector flickers constantly during lectures.",
                ComplaintCategory.PROJECTOR,
                "Science Building",
                "Auditorium 1",
                ComplaintPriority.HIGH,
                faculty
        );
        c2.setStatus(ComplaintStatus.IN_PROGRESS);
        c2.setAssignedTo("Tech Support Team");
        c2.setRemarks("Technician assigned, replacement cable requested.");
        complaintRepository.save(c2);

        Complaint c3 = new Complaint(
                "Water Leakage in Restroom",
                "Severe water leakage from pipe under wash basin causing floor flooding.",
                ComplaintCategory.WATER_LEAKAGE,
                "Library Complex",
                "2nd Floor Restroom",
                ComplaintPriority.CRITICAL,
                student
        );
        c3.setStatus(ComplaintStatus.ASSIGNED);
        c3.setAssignedTo("Plumbing Dept");
        complaintRepository.save(c3);

        Complaint c4 = new Complaint(
                "Damaged Wooden Bench",
                "Bench near entrance has broken wooden slates, potential injury hazard.",
                ComplaintCategory.FURNITURE,
                "Student Activity Center",
                "Main Lobby",
                ComplaintPriority.LOW,
                student
        );
        c4.setStatus(ComplaintStatus.RESOLVED);
        c4.setRemarks("Bench replaced with new furniture.");
        complaintRepository.save(c4);

        logger.info("Initial seed data created successfully!");
    }
}
