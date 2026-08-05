package com.freeslot.backend.controller;

import com.freeslot.backend.dto.JwtResponse;
import com.freeslot.backend.dto.LoginRequest;
import com.freeslot.backend.dto.SignupRequest;
import com.freeslot.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public JwtResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
