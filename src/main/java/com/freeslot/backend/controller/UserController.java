package com.freeslot.backend.controller;

import com.freeslot.backend.dto.UpdateUserRequest;
import com.freeslot.backend.dto.UserResponse;
import com.freeslot.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public UserResponse getUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.getCurrentUser(email);
    }

    @PutMapping("/user")
    public UserResponse updateUser(Authentication authentication, @RequestBody UpdateUserRequest request) {
        String email = authentication.getName();
        return userService.updateUser(email, request);
    }
}
