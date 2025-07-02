package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.ResetPassword;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.FileDownloadUtil;
import com.hrm.application.util.NotificationUtil;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShiftScheduleService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public ShiftScheduleService(BEClientRestUtil client) {
        this.client = client;
    }

    //查詢班表
    public List<ShiftSchedules> queryShiftSchedules(String startDate, String endDate, Integer departmentId) {
        String url = backEndDomain + API.QUERY_SHIFT_SCHEDULES.getPath();

        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("departmentId", departmentId);

        ParameterizedTypeReference<ApiResponse<List<ShiftSchedules>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftSchedules>> response = client.doGet(url, null, queryParams, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    //查詢班表轉成VO
    public List<ShiftSchedulesQueryVO> queryShiftSchedulesVO(String startDate, String endDate, Integer departmentId) {
        String url = backEndDomain + API.QUERY_SHIFT_SCHEDULES.getPath();

        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("departmentId", departmentId);

        ParameterizedTypeReference<ApiResponse<List<ShiftSchedules>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftSchedules>> response = client.doGet(url, null, queryParams, responseType);
        if (response != null && response.getData() != null) {
            List<ShiftSchedulesQueryVO> shiftSchedulesVOList = this.convertToShiftSchedulesQueryVO(response.getData());
            return shiftSchedulesVOList;
        }
        return new ArrayList<>();
    }

    //取得班別及假日配置
    public List<ShiftType> getShiftAndHolidayConfigList() {
        String url = backEndDomain + API.GET_SHIFT_HOLIDAY_TYPES.getPath();

        ParameterizedTypeReference<ApiResponse<List<ShiftType>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftType>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
//            response.getData().forEach(option ->
//            log.info("Option - Value: {}, Label: {}", option.getShiftKey(), option.getShiftName())
//            );
            return response.getData();
        }
        return new ArrayList<>();
    }

    //取得部門清單
    public List<Option<Integer>> getDepartmentOptionList() {
        String url = backEndDomain + API.GET_DEPARTMENT_OPTIONS.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    //查詢各個排班區間
    public List<ShiftSchedulePeriod> getShiftSchedulePeriods(String startDate, String endDate) {
        String url = backEndDomain + API.QUERY_SCHEDULE_PERIODS.getPath();

        ParameterizedTypeReference<ApiResponse<List<ShiftSchedulePeriod>>> responseType = new ParameterizedTypeReference<>() {
        };
        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);

        ApiResponse<List<ShiftSchedulePeriod>> response = client.doGet(url, null, queryParams, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // 儲存員工排班
    public boolean savePersonalShiftSchedules(List<ShiftSchedules> personalShiftSchedules) {
        String url = backEndDomain + API.UPDATE_PERSONAL_SHIFT_SCHEDULES.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response =  client.doPostJson(url,null, personalShiftSchedules, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    // 手動調整排班
    public boolean manuallyAdjustShiftSchedules(List<ShiftSchedules> manualShiftSchedules){
        String url = backEndDomain + API.UPDATE_PERSONAL_SHIFT_SCHEDULES.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response =  client.doPostJson(url,null, manualShiftSchedules, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    // 導出班表
    public StreamResource exportShiftSchedules(String shiftDate, Integer departmentId, String fileName) {
        String url = backEndDomain + API.DOWNLOAD_SHIFT_SCHEDULES.getPath();
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("shiftDate", shiftDate);
        queryParams.put("departmentId", departmentId);
        return FileDownloadUtil.getFile(url, null, queryParams, fileName);
    }


    /**
     * 接口信息
     */
    private enum API {

        GET_SHIFT_HOLIDAY_TYPES("/shiftSchedules/getShiftAndHolidayConfig", HttpMethod.GET, null),
        QUERY_SHIFT_SCHEDULES("/shiftSchedules/queryByMonthAndDepartment", HttpMethod.GET, null),
        QUERY_SCHEDULE_PERIODS("/shiftSchedules/getShiftSchedulePeriods", HttpMethod.GET, null),
        UPDATE_PERSONAL_SHIFT_SCHEDULES("/shiftSchedules/savePersonalShiftSchedules", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_OTHERS_SHIFT_SCHEDULES("/shiftSchedules/manuallyAdjustShiftSchedules", HttpMethod.POST, MediaType.APPLICATION_JSON),

        // ---- department ----
        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),

        // ---- report ----
        DOWNLOAD_SHIFT_SCHEDULES("/report/download/shiftSchedules", HttpMethod.GET, null),



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

    /**
     * 處理ShiftSchedulesQueryVO跟ShiftSchedulesDateTimeQueryVO
     */
    public Map<LocalDate, ShiftSchedulesDateTimeQueryVO> mapSchedulesDateByDate(ShiftSchedulesQueryVO shiftSchedules) {
        if (shiftSchedules == null || shiftSchedules.getSchedulesDates() == null) {
            return Map.of(); // 回傳空 Map 如果資料為空
        }
        return shiftSchedules.getSchedulesDates().stream()
                .collect(Collectors.toMap(
                        ShiftSchedulesDateTimeQueryVO::getShiftDate, // 使用 shiftDate 作為 key
                        schedule -> schedule,
                        (existing, replacement) -> existing         // 如果有重複的 key，保留現有的值
                ));
    }

    /**
    *將時間區間內的shiftSchedule裝填成各別員工的排班物件
    */
    public List<ShiftSchedulesQueryVO> convertToShiftSchedulesQueryVO(List<ShiftSchedules> shiftSchedules) {
        // 按 employeeId 分組
        Map<Integer, List<ShiftSchedules>> groupedByEmployee = shiftSchedules.stream()
                .collect(Collectors.groupingBy(ShiftSchedules::getEmployeeId));

        // 轉換成 List<ShiftSchedulesQueryVO>
        List<ShiftSchedulesQueryVO> shiftSchedulesQueryVOList = new ArrayList<>();

        for (Map.Entry<Integer, List<ShiftSchedules>> entry : groupedByEmployee.entrySet()) {
            Integer employeeId = entry.getKey();
            List<ShiftSchedules> employeeSchedules = entry.getValue();

            // 建立 ShiftSchedulesQueryVO 對像
            ShiftSchedulesQueryVO shiftSchedulesQueryVO = new ShiftSchedulesQueryVO();
            shiftSchedulesQueryVO.setEmployeeId(employeeId);
            shiftSchedulesQueryVO.setNickName(employeeSchedules.get(0).getNickName());
            shiftSchedulesQueryVO.setDepartmentId(employeeSchedules.get(0).getDepartmentId()); // 取第一個記錄的部門ID
            shiftSchedulesQueryVO.setDepartmentName(employeeSchedules.get(0).getDepartmentName());
            shiftSchedulesQueryVO.setId(employeeSchedules.get(0).getId()); // 設定第一個記錄的ID（可根據業務需求調整）
            // 將 ShiftSchedules 轉換為 ShiftSchedulesDateTime 列表
            List<ShiftSchedulesDateTimeQueryVO> schedulesDates = employeeSchedules.stream()
                    .sorted(Comparator.comparing(ShiftSchedules::getShiftDate)) // 按 shiftDate 排序
                    .map(schedule -> {
                        ShiftSchedulesDateTimeQueryVO scheduleDateTimeQueryVO = new ShiftSchedulesDateTimeQueryVO();
                        scheduleDateTimeQueryVO.setId(schedule.getId());
                        scheduleDateTimeQueryVO.setShiftTypes(schedule.getShiftTypes());
                        scheduleDateTimeQueryVO.setShiftDate(schedule.getShiftDate());
                        scheduleDateTimeQueryVO.setStatus(schedule.getStatus());
                        scheduleDateTimeQueryVO.setWeekType(schedule.getWeekType());
                        scheduleDateTimeQueryVO.setRemark(schedule.getRemark());
                        scheduleDateTimeQueryVO.setActionType(schedule.getActionType());
                        scheduleDateTimeQueryVO.setShiftColorCode(schedule.getShiftColorCode());
                        return scheduleDateTimeQueryVO;
                    }).collect(Collectors.toList());
            shiftSchedulesQueryVO.setSchedulesDates(schedulesDates);
            shiftSchedulesQueryVOList.add(shiftSchedulesQueryVO);
        }
        // sort by employee id
        shiftSchedulesQueryVOList.sort(Comparator.comparingInt(ShiftSchedulesQueryVO::getEmployeeId));
        return shiftSchedulesQueryVOList;
    }

    /**
     *將shiftSchedulesQueryVO轉回ShiftSchedulesList
     */
    public List<ShiftSchedules> convertToShiftSchedules(ShiftSchedulesQueryVO shiftSchedulesQueryVO) {
        List<ShiftSchedules> shiftSchedulesList = new ArrayList<>();

        Integer employeeId = shiftSchedulesQueryVO.getEmployeeId();
        String nickName = shiftSchedulesQueryVO.getNickName();
        Integer departmentId = shiftSchedulesQueryVO.getDepartmentId();
        String departmentName = shiftSchedulesQueryVO.getDepartmentName();

        // 遍歷 schedulesDates，將每個 ShiftSchedulesDateTimeQueryVO 轉換為 ShiftSchedules
        for (ShiftSchedulesDateTimeQueryVO dateTimeVO : shiftSchedulesQueryVO.getSchedulesDates()) {
            ShiftSchedules shiftSchedule = new ShiftSchedules();
            shiftSchedule.setId(dateTimeVO.getId());
            shiftSchedule.setEmployeeId(employeeId);
            shiftSchedule.setNickName(nickName);
            shiftSchedule.setDepartmentId(departmentId);
            shiftSchedule.setDepartmentName(departmentName);
            shiftSchedule.setShiftTypes(dateTimeVO.getShiftTypes());
            shiftSchedule.setShiftDate(dateTimeVO.getShiftDate());
            shiftSchedule.setStatus(dateTimeVO.getStatus());
            shiftSchedule.setWeekType(dateTimeVO.getWeekType());
            shiftSchedule.setRemark(dateTimeVO.getRemark());
            shiftSchedule.setActionType(dateTimeVO.getActionType());
            shiftSchedule.setShiftColorCode(dateTimeVO.getShiftColorCode());
            shiftSchedulesList.add(shiftSchedule);
        }
        return shiftSchedulesList;
    }


}
