package com.hrm.application.views.shiftAdjust;

import com.hrm.application.component.ToolBar;
import com.hrm.application.entity.ShiftAdjustmentApply;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftChangePreview;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftAdjustmentRequestService;
import com.hrm.application.util.DateUtil;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.shift.shiftSchedule3.ScheduleMatrixGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.hrm.application.util.DateUtil.DatePattern.YYYY_MM;
import static com.hrm.application.util.DateUtil.DatePattern.YYYY_MM_DD;

@Slf4j
public class ShiftAdjustmentRequestDialog extends Dialog {

    private final ShiftAdjustmentRequestService service;
    private final ScheduleMatrixGrid scheduleMatrixGrid = new ScheduleMatrixGrid();

    private ShiftAdjustmentRequestVO requestVO;
    private final Runnable refreshCallback;


    /*** true = 檢視模式，false = 新增申請模式*/
    private boolean viewOnly;

    // 工具
    private boolean previewEnabled = false;
    Button previewBtn = new Button();

    private final MultiSelectComboBox<Option<Integer>> employeeFilter = new MultiSelectComboBox<>();
    private final ComboBox<Option<Integer>> departmentSelector = new ComboBox<>();

    /*** 月切換元件 */
    // 月份切換用 Tabs：本月 / 下個月
    private final Tabs monthTabs = new Tabs();
    private final Tab currentMonthTab = new Tab();
    private final Tab nextMonthTab = new Tab();

    // 共用資料
    private final LocalDate now = LocalDate.now();
    private LocalDate selectedDate = now.withDayOfMonth(1);
    private List<ShiftSchedulePeriod> periods = List.of();
    private LocalDate displayStart;
    private LocalDate displayEnd;
    private UserInfo userInfo;
    private List<Option<Integer>> departmentList;
    private List<ShiftType> shiftTypes;
    private List<Option<Integer>> employeeList;
    private Map<Integer, Option<Integer>> employeeOptionMap = new HashMap<>();

    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();

    //送出/取消按鈕
    private final Button submitBtn = new Button("送出");
    private final Button closeBtn = new Button("取消");

    //左下[申請表單]欄位
    private VerticalLayout selectForm;
    //左下「選擇班別」表單欄位
    private final ComboBox<Option<Integer>> employee = new ComboBox<>();
    private final DatePicker datePicker = new DatePicker();
    private final ComboBox<ShiftType> originalShift = new ComboBox<>();
    private final ComboBox<ShiftType> targetShiftCombo = new ComboBox<>();
    private final Button addBtn = new Button("新增");
    private final TextArea reason = new TextArea();

    private List<ShiftSchedulesQueryVO> baseShift = new ArrayList<>();
    private List<ShiftSchedulesQueryVO> previewShift = new ArrayList<>();

    //右下[申請列表]
    //暫存的調班清單；供時間軸做「預覽覆蓋」
    private final List<ShiftChangePreview> pendingChanges = new ArrayList<>();
    private final Grid<ShiftChangePreview> changeGrid = new Grid<>(ShiftChangePreview.class, false);

    private final ListDataProvider<ShiftChangePreview> changeProvider = new ListDataProvider<>(pendingChanges); // 直接用 pendingChanges 當資料來源


    public ShiftAdjustmentRequestDialog(ShiftAdjustmentRequestVO requestVO,
                                        ShiftAdjustmentRequestService service,
                                        Runnable refreshCallback,
                                        Boolean viewOnly) {
        this.requestVO = requestVO;
        this.service = service;
        this.viewOnly = viewOnly;
        this.refreshCallback = refreshCallback;
        addClassName("background-plan");
        setWidth("90em");
        setMaxWidth("100%");
        setResizable(true);
        setMaxHeight("100%");

        initData();
        add(buildToolbar(), getContent(), getApplyForm());
        getFooter().add(buildFooter());
    }

    private void initData() {
        userInfo = SessionUtil.getUserInfo();
        if (userInfo == null) {
            return;
        }
        setComponentSize();
        departmentList = service.getDepartmentOptionList();
        shiftTypes = service.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypes, ShiftType::getShiftKey);
    }

    public void setDataForCreate() {
        setView(true);
        Integer deptId = userInfo.getDepartmentId();
        LocalDate targetDate = now;
        configureDepartmentSelector(deptId);
        configureMatrixGrid();
        configureTimeTab(targetDate, deptId);
        updateDateSelector(targetDate);
        updateShiftSchedules(deptId);
        updateMatrixGrid(previewShift);
    }

    public void setDataForCheck(ShiftAdjustmentRequestVO requestVO) {
        setView(false);
        Integer deptId = requestVO.getDepartmentId();
        LocalDate targetDate = now;
        configureDepartmentSelector(deptId);
        configureMatrixGrid();
        configureTimeTab(targetDate, deptId);
        updateDateSelector(targetDate);
        updateShiftSchedules(deptId);
        updateMatrixGrid(previewShift);
        setApplyFormData(requestVO);
    }

    public void setView(Boolean isCreate) {
        addBtn.setVisible(isCreate);
        submitBtn.setVisible(isCreate);
        selectForm.setVisible(isCreate);
    }

    private void setComponentSize() {
        List<HasSize> fields = Arrays.asList(
                employee, datePicker, originalShift, targetShiftCombo, reason
        );
        fields.forEach(field -> {
            field.setWidthFull();
        });
    }

    private Component getContent() {
        HorizontalLayout sheduleHt = new HorizontalLayout(scheduleMatrixGrid);
        sheduleHt.setClassName("grid-content");
        sheduleHt.setWidthFull();
        return sheduleHt;
    }

    private Component getApplyForm() {
        VerticalLayout selectForm = buildSelectForm();
        VerticalLayout applyForm = buildApplyListForm();
        applyForm.setClassName("grid-content");

        FormLayout applyHt = new FormLayout(selectForm, applyForm);
        applyHt.setMinWidth("25em");
        selectForm.setSizeFull();
        applyForm.setSizeFull();

        applyHt.setClassName("grid-content");
        applyHt.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("50em", 2)
        );
        return applyHt;
    }

    private void setApplyFormData(ShiftAdjustmentRequestVO requestVO) {
        pendingChanges.clear();
        if (requestVO == null || requestVO.getShiftMap() == null || requestVO.getShiftMap().isEmpty()) {
            changeProvider.refreshAll();
            return;
        }
        Map<Integer, ShiftSchedulesDateTimeQueryVO> idToSchedule = new HashMap<>();
        Map<Integer, Integer> idToEmp = new HashMap<>();

        for (ShiftSchedulesQueryVO vo : baseShift) {
            Integer empId = vo.getEmployeeId();
            for (ShiftSchedulesDateTimeQueryVO sd : vo.getSchedulesDates()) {
                idToSchedule.put(sd.getId(), sd);
                idToEmp.put(sd.getId(), empId);
            }
        }

        Map<Integer, String> originalShiftMap = requestVO.getOriginalShiftMap();
        boolean hasOriginalMap = originalShiftMap != null && !originalShiftMap.isEmpty();

        // 從request的shiftMap拿toKey; originalMap拿fromKey, 在用id拿sd, 從sd取出shiftType跟color
        for (Map.Entry<Integer, String> e : requestVO.getShiftMap().entrySet()) {
            Integer scheduleId = e.getKey();
            String toKey = e.getValue();

            ShiftSchedulesDateTimeQueryVO sd = idToSchedule.get(scheduleId);
            Integer empId = idToEmp.get(scheduleId);
            if (sd == null || empId == null) {
                continue;
            }

            LocalDate date = sd.getShiftDate();

            String fromKey;
            if (hasOriginalMap && originalShiftMap.containsKey(scheduleId)) {
                fromKey = originalShiftMap.get(scheduleId);
            } else {
                fromKey = sd.getShiftTypes();
            }

            if (fromKey != null) {
                sd.setShiftTypes(fromKey);
                sd.setShiftColorCode(service.resolveShiftColorCode(fromKey, shiftTypeMap));
            }

            pendingChanges.add(new ShiftChangePreview(empId, date, fromKey, toKey));
        }

        changeProvider.refreshAll();
    }

    /**
     * 按鈕與事件。
     * (1)新增(送出申請)
     * (2)更新
     */
    private Component buildFooter() {
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        submitBtn.addClickListener(e -> validateAndSave());
        closeBtn.addClickListener(e -> close());

        var actions = new ToolBar();
        actions.addRight(submitBtn, closeBtn);
        return actions;
    }

    private void validateAndSave() {
        if (pendingChanges.isEmpty()) {
            Notification.show("尚未選擇任何調班資料");
            return;
        }
        // 將畫面上的暫存調班轉成 <原班表ID, 目標班別> 的 Map
        Map<Integer, String> shiftMap = new LinkedHashMap<>();
        for (ShiftChangePreview c : pendingChanges) {
            Long originId = service.findOriginScheduleId(c.getEmployeeId(), c.getDate(), baseShift);
            if (originId != null) {
                shiftMap.put(originId.intValue(), c.getToShiftKey());
            }
        }
        // 建立一筆申請，塞入 shiftMap 與申請原因
        ShiftAdjustmentApply req = new ShiftAdjustmentApply();
        req.setShiftMap(shiftMap);
        String rsn = Optional.ofNullable(reason.getValue()).orElse("").trim();
        req.setReason(rsn);

        boolean success = service.applyShiftAdjustment(req);
        if (success) {
            NotificationUtil.success("申請成功");
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            close();
        } else {
            NotificationUtil.error("申請失敗");
        }
    }


    // -------- tool --------
    private Component buildToolbar() {
        previewBtn.setText("預覽：調班前");
        previewBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        previewBtn.addClickListener(e -> {
            previewEnabled = !previewEnabled;
            updatePreviewBTN(previewBtn);
            refreshPreviewOnMatrixGrid();
        });

        // 員工多選過濾（只影響時間軸顯示）
        employeeFilter.setPlaceholder("員工...");
        employeeFilter.setClearButtonVisible(true);
        employeeFilter.setItemLabelGenerator(Option::getName);
        employeeFilter.addValueChangeListener(e -> scheduleMatrixGrid.applyEmployeeFilter(
                e.getValue().stream()
                        .map(Option::getValue)
                        .collect(Collectors.toSet())
        ));

        var toolbar = new ToolBar();
        toolbar.addLeft(previewBtn);
        toolbar.addCenter(monthTabs);
        toolbar.addRight(departmentSelector, employeeFilter);
        return toolbar;
    }

    /**
     * 新增模式月份顯示
     */
    private void configureTimeTab(LocalDate applyDate, Integer deptId) {
        monthTabs.add(currentMonthTab, nextMonthTab);
        currentMonthTab.addClassName("bordered-tabs");
        nextMonthTab.addClassName("bordered-tabs");
        monthTabs.setWidthFull();
        monthTabs.setSelectedTab(currentMonthTab); // 打開預設本月
        currentMonthTab.getElement().getStyle().set("font-size", "var(--lumo-font-size-l)");
        currentMonthTab.getElement().getStyle().set("font-weight", "600");
        nextMonthTab.getElement().getStyle().set("font-size", "var(--lumo-font-size-l)");
        nextMonthTab.getElement().getStyle().set("font-weight", "600");
        LocalDate currentMonthFirst = now.withDayOfMonth(1);
        LocalDate nextMonthFirst = now.plusMonths(1).withDayOfMonth(1);
        currentMonthTab.setLabel(DateUtil.format(currentMonthFirst.atStartOfDay(), YYYY_MM.getPattern(), DateUtil.Zone.P_8.getZoneId()));
        nextMonthTab.setLabel(DateUtil.format(nextMonthFirst.atStartOfDay(), YYYY_MM.getPattern(), DateUtil.Zone.P_8.getZoneId()));
        // Tabs 切換事件 → 切月份
        monthTabs.addSelectedChangeListener(e -> {
//            if (viewOnly) {
//                // 檢視模式時不要因為程式切 Tab 再去打後端
//                return;
//            }
            previewEnabled = false;
            updatePreviewBTN(previewBtn);

            Tab selected = e.getSelectedTab();
            if (selected == currentMonthTab) {
                // 本月
                selectedDate = applyDate.withDayOfMonth(1);
            } else if (selected == nextMonthTab) {
                // 下個月
                selectedDate = applyDate.plusMonths(1).withDayOfMonth(1);
            }
            pendingChanges.clear();
            changeProvider.refreshAll();
            updateDateSelector(selectedDate);
            updateShiftSchedules(deptId);
            updateMatrixGrid(previewShift);
            if(requestVO != null) {
                setApplyFormData(requestVO);

            }
        });
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
        Map<Integer, Option<Integer>> departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
        departmentSelector.setItems(departmentList);
        departmentSelector.setItemLabelGenerator(Option::getName);
        departmentSelector.setPlaceholder("部門...");
        departmentSelector.setEnabled(false);
        departmentSelector.setWidth("10em");
//        departmentSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        departmentSelector.setValue(departmentMap.get(DeptId));
    }

    // -------- MatrixGrid --------
    private void configureMatrixGrid() {
        scheduleMatrixGrid.getContent().setHeight(null);     //重設高度
        scheduleMatrixGrid.getContent().setWidthFull();
        scheduleMatrixGrid.addCellClickListener(this::onCellClick);
    }

    /**
     * MatrixGrid cell click → 帶入「員工 + 日期 + 原班別」
     */
    private void onCellClick(ScheduleMatrixGrid.CellClickEvent event) {
        if (event == null) {
            return;
        }
        ShiftSchedulesQueryVO row = event.getRow();
        LocalDate date = event.getDate();
        if (row == null || date == null) {
            return;
        }

        Integer empId = row.getEmployeeId();
        if (empId != null) {
            Option<Integer> empOpt = employeeOptionMap.get(empId);
            if (empOpt != null) {
                employee.setValue(empOpt);
            }
        }

        // 設定日期，並強制更新原班別（避免同一天不觸發 valueChange 的情況）
        datePicker.setValue(date);
        updateFromShiftByDate(date);
        targetShiftCombo.focus();
    }

    /**
     * 根據傳入月份與部門，更新顯示區間
     */
    private void updateDateSelector(LocalDate applyDate) {
        LocalDate start = applyDate.withDayOfMonth(1);
        LocalDate end = applyDate.withDayOfMonth(applyDate.lengthOfMonth());
        periods = service.getShiftSchedulePeriods(
                DateUtil.format(start.atStartOfDay(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId())
                , DateUtil.format(end.atStartOfDay(), YYYY_MM_DD.getPattern(), DateUtil.Zone.P_8.getZoneId()));
        displayStart = periods.get(0).getStartDate();
        displayEnd = periods.get(periods.size() - 1).getEndDate();
    }

    private void updateShiftSchedules(Integer deptId) {
        List<ShiftSchedules> raw =
                service.queryShiftSchedules(
                        displayStart.toString(),
                        displayEnd.toString(),
                        deptId
                );
        baseShift = service.convertToShiftSchedulesQueryVO(raw);
        previewShift = service.deepCopySchedule(baseShift);
        employeeList = baseShift.stream()
                .filter(vo -> vo.getEmployeeId() != null)
                .map(vo -> new Option<>(vo.getNickName(), vo.getEmployeeId()))
                .distinct()
                .sorted(Comparator.comparing(Option::getValue))
                .collect(Collectors.toList());
    }

    /**
     * 根據傳入班表資料更新預覽MatrixGrid顯示
     */
    private void updateMatrixGrid(List<ShiftSchedulesQueryVO> viewSchedule) {
        Set<Option<Integer>> keep = employeeFilter.getValue();
        employeeFilter.setItems(employeeList);
        employee.setItems(employeeList);
        employeeOptionMap = ToolUtil.transToMap(employeeList, Option::getValue);
        scheduleMatrixGrid.setData(viewSchedule, shiftTypeMap, periods, displayStart, displayEnd, selectedDate);
        employeeFilter.setValue(keep);
    }

    // -------- SelectForm --------

    private VerticalLayout buildSelectForm() {
        selectForm = new VerticalLayout();
//        selectForm.setWidthFull();
        selectForm.addClassName("grid-content");
        selectForm.add(new H4("選擇班別"));

        // 員工下拉可選同部門；切換員工後，如有日期則重新帶原班別。
        employee.setPlaceholder("員工...");
        employee.setItemLabelGenerator(Option::getName);
        employee.addValueChangeListener(e -> {
            // 切換員工時，如已選日期則重算原班別
            if (datePicker.getValue() != null) {
                updateFromShiftByDate(datePicker.getValue());
            }
        });

        datePicker.setPlaceholder("調班日期");
        datePicker.addValueChangeListener(e -> updateFromShiftByDate(e.getValue()));

        originalShift.setPlaceholder("原始班別");
        targetShiftCombo.setPlaceholder("調班班別");

        originalShift.setItems(shiftTypes);
        originalShift.setItemLabelGenerator(s -> Optional.ofNullable(s.getShiftName()).orElse("未知班別"));
        targetShiftCombo.setItems(shiftTypes);
        targetShiftCombo.setItemLabelGenerator(s -> Optional.ofNullable(s.getShiftName()).orElse("未知班別"));

        FormLayout form = new FormLayout();
        form.addClassName("grid-content");
        form.addFormItem(employee, "員工");
        form.addFormItem(datePicker, "日期");
        form.addFormItem(originalShift, "原始班別");
        form.addFormItem(targetShiftCombo, "調班班別");
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("80em", 2)
        );

        var formTool = new ToolBar();
        formTool.addRight(addBtn);

        // 新增暫存（加入 pendingChanges進行預覽）
        addBtn.addClickListener(e -> onAddChange());

        var valueBox = new VerticalLayout();
        valueBox.add(new H4("申請原因"));
        valueBox.add(reason);
        valueBox.addClassName("grid-content");

        selectForm.add(form, formTool);
        var returnBox = new VerticalLayout(selectForm, valueBox);
        returnBox.setPadding(false);

        return returnBox;
    }

    /**
     * (1)日期選擇改變時，從 originalShift 中找出該員工該日的「原班別」。
     * (2)在換班的班別中，剔除原本的班別。
     */
    private void updateFromShiftByDate(LocalDate date) {
        if (date == null) {
            originalShift.clear();
            targetShiftCombo.clear();
            return;
        }
        Integer empId = Optional.ofNullable(employee.getValue()).map(Option::getValue).orElse(null);
        // 在本月資料中找該員工當天班別
        String fromKey = service.findShiftKeyByEmpAndDate(empId, date, baseShift);
//        log.info("fromKey: " + fromKey);
        originalShift.setValue(Optional.ofNullable(shiftTypeMap.get(fromKey)).orElse(null));
        // 在換班的班別中，剔除原本的班別。
        List<ShiftType> shiftPass = new ArrayList<>();
        if (fromKey != null) {
            shiftPass = shiftTypes.stream()
                    .filter(s -> !Objects.equals(s.getShiftKey(), fromKey))
                    .collect(Collectors.toList());
        }
        targetShiftCombo.setItems(shiftPass);
        targetShiftCombo.clear();
    }

    /**
     * 調班列表
     */
    private VerticalLayout buildApplyListForm() {
        VerticalLayout applyForm = new VerticalLayout();
        applyForm.addClassName("grid-content");
        applyForm.add(new H4("調班列表"));
        changeGrid.setDataProvider(changeProvider);

        changeGrid.addColumn(s -> baseShift.stream()
                .filter(v -> Objects.equals(v.getEmployeeId(), s.getEmployeeId()))
                .map(ShiftSchedulesQueryVO::getNickName)
                .findFirst()
                .orElse(String.valueOf(s.getEmployeeId()))
        ).setHeader("員工");

        var date = changeGrid.addColumn(c -> Optional.ofNullable(c).map(ShiftChangePreview::getDate).orElse(null))
                .setHeader("日期").setAutoWidth(true);

        // 原班別（彩色方塊 + 名稱）
        changeGrid.addColumn(new ComponentRenderer<>(c -> pill(c.getFromShiftKey()))).setHeader("原始班別").setAutoWidth(true);
        // 調換班別（彩色方塊 + 名稱）
        changeGrid.addColumn(new ComponentRenderer<>(c -> pill(c.getToShiftKey()))).setHeader("調換班別").setAutoWidth(true);
        applyForm.add(changeGrid);

        // 刪除按鈕
        changeGrid.addColumn(new ComponentRenderer<>(c -> {
            Button delete = new Button("刪除");
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                pendingChanges.remove(c);
                changeProvider.refreshAll();
                refreshPreviewOnMatrixGrid();
            });
            delete.setVisible(!viewOnly);   //檢視模式不顯示刪除按鈕
            return delete;
        })).setHeader("操作").setAutoWidth(true).setFlexGrow(1);
        applyForm.add(changeGrid);

        changeGrid.sort(List.of(new GridSortOrder<>(date, SortDirection.ASCENDING)));

        return applyForm;
    }

    /**
     * 調班列表的原班別、調換班別（上顏色+名稱）
     */
    private Div pill(String key) {
        Div tag = new Div();
        ShiftType shiftType = shiftTypeMap.get(key);
        String name = Optional.ofNullable(shiftType).map(ShiftType::getShiftName).orElse("NA");     // 名稱
        String color = service.resolveShiftColorCode(key, shiftTypeMap);
        tag.setText(name);
        tag.getStyle().set("background-color", color);
        tag.getStyle().set("color", ToolUtil.getTextColorForHexBackground(color));
        tag.getStyle().set("padding", "0.4rem 0.6rem");
        tag.getStyle().set("border-radius", "6px");
        tag.getStyle().set("text-align", "center");
        return tag;
    }

    /**
     * 新增一筆暫存調班，先移除員工舊紀錄，再加入新紀錄，通知時間軸套用暫存變更（預覽）。
     */
    private void onAddChange() {
        //檢查欄位是否有選擇
        if (employee.getValue() == null || datePicker.getValue() == null || targetShiftCombo.getValue() == null) {
            Notification.show("請選擇完整");
            return;
        }
        if (datePicker.isInvalid()) {
            Notification.show("日期不在範圍內");
            return;
        }

        Integer empId = employee.getValue().getValue();
        LocalDate date = datePicker.getValue();
        //  取得當天原班別，新增或覆蓋同員工同日的暫存
        pendingChanges.removeIf(c -> Objects.equals(c.getEmployeeId(), empId)
                && Objects.equals(c.getDate(), date));
        pendingChanges.add(new ShiftChangePreview(
                empId,
                date,
                service.findShiftKeyByEmpAndDate(empId, date, baseShift),
                targetShiftCombo.getValue().getShiftKey()
        ));
        changeProvider.refreshAll();

        if (previewEnabled) {
            refreshPreviewOnMatrixGrid();
        }
    }

    /**
     * 依目前 previewEnabled & pendingChanges，重畫上方 MatrixGrid。
     */
    private void refreshPreviewOnMatrixGrid() {
        List<ShiftSchedulesQueryVO> base = service.deepCopySchedule(baseShift);
        if (previewEnabled) {
            service.applyPreviewOnSchedulesInPlace(base, pendingChanges, shiftTypeMap);
        }
        scheduleMatrixGrid.updateItems(base);
    }

}
