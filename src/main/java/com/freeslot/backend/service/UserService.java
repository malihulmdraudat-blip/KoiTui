package com.freeslot.backend.service;

import com.freeslot.backend.dto.UpdateUserRequest;
import com.freeslot.backend.dto.UserResponse;
import com.freeslot.backend.exception.ResourceNotFoundException;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse getCurrentUser(String email) {
        User user = getUserByEmail(email);
        return toResponse(user);
    }

    public UserResponse updateUser(String email, UpdateUserRequest request) {
        User user = getUserByEmail(email);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getSemester() != null) {
            user.setSemester(request.getSemester());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getStudentId(), user.getName(),
                user.getEmail(), user.getSemester());
    }
}
