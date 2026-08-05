package com.freeslot.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FreeSlotRequest {
    private List<Long> friendIds;
}
