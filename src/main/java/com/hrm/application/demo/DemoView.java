package com.hrm.application.demo;

import com.hrm.application.component.MonthNavigator;
import com.hrm.application.demo.api168.bean.LotteryData;
import com.hrm.application.demo.api168.service.LotteryService;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route(value = "demo", layout = MainLayout.class)

public class DemoView extends VerticalLayout {

    private final MonthNavigator monthNavigator;

    public DemoView() {
        setSizeFull();
        this.addClassName("background-plan");
        monthNavigator = new MonthNavigator(LocalDate.now());
        configureGrid();


        add(monthNavigator, getContent());

    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout();
//        content.addClassNames("grid-content");
//        content.setSizeFull();
        return content;
    }

    private void configureGrid() {

    }

    private void updateList() {

    }

    private List getData() {
        return new ArrayList();
    }

    private void taskJob() {

    }

}
