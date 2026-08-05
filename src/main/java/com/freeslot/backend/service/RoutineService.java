package com.freeslot.backend.service;

import com.freeslot.backend.dto.RoutineRequest;
import com.freeslot.backend.dto.RoutineResponse;
import com.freeslot.backend.exception.ResourceNotFoundException;
import com.freeslot.backend.entity.Routine;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserService userService;

    public RoutineResponse addRoutine(String email, RoutineRequest request) {
        User user = userService.getUserByEmail(email);

        Routine routine = new Routine();
        routine.setUserId(user.getId());
        routine.setDay(request.getDay().toUpperCase());
        routine.setPeriod(request.getPeriod());
        routine.setCourse(request.getCourse());
        routine.setRoom(request.getRoom());

        Routine saved = routineRepository.save(routine);
        return toResponse(saved);
    }

    public List<RoutineResponse> getMyRoutine(String email) {
        User user = userService.getUserByEmail(email);
        return routineRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoutineResponse updateRoutine(String email, Long routineId, RoutineRequest request) {
        User user = userService.getUserByEmail(email);

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine entry not found"));

        if (!routine.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("Routine entry not found");
        }

        routine.setDay(request.getDay().toUpperCase());
        routine.setPeriod(request.getPeriod());
        routine.setCourse(request.getCourse());
        routine.setRoom(request.getRoom());

        Routine saved = routineRepository.save(routine);
        return toResponse(saved);
    }

    @Transactional
    public void deleteRoutine(String email, Long routineId) {
        User user = userService.getUserByEmail(email);
        routineRepository.deleteByIdAndUserId(routineId, user.getId());
    }

    private RoutineResponse toResponse(Routine routine) {
        return new RoutineResponse(routine.getId(), routine.getDay(), routine.getPeriod(),
                PeriodSchedule.labelFor(routine.getPeriod()), routine.getCourse(), routine.getRoom());
    }
}
