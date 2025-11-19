package com.hrm.application.views.dashboard;

import com.hrm.application.demo.rawAttend.CrawlRawAttendService;
import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.vo.LeaveSpecialRecordsVO2;
import com.hrm.application.service.LeaveTypeService;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.views.attendance.RawAttendanceRecordsQueryView;
import com.hrm.application.views.leave.leaveHour.PersonalLeaveSpecialRecordView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@MenuRouter(label = "userAvailableLeave", icon = VaadinIcon.CLOCK)
@Route(value = "userAvailableLeave", layout = MainLayout.class)
public class UserAvailableLeaveInfo extends VerticalLayout {

    private final LeaveTypeService service;
    private List<LeaveSpecialRecordsVO2> availableLeaveList;
//    private LeaveSpecialRecord availableLeaveData;
    private final LocalDate now = LocalDate.now();
    private final UserInfo userInfo;

    private Span lastClockValue;

    private static final String ANNUAL_LEAVE = "0";


    public UserAvailableLeaveInfo(LeaveTypeService service) {
        this.service = service;
        this.userInfo = SessionUtil.getUserInfo();
        setData();
        setSizeFull();
        add(getTitle(), getContent());
        addClassName("background-plan");
    }

    private void setData(){
        availableLeaveList = service.queryCurrentLeaveSpecialRecordList2();
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        String route = RouteConfiguration.forSessionScope().getUrl(PersonalLeaveSpecialRecordView.class);
        H3 title = new H3("AvailableLeave");
        Anchor anc = new Anchor(route, title);
        anc.addClassName("title-link");

        title.addClassName("title-heading");
        titleHt.add(anc);
        titleHt.setAlignItems(Alignment.CENTER);

        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private Component getContent() {
        FormLayout content = new FormLayout();

        Map<Integer, LeaveSpecialRecordsVO2> leaveAnnualMap = getLeaveMapForYear(availableLeaveList, ANNUAL_LEAVE);

        Double annualLeaveThisYear = Optional.ofNullable(leaveAnnualMap.get(now.getYear()))
                .map(LeaveSpecialRecordsVO2::getMaxLeaveDays)
                .orElse(0.0);

        Double annualLeaveLastYear = Optional.ofNullable(leaveAnnualMap.get(now.minusYears(1).getYear()))
                .map(LeaveSpecialRecordsVO2::getMaxLeaveDays)
                .orElse(0.0);


        addDetail(content, String.valueOf(now.getYear()) + "特休",
                setSpan(getValueOrEmpty(annualLeaveThisYear) + " 天"), 1);
        addDetail(content, String.valueOf(now.minusYears(1).getYear()) + "特休",
                setSpan(getValueOrEmpty(annualLeaveLastYear) + " 天"), 1);


        content.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
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

    private Span setSpan(String text) {
        Span span = new Span(text);
        span.getStyle().set("color", "var(--lumo-primary-text-color)");
        return span;
    }

    private String getValueOrEmpty(Object value) {
        return value != null ? value.toString() : "";
    }


    //重新查資料
    private void refreshClockData() {
//        LocalDateTime latestClock = getClockData();
//        lastClockValue.setText(getValueOrEmpty(latestClock));
    }

    private Map<Integer, LeaveSpecialRecordsVO2> getLeaveMapForYear(List<LeaveSpecialRecordsVO2> availableLeaveList, String leaveType) {
        return availableLeaveList.stream()
                .filter(a -> a.getLeaveTypes().equals(leaveType))
                .collect(Collectors.toMap(
                        a -> a.getStartDate().getYear(),
                        a -> a,
                        (exist, replace) -> replace
                ));

    }
}
