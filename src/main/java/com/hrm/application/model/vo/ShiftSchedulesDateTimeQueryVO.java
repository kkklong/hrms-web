package com.hrm.application.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ShiftSchedulesDateTimeQueryVO implements Serializable {

    /**
     * 排班ID(自動產生)
     */
    private Integer id;

    /**
     * 班別，如：日班、夜班等
     */
    private String shiftTypes;

    /**
     * 排班的具體日期
     */
    private LocalDate shiftDate;

    /**
     * 排班狀態，0：上班，1：請假
     */
    private Byte status;

    /**
     * 周別型別，1 表示第1周，2 表示第2周
     */
    private Byte weekType;

    /**
     * 備註，記錄額外資訊，如特殊說明等
     */
    private String remark;

    /**
     * 班別修改狀態 0: 可修改、1: 不可修改
     */
    private Byte actionType;

    /**
     * 班別色碼
     */
    private String shiftColorCode;
}
