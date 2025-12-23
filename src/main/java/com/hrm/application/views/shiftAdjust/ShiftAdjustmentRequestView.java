package com.hrm.application.views.shiftAdjust;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.application.enums.ShiftRouteStage;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.hrm.application.service.ShiftAdjustmentRequestService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<Integer, Option<Integer>> employeeNameMap = new HashMap<>();
    private Map<Byte, Option<Byte>> approvalStageMap;

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
        approvalStageMap = ToolUtil.transToMap(service.getShiftAdjustmentRequestApprovalStage(), Option::getValue);
        configureGrid();
        refreshData();
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
        ShiftAdjustmentRequestDialog dialog = new ShiftAdjustmentRequestDialog(vo, service, this::refreshData, false);
        dialog.setDataForCreate();
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
        grid.addColumn(ShiftAdjustmentRequestVO::getStatus).setHeader("狀態").setKey("status");
        grid.addColumn(this::resolveApprovalStageName).setHeader("審核階段").setKey("approvalContext");
        grid.addComponentColumn(ShiftAdjustmentRequestVO -> new Button("流程明細", event -> {
            ShiftAdjustmentApprovalHistoryDialog dialog = new ShiftAdjustmentApprovalHistoryDialog(ShiftAdjustmentRequestVO);
            dialog.open();
        })).setHeader("流程明細").setKey("detail");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItems(dataProvider);

        //點擊列表檢視申請單
        grid.asSingleSelect().addValueChangeListener(event -> {
            ShiftAdjustmentRequestVO value = event.getValue();
            if (value != null) {
                ShiftAdjustmentRequestDialog dialog =
                        new ShiftAdjustmentRequestDialog(event.getValue(), service, this::refreshData, true);
                dialog.setDataForCheck(value);
                dialog.open();
            }
        });
    }

    /**
     * 解析 approvalContext(JSON)顯示審核階段名稱
     * (1)route[current]拿到「字串名稱」
     * (2)丟入ShiftRouteStage→ 換成正確的 Byte code
     * (3)shiftAdjustmentRequestApprovalStageMap.get拿中文名稱
     */
    private String resolveApprovalStageName(ShiftAdjustmentRequestVO req) {
        String json = req.getApprovalContext();
        try {
            Map<String, Object> ctx =
                    objectMapper.readValue(json, new TypeReference<>() {
                    });
            Object currentObj = ctx.get("current");
            int currentIndex;
            if (currentObj instanceof Number) {
                currentIndex = ((Number) currentObj).intValue();
            } else {
                currentIndex = Integer.parseInt(currentObj.toString());
            }
            Object routeObj = ctx.get("route");
            if (!(routeObj instanceof List<?> routeList)) {
                return "未知";
            }
            Object step = routeList.get(currentIndex);
            String routeName = (step instanceof String)
                    ? (String) step
                    : String.valueOf(step);
            Byte stageKey = ShiftRouteStage.getStageCodeByRouteName(routeName);

            Option<Byte> opt = approvalStageMap.get(stageKey);
            return opt != null ? opt.getName() : "未知";

        } catch (Exception e) {
            return "未知";
        }
    }

    private void refreshData() {
        List<ShiftAdjustmentRequestVO> list = service.currentEmployeeShiftAdjustments();
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(list);
        dataProvider.refreshAll();
    }
}
