package com.hrm.application.demo;

import com.hrm.application.demo.api168.bean.LotteryData;
import com.hrm.application.demo.api168.service.LotteryService;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "168Api", layout = MainLayout.class)

public class TestLotteryView extends VerticalLayout {
    LotteryService service;
    private Grid<LotteryData> grid = new Grid<>(LotteryData.class, false);

    List openData = new ArrayList<>();

    public TestLotteryView(LotteryService service) {
        setSizeFull();
        this.addClassName("background-plan");
        this.service = service;
        configureGrid();
        updateList();
        taskJob();

        add(getContent());

    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(LotteryData::getPreDrawTime).setHeader("OpenTime");
        grid.addColumn(LotteryData::getPreDrawIssue).setHeader("TurnNum");
        grid.addColumn(LotteryData::getPreDrawCode).setHeader("OpenCode");
    }

    private void updateList() {
        openData.addAll(getData());  // 加進來正確的 LotteryData
        grid.setItems(openData);
    }

    private List getData() {
        return service.fetchLotteryResult();
    }

    private void taskJob() {
        UI.getCurrent().setPollInterval(60000); // 60秒
        UI.getCurrent().addPollListener(event -> updateList());
    }

}
