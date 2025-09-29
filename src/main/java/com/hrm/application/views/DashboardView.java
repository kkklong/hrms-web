package com.hrm.application.views;

import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.AccountService;
import com.hrm.application.service.EmployeeService;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.views.calendar.CalendarConfig;
import com.hrm.application.views.calendar.CalendarView;
import com.hrm.application.views.dashboard.PersonalInfoBoard;
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

    public DashboardView(ShiftScheduleService shiftScheduleService, AccountService accountService) {
        if (SessionUtil.getUserInfo() == null) {
            NotificationUtil.error("Session Not Ready");
            return;
        }
        this.addClassName("background-plan");
        setSizeFull();
        calendar = new CalendarView(shiftScheduleService);
        calendar.setHeight("45em"); // fullCalendar父階沒有height的話初始會失敗(放入formLayout必須先給定值)
        PersonalInfoBoard personalInfo = new PersonalInfoBoard(accountService);
        personalInfo.setSizeFull();
        FormLayout stepLayout = new FormLayout();
        stepLayout.setSizeFull();

        stepLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 2),
                new FormLayout.ResponsiveStep("100em", 4)
        );
        stepLayout.add(personalInfo, calendar);
        stepLayout.setColspan(personalInfo, 2);
        stepLayout.setColspan(calendar, 2);

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
