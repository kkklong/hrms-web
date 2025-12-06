package com.hrm.application.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.*;

@Slf4j
public class DateUtil {

    private static final String YEAR_REGEX   = "^\\d{4}$";
    private static final String MONTH_REGEX  = "^(0?[1-9]|1[0-2])$";
    private static final String DATE_REGEX   = "^([12][0-9]|3[01]|0?[1-9])$";
    private static final String HOUR_REGEX   = "^([0-1]?[0-9]|2[0-3])$";
    private static final String MINUTE_REGEX = "^[0-5][0-9]$";
    private static final String SECOND_REGEX = "^[0-5][0-9]$";

    @Getter
    @AllArgsConstructor
    public enum DatePattern {

        YYYY_MM("yyyy-MM",
                String.format("%s-%s", YEAR_REGEX, MONTH_REGEX)),
        YYYY_MM_DD("yyyy-MM-dd",
                String.format("%s-%s-%s", YEAR_REGEX, MONTH_REGEX, DATE_REGEX)),
        HH_mm_ss("HH:mm:ss",
                String.format("%s:%s:%s", HOUR_REGEX, MINUTE_REGEX, SECOND_REGEX)),
        YYYY_MM_DD_HH_mm("yyyy-MM-dd HH:mm",
                String.format("%s-%s-%s %s:%s", YEAR_REGEX, MONTH_REGEX, DATE_REGEX, HOUR_REGEX, MINUTE_REGEX)),
        YYYY_MM_DD_HH_mm_ss("yyyy-MM-dd HH:mm:ss",
                String.format("%s-%s-%s %s:%s:%s", YEAR_REGEX, MONTH_REGEX, DATE_REGEX, HOUR_REGEX, MINUTE_REGEX, SECOND_REGEX)),

        ;
        final String pattern, checkRegex;
    }

    @Getter
    @AllArgsConstructor
    public enum Zone {
        N_4(-4, ZoneOffset.of("-04:00"), "美東時間(無夏令時)"),
        P_8(8, ZoneOffset.of("+08:00"), "台灣時間")
        ;
        final int value;
        final ZoneId zoneId;
        final String description;
    }

    private static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                ;
    }

    private static DateTimeFormatter getDateTimeFormatter(String pattern, ZoneId zoneId) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(zoneId)
                ;
    }

    public static String format(LocalDateTime localDateTime, String pattern, ZoneId zoneId) {
        try {
            return getDateTimeFormatter(pattern)
                    .format(localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId));
        } catch (UnsupportedTemporalTypeException e) {
            log.error("dateTime:{} pattern{}", localDateTime, pattern, e);
            return null;
        }
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
}
