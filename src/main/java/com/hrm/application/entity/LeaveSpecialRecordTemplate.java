package com.hrm.application.entity;

import lombok.Data;

@Data
public class LeaveSpecialRecordTemplate {

    private Integer id;

    private String chineseName;

    private String leaveTypes;

    private String salaryStandard;

    private Boolean fullAttendanceBonus;

    private Float minLeaveUnit;

    private Integer maxLeaveDays;

    private String calculationPeriod;

    private Boolean continuousLeave;

    private Boolean advanceApplication;

    private Float yearData;

    private Boolean attachmentRequired;
}

