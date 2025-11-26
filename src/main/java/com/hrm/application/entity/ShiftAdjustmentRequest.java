package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShiftAdjustmentRequest implements Serializable {

    private Long id;

    private Integer applicantId;
    private Integer originScheduleId;

    private LocalDate targetDate;
    private String targetShiftTypes;
    private Integer targetDepartmentId;

    private Integer counterpartScheduleId;  // 可為 null
    private String reason;

    /**
     * 0草稿,1審核中,2通過,3駁回,4取消
     */
    private Integer status;

    /**
     * JSON（可用 String 映射）
     */
    private String approvalContext;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private String approvedBy;

    private Integer isOpen;
}
