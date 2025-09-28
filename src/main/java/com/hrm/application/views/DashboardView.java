package com.hrm.application.views;

import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.views.calendar.CalendarView;
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

//    CalendarView calendar;

    public DashboardView(ShiftScheduleService shiftScheduleService) {
        if (SessionUtil.getUserInfo() == null) {
            NotificationUtil.error("Session Not Ready");
            return;
        }
        this.addClassName("background-plan");
        setSizeFull();
//        calendar = new CalendarView(shiftScheduleService);
        CalendarView calendar = new CalendarView(shiftScheduleService);
        calendar.setSizeFull();
//    PersonalInfoBoard personalInfo = new PersonalInfoBoard(employeeService);

        FormLayout stepLayout = new FormLayout();
        stepLayout.setSizeFull();

        stepLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 2),
                new FormLayout.ResponsiveStep("100em", 4)
        );
        stepLayout.add(calendar);
        stepLayout.setColspan(calendar, 2);

        add(stepLayout);
    }
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        calendar.loadEntry();
    }
//
//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        super.onAttach(attachEvent);
//        UI ui = attachEvent.getUI();
//        ui.access(() -> {
//            try {
//                calendar.loadEntry();
//            } catch (Exception e) {
//                NotificationUtil.error("載入資料失敗：" + e.getMessage());
//            }
//        });
//    }
}
