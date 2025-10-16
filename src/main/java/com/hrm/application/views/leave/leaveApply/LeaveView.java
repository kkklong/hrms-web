package com.hrm.application.views.leave.leaveApply;

import com.hrm.application.entity.LeaveRecord;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.model.Option;
import com.hrm.application.model.bo.LeaveRecordBO;
import com.hrm.application.service.LeaveService;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.shift.ShiftSchedulesQueryView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Scope("prototype")
@Route(value = "leave", layout = MainLayout.class)
public class LeaveView extends VerticalLayout {

//    private final LeaveService service;
//
//    // data
//    private List<Option<String>> leaveTypeSelectList;
//    private final Map<String, String> leaveTypeSelectMap = new HashMap<>();
//    private List<Option<Byte>> leaveStatusList;
//    private Map<Byte, Option<Byte>> leaveStatusMap;
//    private Map<Byte, Option<Byte>> approvalStageMap;
//
//
//    // grid
//    Grid<LeaveRecord> grid = new Grid<>(LeaveRecord.class, false);
//    private ListDataProvider<LeaveRecord> dataProvider;
//    private final DatePicker createdDateFilter = new DatePicker();
//    private final ComboBox<String> leaveTypesFilter = new ComboBox<>();
//    private final DatePicker startDateFilter = new DatePicker();
//    private final DatePicker endDateFilter = new DatePicker();
//    private final ComboBox<Option<Byte>> statusFilter = new ComboBox<>();
//
//    // dialog
//    private LeaveCreateDialog createDialog;
//    private LeaveDialog dialog;
//
//
//    public LeaveView(LeaveService service) {
//        this.service = service;
//        setData();
//        setSizeFull();
//        add(getTitle(), getToolBar(), getGridLayout());
//        configureDialog();
//        initializeFilters();
//    }
//
//
//    // 設定畫面----------------------------------------------------------------------------------------------------
//
//    private void setData() {
//        leaveTypeSelectList = service.getLeaveTypeOptionList();
//        leaveTypeSelectList.forEach(item -> leaveTypeSelectMap.put(item.getValue(), item.getName()));
//        leaveStatusList = service.getLeaveStatusList();
//        leaveStatusMap = ToolUtil.transToMap(leaveStatusList, Option::getValue);
//        approvalStageMap = ToolUtil.transToMap(service.getApprovalStageList(), Option::getValue);
//    }
//
//    private HorizontalLayout getTitle() {
//        HorizontalLayout titleHt = new HorizontalLayout();
//        H3 title = new H3("LeaveApply");
//        title.addClassName("title-heading");
//        titleHt.add(title);
//        titleHt.addClassName("title-config");
//        titleHt.setWidthFull();
//        return titleHt;
//    }
//
//    private Component getToolBar() {
//
//        Button createLeaveButton = new Button("新增請假");
//        createLeaveButton.addClickListener(click -> createLeave());
//        Button clearFiltersButton = new Button("重置篩選", event -> initializeFilters());
//        clearFiltersButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//        createLeaveButton.getStyle().set("--vaadin-button-border", "1px solid");
//        clearFiltersButton.getStyle().set("--vaadin-button-border", "1px solid");
//        var toolbar = new HorizontalLayout(createLeaveButton, clearFiltersButton);
//        toolbar.addClassName("toolbar");
//        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
//        return toolbar;
//    }
//
//    private HorizontalLayout getGridLayout() {
//        configureGrid();
//        updateLeaveData();
//        HorizontalLayout content = new HorizontalLayout(grid);
//        content.addClassNames("grid-content");
//        content.setSizeFull();
//        return content;
//    }
//
//    private void configureGrid() {
//        grid.addClassNames("leave-grid");
//        grid.addColumn(LeaveRecord::getCreatedDate).setHeader("申請日期").setKey("createdDate");
//        grid.addColumn(leave -> Optional.ofNullable(leaveTypeSelectMap.get(leave.getLeaveTypes())).orElse("未知")).setHeader("請假類型").setKey("leaveTypes");
//        Grid.Column<LeaveRecord> startDate = grid.addColumn(LeaveRecord::getStartDate).setHeader("請假開始日期").setKey("startDate");
//        grid.addColumn(LeaveRecord::getEndDate).setHeader("請假結束日期").setKey("endDate");
//        grid.addColumn(LeaveRecord::getCountVal).setHeader("請假時數").setKey("countVal");
//        grid.addColumn(leave -> Optional.ofNullable(leaveStatusMap.get(leave.getStatus())).map(Option::getName).orElse("未知")).setHeader("狀態").setKey("status");
//        grid.addColumn(leave -> Optional.ofNullable(approvalStageMap.get(leave.getApprovalStage())).map(Option::getName).orElse("未知")).setHeader("審核階段").setKey("stage");
//        grid.addComponentColumn(leave -> new Button("流程明細", event -> {
//            ApprovalHistoryDialog dialog1 = new ApprovalHistoryDialog(leave);
//            dialog1.open();
//        })).setHeader("流程明細").setKey("detail");
//        grid.setSizeFull();
//        grid.getColumns().forEach(col -> col.setAutoWidth(true));
//        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        grid.setSortableColumns("createdDate", "leaveTypes", "startDate", "endDate");
//        grid.sort(List.of(new GridSortOrder<>(startDate, SortDirection.DESCENDING)));
//        grid.asSingleSelect().addValueChangeListener(event -> checkLeave(event.getValue()));
//
//        configureHeaderRow(grid.appendHeaderRow());
//    }
//
//    private void configureHeaderRow(HeaderRow headerRow) {
//        leaveTypesFilter.setItems(leaveTypeSelectList.stream().map(Option::getValue).collect(Collectors.toList()));
//        leaveTypesFilter.setItemLabelGenerator(value -> Optional.ofNullable(leaveTypeSelectMap.get(value)).orElse("未知"));
//        statusFilter.setItems(leaveStatusList);
//        statusFilter.setItemLabelGenerator(Option::getName);
//
//        createdDateFilter.addValueChangeListener(event -> applyFilter());
//        createdDateFilter.setPlaceholder("...日期之後的申請");
//        leaveTypesFilter.addValueChangeListener(event -> applyFilter());
//        leaveTypesFilter.setPlaceholder("請假類型...");
//        startDateFilter.addValueChangeListener(event -> applyFilter());
//        startDateFilter.setPlaceholder("請假開始時間...");
//        endDateFilter.addValueChangeListener(event -> applyFilter());
//        endDateFilter.setPlaceholder("請假結束時間...");
//        statusFilter.addValueChangeListener(event -> applyFilter());
//        statusFilter.setPlaceholder("狀態...");
//
//        headerRow.getCell(grid.getColumnByKey("createdDate")).setComponent(createdDateFilter);
//        headerRow.getCell(grid.getColumnByKey("leaveTypes")).setComponent(leaveTypesFilter);
//        headerRow.getCell(grid.getColumnByKey("startDate")).setComponent(startDateFilter);
//        headerRow.getCell(grid.getColumnByKey("endDate")).setComponent(endDateFilter);
//        headerRow.getCell(grid.getColumnByKey("status")).setComponent(statusFilter);
//
//        //filter內容顯示清除按鈕
//        leaveTypesFilter.setClearButtonVisible(true);
//        statusFilter.setClearButtonVisible(true);
//        createdDateFilter.setClearButtonVisible(true);
//        startDateFilter.setClearButtonVisible(true);
//        endDateFilter.setClearButtonVisible(true);
//    }
//
//    private void updateLeaveData() {
//        List<LeaveRecord> leaveRecordList = service.getLeaveRecord();
//        dataProvider = new ListDataProvider<>(leaveRecordList);
//        grid.setItems(dataProvider);
//        applyFilter();
//    }
//
//    private void configureDialog() {
//        createDialog = new LeaveCreateDialog(service);
//        createDialog.addSaveListener(this::saveLeave);
//        createDialog.addCloseListener(e -> closeEditor(e.getSource()));
//
//        dialog = new LeaveDialog(service);
//        dialog.addUpdateListener(this::updateLeave);
//        dialog.addCancelListener(this::cancelLeave);
//        dialog.addCloseListener(e -> closeEditor(e.getSource()));
//    }
//
//    // 內部操作----------------------------------------------------------------------------------------------------
//
//    private void initializeFilters() {
//        createdDateFilter.clear();
//        leaveTypesFilter.clear();
//        startDateFilter.clear();
//        endDateFilter.clear();
//        statusFilter.clear();
//    }
//
//    private void applyFilter() {
//        dataProvider.clearFilters();
//
//        if (createdDateFilter.getValue() != null) {
//            LocalDate filterDate = createdDateFilter.getValue();
//            dataProvider.addFilter(leave ->
//                    leave.getCreatedDate() != null && leave.getCreatedDate().toLocalDate().isAfter(filterDate.minusDays(1))
//            );
//        }
//        if (!leaveTypesFilter.isEmpty()) {
//            dataProvider.addFilter(leave -> leaveTypesFilter.getValue().equals(leave.getLeaveTypes()));
//        }
//        if (startDateFilter.getValue() != null) {
//            LocalDate filterDate = startDateFilter.getValue();
//            dataProvider.addFilter(leave ->
//                    leave.getStartDate() != null && leave.getStartDate().toLocalDate().isAfter(filterDate.minusDays(1))
//            );
//        }
//        if (endDateFilter.getValue() != null) {
//            LocalDate filterDate = endDateFilter.getValue();
//            dataProvider.addFilter(leave ->
//                    leave.getEndDate() != null && leave.getEndDate().toLocalDate().isBefore(filterDate.plusDays(1))
//            );
//        }
//        if (!statusFilter.isEmpty()) {
//            dataProvider.addFilter(leave -> Objects.equals(statusFilter.getValue().getValue(), leave.getStatus()));
//        }
//
//    }
//
//    //送出
//    private void saveLeave(LeaveCreateDialog.SaveEvent event) {
//        LeaveRecordBO leave = event.getLeaveRecords();
//        MultiFileMemoryBuffer buffer = event.getBuffer();
//        boolean success = false;
//        if (buffer.getFiles().isEmpty()) {
//            success = service.add(leave, null, null);
//        } else {
//            String fileName = buffer.getFiles().iterator().next();
//            InputStream inputStream = buffer.getInputStream(fileName);
//            try {
//                byte[] fileBytes = IOUtils.toByteArray(inputStream);
//                success = service.add(leave, fileBytes, fileName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (success) {
//            Notification.show("申請成功");
//            closeEditor(createDialog);
//            updateLeaveData();
//        } else {
//            Notification.show("申請失敗");
//        }
//    }
//
//    //創建
//    private void createLeave() {
//        LeaveRecordBO leave = new LeaveRecordBO();
//        grid.asSingleSelect().clear();
//
//        if (leave == null) {
//            closeEditor(createDialog);
//        } else {
//            createDialog.setLeave(leave);
//            createDialog.open();
//            updateLeaveData();
//            addClassName("creating");
//        }
//    }
//
//    //檢視
//    private void checkLeave(LeaveRecord leave) {
//
//        if (leave == null) {
//            closeEditor(dialog);
//        } else {
//            dialog.setLeave(leave);
//            dialog.setDialogView(leave.getStatus());
//            dialog.open();
//            addClassName("checking");
//        }
//    }
//
//    //補件
//    private void updateLeave(LeaveDialog.UpdateEvent event) {
//        LeaveRecord leave = event.getLeaveRecords();
//        MultiFileMemoryBuffer buffer = event.getBuffer();
//        boolean success = false;
//        try {
//            String fileName = buffer.getFiles().iterator().next();
//            InputStream inputStream = buffer.getInputStream(fileName);
//            byte[] fileBytes = IOUtils.toByteArray(inputStream);
//            success = service.update(leave.getId(), fileBytes, fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (success) {
//            Notification.show("上傳成功");
//            closeEditor(event.getSource());
//            updateLeaveData();
//        } else {
//            Notification.show("上傳失敗");
//        }
//    }
//
//    //撤銷
//    private void cancelLeave(LeaveDialog.CancelEvent event) {
//        LeaveRecord leave = event.getLeaveRecords();
//        boolean success = service.cancel(leave);
//        if (success) {
//            Notification.show("撤銷成功");
//            closeEditor(event.getSource());
//            updateLeaveData();
//        } else {
//            Notification.show("撤銷失敗");
//        }
//    }
//
//    private void closeEditor(Dialog dialog) {
//        dialog.close();
//        if (dialog instanceof LeaveCreateDialog) {
//            removeClassName("creating");
//        } else {
//            removeClassName("checking");
//        }
//    }
}
