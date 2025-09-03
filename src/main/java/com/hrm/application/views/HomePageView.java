package com.hrm.application.views;

import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@MenuRouter(label = "Home", icon = VaadinIcon.GLOBE_WIRE)
@PageTitle("Home | HRMSystemDemo")
public class HomePageView extends VerticalLayout {


    public HomePageView() {
        this.addClassName("background-plan");
        setSizeFull();
    }
}
