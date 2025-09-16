package com.hrm.application.views.leave.leaveHour;

import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.enums.Status;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.LeaveTypeService;
import com.hrm.application.util.DateUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hrm.application.util.DateUtil.DatePattern.YYYY_MM_DD;

@Scope("prototype")
@Route(value = "PersonalLeaveSpecialRecordMenu", layout = MainLayout.class)
@MenuRouter(label = "個人可用假別(選單)", icon = VaadinIcon.GOLF)
public class PersonalLeaveSpecialRecordView extends VerticalLayout {
    private final Grid<LeaveSpecialRecord> grid = new Grid<>(LeaveSpecialRecord.class, false);
    private final ListDataProvider<LeaveSpecialRecord> dataProvider;
    private final LeaveTypeService service;

    private List<Option<Integer>> departmentList;
    private List<Option<String>> leaveTypeList;
    private List<Option<String>> salaryStandardList;
    private Map<String, Option<String>> salaryStandardMap;

    public PersonalLeaveSpecialRecordView(LeaveTypeService service) {
        this.service = service;
        dataProvider = new ListDataProvider<>(new ArrayList<>());
        this.addClassName("background-plan");
        setData();
        setSizeFull();
        configureGrid();
        add(titleConfigure(), getToolBar(), getContent());
        updateList();
    }

    private void setData(){
        departmentList = service.getDepartmentOptionList();
        leaveTypeList = service.getNonAutoScheduledLeaves();
        salaryStandardList = service.querySalaryStandard();
        salaryStandardMap = ToolUtil.transToMap(salaryStandardList, Option::getValue);
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("PersonalLeaveSpecialRecord");
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
        grid.addColumn(LeaveSpecialRecord::getChineseName).setHeader("假別").setKey("leaveTypes");
        grid.addColumn(l -> l.getMaxLeaveDays() + "天").setHeader("可請假天數").setTextAlign(ColumnTextAlign.CENTER).setKey("maxLeaveDays");
        grid.addColumn(l -> DateUtil.format(l.getStartDate(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId()))
                .setHeader("生效日期").setTextAlign(ColumnTextAlign.CENTER).setKey("startDate");
        grid.addColumn(l -> DateUtil.format(l.getEndDate(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId()))
                .setHeader("失效日期").setTextAlign(ColumnTextAlign.CENTER).setKey("endDate");
        grid.addColumn(record ->
                Objects.requireNonNull(salaryStandardMap.get(record.getSalaryStandard())).getName()
        ).setHeader("計薪標準").setTextAlign(ColumnTextAlign.CENTER).setKey("salaryStandard");
        grid.addColumn(record ->
                Objects.requireNonNull(Status.fromBoolean(record.getFullAttendanceBonus())).getLabel()
        ).setHeader("計算全勤").setTextAlign(ColumnTextAlign.CENTER).setKey("fullAttendanceBonus");
        grid.addColumn(l -> l.getMinLeaveUnit() + "時").setTextAlign(ColumnTextAlign.CENTER).setHeader("最低請假單位").setKey("minLeaveUnit");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        grid.asSingleSelect().addValueChangeListener(event -> editLeaveRecord(event.getValue()));
    }

}
