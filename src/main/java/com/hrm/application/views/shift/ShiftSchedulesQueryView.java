package com.hrm.application.views.shift;

import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.ShiftSchedulePeriodHoliday;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Scope("prototype")
@Route(value = "shiftSchedules", layout = MainLayout.class)
@MenuRouter(label = "ShiftSchedules", icon = VaadinIcon.CALENDAR)
@PageTitle("部門班表 | 人力資源管理系統")
public class ShiftSchedulesQueryView extends VerticalLayout {
    private ShiftScheduleService service;

    // data
    private List<ShiftType> shiftTypeList;
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();
    private List<Option<Integer>> departmentList;
    private Map<Integer, Option<Integer>> departmentMap;
    private final LocalDate now = LocalDate.now(); // 進入功能的日期
    private List<Integer> years;
    private TextField countDetail = new TextField();

    // selected data
    private LocalDate selectedDate = now; // 目前選擇的年月
    private List<ShiftSchedulePeriod> periods;


    //從api getPeriods後的日期資料
    private LocalDate startDate;
    private int selectDays;


    // selector
    private final Button leftButton = new Button("<");
    private final ComboBox<Integer> yearPicker = new ComboBox<>();
    private final ComboBox<Integer> monthPicker = new ComboBox<>();
    private final Button rightButton = new Button(">");
    private final MultiSelectComboBox<Option<Integer>> nickNameFilter = new MultiSelectComboBox<>();
    private final ComboBox<Option<Integer>> departmentSelector = new ComboBox<>();

    // grid
    Grid<ShiftSchedulesQueryVO> grid = new Grid<>(ShiftSchedulesQueryVO.class, false);
    private HeaderRow dayWeekHeader = grid.prependHeaderRow();
    private HeaderRow dayOfWeekHeader = grid.prependHeaderRow();
    private HeaderRow weekHeader = grid.prependHeaderRow();
    private HeaderRow monthHeader = grid.prependHeaderRow();

    private ShiftSchedulesQueryDialog dialog;


    public ShiftSchedulesQueryView(ShiftScheduleService service) {
        this.service = service;
        this.addClassName("background-plan");
        setSizeFull();
    }

    private void setData() {
        shiftTypeList = service.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, ShiftType::getShiftKey);
        departmentList = service.getDepartmentOptionList();
        departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
        years = IntStream.range(now.getYear() - 1, now.getYear() + 2)
                .boxed()
                .collect(Collectors.toList());
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("ShiftData");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();
        // 中間的日期選擇器佈局，居中顯示
        HorizontalLayout centerLayout = new HorizontalLayout(configureDateSelector());
        centerLayout.setWidthFull();
        centerLayout.setJustifyContentMode(JustifyContentMode.CENTER);  // 設置為居中

        // 右側的過濾器佈局
        HorizontalLayout tool2 = new HorizontalLayout();
        departmentSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        configureDepartmentSelector();
        nickNameFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        configureFilter();
        tool2.add(countDetail, nickNameFilter, departmentSelector);

        // 將各個部分新增到工具欄
        toolbar.add(centerLayout, tool2);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.BASELINE);
        return toolbar;
    }

    /**
     * 設定DateSelector ToolBar
     */
    private HorizontalLayout configureDateSelector() {
        yearPicker.setItems(years);
        yearPicker.setValue(selectedDate.getYear());
        yearPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        yearPicker.setWidth("6em");
        List<Integer> months = IntStream.rangeClosed(1, 12)
                .boxed()
                .collect(Collectors.toList());
        monthPicker.setItems(months);
        monthPicker.setValue(selectedDate.getMonth().getValue());
        monthPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        monthPicker.setItemLabelGenerator(value -> value + "月");
        monthPicker.setWidth("5em");
        // 拼接的查詢日期顯示
        HorizontalLayout DatePickerHt = new HorizontalLayout(yearPicker, monthPicker);
        // 佈局
        HorizontalLayout DateToolHt = new HorizontalLayout(leftButton, DatePickerHt, rightButton);
        DateToolHt.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return DateToolHt;
    }

    // 左箭頭，減少一個月
    private void decrementMonth() {
        selectedDate = selectedDate.minusMonths(1);
        if (selectedDate.getYear() < Collections.min(years)) {
            selectedDate = selectedDate.withYear(Collections.min(years)).withMonth(1);
            return;
        }
        updateDateComboBox();
    }

    // 右箭頭，增加一個月
    private void incrementMonth() {
        selectedDate = selectedDate.plusMonths(1);
        if (selectedDate.getYear() > Collections.max(years)) {
            selectedDate = selectedDate.withYear(Collections.max(years)).withMonth(12);
            return;
        }
        updateDateComboBox();
    }

    private void updateDateComboBox() {
        int year = selectedDate.getYear();
        int month = selectedDate.getMonth().getValue();
        yearPicker.setValue(year);
        monthPicker.setValue(month);
    }

    // 更新 DatePicker 的值
    private void updateSelectedDate() {
        if (yearPicker.isEmpty() || monthPicker.isEmpty()) {
            return;
        }
        int year = yearPicker.getValue();
        Month month = Month.of(monthPicker.getValue());
        selectedDate = LocalDate.of(year, month, 1); // 選擇該月份的第一天
    }

    private void configureDepartmentSelector() {
        Integer userDepartmentId = Optional.ofNullable(SessionUtil.getUserInfo())
                .map(UserInfo::getDepartmentId)
                .orElse(null);
        departmentSelector.setItems(departmentList);
        departmentSelector.setItemLabelGenerator(Option::getName);
        departmentSelector.setPlaceholder("部門...");
        departmentSelector.setValue(departmentMap.get(userDepartmentId));
        departmentSelector.setWidth("10em");
        countDetail.setReadOnly(true);
        countDetail.getStyle().set("font-weight", "bold");
        countDetail.getStyle().set("--vaadin-input-field-readonly-border", "none");
        countDetail.setWidth("25em");
    }

    private void configureFilter() {
        nickNameFilter.setPlaceholder("員工...");
        nickNameFilter.setWidth("10em");
        nickNameFilter.setClearButtonVisible(true);
        nickNameFilter.setItemLabelGenerator(Option::getName);
    }

    // 設定Listener for DataUpdate
    private void setParameterListener() {
        leftButton.addClickListener(e -> decrementMonth());
        rightButton.addClickListener(e -> incrementMonth());
        yearPicker.addValueChangeListener(event -> {
            updateSelectedDate();
            updateSchedulesData();
        });
        monthPicker.addValueChangeListener(event -> {
            updateSelectedDate();
            updateSchedulesData();
        });
        departmentSelector.addValueChangeListener(event -> {
            nickNameFilter.clear();
            updateSchedulesData();
        });
        nickNameFilter.addValueChangeListener(event -> applyFilter());
        grid.asSingleSelect().addValueChangeListener(event -> checkShiftSchedules(event.getValue()));
    }

    private void updateSchedulesData() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = selectedDate;
        date = date.withDayOfMonth(1);
        String startDateTemp = date.format(dateFormatter);
        date = date.plusMonths(1).plusDays(-1);
        String endDateTemp = date.format(dateFormatter);
        //用當月資料request periodsTemp, 拿periodsTemp成為endDate來抓取完整list
        List<ShiftSchedulePeriod> periodsTemp = service.getShiftSchedulePeriods(startDateTemp, endDateTemp);
        startDate = periodsTemp.getFirst().getStartDate();
        LocalDate endDate = periodsTemp.getLast().getEndDate();
        periods = service.getShiftSchedulePeriods(startDate.format(dateFormatter), endDate.format(dateFormatter));
        selectDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Integer selectedDepartment = departmentSelector.getValue() != null
                ? departmentSelector.getValue().getValue()
                : null;
        List<ShiftSchedulesQueryVO> shiftSchedulesList = service.queryShiftSchedulesVO(startDate.format(dateFormatter), endDate.format(dateFormatter), selectedDepartment);
        configureGrid(shiftSchedulesList);  // 重新產生 Grid
        resetEmployeeFilter(shiftSchedulesList);
        // ---- 總計人數, 假日 ----
        //int empCount = shiftSchedulesList == null ? 0 : shiftSchedulesList.size();
        List<String> restKey = Arrays.asList("REST");
        List<String> regularKey = Arrays.asList("REGULAR");
        List<String> nationalKey = Arrays.asList("NATIONAL");
        countDetail.setValue(String.format("[休假日: %d][例假日: %d][國定假日: %d]"
                , getHolidayShiftTypeCount(periods, restKey)
                , getHolidayShiftTypeCount(periods, regularKey)
                , getHolidayShiftTypeCount(periods, nationalKey)
        ));
    }

    private void applyFilter() {
        ListDataProvider<ShiftSchedulesQueryVO> provider = (ListDataProvider<ShiftSchedulesQueryVO>) grid.getDataProvider();
        provider.clearFilters();

        if (!nickNameFilter.isEmpty()) {
            Set<Integer> selectedIds = nickNameFilter.getValue().stream().map(Option::getValue).collect(Collectors.toSet());
            provider.addFilter(schedules ->
                    selectedIds.contains(schedules.getEmployeeId()));
        }
    }

    /**
     * 更新員工篩選列表
     */
    private void resetEmployeeFilter(List<ShiftSchedulesQueryVO> shiftSchedulesList) {
        // 保留之前選取的項目
        Set<Option<Integer>> currentValue = nickNameFilter.getValue();
        List<Option<Integer>> employeeList = shiftSchedulesList.stream()
                .filter(s -> Objects.nonNull(s.getEmployeeId()))
                .filter(s -> Strings.isNotBlank(s.getNickName()))
                .map(schedule -> new Option<>(schedule.getNickName(), schedule.getEmployeeId()))
                .sorted(Comparator.comparing(Option::getValue))
                .collect(Collectors.toList());
        nickNameFilter.setItems(employeeList);
        nickNameFilter.setValue(currentValue);
    }

    /**
     * 設定grid
     */
    private void configureGrid(List<ShiftSchedulesQueryVO> shiftSchedulesList) {
        grid.removeAllColumns();
        grid.getHeaderRows().clear();
        grid.setItems(shiftSchedulesList);

        if (shiftSchedulesList == null || shiftSchedulesList.isEmpty()) {
            return;
        }
        grid.addColumn(shiftSchedulesVO -> Optional.ofNullable(shiftSchedulesVO.getDepartmentName())
                        .orElse("未知部門"))
                .setHeader("部門").setKey("department").setFrozen(true).setVisible(false);
        grid.addColumn(shiftSchedulesVO ->
                        Optional.ofNullable(shiftSchedulesVO.getNickName())
                                .orElse("未知員工"))
                .setHeader("員工\\日期").setKey("nickName").setFrozen(true).setFooter(
                        setEmployeeFooterText("日班", "午班", "夜班", "休假"));

        List<String> headerArrayList = Arrays.asList("部門", "員工");
        // 為每一天創建
        for (int i = 0; i < selectDays; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Long> timeSlotCountMap = calculateTimeSlotCountsForDay(shiftSchedulesList, date, "SHIFT_TYPE"); // 將各 timeSlot 數量裝入 map

            grid.addColumn(new ComponentRenderer<>(scheduleVO -> {
                Map<LocalDate, ShiftSchedulesDateTimeQueryVO> schedulesDateMap = service.mapSchedulesDateByDate(scheduleVO);
                String shiftType = getShiftTypeForDate(schedulesDateMap, date);
                String shiftTypeName = Optional.ofNullable(shiftTypeMap.get(shiftType))
                        .map(ShiftType::getShiftName)
                        .orElse("未知班別");
                String backgroundColor = getShiftColorCodeForDate(schedulesDateMap, date);
                String fontColor = ToolUtil.getTextColorForHexBackground(backgroundColor);
                Byte status = getShiftStatusForDate(schedulesDateMap, date);

                // 根據條件設置文本內容
                String text;
                if (status != null && status == 1) {
                    text = "請";
                } else if (shiftType != null && shiftType.contains("NATIONAL")) {
                    text = "國";
                } else {
                    text = shiftType == null || shiftType.equals("NA") ? "NA" : (shiftType.contains("HOLIDAY") ? "假" : "班");
                }

                return setupCellDiv(text, backgroundColor, fontColor, shiftTypeName);
            })).setFooter(setEmployeeFooterText(
                    String.valueOf(timeSlotCountMap.getOrDefault("morning", 0L)),
                    String.valueOf(timeSlotCountMap.getOrDefault("afternoon", 0L)),
                    String.valueOf(timeSlotCountMap.getOrDefault("night", 0L)),
                    String.valueOf(
                            Optional.ofNullable(calculateStatusCountsForDay(shiftSchedulesList, date, (byte) 1, null)).orElse(0L)
                                    + Optional.ofNullable(calculateStatusCountsForDay(shiftSchedulesList, date, (byte) 0, "HOLIDAY")).orElse(0L)
                    )
            ));
            setUpHeadersForDate(i + 1, date, headerArrayList.size());
        }
        grid.addClassNames("shiftSchedules-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.getColumns().forEach(col -> col.setTextAlign(ColumnTextAlign.CENTER));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.getStyle().set("--vaadin-grid-cell-padding", "0px");
    }

    private Div setupCellDiv(String text, String backgroundColor, String fontColor, String toolTip) {
        Div cellDiv = new Div();
        cellDiv.setText(text); // 設定文本內容
        cellDiv.getStyle().set("background-color", backgroundColor == null ? "#FFFFFF" : backgroundColor); // 默認為白色
        cellDiv.getStyle().set("color", fontColor == null ? "#000000" : fontColor); // 默認為黑色
        cellDiv.getStyle().set("padding", "5px"); // 添加一些內距
        cellDiv.getElement().setProperty("title", toolTip == null ? "" : toolTip); // toolTip 默認為空
        return cellDiv;
    }

    private String getShiftTypeForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> schedulesDateMap, LocalDate date) {
        return schedulesDateMap.get(date) != null ? Optional.ofNullable(schedulesDateMap.get(date).getShiftTypes()).orElse("NA") : "NA";
    }

    private String getShiftColorCodeForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> schedulesDateMap, LocalDate date) {
        return schedulesDateMap.get(date) == null ? "#ffffff" : schedulesDateMap.get(date).getStatus() == 1
                ? "#000000" : Optional.ofNullable(schedulesDateMap.get(date).getShiftColorCode()).orElse("#ffffff");
    }

    private Byte getShiftStatusForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> schedulesDateMap, LocalDate date) {
        return schedulesDateMap.get(date) == null ? null : Optional.ofNullable(schedulesDateMap.get(date).getStatus()).orElse(null);
    }

    private void setUpHeadersForDate(int day, LocalDate date, int headerArrayListSize) {
        int index = day + headerArrayListSize - 1;

        // 設置 dayWeekHeader 顯示日期
        dayWeekHeader.getCell(grid.getColumns().get(index)).setText(date.format(DateTimeFormatter.ofPattern("dd")) + "號");
        var cellDW = dayWeekHeader.getCell(grid.getColumns().get(index));
        int periodIndex = getPeriodIndexForDate(date, periods);
        cellDW.setPartName("cell-period" + periodIndex);

        // 設置 dayWeekHeader 顯示月
        monthHeader.getCell(grid.getColumns().get(index)).setText(date.format(DateTimeFormatter.ofPattern("MM")) + "月");

        // 設置 dayOfWeekHeader 顯示星期幾
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
        var cellDOW = dayOfWeekHeader.getCell(grid.getColumns().get(index));
        String holiday = getCalendarHolidayForDate(date, periods);
        if (holiday != null) {
            cellDOW.setText(dayOfWeek);
            cellDOW.setPartName("holiday-cell");
        } else {
            cellDOW.setText(dayOfWeek);
        }
        // 設 weekHeader 顯示週數
        int weekOfYear = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        weekHeader.getCell(grid.getColumns().get(index)).setText("W" + weekOfYear);
        monthHeader.getCell(grid.getColumns().get(1)).setText("月份");
        dayOfWeekHeader.getCell(grid.getColumns().get(1)).setText("星期");
        weekHeader.getCell(grid.getColumns().get(1)).setText("週數");
    }

    private String getCalendarHolidayForDate(LocalDate date, List<ShiftSchedulePeriod> periods) {
        return periods.stream()
                .flatMap(period -> period.getHolidays().stream())
                .filter(holiday -> holiday.getShiftDate().equals(date))
                .map(ShiftSchedulePeriodHoliday::getShiftTypes)
                .findFirst()
                .orElse(null);
    }

    private int getPeriodIndexForDate(LocalDate date, List<ShiftSchedulePeriod> periods) {
        boolean toggle = true;
        for (ShiftSchedulePeriod period : periods) {
            LocalDate startDate = period.getStartDate();
            LocalDate endDate = period.getEndDate();
            if ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate))) {
                return toggle ? 1 : 0;
            }
            toggle = !toggle;
        }
        return 0;
    }

    // 設定footer----------------------------------------------------------------------------------------------------

    private VerticalLayout setEmployeeFooterText(String dayShift, String afternoonShift, String nightShift, String holidayShift) {
        VerticalLayout gridFooterVt = new VerticalLayout();
        gridFooterVt.addClassName("footer-vertical-layout");

        // 使用 setUpLayout 和 setUpSpan 簡化代碼
        HorizontalLayout layout1 = setUpLayout(setUpSpan(dayShift, "#000000"));
        HorizontalLayout layout2 = setUpLayout(setUpSpan(afternoonShift, "#d2691e"));
        HorizontalLayout layout3 = setUpLayout(setUpSpan(nightShift, "#800080"));
//        HorizontalLayout layout4 = setUpLayout(setUpSpan(leaveShift, "#006400"));
        HorizontalLayout layout5 = setUpLayout(setUpSpan(holidayShift, "#0000cd"));
//        HorizontalLayout layout6 = setUpLayout(setUpSpan(noShift, "#8b0000"));
//        HorizontalLayout layout7 = setUpLayout(setUpSpan(totalSize, "#222222"));

        gridFooterVt.add(layout1, layout2, layout3, layout5);
        return gridFooterVt;
    }

    private Span setUpSpan(String text, String color) {
        Span span = new Span(text);
        span.getStyle().set("color", color);
        span.getStyle().set("font-weight", "bold");
        return span;
    }

    private HorizontalLayout setUpLayout(Span span) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.add(span);
        return layout;
    }

    // 計算各個timeSlot數量----------------------------------------------------------------------------------------------------

    // 統計各時段數量#status=0
    private Map<String, Long> calculateTimeSlotCountsForDay(List<ShiftSchedulesQueryVO> shiftSchedulesList, LocalDate targetDate, String shiftKeyword) {
        Map<String, Long> result = shiftSchedulesList.stream()
                .flatMap(shiftSchedule -> shiftSchedule.getSchedulesDates().stream())
                .filter(shiftDate -> shiftDate.getShiftDate().equals(targetDate))
                .filter(shiftDate -> shiftDate.getStatus() == 0)
                .filter(shiftDate -> shiftKeyword == null || shiftDate.getShiftTypes().contains(shiftKeyword))
                .map(shiftDate -> Optional.ofNullable(shiftTypeMap.get(shiftDate.getShiftTypes()))
                        .map(ShiftType::getTimeSlot)
                        .orElse("noShift")) // 如果為 null，設置預設值 "noShift"
                .filter(timeSlot -> timeSlot != null && !timeSlot.isEmpty()) // 過濾掉空值
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 將未出現的時段設置為 0
        result.keySet().forEach(slot -> result.putIfAbsent(slot, 0L));

        return result;
    }

    // 統計status數量
    private Long calculateStatusCountsForDay(List<ShiftSchedulesQueryVO> shiftSchedulesList, LocalDate targetDate, Byte status, String shiftKeyword) {
        Long result = shiftSchedulesList.stream()
                .flatMap(shiftSchedule -> shiftSchedule.getSchedulesDates().stream())
                .filter(shiftDate -> shiftDate.getShiftDate().equals(targetDate))
                .filter(shiftDate -> shiftDate.getStatus().equals(status))
                .filter(shiftDate -> shiftKeyword == null || shiftDate.getShiftTypes().contains(shiftKeyword))
                .collect(Collectors.counting());
        return result;
    }

    // 統計各別假日數
    private long getHolidayShiftTypeCount(List<ShiftSchedulePeriod> periods, List<String> shiftTypeKeyword) {
        return periods.stream()
                .flatMap(period -> period.getHolidays().stream()) // 展平所有 holidays
                .filter(holiday -> shiftTypeKeyword == null || (holiday.getShiftTypes() != null
                        && shiftTypeKeyword.stream().anyMatch(keyword -> holiday.getShiftTypes().contains(keyword))))
                .count();
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.access(() -> {
            try {
                setData(); // 執行會觸發 webClient.block() 的方法
                updateSchedulesData();
                setParameterListener();
                add(titleConfigure(), getToolbar(), getContent());
                configureDialog(null);
            } catch (Exception e) {
                NotificationUtil.error("載入資料失敗：" + e.getMessage());
            }
        });
    }

    // ---- dialog ----
    private void configureDialog(ShiftSchedulesQueryVO shiftSchedules) {
        dialog = new ShiftSchedulesQueryDialog(shiftTypeList, shiftTypeMap, selectedDate, shiftSchedules);
        dialog.addUpdateListener(this::manuallyAdjustShiftSchedules);
        dialog.addCloseListener(e -> closeEditor());
    }

    private void closeEditor() {
        grid.asSingleSelect().clear();
        dialog.close();
    }

    //查看
    public void checkShiftSchedules(ShiftSchedulesQueryVO schedules) {
        if (schedules == null) {
            closeEditor();
        } else {
            configureDialog(schedules);
            dialog.setShiftSchedules(schedules);
            dialog.open();
            addClassName("checking");
        }
    }

    //儲存1
    public void saveShiftSchedules(ShiftSchedulesQueryDialog.UpdateEvent event) {
        ShiftSchedulesQueryVO schedulesVO = event.getShiftSchedules();
        List<ShiftSchedules> personalSchedulesList = service.convertToShiftSchedules(schedulesVO);
        boolean success = service.savePersonalShiftSchedules(personalSchedulesList);
        if (success) {
            Notification.show("儲存成功");
            updateSchedulesData();
            closeEditor();
        } else {
            Notification.show("儲存失敗");
        }
    }

    //儲存2
    public void manuallyAdjustShiftSchedules(ShiftSchedulesQueryDialog.UpdateEvent event) {
        List<ShiftSchedules> personalSchedulesList = service.convertToShiftSchedules(event.getShiftSchedules());
        boolean success = service.manuallyAdjustShiftSchedules(personalSchedulesList);
        if (success) {
            Notification.show("儲存成功");
            log.info("nickNameFilter0" + nickNameFilter.getValue());

            updateSchedulesData();
            closeEditor();
        } else {
            Notification.show("儲存失敗");
        }
    }

}
