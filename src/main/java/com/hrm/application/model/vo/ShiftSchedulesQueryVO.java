package com.hrm.application.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShiftSchedulesQueryVO implements Serializable {

    /**
     * 排班ID(自動產生)
     */
    private Integer id;

    /**
     * 員工ID
     */
    private Integer employeeId;

    /**
     * 員工英文名
     */
    private String nickName;

    /**
     * 部門ID
     */
    private Integer departmentId;

    /**
     * 部門名稱
     */
    private String departmentName;


    private List<ShiftSchedulesDateTimeQueryVO> schedulesDates;

    public List<ShiftSchedulesDateTimeQueryVO> getSchedulesDates() {
        return schedulesDates;
    }

    public void setSchedulesDates(List<ShiftSchedulesDateTimeQueryVO> schedulesDates) {
        this.schedulesDates = schedulesDates;
    }
}
