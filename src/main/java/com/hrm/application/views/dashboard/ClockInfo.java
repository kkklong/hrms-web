package com.hrm.application.views.dashboard;

import com.hrm.application.demo.rawAttend.CrawlRawAttendService;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.RawAttendanceRecordsQueryService;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.views.attendance.RawAttendanceRecordsQueryView;
import com.hrm.application.views.shift.shiftSchedule3.ScheduleView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@MenuRouter(label = "lastClock", icon = VaadinIcon.CLOCK)
@Route(value = "lastClock", layout = MainLayout.class)
public class ClockInfo extends VerticalLayout {

    private final RawAttendanceRecordsQueryService service;
    private final CrawlRawAttendService service2;
    private List<RawAttendanceRecords> rawAttendanceRecordsList;
    private RawAttendanceRecords clockData;
    private final LocalDate now = LocalDate.now();
    private final UserInfo userInfo;

    private Span lastClockValue;

    public ClockInfo(RawAttendanceRecordsQueryService service, CrawlRawAttendService service2) {
        this.service = service;
        this.service2 = service2;
        this.userInfo = SessionUtil.getUserInfo();

        setSizeFull();
        add(getTitle(), getContent());
        addClassName("background-plan");
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        String route = RouteConfiguration.forSessionScope().getUrl(RawAttendanceRecordsQueryView.class);
        H3 title = new H3("ClockReminder");
        Anchor anc = new Anchor(route, title);
        anc.addClassName("title-link");

        title.addClassName("title-heading");
        titleHt.add(anc, getToolbar());
        titleHt.setAlignItems(Alignment.CENTER);

        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private Component getContent() {
        FormLayout content = new FormLayout();

        // 初始化 UI 時先建立 Span 並保存引用
        Span today = new Span(String.valueOf(now));
        today.getStyle().set("color", "var(--lumo-primary-text-color)");
        lastClockValue = new Span(getValueOrEmpty(getClockData()));
        lastClockValue.getStyle().set("color", "var(--lumo-body-text-color)");

        addDetail(content, "今天日期", today, 1);
        addDetail(content, "最後打卡", lastClockValue, 1);

        content.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
//        content.add(getToolbar());
        content.addClassName("personalInfo-board");
        return content;
    }

    private void addDetail(FormLayout layout, String label, Span valueSpan, int colspan) {
        HorizontalLayout itemLayout = new HorizontalLayout();
        itemLayout.setWidthFull();
        itemLayout.add(new Span(label), valueSpan);
        itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        itemLayout.addClassName("detail-item");
        layout.add(itemLayout);
        layout.setColspan(itemLayout, colspan);
    }

    private String getValueOrEmpty(Object value) {
        return value != null ? value.toString() : "近7日無資料";
    }

    private Component getToolbar() {
        var toolbar = new FormLayout();
        Button update = new Button(new Icon(VaadinIcon.REFRESH));
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(e -> refreshClockData());
//        toolbar.add(update);
        return update;
    }

    //重新查資料
    private void refreshClockData() {
        LocalDateTime latestClock = getClockData();
        lastClockValue.setText(getValueOrEmpty(latestClock));
    }

    private LocalDateTime getClockData() {
        rawAttendanceRecordsList = service.getRawAttendanceRecords(
                now.minusDays(7).atStartOfDay(),
                now.plusDays(1).atStartOfDay().minusSeconds(1),
                userInfo.getNickName(),
                false);

        RawAttendanceRecords userLastClock = rawAttendanceRecordsList.stream()
                .filter(r -> r.getAccount().equals(userInfo.getAccount()))
                .reduce((first, second) -> second)
                .orElse(null);

        if (userLastClock != null) {
            clockData = userLastClock;
            return clockData.getFirstCheckInTime();
        } else {
            return null;
        }
    }
}
