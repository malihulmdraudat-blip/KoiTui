package com.freeslot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponse {
    private Long friendId;
    private String studentId;
    private String name;
    private String semester;
}
