package com.hrm.application.views.shift.shiftSchedule3;

import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


    public ScheduleView(ShiftScheduleService service) {
        this.service = service;
        setSizeFull();
        setData();
        // 排班模式
        matrixGrid.setMode(isManager());
        add(getTitle(), getToolbar(), getContent());

        updateSchedulesData();
        setParameterListener();
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

    private ScheduleMatrixGrid.Mode isManager(){
        return ScheduleMatrixGrid.Mode.MANAGEMENT;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.BASELINE);

        // 左側：儲存
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        saveButton.addClickListener(e -> onSaveAll());
        HorizontalLayout left = new HorizontalLayout(saveButton);

        // 中間：日期
        HorizontalLayout dateCenter = new HorizontalLayout(configureDateSelector());
        dateCenter.setWidthFull();
        dateCenter.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);

        // 右側：過濾器
        HorizontalLayout right = new HorizontalLayout();
        configureDepartmentSelector();
        configureEmployeeFilter();
        right.add(employeeFilter, departmentSelector);

        toolbar.add(left, dateCenter, right);
        return toolbar;
    }

    private HorizontalLayout configureDateSelector() {
        yearPicker.setItems(years);
        yearPicker.setValue(selectedDate.getYear());
        yearPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        yearPicker.setWidth("6em");

        List<Integer> months = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());
        monthPicker.setItems(months);
        monthPicker.setValue(selectedDate.getMonth().getValue());
        monthPicker.setItemLabelGenerator(v -> v + "月");
        monthPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        monthPicker.setWidth("6em");

        HorizontalLayout datePair = new HorizontalLayout(yearPicker, monthPicker);
        leftButton.addClickListener(e -> decrementMonth());
        rightButton.addClickListener(e -> incrementMonth());

        HorizontalLayout ht = new HorizontalLayout(leftButton, datePair, rightButton);
        ht.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
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
        yearPicker.setValue(selectedDate.getYear());
        monthPicker.setValue(selectedDate.getMonthValue());
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
        departmentSelector.setWidth("10em");
        departmentSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
    }

    private void configureEmployeeFilter() {
        employeeFilter.setPlaceholder("員工...");
        employeeFilter.setWidth("10em");
        employeeFilter.setClearButtonVisible(true);
        employeeFilter.setItemLabelGenerator(Option::getName);
        employeeFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
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

        // 顯示範圍（核心雙週 ±1 週）
        LocalDate displayStart = monthlyPeriods.get(0).getStartDate();
        LocalDate displayEnd   = monthlyPeriods.get(monthlyPeriods.size() - 1).getEndDate();

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
    private void onSaveAll(){
        saveButton.setEnabled(false);
    }


    private List<ShiftSchedules> buildPayloadForSave(List<ShiftSchedulesQueryVO> rows, LocalDate start, LocalDate end){
        return new ArrayList<>();
    }
}
