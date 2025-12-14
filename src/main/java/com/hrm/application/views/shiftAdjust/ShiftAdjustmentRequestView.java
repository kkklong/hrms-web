package com.hrm.application.views.shiftAdjust;

import com.hrm.application.entity.ApprovalFlowConfig;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.hrm.application.service.ShiftAdjustmentRequestService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.*;

@Route(value = "ShiftAdjustment", layout = MainLayout.class)
@MenuRouter(label = "ShiftAdjustment", icon = VaadinIcon.COG)
@PageTitle("調班申請 | HRM System")
public class ShiftAdjustmentRequestView extends VerticalLayout {


    private final ShiftAdjustmentRequestService service;

    private Grid<ShiftAdjustmentRequestVO> grid = new Grid<>(ShiftAdjustmentRequestVO.class, false);
    private ListDataProvider<ShiftAdjustmentRequestVO> dataProvider;


    private Map<Integer, Option<Integer>> employeeNameMap = new HashMap<>();

    public ShiftAdjustmentRequestView(ShiftAdjustmentRequestService service) {
        this.service = service;
        dataProvider = new ListDataProvider<>(new ArrayList<>());
        setData();
        add(getTitle(), buildToolbar(), buildContent());
        setSizeFull();
        addClassName("background-plan");
    }

    private void setData() {
        employeeNameMap = ToolUtil.transToMap(service.getEmployeeOptionList(), Option::getValue);
        refreshGrid();
        configureGrid();
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("ShiftAdjustment");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private Component buildToolbar() {
        Button addButton = new Button("新增調班", click -> createRequest());
        Button resetFilterButton = new Button("重置篩選");
        resetFilterButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout toolbar = new FormLayout(addButton, resetFilterButton);
        return toolbar;
    }

    private void createRequest() {
        ShiftAdjustmentRequestVO vo = new ShiftAdjustmentRequestVO();
        grid.asSingleSelect().clear();
        ShiftAdjustmentRequestDialog dialog = new ShiftAdjustmentRequestDialog(vo, service, this::refreshGrid, false);
        dialog.open();
    }

    private Component buildContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        content.addClassNames("grid-content");

        return content;
    }


    private void configureGrid() {
        grid.addColumn(s ->
                Optional.ofNullable(employeeNameMap.get(s.getApplicantId())).map(Option::getName).orElse("未知")).setHeader("申請人").setKey("applicantId");
        grid.addColumn(ShiftAdjustmentRequestVO::getCreatedAt).setHeader("申請時間");
        grid.addColumn(s ->
                Optional.ofNullable(employeeNameMap.get(s.getCounterpartScheduleId())).map(Option::getName).orElse("未知")).setHeader("申請人").setKey("counterpartScheduleId");
        grid.addColumn(ShiftAdjustmentRequestVO::getTargetDate).setHeader("調班日期").setKey("targetDate");
        grid.addColumn(ShiftAdjustmentRequestVO::getStatus).setHeader("狀態").setKey("status");
        grid.addColumn(ShiftAdjustmentRequestVO::getApprovalContext).setHeader("審核階段").setKey("approvalContext");
        grid.addColumn(ShiftAdjustmentRequestVO::getHistoryReview).setHeader("流程明細").setKey("historyReview");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItems(dataProvider);
    }

    private void refreshGrid() {
//        List<ShiftAdjustmentRequestVO> list = service.getPendingShiftAdjustments();
        List<ShiftAdjustmentRequestVO> list = new ArrayList<>();
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(list);
        dataProvider.refreshAll();


    }
}
