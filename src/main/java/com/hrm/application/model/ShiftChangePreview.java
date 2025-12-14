package com.hrm.application.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 暫存「調班預覽」用的資料模型。
 * Dialog 組裝；ShiftTimelineComponent 讀取並呈現預覽。
 */
@Data
public class ShiftChangePreview implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer employeeId;
    private LocalDate date;
    private String fromShiftKey;
    private String toShiftKey;

    public ShiftChangePreview(Integer employeeId, LocalDate date, String fromShiftKey, String toShiftKey) {

        this.employeeId = employeeId;
        this.date = date;
        this.fromShiftKey = fromShiftKey;
        this.toShiftKey = toShiftKey;
    }
}
