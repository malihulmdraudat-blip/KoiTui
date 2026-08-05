package com.freeslot.backend.controller;

import com.freeslot.backend.dto.RoutineRequest;
import com.freeslot.backend.dto.RoutineResponse;
import com.freeslot.backend.service.RoutineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping("/routine")
    public RoutineResponse addRoutine(Authentication authentication, @Valid @RequestBody RoutineRequest request) {
        return routineService.addRoutine(authentication.getName(), request);
    }

    @GetMapping("/routine")
    public List<RoutineResponse> getRoutine(Authentication authentication) {
        return routineService.getMyRoutine(authentication.getName());
    }

    @PutMapping("/routine/{id}")
    public RoutineResponse updateRoutine(Authentication authentication,
                                          @PathVariable Long id,
                                          @Valid @RequestBody RoutineRequest request) {
        return routineService.updateRoutine(authentication.getName(), id, request);
    }

    @DeleteMapping("/routine/{id}")
    public void deleteRoutine(Authentication authentication, @PathVariable Long id) {
        routineService.deleteRoutine(authentication.getName(), id);
    }
}
