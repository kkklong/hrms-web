package com.hrm.application.service;

import com.hrm.application.entity.*;
import com.hrm.application.model.bo.LeaveRecordBO;
import com.hrm.application.model.bo.LeaveRecordDateTimeBO;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.ToolUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaveService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public LeaveService(BEClientRestUtil client) {
        this.client = client;
    }

    //    private final ApiService apiService;
//
//    public LeaveService(ApiService apiService) {
//        this.apiService = apiService;
//    }
//
//
//    // API----------------------------------------------------------------------------------------------------
//
//protected List<LeaveRecord> getCurrentLeaveRecordList() {
//    String url = backEndDomain + API.GET_CURRENT_LEAVE_RECORDS.getPath();
//    ParameterizedTypeReference<ApiResponse<List<LeaveRecord>>> responseType = new ParameterizedTypeReference<>() {
//    };
//
//    ApiResponse<List<LeaveRecord>> response = requestData(api, responseType);
//    if (response != null && response.getData() != null) {
//        return response.getData();
//    }
//    return new ArrayList<>();
//}


//    public List<LeaveRecord> getLeaveRecord() {
//        return combineDatesToLeaveRecords(apiService.getCurrentLeaveRecordList());
//    }
//
//    public List<LeaveSpecialRecord> queryCurrentEmployeeLeaveSpecialRecords() {
//        return apiService.queryCurrentLeaveSpecialRecordList();
//    }
//
//    public boolean add(LeaveRecordBO leave, byte[] fileBytes, String fileName) {
//        return apiService.createLeaveRecord(leave, fileBytes == null ? null : new NameAddedByteArrayResource(fileName, fileBytes));
//    }
//
//    public boolean cancel(LeaveRecord leave) {
//        return apiService.cancelLeaveRecord(leave);
//    }
//
//    public boolean update(Integer leaveRecordsId, byte[] fileBytes, String fileName) {
//        return apiService.updateLeaveRecordFile(leaveRecordsId, fileBytes == null ? null : new NameAddedByteArrayResource(fileName, fileBytes));
//    }
//
//    public Double getAvailableHours(Integer leaveSpecialRecordsId) {
//        return apiService.queryAvailableHour(leaveSpecialRecordsId);
//    }
//
//    public List<Option<String>> getShiftTypeOptionList() {
//        return apiService.getShiftTypeOptionList();
//    }
//
//    public List<ShiftType> getShiftTypeList() {
//        return apiService.getShiftTypeList();
//    }
//
//    public List<Option<String>> getLeaveTypeOptionList() {
//        return apiService.getLeaveTypeOptionList();
//    }
//
//    public List<Option<Byte>> getLeaveStatusList() {
//        return apiService.getLeaveStatusOptionList();
//    }
//
//    public List<Option<Byte>> getApprovalStageList() {
//        return apiService.getApprovalStageOptionList();
//    }
//
//    //審核
//    public List<LeaveRecord> getPendingLeaveRecord() {
//        return combineDatesToLeaveRecords(apiService.getPendingLeaveRecordList());
//    }
//
//    public boolean approve(LeaveRecord leave) {
//        return apiService.approveLeaveRecord(leave);
//    }
//
//    public boolean reject(LeaveRecord leave) {
//        return apiService.rejectLeaveRecord(leave);
//    }


    private List<LeaveRecord> combineDatesToLeaveRecords(List<LeaveRecord> leaveRecordList) {
        for (LeaveRecord leaveRecord : leaveRecordList) {
            List<LeaveRecordDateTime> leaveDates = leaveRecord.getLeaveDates();

            if (leaveDates == null || leaveDates.isEmpty()) {
                continue;
            }

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            for (LeaveRecordDateTime dateTime : leaveDates) {
                LocalDateTime dateTimeStart = dateTime.getStartDate();
                LocalDateTime dateTimeEnd = dateTime.getEndDate();
                startDate = (startDate == null || startDate.isAfter(dateTimeStart)) ? dateTimeStart : startDate;
                endDate = (endDate == null || endDate.isBefore(dateTimeEnd)) ? dateTimeEnd : endDate;
            }
            leaveRecord.setStartDate(startDate);
            leaveRecord.setEndDate(endDate);
        }
        return leaveRecordList;
    }

    // 計算----------------------------------------------------------------------------------------------------

    /**
     * 確認班別是否有表定休息時間
     */
    public boolean checkRegularBreakTime(ShiftType type) {
        return type != null && type.getLunchStartTime() != null && type.getLunchEndTime() != null;
    }

    /**
     * 確認假別是否適用請假半天扣休息時數
     */
    public boolean checkValidForHalfDayRestOffset(LeaveSpecialRecord leaveType) {
        return leaveType != null && leaveType.getLeaveTypes() != null && leaveType.getMinLeaveUnit() == 4;
    }

    private Duration getFixDuration(ShiftType type) {
        return Duration.between(type.getStartTime(), LocalTime.of(9, 0));
    }

    private double getHoursWithMinutes(Temporal startDateTime, Temporal endDateTime) {
        return ToolUtil.getHoursWithMinutes(startDateTime, endDateTime);
    }

    public List<LeaveRecordDateTimeBO> analyzeWorkDateTime(ShiftType shift, LeaveSpecialRecord leaveType, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean startRest, Boolean endRest) {
        ArrayList<LeaveRecordDateTimeBO> workTimeList = new ArrayList<>(splitToWorkTime(startDateTime, endDateTime, shift));
        return IntStream.range(0, workTimeList.size())
                .mapToObj(index -> completeDateTime(workTimeList.get(index), shift, leaveType,
                        index == 0 ? startRest : index == workTimeList.size() - 1 ? endRest : true))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<LeaveRecordDateTimeBO> splitToWorkTime(LocalDateTime startTime, LocalDateTime endTime, ShiftType shiftType) {

        List<LeaveRecordDateTimeBO> result = new ArrayList<>();

        if (startTime == null || endTime == null || shiftType == null ||
                !startTime.isBefore(endTime)) {
            return result;
        }

        // 固定調整為日班時間來計算
        Duration fixDuration = getFixDuration(shiftType);
        LocalDateTime startTimeFix = startTime.plus(fixDuration);
        LocalDateTime endTimeFix = endTime.plus(fixDuration);
        LocalTime shiftStartTimeFix = shiftType.getStartTime().plus(fixDuration);
        LocalTime shiftEndTimeFix = shiftType.getEndTime().plus(fixDuration);

        LocalDateTime startFix = startTimeFix;
        while (startFix.isBefore(endTimeFix)) {

            LocalDateTime dayEndFix = LocalDateTime.of(startFix.toLocalDate(), LocalTime.of(23, 59, 59));
            LocalDateTime nextDayStartFix = dayEndFix.plusSeconds(1);

            LocalDateTime endFix = endTimeFix.isBefore(dayEndFix) ? endTimeFix : dayEndFix;

            LeaveRecordDateTimeBO workRimeRange = getWorkTimeRange(startFix, endFix, shiftStartTimeFix, shiftEndTimeFix, shiftType.getFlexibleWork());
            if (workRimeRange != null) {
                workRimeRange.setStartDate(workRimeRange.getStartDate().minus(fixDuration));
                workRimeRange.setEndDate(workRimeRange.getEndDate().minus(fixDuration));
                result.add(workRimeRange);
            }

            startFix = nextDayStartFix;
        }

        return result;
    }

    /**
     * 以日班邏輯將時間區段限縮為工作時段，input、output皆需為同日時間
     */
    private LeaveRecordDateTimeBO getWorkTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd, LocalTime shiftStartTime, LocalTime shiftEndTime, boolean flexible) {

        if (rangeStart == null || rangeEnd == null || shiftStartTime == null || shiftEndTime == null) {
            return null;
        }

        // 驗證時間區間先後，以及是否在同一天
        if (!rangeStart.isBefore(rangeEnd) || !rangeStart.toLocalDate().isEqual(rangeEnd.toLocalDate()) ||
                !shiftStartTime.isBefore(shiftEndTime)) {
            return null;
        }

        LocalTime rangeStartTime = rangeStart.toLocalTime();
        LocalTime rangeEndTime = rangeEnd.toLocalTime();

        // 是否使用彈性上下班
        if (flexible && getHoursWithMinutes(shiftEndTime, rangeEndTime) == 0.5 && getHoursWithMinutes(shiftStartTime, rangeStartTime) >= 0.5) {
            shiftStartTime = shiftStartTime.plusMinutes(30);
            shiftEndTime = shiftEndTime.plusMinutes(30);
        }

        // 驗證區間是否重疊
        if (!rangeStartTime.isBefore(shiftEndTime) || !rangeEndTime.isAfter(shiftStartTime)) {
            return null;
        }

        LocalTime workStartTime = rangeStartTime.isAfter(shiftStartTime) ? rangeStartTime : shiftStartTime;
        LocalTime workEndTime = rangeEndTime.isBefore(shiftEndTime) ? rangeEndTime : shiftEndTime;

        LeaveRecordDateTimeBO result = new LeaveRecordDateTimeBO();
        result.setStartDate(LocalDateTime.of(rangeStart.toLocalDate(), workStartTime));
        result.setEndDate(LocalDateTime.of(rangeStart.toLocalDate(), workEndTime));

        return result;
    }

    private LeaveRecordDateTimeBO completeDateTime(LeaveRecordDateTimeBO workRange, ShiftType shiftType, LeaveSpecialRecord leaveType,
                                                   boolean includeBreakTime) {


        if (workRange == null || workRange.getStartDate() == null || workRange.getEndDate() == null ||
                shiftType == null || leaveType == null) {
            return null;
        }

        LocalDateTime rangeStart = workRange.getStartDate();
        LocalDateTime rangeEnd = workRange.getEndDate();

        // 固定調整為日班時間來計算
        Duration fixDuration = getFixDuration(shiftType);
        LocalTime rangeStartTimeFix = rangeStart.plus(fixDuration).toLocalTime();
        LocalTime rangeEndTimeFix = rangeEnd.plus(fixDuration).toLocalTime();


        // 日班特殊處理
        if (checkRegularBreakTime(shiftType)) {

            // 固定調整為日班時間來計算
            LocalTime shiftStartFix = shiftType.getStartTime().plus(fixDuration);
            LocalTime shiftEndFix = shiftType.getEndTime().plus(fixDuration);
            LocalTime breakStartFix = shiftType.getLunchStartTime().plus(fixDuration);
            LocalTime breakEndFix = shiftType.getLunchEndTime().plus(fixDuration);

            // 假別最低請4小時，但班別前段只有3.5小時
            if (checkValidForHalfDayRestOffset(leaveType) && getHoursWithMinutes(shiftStartFix, breakStartFix) == 3.5 &&
                    // 請假區間為上班時間的第4~9小時
                    rangeEndTimeFix.equals(shiftEndFix) && getHoursWithMinutes(rangeStartTimeFix, rangeEndTimeFix) == 5) {
                includeBreakTime = true;
            } else {

                if (!rangeStartTimeFix.isBefore(breakStartFix) && !rangeEndTimeFix.isAfter(breakEndFix)) {
                    // 開始結束都在休息時間
                    return null;
                } else if (!rangeStartTimeFix.isBefore(breakStartFix) && !rangeStartTimeFix.isAfter(breakEndFix)) {
                    // 開始在休息時間
                    rangeStartTimeFix = breakEndFix;
                    includeBreakTime = false;
                } else if (!rangeEndTimeFix.isBefore(breakStartFix) && !rangeEndTimeFix.isAfter(breakEndFix)) {
                    // 結束在休息時間
                    rangeEndTimeFix = breakStartFix;
                    includeBreakTime = false;
                } else if (rangeStartTimeFix.isBefore(breakStartFix) && rangeEndTimeFix.isAfter(breakEndFix)) {
                    // 跨休息時間
                    includeBreakTime = true;
                } else {
                    includeBreakTime = false;
                }
            }
        }

        double hours = getHoursWithMinutes(rangeStartTimeFix, rangeEndTimeFix);
        double breakHours = includeBreakTime ? 1.0 : 0.0;
        if (breakHours >= hours) {
            return null;
        }

        LeaveRecordDateTimeBO result = new LeaveRecordDateTimeBO();
        result.setStartDate(LocalDateTime.of(workRange.getStartDate().toLocalDate(), rangeStartTimeFix).minus(fixDuration));
        result.setEndDate(LocalDateTime.of(workRange.getStartDate().toLocalDate(), rangeEndTimeFix).minus(fixDuration));
        result.setIncludesBreak(includeBreakTime);
        result.setShiftType(shiftType.getShiftKey());
        result.setCountVal(hours - breakHours);

        return result;
    }



    private enum API {



        NONE("", null, null);

        String path;
        HttpMethod method;
        MediaType type;

        API(String path, HttpMethod method, MediaType type) {
            this.path = path;
            this.method = method;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public MediaType getType() {
            return type;
        }

        public void setType(MediaType type) {
            this.type = type;
        }

    }
}
