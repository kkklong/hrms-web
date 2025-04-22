package com.hrm.application.views;

import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuItem;
import com.hrm.application.service.AccountService;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@MenuItem(label = "Dashboard", icon = VaadinIcon.GLOBE_WIRE)
@PageTitle("Home | HRMSystemDemo")
public class DashboardView extends VerticalLayout {




}
