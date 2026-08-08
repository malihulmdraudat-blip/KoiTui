package com.freeslot.backend.service;

import com.freeslot.backend.dto.ForgotPasswordRequest;
import com.freeslot.backend.dto.JwtResponse;
import com.freeslot.backend.dto.LoginRequest;
import com.freeslot.backend.dto.MessageResponse;
import com.freeslot.backend.dto.SignupRequest;
import com.freeslot.backend.exception.BadRequestException;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.UserRepository;
import com.freeslot.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public JwtResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new BadRequestException("An account with this student ID already exists");
        }

        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // NEVER store plain-text passwords - always hash with BCrypt
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSemester(request.getSemester());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail());

        return new JwtResponse(token, saved.getId(), saved.getName(), saved.getEmail());
    }

    public JwtResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new JwtResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    // Simple identity-verification based reset: no email step required.
    // The user proves ownership of the account by supplying both the email
    // AND the student ID on file, then sets a brand new password directly.
    public MessageResponse resetPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account matches that email and student ID"));

        if (!user.getStudentId().equalsIgnoreCase(request.getStudentId())) {
            throw new BadRequestException("No account matches that email and student ID");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new MessageResponse("Password updated successfully. You can now log in with your new password.");
    }
}
