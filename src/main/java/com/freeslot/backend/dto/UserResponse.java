package com.freeslot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String studentId;
    private String name;
    private String email;
    private String semester;
}
