package com.gdut.sms.common.utils;

import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class DateTimeUtils {

    public static long toTimestamp(LocalDateTime dt) {
        return dt != null ? dt.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() : 0;
    }

    public static LocalDateTime startOfMonth() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime endOfMonth() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    public static LocalDateTime firstTimeOfTheYear(int year) {
        return LocalDate.of(year, 1, 1).atStartOfDay();
    }

    public static LocalDateTime lastTimeOfTheYear(int year) {
        boolean isCurrentYear = year == LocalDate.now().getYear();
        return LocalDate.of(year,
                isCurrentYear ? LocalDate.now().getMonthValue() : 12,
                isCurrentYear ? LocalDate.now().getDayOfMonth() : 31
        ).atTime(isCurrentYear ? LocalTime.now() : LocalTime.of(23, 59, 59));
    }

}
