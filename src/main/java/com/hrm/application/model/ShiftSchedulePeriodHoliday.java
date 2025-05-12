package com.hrm.application.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftSchedulePeriodHoliday {
    private LocalDate shiftDate;

    private String remark;

    private String shiftTypes;
}