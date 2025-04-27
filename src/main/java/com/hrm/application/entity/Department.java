package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer departmentParent;

    private String departmentName;

    private String description;

    private Integer managerId;

    private String managerNickName;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String createdId;

    private Integer updatedId;

    private String workType;

    private Integer everyDayMorningCount;

    private Integer everyDayAfternoonCount;

    private Integer everyDayNightCount;

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", departmentParent=" + departmentParent +
                ", departmentName='" + departmentName + '\'' +
                ", description='" + description + '\'' +
                ", managerId=" + managerId +
                ", managerNickName='" + managerNickName + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", createdId='" + createdId + '\'' +
                ", updatedId=" + updatedId +
                ", workType='" + workType + '\'' +
                ", everyDayMorningCount=" + everyDayMorningCount +
                ", everyDayAfternoonCount=" + everyDayAfternoonCount +
                ", everyDayNightCount=" + everyDayNightCount +
                '}';
    }
}
