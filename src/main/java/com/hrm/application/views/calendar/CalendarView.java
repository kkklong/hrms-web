package com.hrm.application.views.calendar;

import com.hrm.application.calendar.AbstractCalendarView;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuItem;
import com.hrm.application.service.AccountService;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.fullcalendar.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Route(value = "calendar", layout = MainLayout.class)
@MenuItem(label = "Calendar")
@PageTitle("Calendar | HRMSystemDemo")
public class CalendarView extends AbstractCalendarView implements AfterNavigationObserver {

    @Autowired
    private ShiftScheduleService shiftScheduleService;
    @Autowired
    private AccountService accountService;

    private FullCalendar calendar;
    protected List<ShiftType> shiftTypeList;
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();


    @Override
    protected FullCalendar createCalendar(JsonObject defaultInitialOptions) {
        defaultInitialOptions.put("firstDay", 1);
        calendar = FullCalendarBuilder.create()
                .withInitialOptions(defaultInitialOptions)
                .withEntryLimit(3)
                .build();

        return calendar;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadShiftSchedules();
    }

    private void setData() {
        shiftTypeList = shiftScheduleService.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, ShiftType::getShiftKey);
    }

    private void loadShiftSchedules() {
        setData();

        String accessToken = accountService.login("admin", "123456");
        SessionUtil.setToken(accessToken);

        // 設定查詢的時間範圍 (這裡舉例用當月的第一天和最後一天)
        LocalDate now = LocalDate.now();
        String startDate = now.withDayOfMonth(1).toString();
        LocalDate endMonth = now.plusMonths(1);
        String endDate = endMonth.withDayOfMonth(endMonth.lengthOfMonth()).toString();
        Integer departmentId = 4;

        List<ShiftSchedules> schedules = shiftScheduleService.queryShiftSchedules(startDate, endDate, departmentId);
        List<ShiftSchedules> schedulesSorted = schedules.stream()
                .filter(shift -> shift.getEmployeeId().equals(14))
                .collect(Collectors.toList());
//        log.info("ShiftData" + schedulesSorted.toString());
        // 轉換成 Entry 物件
        List<Entry> entries = schedulesSorted.stream()
//                .filter(shift -> shift.getEmployeeId().equals(14))
                .map(this::convertToEntry)
                .collect(Collectors.toList());

        System.out.println("Loaded Entries: " + entries.size());
        entries.forEach(entry -> System.out.println("Each data" + entry.getTitle()));

        calendar.getEntryProvider().asInMemory().removeAllEntries();
        calendar.getEntryProvider().asInMemory().addEntries(entries);
        calendar.render();
    }

    private Entry convertToEntry(ShiftSchedules schedule) {
        Entry entry = new Entry();
        String shiftType = schedule.getShiftTypes(); // 取得 ShiftType
        String shiftTypeName = Optional.ofNullable(shiftTypeMap.get(shiftType))
                .map(ShiftType::getShiftName)
                .orElse("未知班別");

        entry.setTitle(shiftTypeName); // 設定標題
        entry.setStart(schedule.getShiftDate());
        entry.setEnd(schedule.getShiftDate());
//        entry.setEditable(false);
        entry.setStartEditable(false); // 禁止修改開始時間
//        entry.setDurationEditable(false); // 禁止改變長度（防止調整大小）
        entry.setAllDay(true);
        // 根據 ShiftType 設定不同的配置
        if (shiftType.contains("SHIFT")) {
            entry.setDisplayMode(DisplayMode.LIST_ITEM); // 顯示為背景
        } else if (shiftType.contains("HOLIDAY")) {
            entry.setColor("#f08080");
            entry.setDisplayMode(DisplayMode.BLOCK); // 以標題顯示
        } else {
            entry.setDisplayMode(DisplayMode.LIST_ITEM); // 預設顯示模式
        }
        return entry;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        loadShiftSchedules();
    }
}
