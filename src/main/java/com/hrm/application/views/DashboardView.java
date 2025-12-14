package com.hrm.application.views;

import com.hrm.application.demo.rawAttend.CrawlRawAttendService;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.*;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.views.calendar.CalendarConfig;
import com.hrm.application.views.calendar.CalendarView;
import com.hrm.application.views.dashboard.ClockInfo;
import com.hrm.application.views.dashboard.PersonalInfoBoard;
import com.hrm.application.views.dashboard.UserAvailableLeaveInfo;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@MenuRouter(label = "Dashboard", icon = VaadinIcon.GLOBE_WIRE)
@PageTitle("Home | HRMSystemDemo")
public class DashboardView extends VerticalLayout implements AfterNavigationObserver {

    CalendarView calendar;

    public DashboardView(ShiftScheduleService s1, AccountService s2,
                         RawAttendanceRecordsQueryService s3,
                         CrawlRawAttendService s4,
                         LeaveTypeService s5) {
        if (SessionUtil.getUserInfo() == null) {
            NotificationUtil.error("Session Not Ready");
            return;
        }
        this.addClassName("background-plan");
        getStyle().set("overflow", "auto");
        setSizeFull();
        calendar = new CalendarView(s1);
        calendar.setWidthFull();
        calendar.setHeight("45em"); // fullCalendar父階沒有height的話初始會失敗(放入formLayout必須先給定值)
        PersonalInfoBoard personalInfo = new PersonalInfoBoard(s2);
        personalInfo.setSizeFull();
        ClockInfo clockInfo = new ClockInfo(s3, s4);
        clockInfo.setSizeFull();
        UserAvailableLeaveInfo userALInfo = new UserAvailableLeaveInfo(s5);
        userALInfo.setSizeFull();

        FormLayout stepLayout = new FormLayout();
        stepLayout.setSizeFull();

        stepLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0em", 1),
                new FormLayout.ResponsiveStep("70em", 2)
        );
        stepLayout.add(clockInfo, userALInfo, personalInfo, calendar);

        add(stepLayout);

        // for f5時DOM未完整渲染, fullCalendar size會跑
        addAttachListener(e -> {
            calendar.getCalendar().setMinHeight("10em");
            calendar.getCalendar().setSizeFull();
            calendar.loadEntry();
            calendar.getCalendar().render();
        });
    }
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        calendar.loadEntry();
    }


}
