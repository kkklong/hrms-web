package com.hrm.application.views.calendar;

import com.hrm.application.calendar.AbstractCalendarView;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import elemental.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vaadin.stefan.fullcalendar.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalendarConfig extends AbstractCalendarView implements AfterNavigationObserver {

    private ShiftScheduleService shiftScheduleService;

    private FullCalendar calendar;
    private List<ShiftType> shiftTypeList;
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();


    public CalendarConfig(ShiftScheduleService shiftScheduleService) {
        this.shiftScheduleService = shiftScheduleService;
    }

    @Override
    protected FullCalendar createCalendar(JsonObject defaultInitialOptions) {
        defaultInitialOptions.put("firstDay", 1);
//        loadShiftSchedules();
        calendar = FullCalendarBuilder.create()
                .withInitialOptions(defaultInitialOptions)
                .withEntryLimit(3)
                .build();
        return calendar;
    }

    private void setData() {
        shiftTypeList = shiftScheduleService.getShiftAndHolidayConfigList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, ShiftType::getShiftKey);
    }

    public void loadShiftSchedules(List<ShiftSchedules> schedulesSorted) {
        setData();
        // 轉換成 Entry 物件
        List<Entry> entries = schedulesSorted.stream()
                .map(this::convertToEntry)
                .collect(Collectors.toList());

        System.out.println("Loaded Entries: " + entries.size());
//        entries.forEach(entry -> System.out.println("Each data" + entry.getTitle()));

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
        if (schedule.getStatus() == 1) {
            entry.setColor("#000000");
            entry.setTitle("請假");
            entry.setDisplayMode(DisplayMode.BLOCK);
        } else if (shiftType.contains("SHIFT")) {
            entry.setDisplayMode(DisplayMode.LIST_ITEM); // 顯示為背景
        } else if (shiftType.contains("HOLIDAY")) {
            entry.setColor("#f08080");
            entry.setTextColor("#000000");
            entry.setDisplayMode(DisplayMode.BLOCK); // 以標題顯示
        } else {
            entry.setDisplayMode(DisplayMode.LIST_ITEM); // 預設顯示模式
        }
        return entry;
    }

    @Override
    public FullCalendar getCalendar() {
        return calendar;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
