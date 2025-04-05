package com.hrm.application.entity;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ShiftType {

    private Integer id;

    private String shiftName;

    private String shiftKey;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime lunchStartTime;

    private LocalTime lunchEndTime;

    private Boolean flexibleWork;

    private String shiftColorCode;

    private String timeSlot;

    public String getShiftKey() {
        return shiftKey;
    }

    public void setShiftKey(String shiftKey) {
        this.shiftKey = shiftKey;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftColorCode() {
        return shiftColorCode;
    }
    public void setShiftColorCode(String shiftColorCode) {
        this.shiftColorCode = shiftColorCode;
    }

}