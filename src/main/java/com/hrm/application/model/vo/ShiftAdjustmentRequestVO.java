package com.hrm.application.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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
     * 申請人姓名
     */
    private String applicantName;

    /**
     * 申請人部門ID
     */
    private Integer departmentId;

    /**
     * <申請人原班表ID (shift_schedules.id), 目標班別>
     */
    private Map<Integer, String> shiftMap;
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

    /**
     * 是否能被當前登入者審核
     */
    private Boolean eligibleForApproval;

    /**
     * 原始班別明細
     */
    private Map<Integer, String> originalShiftMap;
}
