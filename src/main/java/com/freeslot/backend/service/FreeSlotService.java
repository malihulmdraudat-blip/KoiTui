package com.freeslot.backend.service;

import com.freeslot.backend.dto.FreeSlotResponse;
import com.freeslot.backend.entity.Routine;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FreeSlotService {

    // Same day list/order the frontend uses (routine.js / freeslot.js).
    private static final List<String> DAYS = List.of(
            "SATURDAY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY");

    private final RoutineRepository routineRepository;
    private final UserService userService;

    // Finds every (day, period) slot in the week where NEITHER the current
    // user NOR any of the given friends has a class scheduled.
    public List<FreeSlotResponse> findCommonFreeSlots(String email, List<Long> friendIds) {
        User user = userService.getUserByEmail(email);

        List<Long> userIds = new ArrayList<>();
        userIds.add(user.getId());
        userIds.addAll(friendIds);

        List<Routine> routines = routineRepository.findByUserIdIn(userIds);

        Set<String> busySlots = new HashSet<>();
        for (Routine routine : routines) {
            busySlots.add(slotKey(routine.getDay(), routine.getPeriod()));
        }

        List<FreeSlotResponse> freeSlots = new ArrayList<>();
        for (String day : DAYS) {
            for (int period = 1; period <= PeriodSchedule.PERIODS_PER_DAY; period++) {
                if (!busySlots.contains(slotKey(day, period))) {
                    freeSlots.add(new FreeSlotResponse(day, period, PeriodSchedule.labelFor(period)));
                }
            }
        }

        return freeSlots;
    }

    private String slotKey(String day, int period) {
        return day + "-" + period;
    }
}
