package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.ShiftAdjustmentApply;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftChangePreview;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.util.BEClientRestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftAdjustmentRequestService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public ShiftAdjustmentRequestService(BEClientRestUtil client) {
        this.client = client;
    }

    // ---- 申請 ----
    public boolean applyShiftAdjustment(ShiftAdjustmentApply req) {
        String url = backEndDomain + API.SHIFT_ADJUSTMENT_APPLY.getPath();

        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Object> resp = client.doPostJson(url, null, req, responseType);
        return resp != null;
    }

    // ---- 查詢個人申請清單 ----
    public List<ShiftAdjustmentRequestVO> currentEmployeeShiftAdjustments() {
        String url = backEndDomain + API.CURRENT_EMPLOYEE_SHIFT_ADJUSTMENTS.getPath();

        ParameterizedTypeReference<ApiResponse<List<ShiftAdjustmentRequestVO>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftAdjustmentRequestVO>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<ShiftAdjustmentRequestVO> getPendingShiftAdjustments() {
        String url = backEndDomain + API.GET_PENDING_SHIFT_ADJUSTMENTS.getPath();

        ParameterizedTypeReference<ApiResponse<List<ShiftAdjustmentRequestVO>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftAdjustmentRequestVO>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<Byte>> getShiftAdjustmentRequestApprovalStage() {
        String url = backEndDomain + API.GET_SHIFT_ADJUSTMENT_REQUEST_APPROVAL_STAGE.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Byte>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Byte>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    protected List<Option<Byte>> getShiftAdjustmentRequestStatus() {
        String url = backEndDomain + API.GET_SHIFT_ADJUSTMENT_REQUEST_STATUS.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Byte>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Byte>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    // ---- 查詢班表 ----
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



    // ---- MenuOption ----
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

    // 公司下拉
//    public List<Option<String>> getCompanyOptionsList() {
//        String url = backEndDomain + API.GET_COMPANY_TYPE_OPTIONS.getPath();
//        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
//        };
//        ApiResponse<List<Option<String>>> response = client.doPostJson(url, null, null, responseType);
//        if (response != null) {
//            return response.getData();
//        }
//        return new ArrayList<>();
//    }

    // 取得員工清單 下拉選單
    public List<Option<Integer>> getEmployeeOptionList() {
        String url = backEndDomain + API.GET_EMPLOYEE_OPTIONS.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    private enum API {
        //調班申請
        SHIFT_ADJUSTMENT_APPLY("/shiftAdjustmentRequest/apply", HttpMethod.POST, MediaType.APPLICATION_JSON),
        CURRENT_EMPLOYEE_SHIFT_ADJUSTMENTS("/shiftAdjustmentRequest/currentEmployeeShiftAdjustments", HttpMethod.GET, null),
        GET_PENDING_SHIFT_ADJUSTMENTS("/shiftAdjustmentRequest/getPendingShiftAdjustments", HttpMethod.GET, null),
        SHIFT_ADJUSTMENT_APPROVE("/shiftAdjustmentRequest/approve", HttpMethod.POST, MediaType.APPLICATION_JSON),
        SHIFT_ADJUSTMENT_REJECT("/shiftAdjustmentRequest/reject", HttpMethod.POST, MediaType.APPLICATION_JSON),

        // ---- shiftSchedules ----
        QUERY_SHIFT_SCHEDULES("/shiftSchedules/queryByMonthAndDepartment", HttpMethod.GET, null),
        QUERY_SCHEDULE_PERIODS("/shiftSchedules/getShiftSchedulePeriods", HttpMethod.GET, null),
        GET_SHIFT_HOLIDAY_TYPES("/shiftSchedules/getShiftAndHolidayConfig", HttpMethod.GET, null),


        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),
        GET_SHIFT_ADJUSTMENT_REQUEST_APPROVAL_STAGE("/enum/getShiftAdjustmentRequestApprovalStage",HttpMethod.GET, null),
        GET_SHIFT_ADJUSTMENT_REQUEST_STATUS("/enum/getShiftAdjustmentRequestStatus", HttpMethod.GET, null),


//        GET_COMPANY_TYPE_OPTIONS("/enum/getCompanyType", HttpMethod.GET, null),


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


    public List<ShiftSchedulesQueryVO> deepCopySchedule(List<ShiftSchedulesQueryVO> src){
        if (src == null) return List.of();
        List<ShiftSchedulesQueryVO> cloned = new ArrayList<>();
        for (ShiftSchedulesQueryVO vo : src) {
            ShiftSchedulesQueryVO copy = new ShiftSchedulesQueryVO();
            copy.setEmployeeId(vo.getEmployeeId());
            copy.setEmployeeNumber(vo.getEmployeeNumber());
            copy.setNickName(vo.getNickName());
            copy.setDepartmentId(vo.getDepartmentId());
            copy.setDepartmentName(vo.getDepartmentName());

            List<ShiftSchedulesDateTimeQueryVO> datesCopy = vo.getSchedulesDates().stream()
                    .map(sd -> {
                        ShiftSchedulesDateTimeQueryVO s = new ShiftSchedulesDateTimeQueryVO();
                        s.setId(sd.getId());
                        s.setShiftDate(sd.getShiftDate());
                        s.setShiftTypes(sd.getShiftTypes());
                        s.setShiftColorCode(sd.getShiftColorCode());
                        s.setStatus(sd.getStatus());
                        s.setActionType(sd.getActionType());
                        return s;
                    })
                    .collect(Collectors.toList());
            copy.setSchedulesDates(datesCopy);

            cloned.add(copy);
        }
        return cloned;
    }

    /**
     *將時間區間內的shiftSchedule裝填成各別員工的排班物件
     */
    public List<ShiftSchedulesQueryVO> convertToShiftSchedulesQueryVO(List<ShiftSchedules> shiftSchedules) {
        // 按 employeeId 分組
        Map<Integer, List<ShiftSchedules>> groupedByEmployee = shiftSchedules.stream()
                .collect(Collectors.groupingBy(ShiftSchedules::getEmployeeId));

        // 轉換成 List<ShiftSchedulesQueryVO>
        List<ShiftSchedulesQueryVO> vos = new ArrayList<>();

        for (Map.Entry<Integer, List<ShiftSchedules>> entry : groupedByEmployee.entrySet()) {
            Integer employeeId = entry.getKey();
            List<ShiftSchedules> employeeSchedules = entry.getValue();
            if (employeeSchedules.isEmpty()) continue;

            ShiftSchedules first = employeeSchedules.get(0); // 取第一個記錄的部門ID

            // 建立 ShiftSchedulesQueryVO 對像
            ShiftSchedulesQueryVO vo = new ShiftSchedulesQueryVO();
            vo.setEmployeeId(employeeId);
            vo.setNickName(first.getNickName());
            vo.setDepartmentId(first.getDepartmentId());
            vo.setDepartmentName(first.getDepartmentName());
            vo.setId(first.getId());
            vo.setEmployeeNumber(first.getEmployeeNumber());
            // 將 ShiftSchedules 轉換為 ShiftSchedulesDateTime 列表
            List<ShiftSchedulesDateTimeQueryVO> dates = employeeSchedules.stream()
                    .sorted(Comparator.comparing(ShiftSchedules::getShiftDate)) // 按 shiftDate 排序
                    .map(schedule -> {
                        ShiftSchedulesDateTimeQueryVO d = new ShiftSchedulesDateTimeQueryVO();
                        d.setId(schedule.getId());
                        d.setShiftTypes(schedule.getShiftTypes());
                        d.setShiftDate(schedule.getShiftDate());
                        d.setStatus(schedule.getStatus());
                        d.setWeekType(schedule.getWeekType());
                        d.setRemark(schedule.getRemark());
                        d.setActionType(schedule.getActionType());
                        d.setShiftColorCode(schedule.getShiftColorCode());
                        return d;
                    }).collect(Collectors.toList());
            vo.setSchedulesDates(dates);
            vos.add(vo);
        }
        // sort by employee id
        vos.sort(Comparator.comparingInt(ShiftSchedulesQueryVO::getEmployeeId));
        return vos;
    }

    /**
     * 由快取資料 originalMonthSchedules 內，找出「某員工在某日期」的班別 key。
     */
    public String findShiftKeyByEmpAndDate(Integer employeeId, LocalDate date, List<ShiftSchedulesQueryVO> baseShift) {
        if (employeeId == null || date == null) {
            return null;
        }
        return baseShift.stream()
                .filter(vo -> Objects.equals(vo.getEmployeeId(), employeeId))
                .findFirst()
                .map(ShiftSchedulesQueryVO::getSchedulesDates)
                .orElseGet(List::of)
                .stream()
                .filter(sd -> Objects.equals(sd.getShiftDate(), date))
                .map(ShiftSchedulesDateTimeQueryVO::getShiftTypes)
                .findFirst()
                .orElse(null);
    }

    /**
     * 將changeList中對應date的shiftKey更新至baseList。
     */
    public void applyPreviewOnSchedulesInPlace(
            List<ShiftSchedulesQueryVO> base,
            List<ShiftChangePreview> changes,
            Map<String, ShiftType> shiftTypeMap
    ) {
        Map<Integer, ShiftSchedulesQueryVO> byEmp = base.stream()
                .filter(v -> v.getEmployeeId() != null)
                .collect(Collectors.toMap(ShiftSchedulesQueryVO::getEmployeeId, v -> v, (a, b) -> a));

        for (ShiftChangePreview c : changes) {
            ShiftSchedulesQueryVO vo = byEmp.get(c.getEmployeeId());
            if (vo == null) {
                continue;
            }
            for (ShiftSchedulesDateTimeQueryVO sd : vo.getSchedulesDates()) {
                if (Objects.equals(sd.getShiftDate(), c.getDate())) {
                    sd.setShiftTypes(c.getToShiftKey());
                    sd.setShiftColorCode(resolveShiftColorCode(c.getToShiftKey(), shiftTypeMap));
                    break;
                }
            }
        }
    }

    public String resolveShiftColorCode(String shiftKey, Map<String, ShiftType> shiftTypeMap) {
        ShiftType shiftType = shiftTypeMap.get(shiftKey);
        String color = "#eeeeee";
        if (shiftType != null) {
            color = Optional.ofNullable(shiftType.getShiftColorCode()).orElse("#eeeeee");
//            try {
//                var m = shiftType.getClass().getMethod("getShiftColorCode");
//                Object v = m.invoke(shiftType);
//                if (v != null) {
//                    color = String.valueOf(v);
//                }
//            } catch (Exception ignore) {
//            }
        }
        return color;
    }

    /**
     * 由快取資料 originalMonthSchedules 找出「某員工在某日期」對應的原班表 ID。
     */
    public Long findOriginScheduleId(Integer employeeId, LocalDate date, List<ShiftSchedulesQueryVO> baseShift) {
        Integer id = baseShift.stream()
                .filter(vo -> Objects.equals(vo.getEmployeeId(), employeeId))
                .findFirst()
                .map(ShiftSchedulesQueryVO::getSchedulesDates)
                .orElseGet(List::of)
                .stream()
                .filter(sd -> Objects.equals(sd.getShiftDate(), date))
                .map(ShiftSchedulesDateTimeQueryVO::getId)
                .findFirst()
                .orElse(null);
        return id == null ? null : id.longValue();
    }

}
