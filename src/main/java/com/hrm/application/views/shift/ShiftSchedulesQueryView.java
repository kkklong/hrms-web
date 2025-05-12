package com.hrm.application.views.shift;

import com.hrm.application.entity.ShiftType;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.model.ShiftSchedulePeriod;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.service.ShiftScheduleService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Scope("prototype")
@Route(value = "shiftSchedules", layout = MainLayout.class)
@MenuRouter(label = "shiftSchedules", icon = VaadinIcon.CALENDAR)
@PageTitle("部門班表 | 人力資源管理系統")
public class ShiftSchedulesQueryView extends VerticalLayout {
    private ShiftScheduleService service;

    // data
    private Map<String, ShiftType> shiftTypeMap = new HashMap<>();
    private List<Option<Integer>> departmentList;
    private Map<Integer, Option<Integer>> departmentMap;
    private final LocalDate now = LocalDate.now(); // 進入功能的日期
    private List<Integer> years;

    // selected data
    private LocalDate selectedDate = now; // 目前選擇的年月
    private List<ShiftSchedulePeriod> periods;

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

    public ShiftSchedulesQueryView(ShiftScheduleService service) {
        setSizeFull();
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
        tool2.add(nickNameFilter, departmentSelector);

        // 將各個部分新增到工具欄
        toolbar.add(centerLayout, tool2);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.BASELINE);
        return toolbar;
    }

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
        monthPicker.setWidth("6em");

        // 拼接的查詢日期顯示
        HorizontalLayout DatePickerHt = new HorizontalLayout(yearPicker, monthPicker);
        // 佈局
        HorizontalLayout DateToolHt = new HorizontalLayout(leftButton, DatePickerHt, rightButton);
        DateToolHt.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return DateToolHt;
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
    }

    private void configureFilter() {
        nickNameFilter.setPlaceholder("員工...");
        nickNameFilter.setWidth("10em");
        nickNameFilter.setClearButtonVisible(true);
        nickNameFilter.setItemLabelGenerator(Option::getName);
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    /**
     * 設定grid
     */
    private void configureGrid(List<ShiftSchedulesQueryVO> shiftSchedulesList) {

    }
}
