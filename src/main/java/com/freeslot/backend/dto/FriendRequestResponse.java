package com.freeslot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendRequestResponse {
    private Long requestId;    // id of the pending Friend row - use this to accept/reject
    private Long requesterId;  // the user who sent the request
    private String studentId;
    private String name;
    private String semester;
}
