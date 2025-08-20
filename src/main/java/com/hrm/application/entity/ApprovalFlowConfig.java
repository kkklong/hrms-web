package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ApprovalFlowConfig implements Serializable {


    private Integer id;

    /**
     * EMPLOYEE / DEPARTMENT / COMPANY / GLOBAL
     */
    private String scopeType;

    /**
     * 對應員工ID / 部門ID / 公司代碼 / "*"
     */
    private String scopeValue;

    /**
     * 簽核路徑設定（含 GE_24、LT_24 陣列）
     */
    private String flowJson;
    /**
     * 1 啟用 / 0 停用
     */
    private Integer active;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 最後更新時間
     */
    private LocalDateTime updatedAt;
}

