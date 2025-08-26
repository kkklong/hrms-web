package com.hrm.application.menu;

import com.hrm.application.views.approvalFlowConfig.ApprovalFlowConfigView;
import com.hrm.application.views.department.DepartmentView;
import com.hrm.application.views.employee.EmployeeView;
import com.hrm.application.views.notice.NoticeView;
import com.hrm.application.views.role.Role2View;
import com.hrm.application.views.role.RoleView;
import com.hrm.application.views.shift.ShiftSchedulesQueryView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum MenuLink {

//    DASHBOARD("首頁", "", DashboardView.class, VaadinIcon.DASHBOARD),

    AUTHORIZATION_MANAGE("權限管理", "0000", null, VaadinIcon.KEY),
    ROLE("角色權限", "000001", RoleView.class, VaadinIcon.MEDAL),


    MEMBER_MANAGE("人事資料管理", "0001", null, VaadinIcon.BUILDING),
    DEPARTMENT("部門資料", "000101", DepartmentView.class, VaadinIcon.GROUP),
    EMPLOYEE("員工資料", "000102", EmployeeView.class, VaadinIcon.USER),

    MESSAGE_MANAGE("資訊中心", "0002", null, VaadinIcon.SERVER),
    NOTICE("公告管理", "000201", NoticeView.class, VaadinIcon.MEGAPHONE),
//    NOTIFICATION("個人通知", "000202", NotificationView.class, VaadinIcon.BELL),

    PERFORMANCE_MANAGE("考勤管理", "0003", null, VaadinIcon.CLIPBOARD_TEXT),
//    LEAVE_TEMPLATE("假別模板", "000301", LeaveTemplateView.class, VaadinIcon.FLIGHT_TAKEOFF),
//    LEAVE_TYPE("員工假別", "000302", LeaveTypeView.class, VaadinIcon.GOLF),
//    LEAVE_APPROVE("請假審核", "000303", LeaveManagerView.class, VaadinIcon.CLIPBOARD_CHECK),
//    LEAVE_APPLY("請假申請", "000304", LeaveView.class, VaadinIcon.CLIPBOARD_USER),
//    PUNCH("打卡紀錄", "000305", RawAttendanceRecordsQueryView.class, VaadinIcon.PRINT),
//    ATTENDANCE("個人出勤", "000306", PersonalAttendanceRecordsView.class, VaadinIcon.OFFICE),
//    REMOTE_ATTENDANCE_MANAGE("遠端打卡管理", "000307", RemoteAttendanceManagerView.class, VaadinIcon.CLOUD),
//    SCHEDULING("個人排班", "000308", ShiftScheduleView.class, VaadinIcon.CALENDAR_USER),
//    SCHEDULING_MANAGE("排班管理", "000309", ShiftScheduleManagementView.class, VaadinIcon.CALENDAR_BRIEFCASE),
//    REMOTE_ATTENDANCE("遠端打卡", "000310", RemoteAttendanceView.class, VaadinIcon.CLOUD_UPLOAD),
//    SHIFT("班別管理", "000311", ShiftTypeConfigView.class, VaadinIcon.MOON),
//    OVERTIME_APPLY("個人加班", "000313", OvertimeView.class, VaadinIcon.USER_CLOCK),
//    OVERTIME_APPROVE("審核加班", "000314", OvertimeApprovalView.class, VaadinIcon.TIME_FORWARD),
    APPROVAL_FLOW_CONFIG("審核流程", "000315", ApprovalFlowConfigView.class, VaadinIcon.ARROWS_LONG_RIGHT),

    REPORT_MANAGE("報表管理", "0004", null, VaadinIcon.NEWSPAPER),
    //    EMPLOYEE_CARD_REPORT("員工名卡", "000401", EmployeeCardView.class, VaadinIcon.USER_CARD),
//    EMPLOYEE_ROSTER_REPORT("員工名冊", "000402", EmployeeRosterView.class, VaadinIcon.LINES_LIST),
//    ATTENDANCE_MANAGE("出勤管理", "000403", AttendanceRecordsManagerView.class, VaadinIcon.WORKPLACE),
//    ATTENDANCE_REPORT("考勤月報", "000404", AttendanceSummaryReportView.class, VaadinIcon.HOSPITAL),
    SHIFT_SCHEDULE("部門班表", "000405", ShiftSchedulesQueryView.class, VaadinIcon.CALENDAR),

    NONE("", null, null, null);

    private String name;
    private String code;
    private Class<? extends Component> page;
    private VaadinIcon icon;

    MenuLink(String name, String code, Class<? extends Component> page, VaadinIcon icon) {
        this.name = name;
        this.code = code;
        this.page = page;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class<? extends Component> getPage() {
        return page;
    }

    public void setPage(Class<? extends Component> page) {
        this.page = page;
    }

    public VaadinIcon getIcon() {
        return icon;
    }

    public void setIcon(VaadinIcon icon) {
        this.icon = icon;
    }

    public static MenuLink getMenuByCode(String code) {
        return Arrays.stream(MenuLink.values()).filter(m -> StringUtils.equals(m.getCode(), code)).findFirst().orElse(NONE);
    }
}
