package com.hrm.application.views.calendar;

import com.hrm.application.calendar.AbstractCalendarView;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.SessionUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Scope("prototype")
@Route(value = "calendar", layout = MainLayout.class)
@MenuRouter(label = "Calendar", icon = VaadinIcon.CALENDAR_O)
@PageTitle("Calendar | 人力資源管理系統")
public class CalendarView extends VerticalLayout implements AfterNavigationObserver {

    private ShiftScheduleService service;
    private CalendarConfig calendar;

    private UserInfo userInfo;

    public CalendarView(ShiftScheduleService service) {
        this.service = service;
        calendar = new CalendarConfig(service);
//        calendar.setSizeFull();
//        Button refresh = new Button("更新", click -> loadEntry());
        add(titleConfigure(),getContent());
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        this.addClassName("background-plan");
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("Calendar");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(calendar);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    public void loadEntry() {
        userInfo = SessionUtil.getUserInfo();
        if(userInfo == null) {
            calendar.loadShiftSchedules(new ArrayList<>());
        }
        else {
            Integer userDepartmentId = userInfo.getDepartmentId();
            Integer employeeId = userInfo.getDepartmentId();

            // 設定查詢的時間範圍 (這裡舉例用當月的第一天和最後一天)
            LocalDate now = LocalDate.now();
            String startDate = now.withDayOfMonth(1).toString();
            LocalDate endMonth = now.plusMonths(1);
            String endDate = endMonth.withDayOfMonth(endMonth.lengthOfMonth()).toString();

            List<ShiftSchedules> schedules = service.queryShiftSchedules(startDate, endDate, userDepartmentId);
            List<ShiftSchedules> schedulesSorted = schedules.stream()
                    .filter(shift -> shift.getEmployeeId().equals(employeeId))
                    .collect(Collectors.toList());

            calendar.loadShiftSchedules(schedulesSorted);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        loadEntry();
    }
}
