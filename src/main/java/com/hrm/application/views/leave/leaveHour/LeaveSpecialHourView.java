package com.hrm.application.views.leave.leaveHour;

import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.LeaveTypeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;

@Scope("prototype")
@Route(value = "LeaveSpecialHour", layout = MainLayout.class)
@MenuRouter(label = "個人假別時數", icon = VaadinIcon.GOLF)
public class LeaveSpecialHourView extends VerticalLayout {
    private final Grid<LeaveSpecialRecord> grid = new Grid<>(LeaveSpecialRecord.class, false);
    private final ListDataProvider<LeaveSpecialRecord> dataProvider;

    private final LeaveTypeService service;

    public LeaveSpecialHourView(LeaveTypeService service) {
        this.service = service;
        dataProvider = new ListDataProvider<>(new ArrayList<>());
        this.addClassName("background-plan");

    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("LeaveSpecialHour");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getToolBar() {
        Button resetFiltersButton = new Button("重置篩選");
        resetFiltersButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//        resetFiltersButton.addClickListener(e -> initializeFilters());
        HorizontalLayout toolbar = new HorizontalLayout(resetFiltersButton);
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(service.queryCurrentLeaveSpecialRecordList());
        dataProvider.refreshAll();
        grid.setItems(dataProvider);
    }
    private void configureGrid() {
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addColumn(l -> l.getStartDate().getYear()).setHeader("年分").setKey("year");

    }

}
