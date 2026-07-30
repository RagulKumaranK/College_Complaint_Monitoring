package com.campus.complaint.service;

import com.campus.complaint.dto.AuthResponse;
import com.campus.complaint.dto.LoginRequest;
import com.campus.complaint.dto.RegisterRequest;

/**
 * Service interface for user registration and authentication operations.
 */
public interface AuthService {

    /**
     * Register a new user account.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate an existing user and generate a JWT token.
     */
    AuthResponse login(LoginRequest request);
}
