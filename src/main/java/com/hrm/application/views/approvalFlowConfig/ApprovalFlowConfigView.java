package com.hrm.application.views.approvalFlowConfig;

import com.hrm.application.entity.ApprovalFlowConfig;
import com.hrm.application.entity.Employee;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.ApprovalFlowConfigService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Scope("prototype")
@Route(value = "approvalflowconfig", layout = MainLayout.class)
@MenuRouter(label = "ApprovalFlowConfig", icon = VaadinIcon.COG)
@PageTitle("審核流程設定 | 人力資源管理系統")
public class ApprovalFlowConfigView extends VerticalLayout {

    private Grid<ApprovalFlowConfig> grid = new Grid<>(ApprovalFlowConfig.class, false);
    private ApprovalFlowConfigService service;
    private ListDataProvider<ApprovalFlowConfig> dataProvider;
    private ApprovalFlowConfigDialog dialog;


    private final ComboBox<Option<String>> scopeTypeFilter = new ComboBox<>();
    private final ComboBox<Integer> activeFilter = new ComboBox<>();

    private List<Option<String>> scopeTypeList;
    private List<Option<String>> companyList;
    private List<Option<String>> reviewList;
    private List<Option<String>> intervalList;
    private List<Option<Integer>> departmentList;
    private List<Option<Integer>> employeeList;


    private final Map<String, String> scopeTypeLut = new HashMap<>();
    private final Map<String, String> companyLut = new HashMap<>();
    private final Map<String, String> reviewLut = new HashMap<>();
    private final Map<String, String> intervalLut = new HashMap<>();
    private final Map<String, String> deptLut = new HashMap<>();
    private final Map<String, String> empLut = new HashMap<>();


    public ApprovalFlowConfigView(ApprovalFlowConfigService service) {
        this.service = service;
        setData();
        updateList();
        configureGrid();
        configureDialog();
        add(titleConfigure(), getToolbar(), getContent());
        setSizeFull();
        this.addClassName("background-plan");
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("ApprovalFlowConfig");
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

    private Component getToolbar() {
        Button addConfigButton = new Button("新增", click -> createApprovalFlowConfig());

        var toolbar = new HorizontalLayout(addConfigButton, scopeTypeFilter, activeFilter);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return toolbar;
    }

    private void setData() {
        scopeTypeList = service.getScopeTypeOptions();
        companyList = service.getCompanyOptionsList();
        reviewList = service.getReviewOptions();
        intervalList = service.getReviewIntervalOptions();
        departmentList = service.getDepartmentOptionList();
        employeeList = service.getEmployeeOptionList();
        loadSelectors();
        scopeTypeFilter.setItems(scopeTypeList);
        scopeTypeFilter.setPlaceholder("範圍類型...");
        scopeTypeFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        scopeTypeFilter.setItemLabelGenerator(Option::getName);
        scopeTypeFilter.setClearButtonVisible(true);
        activeFilter.setItems(0, 1); // 選項是 0 和 1
        activeFilter.setItemLabelGenerator(i -> i == 0 ? "停用" : "啟用");
        activeFilter.setClearButtonVisible(true);
        activeFilter.setPlaceholder("狀態...");
        activeFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");


    }

    private void configureGrid() {
        grid.addColumn(ApprovalFlowConfig::getScopeType).setHeader("範圍類型").setKey("ScopeType");
        grid.addColumn(this::displayNameForScopeValue).setHeader("範圍值").setKey("ScopeValue");
        grid.addColumn(ApprovalFlowConfig::getFlowJson).setHeader("簽核流程").setKey("FlowJson");
        grid.addColumn(cfg -> (cfg.getActive() != null && cfg.getActive() == 1) ? "啟用" : "停用").setHeader("狀態").setKey("Active");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event -> editApprovalFlowConfig(event.getValue()));
    }

    private void updateList() {
        String selectedScopeType = scopeTypeFilter.getValue() != null
                ? scopeTypeFilter.getValue().getValue() : null;
        dataProvider = new ListDataProvider<>(service.queryApprovalFlowConfigs(selectedScopeType, activeFilter.getValue()));
        grid.setItems(dataProvider);
    }

    private void loadSelectors() {
        // 範圍類型
        for (Option<String> o : safe(scopeTypeList)) {
            scopeTypeLut.put(o.getValue(), o.getName());
        }
        // 公司
        for (Option<String> o : safe(companyList)) {
            companyLut.put(o.getValue(), o.getName());
        }
        // 審核人
        for (Option<String> o : safe(reviewList)) {
            reviewLut.put(o.getValue(), o.getName());
        }
        // 區間
        for (Option<String> o : safe(intervalList)) {
            intervalLut.put(o.getValue(), o.getName());
        }
        // 部門
        for (Option<Integer> o : safe(departmentList)) {
            deptLut.put(String.valueOf(o.getValue()), o.getName());
        }
        // 員工
        for (Option<Integer> o : safe(employeeList)) {
            empLut.put(String.valueOf(o.getValue()), o.getName());
        }
    }

    private <T> List<T> safe(List<T> in) {
        return in == null ? List.of() : in;
    }

    /**
     * 依 scopeType 將 scopeValue 轉成對應名稱
     */
    private String displayNameForScopeValue(ApprovalFlowConfig cfg) {
        return switch (cfg.getScopeType()) {
            case "COMPANY" -> companyLut.getOrDefault(cfg.getScopeValue(), cfg.getScopeValue());
            case "DEPARTMENT" -> deptLut.getOrDefault(cfg.getScopeValue(), cfg.getScopeValue());
            case "EMPLOYEE" -> empLut.getOrDefault(cfg.getScopeValue(), cfg.getScopeValue());
            default -> cfg.getScopeValue();
        };
    }

    private void configureDialog() {
        dialog = new ApprovalFlowConfigDialog(service, scopeTypeList, companyList, reviewList, intervalList, departmentList, employeeList);
//        dialog.addSaveListener(this::saveEmployee);
//        dialog.addUpdateListener(this::updateEmployee);
//        dialog.addDeleteListener(this::deleteEmployee);
        dialog.addCloseListener(e -> closeEditor());
    }

    public void createApprovalFlowConfig() {
        grid.asSingleSelect().clear();
        dialog.setApprovalFlowDialog(new ApprovalFlowConfig());
        dialog.setDialogView(true);
        dialog.open();
    }

    public void editApprovalFlowConfig(ApprovalFlowConfig afcfg) {
        if (afcfg == null) {
            closeEditor();
        } else {
            dialog.setApprovalFlowDialog(afcfg);
            dialog.setDialogView(false);
            dialog.open();
        }
    }

    private void closeEditor() {
        if (dialog != null) {
            dialog.setApprovalFlowDialog(new ApprovalFlowConfig());
            dialog.close();
        }
        dialog.close();
    }
}
