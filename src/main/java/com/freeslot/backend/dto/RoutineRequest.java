package com.freeslot.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoutineRequest {
    @NotBlank(message = "Day is required")
    private String day;

    @NotNull(message = "Period is required")
    @Min(value = 1, message = "Period must be between 1 and 7")
    @Max(value = 7, message = "Period must be between 1 and 7")
    private Integer period;

    private String course;
    private String room;
}
