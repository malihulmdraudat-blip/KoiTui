package com.freeslot.backend.service;

import java.util.List;

/**
 * Maps each period number (1-7) to the actual class time at this university.
 * If your class times ever change, this is the ONLY place you need to edit -
 * the frontend pulls these same labels from the API responses instead of
 * hardcoding them a second time.
 */
public final class PeriodSchedule {

    private PeriodSchedule() {
        // utility class, not meant to be instantiated
    }

    public static final int PERIODS_PER_DAY = 7;

    // Index 0 = period 1, index 1 = period 2, etc.
    private static final List<String> PERIOD_LABELS = List.of(
            "8:00 AM - 9:20 AM",   // period 1
            "9:30 AM - 10:50 AM",  // period 2
            "11:00 AM - 12:20 PM", // period 3
            "12:30 PM - 1:50 PM",  // period 4
            "2:00 PM - 3:20 PM",   // period 5
            "3:30 PM - 4:50 PM",   // period 6
            "5:00 PM - 6:20 PM"    // period 7
    );

    public static String labelFor(int period) {
        if (period < 1 || period > PERIODS_PER_DAY) {
            return "Unknown period";
        }
        return PERIOD_LABELS.get(period - 1);
    }
}
