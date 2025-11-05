package com.hrm.application.views.attendance;

import com.hrm.application.demo.rawAttend.CrawlRawAttendService;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.model.bo.UpdateAttendanceRequest;
import com.hrm.application.service.RawAttendanceRecordsQueryService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.WebClientUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Scope("prototype")
@Route(value = "RawAttendanceRecords", layout = MainLayout.class)
@PageTitle("打卡資料 | HRM System")
public class RawAttendanceRecordsQueryView extends VerticalLayout {

    Grid<RawAttendanceRecords> grid = new Grid<>(RawAttendanceRecords.class, false);
    private final RawAttendanceRecordsQueryService service;
    private final CrawlRawAttendService service2;
    private List<RawAttendanceRecords> rawAttendanceRecordsList;

    private DateTimePicker startDate = new DateTimePicker();
    private DateTimePicker endDate = new DateTimePicker();
    private TextField nickName = new TextField();
    private Checkbox showDetail = new Checkbox("查詢逐筆紀錄");

    private Button transmit = new Button("查詢");

    public RawAttendanceRecordsQueryView(RawAttendanceRecordsQueryService service, CrawlRawAttendService service2) {
        this.service = service;
        this.service2 = service2;
        setData();
        configureGrid();
        updateSchedulesData();
        setSizeFull();
        add(getTitle(), getToolbar(), getContent());
        addClassName("background-plan");
//        configureFilter();
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("RawAttendanceRecords");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }


    private void setData() {
        startDate.setWidth("18em");
        endDate.setWidth("18em");

        startDate.setStep(Duration.ofMinutes(30));
        endDate.setStep(Duration.ofMinutes(30));
        startDate.setValue(LocalDate.now().atStartOfDay());
        endDate.setValue(LocalDate.now().atTime(23, 59, 59));
        transmit.addClickListener(event -> updateSchedulesData());
        nickName.setPlaceholder("員工英文名...");
        nickName.setClearButtonVisible(true);
        showDetail.addClassName("text-1line");
//        showDetail.getStyle().set("min-width", "0");
    }

    private FormLayout getToolbar() {
        var toolbar = new FormLayout();
        Button update = new Button(new Icon(VaadinIcon.REFRESH));
        update.getStyle().set("--vaadin-button-height","--lumo-size-m");
        update.addClickListener(event -> updateRawData());

        toolbar.add(startDate, endDate, nickName, showDetail, transmit, update);
        toolbar.setColspan(startDate, 6);
        toolbar.setColspan(endDate, 6);
        toolbar.setColspan(nickName, 3);
        toolbar.setColspan(showDetail, 3);
        toolbar.setColspan(transmit, 2);
        toolbar.setColspan(update, 1);

        toolbar.setMaxWidth("70em");
        toolbar.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0em", 6),
                new FormLayout.ResponsiveStep("20em", 7),
                new FormLayout.ResponsiveStep("40em", 13),
                new FormLayout.ResponsiveStep("60em", 22)
        );
//        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return toolbar;
    }

    private void updateRawData() {
        LocalDate now = LocalDate.now();
//        LocalDate selectedDate = LocalDate.of(now.getYear(), now.getMonth(), 1);
//        LocalDateTime startDate = LocalDateTime.parse(selectedDate.atStartOfDay().toString());
//        LocalDateTime endDate = LocalDateTime.parse(selectedDate.plusMonths(1).atStartOfDay().minusSeconds(1).toString());

        LocalDateTime startDate = LocalDateTime.parse(now.minusDays(3).atStartOfDay().toString());
        LocalDateTime endDate = LocalDateTime.parse(now.plusDays(1).atStartOfDay().minusSeconds(1).toString());
        String account = "";
        Boolean showDetail = false;

        List<RawAttendanceRecords> resp = service2.doFetchData(startDate, endDate, account, showDetail);
        log.info("Items:" + resp);
        UpdateAttendanceRequest items = new UpdateAttendanceRequest();
        items.setVos(resp);
        items.setStartDate(startDate);
        items.setEndDate(endDate);

        boolean result = service.updateData(items);
        NotificationUtil.success(result ? "成功" : "失敗");
    }

    private HorizontalLayout getContent() {
        Scroller scroller = new Scroller(grid);
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.BOTH);
        scroller.getStyle().set("min-width", "0");

        HorizontalLayout content = new HorizontalLayout(scroller);
        content.addClassNames("grid-content");
        content.setSizeFull();
        content.setFlexGrow(1, scroller);
        return content;
    }

    private void configureGrid() {
        grid.addColumn(attendanceRecords ->
                        Optional.ofNullable(attendanceRecords.getAccount())
                                .orElse(null))
                .setHeader("員工").setKey("nickName");
        grid.addColumn(attendanceRecords ->
                        Optional.ofNullable(attendanceRecords.getFirstCheckInTime())
                                .orElse(null))
                .setHeader("首次打卡").setKey("firstCheckIn");
        grid.addColumn(attendanceRecords ->
                        Optional.ofNullable(attendanceRecords.getLastCheckOutTime())
                                .orElse(null))
                .setHeader("最後打卡").setKey("lastCheckOut");
        grid.setSizeFull();
        grid.addClassName("grid-mobile-m");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateSchedulesData() {
        rawAttendanceRecordsList = service.getRawAttendanceRecords(startDate.getValue(), endDate.getValue(), nickName.getValue(), showDetail.getValue());
        updateGridColumns();
        grid.setItems(rawAttendanceRecordsList);
    }

    private void updateGridColumns() {
        var firstCheckInColumn = grid.getColumnByKey("firstCheckIn");
        var lastCheckOutColumn = grid.getColumnByKey("lastCheckOut");

        if (showDetail.getValue()) {
            lastCheckOutColumn.setVisible(false);
            firstCheckInColumn.setHeader("打卡時間");
        } else {
            lastCheckOutColumn.setVisible(true);
            firstCheckInColumn.setHeader("首次打卡");
        }
    }
}
