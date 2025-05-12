package com.hrm.application.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ShiftSchedulePeriod {
    private LocalDate startDate;

    private LocalDate endDate;

    private List<ShiftSchedulePeriodHoliday> holidays;
}
