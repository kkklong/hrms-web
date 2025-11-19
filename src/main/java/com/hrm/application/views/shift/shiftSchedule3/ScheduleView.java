package com.hrm.application.views.shift.shiftSchedule3;

import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.shift.ExportShiftSchedulesDialog;
import com.hrm.application.views.shift.ImportShiftSchedulesDialog;
import com.hrm.application.views.shift.managerTool.CloseShiftScheduleDialog;
import com.hrm.application.views.shift.managerTool.ConflictCheckDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Scope("prototype")
@Route(value = "schedule", layout = MainLayout.class)
public class ScheduleView extends VerticalLayout {

    private final ShiftScheduleService service;

    // data
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();
    private List<Option<Integer>> departmentList;
    private Map<Integer, Option<Integer>> departmentMap;
    private final LocalDate now = LocalDate.now();
    private List<Integer> years;
    LocalDate displayStart;
    LocalDate displayEnd;

    // selected
    private LocalDate selectedDate = now;

    // UI controls
    private final Button leftButton = new Button("<");
    private final Button rightButton = new Button(">");
    private final ComboBox<Integer> yearPicker = new ComboBox<>();
    private final ComboBox<Integer> monthPicker = new ComboBox<>();
    private final MultiSelectComboBox<Option<Integer>> employeeFilter = new MultiSelectComboBox<>();
    private final ComboBox<Option<Integer>> departmentSelector = new ComboBox<>();

    // 儲存按鈕（左上）
    private final Button saveButton = new Button("儲存班表");

    // grid
    private final ScheduleMatrixGrid matrixGrid = new ScheduleMatrixGrid();

    private ConflictCheckDialog conflictDialog;
    private CloseShiftScheduleDialog closeShiftDialog;


    public ScheduleView(ShiftScheduleService service) {
        this.service = service;
        setSizeFull();
        setData();
        configureDialog();
        // 排班模式
        matrixGrid.setMode(isManager());
        add(getTitle(), getToolbar(), getContent());

        updateSchedulesData();
        setParameterListener();
        addClassName("background-plan");
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("ShiftData2");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(matrixGrid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        content.setFlexGrow(1, matrixGrid);
        return content;
    }

    private void setData() {
        List<ShiftType> shiftTypeList = service.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, ShiftType::getShiftKey);
        departmentList = service.getDepartmentOptionList();
        departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);

        years = IntStream.range(now.getYear() - 1, now.getYear() + 2)
                .boxed().collect(Collectors.toList());
    }

    private void configureDialog() {
        conflictDialog   = new ConflictCheckDialog(service, departmentList);
        closeShiftDialog = new CloseShiftScheduleDialog(service, departmentList);
    }

    private ScheduleMatrixGrid.Mode isManager(){
        return ScheduleMatrixGrid.Mode.MANAGEMENT;
    }

    private FormLayout getToolbar() {
        var toolbar = new FormLayout();
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
//        toolbar.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
//        toolbar.setAlignItems(Alignment.BASELINE);

        // 左側：儲存
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> onSaveAll());
        HorizontalLayout left = new HorizontalLayout(saveButton, managerToolConfigure());

        // 中間：日期
        HorizontalLayout dateCenter = configureDateSelector();
        dateCenter.setWidthFull();
        dateCenter.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);

        // 右側：過濾器
        HorizontalLayout right = new HorizontalLayout();
        configureDepartmentSelector();
        configureEmployeeFilter();
        right.add(employeeFilter, departmentSelector);

        toolbar.add(left, dateCenter, right);
        toolbar.setColspan(left, 8);
        toolbar.setColspan(dateCenter, 10);
        toolbar.setColspan(right, 10);
        toolbar.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("10em", 4),
                new FormLayout.ResponsiveStep("20em", 10),
                new FormLayout.ResponsiveStep("40em", 20),
        new FormLayout.ResponsiveStep("60em", 30)



        );
        return toolbar;
    }

    //管理者Tool
    private MenuBar managerToolConfigure() {
        MenuBar managerToolMenu = new MenuBar();
        managerToolMenu.getStyle().set("border", "1px solid var(--lumo-contrast-30pct)");
        managerToolMenu.getStyle().set("border-radius", "var(--lumo-border-radius-s");

        HorizontalLayout detail1Ht = new HorizontalLayout();
        MenuItem toolDetailItem = managerToolMenu.addItem("管理工具");
        Button importShiftSchedules = new Button("載入預設班表");
        Button exportShiftSchedules = new Button("導出班表");
        detail1Ht.add(importShiftSchedules, exportShiftSchedules);

        Button checkConflictButton = new Button("檢測下月排班");
        Button closeShiftButton    = new Button("關閉下月排班");
        checkConflictButton.addClickListener(e -> conflictDialog.openDialogWithParameter(departmentSelector.getValue()));
        closeShiftButton.addClickListener(e -> closeShiftDialog.openDialogWithParameter(departmentSelector.getValue()));
        HorizontalLayout detail2Ht = new HorizontalLayout();
        detail2Ht.add(checkConflictButton, closeShiftButton);


        SubMenu detailSubMenu = toolDetailItem.getSubMenu();

        // 創建載入預設班表對話框及按鈕事件
        Dialog loadDefaultShiftScheduleDialog = new ImportShiftSchedulesDialog(service);
        importShiftSchedules.addClickListener(e -> loadDefaultShiftScheduleDialog.open());
        // 創建導出班表對話框及按鈕事件
        exportShiftSchedules.addClickListener(e -> {
            Integer year = yearPicker.getValue();
            Integer month = monthPicker.getValue();
            Option<Integer> department = departmentSelector.getValue();
            Dialog exportShiftSchedulesDialog = new ExportShiftSchedulesDialog(service, year, month, department);
            exportShiftSchedulesDialog.open();
        });

        detailSubMenu.addItem(detail1Ht);
        detailSubMenu.add(new Hr());
        detailSubMenu.addItem(detail2Ht);
        return managerToolMenu;
    }

    /**
     * 設定DateSelector ToolBar
     */

    private HorizontalLayout configureDateSelector() {
        yearPicker.setItems(years);
        yearPicker.setValue(selectedDate.getYear());
        yearPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        yearPicker.getStyle().set("--vaadin-combo-box-overlay-width", "6em");
        yearPicker.setWidth("6em");

        List<Integer> months = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());
        monthPicker.setItems(months);
        monthPicker.setValue(selectedDate.getMonth().getValue());
        monthPicker.setItemLabelGenerator(v -> v + "月");
        monthPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        monthPicker.getStyle().set("--vaadin-combo-box-overlay-width", "6em");
        monthPicker.setWidth("6em");

        HorizontalLayout datePair = new HorizontalLayout(yearPicker, monthPicker);
        leftButton.addClickListener(e -> decrementMonth());
        leftButton.getStyle().set("--vaadin-button-height","--lumo-size-m");
        leftButton.setWidth("3em");
        rightButton.addClickListener(e -> incrementMonth());
        rightButton.getStyle().set("--vaadin-button-height","--lumo-size-m");
        rightButton.setWidth("3em");
        HorizontalLayout ht = new HorizontalLayout(leftButton, datePair, rightButton);
//        ht.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return ht;
    }

    private void decrementMonth() {
        selectedDate = selectedDate.minusMonths(1);
        if (selectedDate.getYear() < Collections.min(years)) {
            selectedDate = selectedDate.withYear(Collections.min(years)).withMonth(1);
        }
        updateDateComboBox();
    }

    private void incrementMonth() {
        selectedDate = selectedDate.plusMonths(1);
        if (selectedDate.getYear() > Collections.max(years)) {
            selectedDate = selectedDate.withYear(Collections.max(years)).withMonth(12);
        }
        updateDateComboBox();
    }

    private void updateDateComboBox() {
        int year = selectedDate.getYear();
        int month = selectedDate.getMonth().getValue();
        yearPicker.setValue(year);
        monthPicker.setValue(month);
    }

    private void updateSelectedDate() {
        if (yearPicker.isEmpty() || monthPicker.isEmpty()) return;
        int year = yearPicker.getValue();
        Month month = Month.of(monthPicker.getValue());
        selectedDate = LocalDate.of(year, month, 1);
    }

    private void configureDepartmentSelector() {
        Integer userDeptId = Optional.ofNullable(SessionUtil.getUserInfo())
                .map(u -> u.getDepartmentId())
                .orElse(null);
        departmentSelector.setItems(departmentList);
        departmentSelector.setItemLabelGenerator(Option::getName);
        departmentSelector.setPlaceholder("部門...");
        departmentSelector.setValue(departmentMap.get(userDeptId));
        departmentSelector.setEnabled(isManager().equals(ScheduleMatrixGrid.Mode.MANAGEMENT) ? true : false);
        departmentSelector.setWidth("8em");
        departmentSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        departmentSelector.getStyle().set("--vaadin-combo-box-overlay-width", "8em");
    }

    private void configureEmployeeFilter() {
        employeeFilter.setPlaceholder("員工...");
        employeeFilter.setWidth("10em");
        employeeFilter.setClearButtonVisible(true);
        employeeFilter.setItemLabelGenerator(Option::getName);
        employeeFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        employeeFilter.getStyle().set("--vaadin-combo-box-overlay-width", "8em");
    }

    // 所選月份涵蓋的雙週 ±1 週（顯示）
    private void updateSchedulesData() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate first = selectedDate.withDayOfMonth(1);
        LocalDate last = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());

        // 當月雙週（核心期間）
        List<ShiftSchedulePeriod> monthlyPeriods = service.getShiftSchedulePeriods(first.format(fmt), last.format(fmt));
//        if (monthlyPeriods == null || monthlyPeriods.isEmpty()) {
//            matrixGrid.setData(Collections.emptyList(), shiftTypeMap, Collections.emptyList(), first, last, selectedDate);
//            resetEmployeeFilter(Collections.emptyList());
//            return;
//        }

        // 顯示範圍（核心雙週 ）# 移除 ±1 週 .minusWeeks & .plusWeeks
        displayStart = monthlyPeriods.get(0).getStartDate();
        displayEnd   = monthlyPeriods.get(monthlyPeriods.size() - 1).getEndDate();

        Integer deptId = departmentSelector.getValue() != null ? departmentSelector.getValue().getValue() : null;
        var flatList = service.queryShiftSchedules(displayStart.toString(), displayEnd.toString(), deptId);
        List<ShiftSchedulesQueryVO> voList = service.convertToShiftSchedulesQueryVO(flatList);

        // 傳「monthlyPeriods」作為核心期間；displayStart/End 作為顯示區
        matrixGrid.setData(voList, shiftTypeMap, monthlyPeriods, displayStart, displayEnd, selectedDate);
        resetEmployeeFilter(voList);
    }

    private void resetEmployeeFilter(List<ShiftSchedulesQueryVO> voList) {
        Set<Option<Integer>> keep = employeeFilter.getValue();
        List<Option<Integer>> employees = voList.stream()
                .filter(s -> s.getEmployeeId() != null)
                .filter(s -> s.getNickName() != null && !s.getNickName().isBlank())
                .map(s -> new Option<>(s.getNickName(), s.getEmployeeId()))
                .sorted(Comparator.comparing(Option::getValue))
                .collect(Collectors.toList());
        employeeFilter.setItems(employees);
        employeeFilter.setValue(keep);
    }

    private void setParameterListener() {
        yearPicker.addValueChangeListener(e -> {
            updateSelectedDate();
            updateSchedulesData();
        });
        monthPicker.addValueChangeListener(e -> {
            updateSelectedDate();
            updateSchedulesData();
        });
        departmentSelector.addValueChangeListener(e -> {
            employeeFilter.clear();
            updateSchedulesData();
        });
        employeeFilter.addValueChangeListener(e -> applyFilter());
    }

    private void applyFilter() {
        Set<Integer> ids = employeeFilter.getValue().stream().map(Option::getValue).collect(Collectors.toSet());
        matrixGrid.applyEmployeeFilter(ids);
    }

    // ---------- 儲存：只存目前登入者 + 本月雙週區間 ----------
    private void onSaveAll() {
        Integer currentUserId = Optional.ofNullable(SessionUtil.getUserInfo())
                .map(u -> u.getId()).orElse(null);
        if (currentUserId == null) {
            NotificationUtil.error("無法取得目前使用者資訊");
            saveButton.setEnabled(true);
            return;
        }
        List<ShiftSchedulesQueryVO> requestRows = new ArrayList<>();
        List<ShiftSchedulesQueryVO> allRows = matrixGrid.getAllRows();
        List<ShiftSchedulesQueryVO> myRows = allRows.stream()
                .filter(r -> Objects.equals(currentUserId, r.getEmployeeId()))
                .collect(Collectors.toList());
        requestRows = isManager() == ScheduleMatrixGrid.Mode.MANAGEMENT ?
                allRows :
                myRows;
        EmpSelectDialog selectDialog = new EmpSelectDialog(requestRows);
        selectDialog.open();

        selectDialog.addSaveListener(e -> saveShiftSchedule(e, displayStart, displayEnd));
    }

    private void saveShiftSchedule(EmpSelectDialog.SaveEvent event, LocalDate displayStart, LocalDate displayEnd) {
        List<ShiftSchedules> payload = service.buildPayload(event.getRows(), displayStart, displayEnd);
        if (payload.isEmpty()) {
            Notification.show("班表資料異常");
            return;
        }
        boolean ok = service.manuallyAdjustShiftSchedules(payload);
        if (ok) {
            Notification.show("排班儲存成功");
            updateSchedulesData();
        } else {
            Notification.show("儲存失敗");
        }
    }
}
