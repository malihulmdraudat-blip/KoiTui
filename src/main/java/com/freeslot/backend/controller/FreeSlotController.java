package com.freeslot.backend.controller;

import com.freeslot.backend.dto.FreeSlotResponse;
import com.freeslot.backend.service.FreeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FreeSlotController {

    private final FreeSlotService freeSlotService;

    // Example: GET /free-slot?friendIds=2,3,5
    @GetMapping("/free-slot")
    public List<FreeSlotResponse> getFreeSlots(Authentication authentication,
                                                @RequestParam(required = false) List<Long> friendIds) {
        return freeSlotService.findCommonFreeSlots(authentication.getName(),
                friendIds == null ? Collections.emptyList() : friendIds);
    }
}
