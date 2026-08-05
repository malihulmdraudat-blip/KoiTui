package com.freeslot.backend.service;

import com.freeslot.backend.dto.FreeSlotResponse;
import com.freeslot.backend.entity.Routine;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeSlotService {

    private final RoutineRepository routineRepository;
    private final UserService userService;

    // Bangladesh-style academic week. Change this if your university's week is different.
    private static final List<String> DAYS = Arrays.asList(
            "SATURDAY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY"
    );

    /**
     * Core algorithm (matches the guideline):
     * 1. Represent each day's periods with 0 (free) and 1 (busy) for every user.
     * 2. Compare all selected users period-by-period.
     * 3. If every user has 0 (free) at that period, it's a common free slot.
     */
    public List<FreeSlotResponse> findCommonFreeSlots(String email, List<Long> friendIds) {
        User currentUser = userService.getUserByEmail(email);

        List<Long> userIds = new ArrayList<>();
        userIds.add(currentUser.getId());
        if (friendIds != null) {
            for (Long id : friendIds) {
                if (!userIds.contains(id)) {
                    userIds.add(id);
                }
            }
        }

        List<Routine> allRoutines = routineRepository.findByUserIdIn(userIds);

        // busyGrid[userIndex][dayIndex][period-1] = true if that user is busy at that slot
        boolean[][][] busyGrid = new boolean[userIds.size()][DAYS.size()][PeriodSchedule.PERIODS_PER_DAY];

        for (Routine r : allRoutines) {
            int userIndex = userIds.indexOf(r.getUserId());
            int dayIndex = DAYS.indexOf(r.getDay().toUpperCase());
            int periodIndex = r.getPeriod() - 1;

            if (userIndex == -1 || dayIndex == -1 || periodIndex < 0 || periodIndex >= PeriodSchedule.PERIODS_PER_DAY) {
                continue; // skip malformed / out-of-range entries instead of crashing
            }
            busyGrid[userIndex][dayIndex][periodIndex] = true;
        }

        List<FreeSlotResponse> freeSlots = new ArrayList<>();

        for (int dayIndex = 0; dayIndex < DAYS.size(); dayIndex++) {
            for (int period = 0; period < PeriodSchedule.PERIODS_PER_DAY; period++) {
                boolean allFree = true;
                for (int u = 0; u < userIds.size(); u++) {
                    if (busyGrid[u][dayIndex][period]) {
                        allFree = false;
                        break;
                    }
                }
                if (allFree) {
                    int periodNumber = period + 1;
                    freeSlots.add(new FreeSlotResponse(DAYS.get(dayIndex), periodNumber, PeriodSchedule.labelFor(periodNumber)));
                }
            }
        }

        return freeSlots;
    }
}
