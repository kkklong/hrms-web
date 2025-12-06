package com.hrm.application.views.shiftAdjust;

import com.hrm.application.component.ToolBar;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftAdjustmentRequestService;
import com.hrm.application.util.DateUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.shift.shiftSchedule3.ScheduleMatrixGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.hrm.application.util.DateUtil.DatePattern.YYYY_MM_DD;

@Slf4j
public class ShiftAdjustmentRequestDialog extends Dialog {

    private final ShiftAdjustmentRequestService service;
    private final ScheduleMatrixGrid scheduleMatrixGrid = new ScheduleMatrixGrid();


    // 工具
    private boolean previewEnabled = false;
    private final MultiSelectComboBox<Option<Integer>> employeeFilter = new MultiSelectComboBox<>();
    private final ComboBox<Option<Integer>> departmentSelector = new ComboBox<>();

    // 共用資料
    private final LocalDate now = LocalDate.now();
    private LocalDate selectedDate = now.withDayOfMonth(1);
    private List<ShiftSchedulePeriod> periods = List.of();
    private LocalDate displayStart;
    private LocalDate displayEnd;
    private UserInfo userInfo;
    private List<Option<Integer>> departmentList;
    List<Option<Integer>> employeeList;
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();

    //送出/取消按鈕
    private final Button submitBtn = new Button("送出");
    private final Button closeBtn = new Button("取消");


    //左下[申請表單]欄位



    private List<ShiftSchedulesQueryVO> baseShift = new ArrayList<>();





    public ShiftAdjustmentRequestDialog(ShiftAdjustmentRequestService service) {
        this.service = service;
        setMaxWidth("1200px");
        setWidth("95vx");
        initData();
        add(buildToolbar(), scheduleMatrixGrid);

    }

    private void initData() {
        userInfo = SessionUtil.getUserInfo();
        if (userInfo == null) {
            return;
        }
        departmentList = service.getDepartmentOptionList();
        configureDepartmentSelector(userInfo.getDepartmentId());
        List<ShiftType> shiftTypes = service.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypes, ShiftType::getShiftKey);

        configureMatrixGrid();
        updateDateSelector(userInfo.getDepartmentId(), now);
        updateMatrixGrid(baseShift);
    }

    private Component getContent(){
//        HorizontalLayout sheduleHt = new HorizontalLayout();
//        sheduleHt.setClassName("grid-content");
//        sheduleHt.setWidthFull();
        var content = new VerticalLayout(scheduleMatrixGrid);
        return content;
    }

    // -------- tool --------
    private Component buildToolbar() {
        Button previewBtn = new Button();
        previewBtn.setText("預覽：調班前");
        previewBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        previewBtn.addClickListener(e -> {
            previewEnabled = !previewEnabled;
            updatePreviewBTN(previewBtn);
        });

        // 員工多選過濾（只影響時間軸顯示）
        employeeFilter.setPlaceholder("員工...");
        employeeFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        employeeFilter.setClearButtonVisible(true);
        employeeFilter.setItemLabelGenerator(Option::getName);
        employeeFilter.addValueChangeListener(e -> scheduleMatrixGrid.applyEmployeeFilter(
                e.getValue().stream()
                        .map(Option::getValue)
                        .collect(Collectors.toSet())
        ));

        var toolbar = new ToolBar();
        toolbar.addLeft(previewBtn);
        toolbar.addRight(departmentSelector, employeeFilter);
        return toolbar;
    }

    private void updatePreviewBTN(Button previewBtn) {
        if (previewEnabled) {
            previewBtn.setText("預覽：調班後");
            previewBtn.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
            previewBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        } else {
            previewBtn.setText("預覽：調班前");
            previewBtn.removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
            previewBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        }
    }

    private void configureDepartmentSelector(Integer DeptId) {
        Map<Integer, Option<Integer>>  departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
        departmentSelector.setItems(departmentList);
        departmentSelector.setItemLabelGenerator(Option::getName);
        departmentSelector.setPlaceholder("部門...");
        departmentSelector.setValue(departmentMap.get(DeptId));
        departmentSelector.setEnabled(false);
        departmentSelector.setWidth("10em");
        departmentSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
    }


    // -------- toolE --------

    private void configureMatrixGrid() {
        scheduleMatrixGrid.setClassName("grid-content");
        scheduleMatrixGrid.getContent().setHeight(null);     //重設高度
        scheduleMatrixGrid.getContent().setWidthFull();
//        scheduleMatrixGrid.addCellClickListener(this::onCellClick);
    }

    /**
     *根據傳入月份與部門，更新顯示區間與baseShift與employeeFilter資料
     */
    private void updateDateSelector(Integer deptId, LocalDate applyDate) {
        LocalDate start = applyDate.withDayOfMonth(1);
        LocalDate end = applyDate.withDayOfMonth(applyDate.lengthOfMonth());
        periods = service.getShiftSchedulePeriods(
                        DateUtil.format(start.atStartOfDay(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId())
                        , DateUtil.format(end.atStartOfDay(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId()));
        displayStart = periods.get(0).getStartDate();
        displayEnd   = periods.get(periods.size() - 1).getEndDate();

        List<ShiftSchedules> raw =
                service.queryShiftSchedules(
                        displayStart.toString(),
                        displayEnd.toString(),
                        deptId
                );
        baseShift = service.convertToShiftSchedulesQueryVO(raw);

        employeeList = baseShift.stream()
                .filter(vo -> vo.getEmployeeId() != null)
                .map(vo -> new Option<>(vo.getNickName(), vo.getEmployeeId()))
                .distinct()
                .sorted(Comparator.comparing(Option::getValue))
                .collect(Collectors.toList());
        employeeFilter.setItems(employeeList);
    }

    /**
     * 根據傳入班表資料更新預覽MatrixGrid顯示
     */
    private void updateMatrixGrid(List<ShiftSchedulesQueryVO> viewSchedule) {
        Set<Option<Integer>> keep = employeeFilter.getValue();
        scheduleMatrixGrid.setData(viewSchedule, shiftTypeMap, periods, displayStart, displayEnd, selectedDate);
        employeeFilter.setValue(keep);
    }


    private Component buildSelectForm() {


        FormLayout form = new FormLayout();
        return form;
    }

}
