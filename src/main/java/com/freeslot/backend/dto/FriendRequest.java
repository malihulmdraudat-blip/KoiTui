package com.freeslot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FriendRequest {
    @NotBlank(message = "Friend's student ID is required")
    private String friendStudentId;
}
