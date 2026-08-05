package com.freeslot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreeSlotResponse {
    private String day;
    private Integer period;
    private String timeLabel; // e.g. "8:00 AM - 9:20 AM" - so the frontend never has to hardcode times
}
