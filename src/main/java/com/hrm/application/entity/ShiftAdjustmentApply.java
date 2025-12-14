package com.hrm.application.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ShiftAdjustmentApply implements Serializable {

    /**
     * <申請人原班表ID (shift_schedules.id), 目標班別>
     */
    private Map<Integer, String> shiftMap;

    /**
     * 申請原因
     */
    private String reason;

}