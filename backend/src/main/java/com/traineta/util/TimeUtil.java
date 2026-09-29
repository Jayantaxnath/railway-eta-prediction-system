package com.traineta.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm[:ss]");

    public static int extractHourOfDay(LocalDateTime timestamp) {
        return timestamp != null ? timestamp.getHour() : 0;
    }

    public static int extractDayOfWeek(LocalDateTime timestamp) {
        return timestamp != null ? timestamp.getDayOfWeek().getValue() : 1;
    }

    /**
     * Calculates remaining scheduled minutes between currentTime and scheduledTimeStr (HH:mm).
     * Handles overnight transitions gracefully.
     */
    public static long calculateMinutesBetween(LocalDateTime currentTime, String scheduledTimeStr) {
        if (currentTime == null || scheduledTimeStr == null || scheduledTimeStr.trim().isEmpty()) {
            return 0L;
        }

        try {
            LocalTime scheduledTime = LocalTime.parse(scheduledTimeStr, TIME_FORMATTER);
            LocalDateTime targetDateTime = currentTime.with(scheduledTime);

            // If scheduled time is earlier today than current time, assume next day arrival
            if (targetDateTime.isBefore(currentTime)) {
                targetDateTime = targetDateTime.plusDays(1);
            }

            return Duration.between(currentTime, targetDateTime).toMinutes();
        } catch (Exception e) {
            return 0L;
        }
    }
}
