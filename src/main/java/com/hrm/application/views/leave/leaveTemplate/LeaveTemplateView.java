package com.hrm.application.views.leave.leaveTemplate;

import com.hrm.application.entity.LeaveSpecialRecordTemplate;
import com.hrm.application.enums.Status;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.LeaveTemplateService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Objects;

@Scope("prototype")
@Route(value = "leaveTemplate", layout = MainLayout.class)
@MenuRouter(label = "假別模板", icon = VaadinIcon.FLIGHT_TAKEOFF)
public class LeaveTemplateView extends VerticalLayout {
    private final LeaveTemplateService service;
    private final Map<String, Option<String>> salaryStandardMap;
    private final Map<String, Option<String>> calculationPeriodMap;

    Grid<LeaveSpecialRecordTemplate> grid = new Grid<>(LeaveSpecialRecordTemplate.class, false);
    LeaveTemplateDialog dialog;

    public LeaveTemplateView(LeaveTemplateService service) {
        this.service = service;

        this.salaryStandardMap = ToolUtil.transToMap(service.querySalaryStandard(), Option::getValue);
        this.calculationPeriodMap = ToolUtil.transToMap(service.queryCalculationPeriod(), Option::getValue);

        addClassName("background-plan");
        setSizeFull();
        configureGrid();
        configureDialog();

        add(titleConfigure(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("LeaveTemplate");
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

    private void configureGrid() {
        grid.addClassNames("leave-template-grid");
        grid.setSizeFull();
        grid.addColumn(service::getLeaveName).setHeader("假別/年資");
        grid.addColumn(LeaveSpecialRecordTemplate ->
                Objects.requireNonNull(salaryStandardMap.get(LeaveSpecialRecordTemplate.getSalaryStandard())).getName()
        ).setHeader("計薪標準");
        grid.addColumn(LeaveSpecialRecordTemplate ->
                        Objects.requireNonNull(Status.fromBoolean(LeaveSpecialRecordTemplate.getFullAttendanceBonus())).getLabel())
                .setHeader("計算全勤");
        grid.addColumn(LeaveSpecialRecordTemplate::getMinLeaveUnit)
                .setHeader("最低請假單位");
        grid.addColumn(LeaveSpecialRecordTemplate::getMaxLeaveDays)
                .setHeader("期間可請假天數");
        grid.addColumn(LeaveSpecialRecordTemplate ->
                Objects.requireNonNull(calculationPeriodMap.get(LeaveSpecialRecordTemplate.getCalculationPeriod())).getName()
        ).setHeader("計算期間類型");
        grid.asSingleSelect().addValueChangeListener(event -> editLeaveTemplate(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void editLeaveTemplate(LeaveSpecialRecordTemplate leaveSpecialRecordTemplate) {
        if (leaveSpecialRecordTemplate == null) {
            closeEditor();
        } else {

            dialog.setLeaveSpecialRecordsTemplate(leaveSpecialRecordTemplate);
            dialog.open();
        }
    }

    private void updateList() {
        grid.setItems(service.queryLeaveTemplate());
    }

    private void closeEditor() {
        dialog.close();
        removeClassName("editing");
    }

    private void configureDialog() {
        dialog = new LeaveTemplateDialog(service);
        dialog.addSaveListener(this::saveTemplate);
        dialog.addCloseListener(e -> closeEditor());
    }

    private void saveTemplate(LeaveTemplateDialog.SaveEvent event) {
        LeaveSpecialRecordTemplate leaveSpecialRecordTemplate = event.getLeaveSpecialRecordsTemplate();
        boolean success = service.updateLeaveTemplate(leaveSpecialRecordTemplate);
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        }
    }

    private String formatYearData(Double yearData) {
        if (yearData == 0.5) {
            return "(半年)";
        } else if (yearData >= 1) {
            return "(" + (int) Math.floor(yearData) + "年)";
        } else {
            return ""; // yearData = 0 或其他< 1 的非 0.5 值，返回空字串
        }
    }
}