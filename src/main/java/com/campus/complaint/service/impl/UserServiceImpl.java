package com.campus.complaint.service.impl;

import com.campus.complaint.dto.UserResponse;
import com.campus.complaint.entity.User;
import com.campus.complaint.exception.ResourceNotFoundException;
import com.campus.complaint.repository.UserRepository;
import com.campus.complaint.service.UserService;
import com.campus.complaint.util.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public UserResponse getUserProfile(String email) {
        User user = getUserByEmail(email);
        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }
}
