package com.hrm.application.layout;

import com.hrm.application.views.DashboardView;
import com.hrm.application.views.calendar.CalendarView;
import com.vaadin.flow.component.sidenav.SideNav;

public class MainLayout extends AbstractLayout {
    @Override
    protected void createMenuEntries(SideNav nav) {
        addMenu(nav, DashboardView.class);
        addMenu(nav, CalendarView.class);

    }
}