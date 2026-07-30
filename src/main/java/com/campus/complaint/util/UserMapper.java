package com.campus.complaint.util;

import com.campus.complaint.dto.UserResponse;
import com.campus.complaint.entity.User;

/**
 * Manual mapper utility for converting between User entities and DTOs.
 */
public final class UserMapper {

    private UserMapper() {
        // Utility class — prevent instantiation
    }

    /**
     * Convert a User entity to a UserResponse DTO (excludes password).
     */
    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
