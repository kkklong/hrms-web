package com.hrm.application.model.bo;

import com.hrm.application.entity.RawAttendanceRecords;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateAttendanceRequest implements Serializable {
    private List<RawAttendanceRecords> vos;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
