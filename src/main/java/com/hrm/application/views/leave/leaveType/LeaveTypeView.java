package com.hrm.application.views.leave.leaveType;

import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.enums.Status;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.LeaveTypeService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Scope("prototype")
@Route(value = "LeaveType", layout = MainLayout.class)
@MenuRouter(label = "員工假別管理", icon = VaadinIcon.GOLF)
public class LeaveTypeView extends VerticalLayout {
    private final Grid<LeaveSpecialRecord> grid = new Grid<>(LeaveSpecialRecord.class, false);
    private final LeaveTypeService service;
    private final List<Option<Integer>> departmentList;
    private final List<Option<String>> leaveTypeList;
    private final List<Option<String>> salaryStandardList;
    private final Map<String, Option<String>> salaryStandardMap;

    private LeaveSpecialRecord selectedLeaveRecord;
    LeaveTypeDialog dialog;

    private final ComboBox<Option<Integer>> departmentNameFilter = new ComboBox<>();
    private final TextField nickNameFilter = new TextField();
    private final ComboBox<Option<String>> leaveTypesFilter = new ComboBox<>();
    private final ComboBox<Option<String>> salaryStandardFilter = new ComboBox<>();
    private final ComboBox<Status> fullAttendanceBonusFilter = new ComboBox<>();
    private final TextField minLeaveUnitFilter = new TextField();
    private final TextField maxLeaveDaysFilter = new TextField();
    private final TextField yearFilter = new TextField();

    private HeaderRow headerRow;
    private final ListDataProvider<LeaveSpecialRecord> dataProvider;

    public LeaveTypeView(LeaveTypeService service) {
        this.service = service;

        this.dataProvider = new ListDataProvider<>(service.getAllLeaveSpecialRecords());

        departmentList = service.getDepartmentOptionList();
        leaveTypeList = service.getNonAutoScheduledLeaves();

        this.salaryStandardList = service.querySalaryStandard();
        this.salaryStandardMap = ToolUtil.transToMap(salaryStandardList, Option::getValue);
        this.addClassName("background-plan");

        setSizeFull();

        configureGrid();
        configureDialog();

        add(titleConfigure(), getToolBar(), getContent());
        updateGrid();
        closeEditor();
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("LeaveType");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getToolBar() {
        Button addButton = new Button("新增");
        addButton.addClickListener(e -> addLeaveRecord());

        Button resetFiltersButton = new Button("重置篩選");
        resetFiltersButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resetFiltersButton.addClickListener(e -> initializeFilters());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, resetFiltersButton);
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setClassName("leave-type-view-grid");
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);

        grid.addColumn(LeaveSpecialRecord::getDepartmentName).setHeader("部門").setKey("departmentName");
        grid.addColumn(LeaveSpecialRecord::getNickName).setHeader("員工").setKey("nickName");
        grid.addColumn(l -> l.getStartDate().getYear()).setHeader("年分").setKey("year");
        grid.addColumn(LeaveSpecialRecord::getChineseName).setHeader("假別").setKey("leaveTypes");
        grid.addColumn(record ->
                Objects.requireNonNull(salaryStandardMap.get(record.getSalaryStandard())).getName()
        ).setHeader("計薪標準").setKey("salaryStandard");
        grid.addColumn(record ->
                Objects.requireNonNull(Status.fromBoolean(record.getFullAttendanceBonus())).getLabel()
        ).setHeader("計算全勤").setKey("fullAttendanceBonus");
        grid.addColumn(LeaveSpecialRecord::getMinLeaveUnit).setHeader("最低請假單位").setKey("minLeaveUnit");
        grid.addColumn(LeaveSpecialRecord::getMaxLeaveDays).setHeader("期間可請假天數").setKey("maxLeaveDays");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event -> editLeaveRecord(event.getValue()));
    }

    private void applyFilter() {
//        ListDataProvider<LeaveSpecialRecord> provider = (ListDataProvider<LeaveSpecialRecord>) grid.getDataProvider();
        dataProvider.clearFilters();

        if (!departmentNameFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    departmentNameFilter.getValue().getValue().equals(leave.getDepartmentId()));
        }

        if (!yearFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    yearFilter.getValue().equals(String.valueOf(leave.getStartDate().getYear())));
        }

        if (!nickNameFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    leave.getNickName() != null && leave.getNickName().toLowerCase().contains(nickNameFilter.getValue().toLowerCase())
            );
        }

        if (!leaveTypesFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    leaveTypesFilter.getValue().getValue().equals(leave.getLeaveTypes()));
        }

        if (!salaryStandardFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    Objects.equals(salaryStandardFilter.getValue().getValue(), leave.getSalaryStandard()));
        }

        if (!fullAttendanceBonusFilter.isEmpty()) {
            dataProvider.addFilter(leave ->
                    fullAttendanceBonusFilter.getValue().equals(Status.fromBoolean(leave.getFullAttendanceBonus())));
        }

        if (!minLeaveUnitFilter.isEmpty()) {
            try {
                Float minLeaveUnit = Float.parseFloat(minLeaveUnitFilter.getValue());
                dataProvider.addFilter(leave ->
                        leave.getMinLeaveUnit() != null && leave.getMinLeaveUnit().equals(minLeaveUnit)
                );
            } catch (NumberFormatException e) {
                Notification.show("最低請假單位必須是一個有效的數字");
            }
        }

        if (!maxLeaveDaysFilter.isEmpty()) {
            try {
                Integer maxLeaveDays = Integer.parseInt(maxLeaveDaysFilter.getValue());
                dataProvider.addFilter(leave ->
                        leave.getMaxLeaveDays() != null && leave.getMaxLeaveDays().equals(maxLeaveDays)
                );
            } catch (NumberFormatException e) {
                Notification.show("期間可請假天數必須是一個有效的整數");
            }
        }
    }

    private void editLeaveRecord(LeaveSpecialRecord leaveRecord) {
        if (leaveRecord == null) {
            closeEditor();
        } else {
            selectedLeaveRecord = leaveRecord;
            dialog.removeCreateItemListenerAction();
            dialog.setLeaveSpecialRecords(selectedLeaveRecord);
            dialog.setDialogView(false, true);
            dialog.open();
        }
    }

    private void addLeaveRecord() {
        selectedLeaveRecord = new LeaveSpecialRecord();
        dialog.setCreateItemListenerAction();
        dialog.setLeaveSpecialRecords(selectedLeaveRecord);
        dialog.setDialogView(true, false);
        dialog.open();
    }

    private void updateGrid() {
        grid.setItems(dataProvider);

        if (headerRow == null) {
            headerRow = grid.appendHeaderRow();
            configureHeaderRow();
        }
        initializeFilters();
    }

    private void initializeFilters() {
        departmentNameFilter.clear();
        nickNameFilter.clear();
        leaveTypesFilter.clear();
        salaryStandardFilter.clear();
        fullAttendanceBonusFilter.clear();
        minLeaveUnitFilter.clear();
        maxLeaveDaysFilter.clear();
        dataProvider.clearFilters();
    }

    private void configureHeaderRow() {
        departmentNameFilter.setItems(departmentList);
        departmentNameFilter.setItemLabelGenerator(Option::getName);
        departmentNameFilter.addValueChangeListener(event -> applyFilter());
        departmentNameFilter.setPlaceholder("部門...");
        departmentNameFilter.setWidth("8em");
        departmentNameFilter.getStyle().set("--vaadin-combo-box-overlay-width", "8em");

        yearFilter.addValueChangeListener(event -> applyFilter());
        yearFilter.setPlaceholder("年分...");
        yearFilter.setWidth("6em");

        nickNameFilter.addValueChangeListener(event -> applyFilter());
        nickNameFilter.setPlaceholder("員工...");
        nickNameFilter.setWidth("8em");

        leaveTypesFilter.setItems(service.getLeaveTypeSelectList());
        leaveTypesFilter.setItemLabelGenerator(Option::getName);
        leaveTypesFilter.addValueChangeListener(event -> applyFilter());
        leaveTypesFilter.setPlaceholder("假別...");
        leaveTypesFilter.setWidth("8em");
        leaveTypesFilter.getStyle().set("--vaadin-combo-box-overlay-width", "12em");

        salaryStandardFilter.setItems(salaryStandardList);
        salaryStandardFilter.setItemLabelGenerator(Option::getName);
        salaryStandardFilter.addValueChangeListener(event -> applyFilter());
        salaryStandardFilter.setPlaceholder("計薪...");
        salaryStandardFilter.setWidth("6em");
        salaryStandardFilter.getStyle().set("--vaadin-combo-box-overlay-width", "8em");


        fullAttendanceBonusFilter.setItems(Status.values());
        fullAttendanceBonusFilter.setItemLabelGenerator(Status::getLabel);
        fullAttendanceBonusFilter.addValueChangeListener(event -> applyFilter());
        fullAttendanceBonusFilter.setPlaceholder("是否...");
        fullAttendanceBonusFilter.setWidth("6em");

        minLeaveUnitFilter.addValueChangeListener(event -> applyFilter());
        minLeaveUnitFilter.setPlaceholder("最低...");
        minLeaveUnitFilter.setWidth("6em");

        maxLeaveDaysFilter.addValueChangeListener(event -> applyFilter());
        maxLeaveDaysFilter.setPlaceholder("天數...");
        maxLeaveDaysFilter.setWidth("6em");

        headerRow.getCell(grid.getColumnByKey("departmentName")).setComponent(departmentNameFilter);
        headerRow.getCell(grid.getColumnByKey("year")).setComponent(yearFilter);
        headerRow.getCell(grid.getColumnByKey("nickName")).setComponent(nickNameFilter);
        headerRow.getCell(grid.getColumnByKey("leaveTypes")).setComponent(leaveTypesFilter);
        headerRow.getCell(grid.getColumnByKey("salaryStandard")).setComponent(salaryStandardFilter);
        headerRow.getCell(grid.getColumnByKey("fullAttendanceBonus")).setComponent(fullAttendanceBonusFilter);
        headerRow.getCell(grid.getColumnByKey("minLeaveUnit")).setComponent(minLeaveUnitFilter);
        headerRow.getCell(grid.getColumnByKey("maxLeaveDays")).setComponent(maxLeaveDaysFilter);

        //filter顯示清除按鈕
        departmentNameFilter.setClearButtonVisible(true);
        yearFilter.setClearButtonVisible(true);
        nickNameFilter.setClearButtonVisible(true);
        leaveTypesFilter.setClearButtonVisible(true);
        salaryStandardFilter.setClearButtonVisible(true);
        fullAttendanceBonusFilter.setClearButtonVisible(true);
        minLeaveUnitFilter.setClearButtonVisible(true);
        maxLeaveDaysFilter.setClearButtonVisible(true);
        initializeFilters();
    }

    private void closeEditor() {
        dialog.close();
        removeClassName("editing");
    }

    private void configureDialog() {
        dialog = new LeaveTypeDialog(service);
        dialog.addSaveListener(this::saveLeaveRecord);
        dialog.addCloseListener(e -> closeEditor());
    }

    private void saveLeaveRecord(LeaveTypeDialog.SaveEvent event) {
        LeaveSpecialRecord leaveSpecialRecord = event.getLeaveSpecialRecords();

        if (service.saveLeaveSpecialRecords(leaveSpecialRecord)) {
            Notification.show("儲存成功");

            List<LeaveSpecialRecord> updatedRecords = service.getAllLeaveSpecialRecords();
            dataProvider.getItems().clear();
            dataProvider.getItems().addAll(updatedRecords);
            dataProvider.refreshAll();

            updateGrid();
            closeEditor();
        }
    }
}

