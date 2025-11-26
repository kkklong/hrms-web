package com.hrm.application.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShiftAdjustmentRequestVO implements Serializable {


    /**
     * 申請單ID(自動生成)
     */
    private Long id;
    /**
     * 申請人ID
     */
    private Integer applicantId;
    /**
     * 申請人原班表ID (shift_schedules.id)
     */
    private Integer originScheduleId;
    /**
     * 目標日期
     */
    private LocalDate targetDate;
    /**
     * 目標班別
     */
    private String targetShiftTypes;
    /**
     * 目標部門ID，NULL 表同部門
     */
    private Integer targetDepartmentId;
    /**
     * 互換對象的班表ID，如無互換則NULL
     */
    private Integer counterpartScheduleId;
    /**
     * 目標班表的員工ID
     */
    private Integer targetEmployeeId;
    /**
     * 申請原因
     */
    private String reason;
    /**
     * 狀態：0草稿,1審核中,2通過,3駁回,4取消
     */
    private Byte status;
    /**
     * 審核流程快照
     */
    private String approvalContext;
    /**
     * 申請時間
     */
    private LocalDateTime createdAt;

    /**
     * 記錄每個階段的操作過程
     */
    private String historyReview;
}
