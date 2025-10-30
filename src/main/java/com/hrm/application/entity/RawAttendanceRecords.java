package com.hrm.application.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RawAttendanceRecords {

    /**
     * 員工帳號
     */
    private String account;

    /**
     * 首次打卡時間
     */
    private LocalDateTime firstCheckInTime;

    /**
     * 最後打卡時間
     */
    private LocalDateTime lastCheckOutTime;

    @Override
    public String toString() {
        return "RawAttendanceRecords{" +
                "account='" + account + '\'' +
                ", firstCheckInTime=" + firstCheckInTime +
                ", lastCheckOutTime=" + lastCheckOutTime +
                '}';
    }
}
