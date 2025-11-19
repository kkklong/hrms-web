package com.hrm.application.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class LeaveSpecialRecordsVO2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer employeeId;

    private String nickName;

    private Integer departmentId;

    private String departmentName;

    private String leaveTypes;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String chineseName;

    private String salaryStandard;

    private Boolean fullAttendanceBonus;

    private Float minLeaveUnit;

    private Double maxLeaveDays;

    private Boolean continuousLeave;

    private Boolean advanceApplication;

    private String description;

    private String settlementDate;

    private Float settlementCount;

    private Boolean attachmentRequired;
}
