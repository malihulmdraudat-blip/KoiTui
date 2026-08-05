package com.freeslot.backend.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String semester;
    private String password;
}
