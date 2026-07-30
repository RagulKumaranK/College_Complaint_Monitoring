package com.campus.complaint.service;

import com.campus.complaint.dto.UserResponse;
import com.campus.complaint.entity.User;

import java.util.List;

/**
 * Service interface for user operations.
 */
public interface UserService {

    User getUserByEmail(String email);

    UserResponse getUserProfile(String email);

    List<UserResponse> getAllUsers();
}
