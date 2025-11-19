package com.hrm.application.views.shift.shiftSchedule3;


import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.ShiftSchedulePeriodHoliday;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScheduleMatrixGrid extends Div {

    // 模式
    public enum Mode { PERSONAL, MANAGEMENT }
    private Mode mode = Mode.PERSONAL;
    public void setMode(Mode mode) { this.mode = (mode == null ? Mode.PERSONAL : mode); }
    private boolean isManagement() { return mode == Mode.MANAGEMENT; }

    // 常數
    private static final byte STATUS_LEAVE = 1;         // status==1: 請假
    private static final byte ACTION_LOCKED = 1;        // actionType==1: 鎖住
    private static final String NO_ASSIGN = "NA";       // 無班別
    private static final String SHIFT_TYPE_TOKEN = "SHIFT_TYPE";
    private static final String[] DOW_ZH_NUM = {"一","二","三","四","五","六","日"};

    // Grid 與表頭
    private Grid<ShiftSchedulesQueryVO> grid = new Grid<>(ShiftSchedulesQueryVO.class, false);
    private HeaderRow dayHeader;
    private HeaderRow dowHeader;
    private HeaderRow weekHeader;

    // 日期欄位&清單
    private final List<Grid.Column<ShiftSchedulesQueryVO>> dayColumns = new ArrayList<>();
    private final List<LocalDate> dayList = new ArrayList<>();

    // 表頭元件
    private final List<Span> weekHeaderSpans = new ArrayList<>();
    private final List<Span> dowHeaderSpans  = new ArrayList<>();
    private final List<Span> dayHeaderSpans  = new ArrayList<>();

    // 資料
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();

    /** 核心期間（可編輯）：傳入本月雙週 monthlyPeriods（不含延伸） */
    private List<ShiftSchedulePeriod> periods = new ArrayList<>();

    private LocalDate displayStart;
    private LocalDate displayEnd;
    private LocalDate selectedMonth;

    // 目前登入員工
    private final Integer currentUserId =
            Optional.ofNullable(SessionUtil.getUserInfo()).map(u -> u.getId()).orElse(null);

    // 快取：row -> (date -> sd)
    private final Map<ShiftSchedulesQueryVO, Map<LocalDate, ShiftSchedulesDateTimeQueryVO>> rowDateIndex =
            new IdentityHashMap<>();

    public ScheduleMatrixGrid() {
        setSizeFull();
        initGrid();
        add(grid);
    }

    private void
    initGrid() {
        grid.addClassNames("shiftSchedules-grid");
//        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.getStyle().set("--vaadin-grid-cell-padding", "0px");
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    public void setData(List<ShiftSchedulesQueryVO> items,
                        Map<String, ShiftType> shiftTypeMap,
                        List<ShiftSchedulePeriod> periodsForHeader,
                        LocalDate displayStart,
                        LocalDate displayEnd,
                        LocalDate selectedMonth) {
        this.shiftTypeMap = (shiftTypeMap != null) ? shiftTypeMap : new HashMap<>();
        this.periods = (periodsForHeader != null) ? periodsForHeader : new ArrayList<>();
        this.displayStart = displayStart;
        this.displayEnd = displayEnd;
        this.selectedMonth = selectedMonth;
        rebuildGrid(items);
    }

    /** 提供 View 取資料做儲存 */
    public List<ShiftSchedulesQueryVO> getAllRows() {
        if (!(grid.getDataProvider() instanceof ListDataProvider)) return Collections.emptyList();
        Collection<ShiftSchedulesQueryVO> items =
                ((ListDataProvider<ShiftSchedulesQueryVO>) grid.getDataProvider()).getItems();
        return new ArrayList<>(items);
    }

    // ======================= Rebuild =======================

    private void rebuildGrid(List<ShiftSchedulesQueryVO> items) {
        removeAll();
        grid = new Grid<>(ShiftSchedulesQueryVO.class, false);
        initGrid();
        add(grid);

        // reset
        dayColumns.clear(); dayList.clear();
        weekHeaderSpans.clear(); dowHeaderSpans.clear(); dayHeaderSpans.clear();
        rowDateIndex.clear();

        grid.setItems(items);
        if (items == null || items.isEmpty() || displayStart == null || displayEnd == null) return;

        dayHeader  = grid.prependHeaderRow();
        dowHeader  = grid.prependHeaderRow();
        weekHeader = grid.prependHeaderRow();

        grid.addColumn(vo -> Optional.ofNullable(vo.getDepartmentName()).orElse("未知部門"))
                .setHeader("部門").setKey("department").setFrozen(true).setVisible(false);

        Grid.Column<ShiftSchedulesQueryVO> employeeNickNameColumn = grid.addColumn(vo -> Optional.ofNullable(vo.getNickName()).orElse("未知員工"))
                .setHeader("員工\\日期").setKey("nickName").setFrozen(true)
                .setComparator(Comparator.comparing(ShiftSchedulesQueryVO::getEmployeeNumber, String.CASE_INSENSITIVE_ORDER))
                .setFooter(setEmployeeFooterText("日班", "午班", "夜班"));

        grid.addColumn(vo -> countHolidayWorkTimes(vo, selectedMonth))
                .setKey("holidayCount")
                .setFrozen(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader(setUpSpan("值班", "var(--lumo-primary-color)"));

        List<LocalDate> days = generateDateRange(displayStart, displayEnd);
        dayList.addAll(days);

        // 預先統計
        Map<LocalDate, Map<String, Long>> slotCountsByDate = computeSlotCountsByDate(items, days);

        // 建立每一日的欄位
        for (LocalDate d : days) {
            Grid.Column<ShiftSchedulesQueryVO> dayCol =
                    buildDayColumn(d, slotCountsByDate.getOrDefault(d, Map.of()));
            dayColumns.add(dayCol);
        }

        // 表頭
        for (int i = 0; i < days.size(); i++) {
            setUpHeadersForDate(dayColumns.get(i), days.get(i));
        }
        setHeaderTitle();

        // 個人模式：需要反灰的
        if (!isManagement()) {
            Set<LocalDate> lockedByActionDates = computeHeaderLockedDatesForCurrentUser(items);
            for (LocalDate d : days) {
                if (shouldGreyHeader(d, lockedByActionDates)) {
                    greyHeader(d);
                }
            }
        }

        grid.getColumns().forEach(c -> {
            c.setAutoWidth(true);
            c.setTextAlign(ColumnTextAlign.CENTER);
        });
        grid.sort(List.of(new GridSortOrder<>(employeeNickNameColumn, SortDirection.ASCENDING)));

    }

    private Grid.Column<ShiftSchedulesQueryVO> buildDayColumn(LocalDate date, Map<String, Long> slotCounts) {
        ComponentRenderer<Div, ShiftSchedulesQueryVO> renderer = new ComponentRenderer<>(
                () -> {
                    Div cell = new Div();
                    cell.addClassName("shift-cell");
                    cell.getStyle().set("padding", "5px");
                    cell.getStyle().set("cursor", "pointer");
                    cell.getStyle().set("pointer-events", "auto");
                    cell.addClickListener(ev -> onCellClick(ev.getSource()));
                    return cell;
                },
                (cell, row) -> {
                    Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap = indexRow(row);
                    updateCell(cell, row, date, dateMap);
                }
        );

        Grid.Column<ShiftSchedulesQueryVO> col = grid.addColumn(renderer);

        String m = String.valueOf(slotCounts.getOrDefault("morning", 0L));
        String a = String.valueOf(slotCounts.getOrDefault("afternoon", 0L));
        String n = String.valueOf(slotCounts.getOrDefault("night", 0L));
        col.setFooter(setEmployeeFooterText(m, a, n));

        return col;
    }

    // =================== 點擊 / 更新 Cell ===================

    private void onCellClick(Div cell) {
        if ("1".equals(cell.getElement().getProperty("disabled"))) return;

        String emp = cell.getElement().getProperty("empId");
        String dateStr = cell.getElement().getProperty("date");

        if (emp == null || emp.isBlank() || dateStr == null || dateStr.isBlank()) return;

        final Integer empId;
        final LocalDate clickedDate;
        try {
            empId = Integer.valueOf(emp);
            clickedDate = LocalDate.parse(dateStr);
        } catch (Exception ex) { return; }

        // 個人模式：非當下員工不得操作
        if (!isManagement() && currentUserId != null && !currentUserId.equals(empId)) return;

        ShiftSchedulesQueryVO row = findRowByEmployeeId(empId);
        if (row == null) return;

        // 生成schedulesDateMap for 統計
        Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap = indexRow(row);

        String currentKey = getShiftTypeForDate(dateMap, clickedDate);
        ShiftType current = shiftTypeMap.get(currentKey);
        List<ShiftType> allTypes = shiftTypeMap.values().stream()
                .sorted(Comparator.comparing(ShiftType::getId, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
        String remark = getRemarkForDate(dateMap, clickedDate);
        ShiftSelectDialog dialog = new ShiftSelectDialog(clickedDate, allTypes, current, remark);
        dialog.addSaveListener(saveEv -> {
            ShiftType selected = saveEv.getShiftType();
            String mark = saveEv.getRemark();
            if (selected == null) return;

            // 更新資料
            indexRow(row).computeIfPresent(clickedDate, (d, sd) -> {
                sd.setShiftTypes(selected.getShiftKey());
                sd.setShiftColorCode(selected.getShiftColorCode());
                sd.setRemark(mark);
                return sd;
            });

            updateCell(cell, row, clickedDate, dateMap);

            try {
                grid.getDataProvider().refreshItem(row);
            } catch (ClassCastException ex) {
                grid.getDataProvider().refreshAll();
            }

            updateFooterForDate(clickedDate);

            getUI().ifPresent(ui ->
                    ui.beforeClientResponse(grid,
                            ctx -> grid.getElement().callJsFunction("requestContentUpdate")));
        });
        dialog.open();
    }

    private void updateCell(Div cell, ShiftSchedulesQueryVO row, LocalDate date, Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap) {
        Integer empId = (row != null ? row.getEmployeeId() : null);
        cell.getElement().setProperty("empId", empId == null ? "" : String.valueOf(empId));
        cell.getElement().setProperty("date", date == null ? "" : date.toString());


        String shiftTypeKey = (row == null) ? NO_ASSIGN : getShiftTypeForDate(dateMap, date);
        String shiftTypeName = Optional.ofNullable(shiftTypeMap.get(shiftTypeKey))
                .map(ShiftType::getShiftName)
                .orElse("未知班別");
        String remark = getRemarkForDate(dateMap, date);
        cell.getElement().setProperty("remark", date == null ? "" : remark);


        Byte status = (row == null) ? null : getShiftStatusForDate(dateMap, date);
        Byte actionType = (row == null) ? null : getActionTypeForDate(dateMap, date);

        // 內容文字
        cell.setText(
                isLeave(status) ? "請"
                        : (NO_ASSIGN.equals(shiftTypeKey) ? NO_ASSIGN
                        : shiftTypeKey.contains("NATIONAL") ? "國"
                        : shiftTypeKey.contains("HOLIDAY") ? "假" : "班")

        );
        cell.getElement().setProperty("title", shiftTypeName + "\n" + remark); //toolTip

        //含備註cell添加渲染
        if (!remark.isEmpty()) {
            // 建立右上角的小紅色三角形
            Div marker = new Div();
            marker.getStyle()
                    .set("width", "0")
                    .set("height", "0")
                    .set("border-bottom", "8px solid transparent")
                    .set("border-right", "8px solid red")
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("right", "0");
            cell.add(marker);
        }

        // 顏色
        String bg = (row == null) ? "#ffffff" : getShiftColorCodeForDate(dateMap, date);
        String fg = ToolUtil.getTextColorForHexBackground(bg);
        cell.getStyle().set("background-color", bg);
        cell.getStyle().set("color", fg);

        // 管理模式：一律可互動
        if (isManagement()) {
            setCellInteractive(cell);
            return;
        }

        // 個人模式：依規則處理
        if (lockedByDisplay(date)) {
            setCellLocked(cell);
            return;
        }
        if (lockedByAction(actionType)) {
            setCellLocked(cell);
            greyHeader(date);
            return;
        }
        if (blockedByOther(empId)) {
            setCellReadOnly(cell);
            return;
        }
        setCellInteractive(cell);
    }

    // ============ 規則（集中條件） ============
    private boolean lockedByDisplay(LocalDate date) {
        return !isManagement() && isDisplayOnly(date);
    }
    private boolean lockedByAction(Byte actionType) {
        return !isManagement() && actionType != null && actionType == ACTION_LOCKED;
    }
    private boolean blockedByOther(Integer empId) {
        return !isManagement() && currentUserId != null && !Objects.equals(empId, currentUserId);
    }
    private boolean shouldGreyHeader(LocalDate date, Set<LocalDate> actionLockedDates) {
        return !isManagement() && (isDisplayOnly(date) || actionLockedDates.contains(date));
    }

    // 樣式/互動行為統一方法
    private void setCellLocked(Div cell) {
        cell.getStyle().set("opacity", "0.55");
        cell.getStyle().set("pointer-events", "none");
        cell.getElement().setProperty("disabled", "1");
    }
    private void setCellReadOnly(Div cell) {
        cell.getStyle().set("opacity", "1");
        cell.getStyle().set("pointer-events", "none");
        cell.getElement().setProperty("disabled", "0");
    }
    private void setCellInteractive(Div cell) {
        cell.getStyle().set("opacity", "1");
        cell.getStyle().set("pointer-events", "auto");
        cell.getElement().setProperty("disabled", "0");
        cell.getElement().setAttribute("data-state", "interactive");
    }

    // ======================= Header =======================

    /** 將指定「日期欄」的週數/星期/日期三層表頭做反灰（個人模式用） */
    private void greyHeader(LocalDate date) {
        if (isManagement() || date == null || dayList.isEmpty()) return;
        int idx = dayList.indexOf(date);
        if (idx < 0) return;

        ensureHeaderListSize(weekHeaderSpans, idx);
        ensureHeaderListSize(dowHeaderSpans, idx);
        ensureHeaderListSize(dayHeaderSpans, idx);

        Span w = weekHeaderSpans.get(idx);
        Span d = dowHeaderSpans.get(idx);
        Span day = dayHeaderSpans.get(idx);
        if (w == null || d == null || day == null) return;

        w.getStyle().set("opacity", "0.55");
        d.getStyle().set("opacity", "0.55");
        day.getStyle().set("opacity", "0.55");
    }

    private void setHeaderTitle() {
        weekHeader.getCell(grid.getColumns().get(2)).setText(selectedMonth.format(DateTimeFormatter.ofPattern("MM")) + "月");
        dowHeader.getCell(grid.getColumns().get(2)).setText("假日");

    }

    private void ensureHeaderListSize(List<Span> list, int idx) {
        while (list.size() <= idx) list.add(null);
    }

    /** 自訂表頭：週數/星期/日期 粗體；當月日期紅字；延伸週時把「日期」加深灰底（個人模式） */
    private void setUpHeadersForDate(Grid.Column<ShiftSchedulesQueryVO> col, LocalDate date) {
        int idx = dayList.indexOf(date);

        // 週數
        int w = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        Span weekSpan = new Span("W" + w);
        weekSpan.getStyle().set("font-weight", "bold");
        weekSpan.getStyle().set("padding", "5px");
        weekHeader.getCell(col).setComponent(weekSpan);
//        weekHeader.getCell(col).setPartName("cell-period" + getPeriodIndexForDate(date, periods));

        // 星期
        String dowTxt = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        Span dowSpan = new Span(dowTxt);
        dowSpan.getStyle().set("font-weight", "bold");
        var dowCell = dowHeader.getCell(col);
        dowCell.setComponent(dowSpan);
        if (getCalendarHolidayForDate(date, periods) != null) {
            dowCell.setPartName("holiday-cell");
        }

        // 日期（dd）
        String dd = date.format(DateTimeFormatter.ofPattern("dd"));
        Span daySpan = new Span(dd);
        daySpan.getStyle().set("font-weight", "bold");
        // 當月日期紅字
        if (selectedMonth != null
                && date.getYear() == selectedMonth.getYear()
                && date.getMonthValue() == selectedMonth.getMonthValue()) {
            daySpan.getStyle().set("color", "red");
        }

        // 月份縮寫
        Span monSpan = new Span(monthAbbr(date));
        monSpan.getStyle().set("font-size", "10px");
        monSpan.getStyle().set("line-height", "1");
        monSpan.getStyle().set("opacity", "0.75");
        monSpan.getStyle().set("margin-bottom", "2px");

        // 當月日期紅字
        if (selectedMonth != null
                && date.getYear() == selectedMonth.getYear()
                && date.getMonthValue() == selectedMonth.getMonthValue()) {
            daySpan.getStyle().set("color", "red");
            monSpan.getStyle().set("color", "red");
        }

        // 垂直堆疊同一格 cell
        Div dayStack = new Div();
        dayStack.getStyle().set("display", "flex");
        dayStack.getStyle().set("flex-direction", "column");
        dayStack.getStyle().set("align-items", "center");
        dayStack.getStyle().set("line-height", "1");
        dayStack.add(monSpan, daySpan);

        var dayCell = dayHeader.getCell(col);
        dayCell.setComponent(dayStack);

        if (!isManagement() && isDisplayOnly(date)) {
            dayCell.setPartName("locked-day");
        }
        dayHeader.getCell(col).setPartName("cell-period" + getPeriodIndexForDate(date, periods));


        ensureHeaderListSize(weekHeaderSpans, idx);
        ensureHeaderListSize(dowHeaderSpans, idx);
        ensureHeaderListSize(dayHeaderSpans, idx);
        weekHeaderSpans.set(idx, weekSpan);
        dowHeaderSpans.set(idx, dowSpan);
        dayHeaderSpans.set(idx, daySpan);

        // 左側標題
        dowHeader.getCell(grid.getColumnByKey("nickName")).setText("星期");
        weekHeader.getCell(grid.getColumnByKey("nickName")).setText("週數");
    }

    // =================== 查找 / 快取 ===================

    private Map<LocalDate, ShiftSchedulesDateTimeQueryVO> indexRow(ShiftSchedulesQueryVO row) {
        return rowDateIndex.computeIfAbsent(row, r ->
                r.getSchedulesDates().stream()
                        .filter(sd -> sd.getShiftDate() != null)
                        .collect(Collectors.toMap(ShiftSchedulesDateTimeQueryVO::getShiftDate,
                                Function.identity(), (a,b) -> a)));
    }

    private ShiftSchedulesQueryVO findRowByEmployeeId(Integer empId) {
        if (empId == null) return null;
        if (!(grid.getDataProvider() instanceof ListDataProvider)) return null;
        Collection<ShiftSchedulesQueryVO> items =
                ((ListDataProvider<ShiftSchedulesQueryVO>) grid.getDataProvider()).getItems();
        for (ShiftSchedulesQueryVO vo : items) {
            if (empId.equals(vo.getEmployeeId())) return vo;
        }
        return null;
    }

    // 個人模式下，用目前登入者的 actionType==1 日期來反灰表頭
    private Set<LocalDate> computeHeaderLockedDatesForCurrentUser(List<ShiftSchedulesQueryVO> items) {
        if (isManagement() || currentUserId == null || items == null) return Collections.emptySet();
        return items.stream()
                .filter(r -> Objects.equals(currentUserId, r.getEmployeeId()))
                .flatMap(r -> r.getSchedulesDates().stream())
                .filter(sd -> sd.getActionType() != null && sd.getActionType() == ACTION_LOCKED)
                .map(ShiftSchedulesDateTimeQueryVO::getShiftDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    // ======================= Footer 更新 =======================

    private void updateFooterForDate(LocalDate date) {
        if (date == null || dayList.isEmpty()) return;
        int idx = dayList.indexOf(date);
        if (idx < 0 || idx >= dayColumns.size()) return;

        if (!(grid.getDataProvider() instanceof ListDataProvider)) return;
        Collection<ShiftSchedulesQueryVO> items =
                ((ListDataProvider<ShiftSchedulesQueryVO>) grid.getDataProvider()).getItems();

        Map<String, Long> slotCounts = calculateTimeSlotCountsForDay(new ArrayList<>(items), date);
        dayColumns.get(idx).setFooter(setEmployeeFooterText(
                String.valueOf(slotCounts.getOrDefault("morning", 0L)),
                String.valueOf(slotCounts.getOrDefault("afternoon", 0L)),
                String.valueOf(slotCounts.getOrDefault("night", 0L))
        ));
    }

    // ======================= 統計 & 工具 =======================

    private Map<LocalDate, Map<String, Long>> computeSlotCountsByDate(
            List<ShiftSchedulesQueryVO> rows, List<LocalDate> days) {
        Map<LocalDate, Map<String, Long>> byDate = new HashMap<>();
        for (LocalDate d : days) {
            Map<String, Long> slotCounts = rows.stream()
                    .flatMap(r -> r.getSchedulesDates().stream())
                    .filter(sd -> d.equals(sd.getShiftDate()))
                    .filter(sd -> sd.getStatus() != null && sd.getStatus() == 0)
                    .filter(sd -> sd.getShiftTypes() != null && sd.getShiftTypes().contains(SHIFT_TYPE_TOKEN))
                    .map(sd -> Optional.ofNullable(shiftTypeMap.get(sd.getShiftTypes()))
                            .map(ShiftType::getTimeSlot).orElse("noShift"))
                    .filter(ts -> !ts.isEmpty())
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            byDate.put(d, slotCounts);
        }
        return byDate;
    }

    private Map<String, Long> calculateTimeSlotCountsForDay(List<ShiftSchedulesQueryVO> rows, LocalDate target) {
        return rows.stream()
                .flatMap(r -> r.getSchedulesDates().stream())
                .filter(sd -> target.equals(sd.getShiftDate()))
                .filter(sd -> sd.getStatus() != null && sd.getStatus() == 0)
                .filter(sd -> sd.getShiftTypes() != null && sd.getShiftTypes().contains(SHIFT_TYPE_TOKEN))
                .map(sd -> Optional.ofNullable(shiftTypeMap.get(sd.getShiftTypes()))
                        .map(ShiftType::getTimeSlot)
                        .orElse("noShift"))
                .filter(ts -> !ts.isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private int countHolidayWorkTimes(ShiftSchedulesQueryVO vo, LocalDate month) {
        if (month == null) return 0;
        return (int) vo.getSchedulesDates().stream()
                .filter(sd -> sd.getShiftDate().getYear() == month.getYear()
                        && sd.getShiftDate().getMonthValue() == month.getMonthValue())
                .filter(sd -> getCalendarHolidayForDate(sd.getShiftDate(), periods) != null)
                .filter(sd -> sd.getStatus() != null && sd.getStatus() == 0)
                .filter(sd -> sd.getShiftTypes() != null && sd.getShiftTypes().contains(SHIFT_TYPE_TOKEN))
                .count();
    }

    private int getPeriodIndexForDate(LocalDate date, List<ShiftSchedulePeriod> periods) {
        boolean toggle = true;
        for (ShiftSchedulePeriod p : periods) {
            LocalDate s = p.getStartDate();
            LocalDate e = p.getEndDate();
            if ((date.isEqual(s) || date.isAfter(s)) && (date.isEqual(e) || date.isBefore(e))) {
                return toggle ? 1 : 0;
            }
            toggle = !toggle;
        }
        return 0;
    }

    private String getCalendarHolidayForDate(LocalDate date, List<ShiftSchedulePeriod> periods) {
        return periods.stream()
                .flatMap(p -> p.getHolidays().stream())
                .filter(h -> date.equals(h.getShiftDate()))
                .map(ShiftSchedulePeriodHoliday::getShiftTypes)
                .findFirst().orElse(null);
    }

    private List<LocalDate> generateDateRange(LocalDate start, LocalDate endInclusive) {
        if (start == null || endInclusive == null || endInclusive.isBefore(start)) return List.of();
        List<LocalDate> r = new ArrayList<>();
        LocalDate d = start;
        while (!d.isAfter(endInclusive)) {
            r.add(d);
            d = d.plusDays(1);
        }
        return r;
    }

    // from VO 快取讀取
    private String getShiftTypeForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap, LocalDate date) {
        ShiftSchedulesDateTimeQueryVO sd = dateMap.get(date);
        return (sd == null) ? NO_ASSIGN : sd.getShiftTypes();
    }
    private Byte getShiftStatusForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap, LocalDate date) {
        ShiftSchedulesDateTimeQueryVO sd = dateMap.get(date);
        return (sd == null) ? null : sd.getStatus();
    }
    private Byte getActionTypeForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap, LocalDate date) {
        ShiftSchedulesDateTimeQueryVO sd = dateMap.get(date);
        return (sd == null) ? null : sd.getActionType();
    }
    private String getShiftColorCodeForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap, LocalDate date) {
        ShiftSchedulesDateTimeQueryVO sd = dateMap.get(date);
        if (sd == null) return "#ffffff";
        if (isLeave(sd.getStatus())) return "#000000";
        return Optional.ofNullable(sd.getShiftColorCode()).orElse("#ffffff");
    }

    private String getRemarkForDate(Map<LocalDate, ShiftSchedulesDateTimeQueryVO> dateMap, LocalDate date) {
        return dateMap.get(date) != null ? Optional.ofNullable(dateMap.get(date).getRemark()).orElse("") : "";
    }

    // 延伸週判斷
    private LocalDate firstStart() {
        if (periods == null || periods.isEmpty()) return null;
        return periods.stream().map(ShiftSchedulePeriod::getStartDate)
                .min(Comparator.naturalOrder()).orElse(null);
    }
    private LocalDate lastEnd() {
        if (periods == null || periods.isEmpty()) return null;
        return periods.stream().map(ShiftSchedulePeriod::getEndDate)
                .max(Comparator.naturalOrder()).orElse(null);
    }
    /** 在 display 範圍內、但不在核心 periods 範圍內 → 延伸週（個人頁要鎖＋反灰；管理頁不適用） */
    private boolean isDisplayOnly(LocalDate date) {
        if (date == null || displayStart == null || displayEnd == null) return false;
        LocalDate coreStart = firstStart();
        LocalDate coreEnd   = lastEnd();
        if (coreStart == null || coreEnd == null) return false;
        boolean inDisplay = !date.isBefore(displayStart) && !date.isAfter(displayEnd);
        boolean outOfCore = date.isBefore(coreStart) || date.isAfter(coreEnd);
        return inDisplay && outOfCore;
    }

    private boolean isLeave(Byte status) { return status != null && status == STATUS_LEAVE; }

    // Filter
    public void applyEmployeeFilter(Set<Integer> employeeIds) {
        ListDataProvider<ShiftSchedulesQueryVO> provider =
                (ListDataProvider<ShiftSchedulesQueryVO>) grid.getDataProvider();
        provider.clearFilters();
        if (employeeIds != null && !employeeIds.isEmpty()) {
            provider.addFilter(r -> employeeIds.contains(r.getEmployeeId()));
        }
    }

    // Footer 三行：日/午/夜班
    private VerticalLayout setEmployeeFooterText(String dayShift, String afternoonShift, String nightShift) {
        var vt = new VerticalLayout();
        vt.addClassName("footer-vertical-layout");
        vt.setPadding(false);
        vt.setSpacing(false);
        vt.setMargin(false);
        vt.add(
                setUpLayout(setUpSpan(dayShift, "#000000")),      // 日班
                setUpLayout(setUpSpan(afternoonShift, "#d2691e")),// 午班
                setUpLayout(setUpSpan(nightShift, "#800080"))     // 夜班
        );
        return vt;
    }
    private Span setUpSpan(String text, String color) {
        var s = new Span(text);
        s.getStyle().set("color", color);
        s.getStyle().set("font-weight", "bold");
        return s;
    }
    private HorizontalLayout setUpLayout(Span span) {
        var hl = new HorizontalLayout();
        hl.setWidthFull();
        hl.setPadding(false);
        hl.setSpacing(false);
        hl.setMargin(false);
        hl.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        hl.add(span);
        return hl;
    }

    private String monthAbbr(LocalDate date) {
        String s = date.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
        s = s.replace(".", "");
        return s.toUpperCase(Locale.ENGLISH);
    }
}
