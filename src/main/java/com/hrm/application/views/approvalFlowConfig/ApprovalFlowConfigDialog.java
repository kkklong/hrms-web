package com.hrm.application.views.approvalFlowConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.application.component.ConfirmDialog;
import com.hrm.application.component.DragAndSelect;
import com.hrm.application.entity.ApprovalFlowConfig;
import com.hrm.application.enums.ApprovalScopeType;
import com.hrm.application.model.Option;
import com.hrm.application.service.ApprovalFlowConfigService;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.shift.ShiftSchedulesQueryDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.hrm.application.util.ToolUtil.dataConverter;

@Slf4j
public class ApprovalFlowConfigDialog extends Dialog {
    private final ApprovalFlowConfigService service;
    private Binder<ApprovalFlowConfig> binder = new BeanValidationBinder<>(ApprovalFlowConfig.class);

    private final ComboBox<Option<String>> scopeType = new ComboBox<>("範圍類型");
    private final TextField scopeValue = new TextField("範圍值");
    private final TextField flowJson = new TextField("簽核流程");
    private final Checkbox active = new Checkbox("是否啟用");

    private final ComboBox<Option<String>> companySelect = new ComboBox<>("公司名稱");
    private final ComboBox<Option<Integer>> departmentSelect = new ComboBox<>("部門名稱");
    private final ComboBox<Option<Integer>> employeeSelect = new ComboBox<>("員工姓名");

    private List<Option<String>> scopeTypeOpts = List.of();
    private List<Option<String>> companyOpts = List.of();
    private List<Option<String>> reviewOpts = List.of();
    private List<Option<String>> intervalOpts = List.of();
    private List<Option<Integer>> departmentOpts = List.of();
    private List<Option<Integer>> employeeOpts = List.of();

    private Map<String, Option<String>> companyOptMap;
    private Map<Integer, Option<Integer>> departmentOptMap;
    private Map<Integer, Option<Integer>> employeeOptMap;
    private Map<Integer, List<Option<Integer>>> deptToEmployees;
    private Map<Integer, Integer> empToDept;
    private Map<String, Option<String>> reviewOptMap = new HashMap<>();

    private Button save = new Button("儲存");
    private Button update = new Button("更新");
    private Button delete = new Button("刪除");
    private Button close = new Button("關閉");

    //    @Resource
//    ObjectMapper objectMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VerticalLayout flowLayout = new VerticalLayout();
    private ConfirmDialog confirmDialog;
    private final Map<String, DragAndSelect<Option<String>>> reviewFlowGridMap = new HashMap<>();


    public ApprovalFlowConfigDialog(ApprovalFlowConfigService service
            , List<Option<String>> scopeTypeList
            , List<Option<String>> companyList
            , List<Option<String>> reviewList
            , List<Option<String>> intervalList
            , List<Option<Integer>> departmentList
            , List<Option<Integer>> employeeList
    ) {
        this.service = service;
        this.scopeTypeOpts = scopeTypeList;
        this.companyOpts = companyList;
        this.reviewOpts = reviewList;
        this.intervalOpts = intervalList;
        this.departmentOpts = departmentList;
        this.employeeOpts = employeeList;

        setData();
        add(getContent());
        getFooter().add(createButtonsLayout());

        binder.forField(scopeType)
                .withConverter(dataConverter(scopeTypeList))
                .bind(ApprovalFlowConfig::getScopeType, ApprovalFlowConfig::setScopeType);

        binder.forField(active)
                .withConverter(
                        // Boolean -> Integer (存回後端用)
                        checked -> checked ? 1 : 0,
                        // Integer -> Boolean (顯示在UI用)
                        active -> active != null && active == 1,
                        "Active flag must be 0 or 1"
                )
                .bind(ApprovalFlowConfig::getActive, ApprovalFlowConfig::setActive);

        binder.bindInstanceFields(this);
    }

    private void setData() {
        companyOptMap = ToolUtil.transToMap(companyOpts, Option::getValue);
        departmentOptMap = ToolUtil.transToMap(departmentOpts, Option::getValue);
        employeeOptMap = ToolUtil.transToMap(employeeOpts, Option::getValue);
        deptToEmployees = service.buildDeptToEmployeesMap(employeeOpts);
        empToDept = service.buildEmployeeToDeptMap(employeeOpts);
        reviewOptMap = ToolUtil.transToMap(reviewOpts, Option::getValue);
        configureSelector();
        configureFlowLayout();
    }

    private VerticalLayout getContent() {
        FormLayout form = new FormLayout();
        form.add(scopeType, scopeValue, companySelect, departmentSelect, employeeSelect);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("40em", 2)
        );
        form.setWidth("40em");
        return new VerticalLayout(form, flowLayout, new HorizontalLayout(active));
    }

    private void configureSelector() {
        scopeType.setItems(scopeTypeOpts);
        scopeType.setItemLabelGenerator(Option::getName);
        scopeType.setPlaceholder("請選擇");
        scopeType.addValueChangeListener(e -> {
            resetScopeInputs();
            applyScopeTypeRules(e.getValue() != null ? e.getValue().getValue() : null);
        });
        // 公司
        companySelect.setItems(companyOpts);
        companySelect.setItemLabelGenerator(Option::getName);
        companySelect.addValueChangeListener(e -> {
            scopeValue.setValue(e.getValue() != null ? e.getValue().getValue() : "");
        });
        // 部門
        departmentSelect.setItems(departmentOpts);
        departmentSelect.setItemLabelGenerator(Option::getName);
        departmentSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                scopeValue.setValue(String.valueOf(e.getValue().getValue()));
                // 若為 EMPLOYEE：依部門載入員工
                if (scopeType.getValue().getValue().equals(ApprovalScopeType.EMPLOYEE.value())) {
                    Integer deptId = e.getValue().getValue();
                    employeeSelect.setItems(deptToEmployees.get(deptId));
                    employeeSelect.clear();
                    employeeSelect.setVisible(true);
                }
            } else {
                employeeSelect.clear();
                employeeSelect.setItems(List.of());
                employeeSelect.setVisible(false);
            }
        });
        // 員工
        employeeSelect.setItems(employeeOpts);
        employeeSelect.setItemLabelGenerator(Option::getName);
        employeeSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) scopeValue.setValue(String.valueOf(e.getValue().getValue()));
        });
        // 其他
        setComponentSize();
    }

    private void setComponentSize() {
        List<HasSize> fields = Arrays.asList(
                scopeType, companySelect, departmentSelect, employeeSelect, active, scopeValue
        );
        fields.forEach(field -> {
            field.setWidthFull();
        });
    }

    private void applyScopeTypeRules(String value) {
        if (value == null) {
            return;
        }
        if (value.equals(ApprovalScopeType.COMPANY.value())) {
            companySelect.setVisible(true);
            companySelect.focus();
        } else if (value.equals(ApprovalScopeType.DEPARTMENT.value())) {
            departmentSelect.setVisible(true);
            departmentSelect.focus();
        } else if (value.equals(ApprovalScopeType.EMPLOYEE.value())) {
            departmentSelect.setVisible(true);
            employeeSelect.clear();
            employeeSelect.setItems(List.of());
            employeeSelect.setVisible(false);
            departmentSelect.focus();
        }
    }

    private void configureFlowLayout() {
        flowLayout.removeAll();
        reviewFlowGridMap.clear();
        for (Option<String> interval : intervalOpts) {
            H5 title = new H5("審核流程 " + "(" + interval.getName() + ")");
            DragAndSelect<Option<String>> selectGrid = new DragAndSelect<>(reviewOpts);
            selectGrid.setAllRowsVisible(true);
            selectGrid.addColumn(Option::getName).setHeader("審核人員 [全選/取消]");
            reviewFlowGridMap.put(interval.getValue(), selectGrid);
            flowLayout.add(title, selectGrid);
            flowLayout.addClassNames("grid-content");
        }
    }

    private List<Option<String>> excludeGlobal(List<Option<String>> src) {
        return src.stream()
                .filter(o -> ApprovalScopeType.fromValue(o.getValue()) != ApprovalScopeType.GLOBAL)
                .toList();
    }

    public void setNewApprovalFlowDialog(ApprovalFlowConfig afcfg) {
        afcfg.setActive(1);
        binder.setBean(afcfg);
        // 新增時不允許選 GLOBAL
        scopeType.setItems(excludeGlobal(scopeTypeOpts));
        setFlowJsonToGrid(afcfg, true);
    }

    public void setApprovalFlowDialog(ApprovalFlowConfig afcfg) {
        binder.setBean(afcfg);
        setBeanToSelectorData(binder.getBean());
        setFlowJsonToGrid(binder.getBean(), false);
    }

    public void setBeanToSelectorData(ApprovalFlowConfig bean) {
        ApprovalScopeType type = ApprovalScopeType.fromValue(bean.getScopeType());
        switch (type) {
            case COMPANY -> {
                companySelect.setVisible(true);
                companySelect.setValue(companyOptMap.get(bean.getScopeValue()));
            }
            case DEPARTMENT -> {
                departmentSelect.setVisible(true);
                Integer deptId = Integer.valueOf(bean.getScopeValue());
                departmentSelect.setValue(departmentOptMap.get(deptId));
            }
            case EMPLOYEE -> {
                Integer empId = Integer.valueOf((bean.getScopeValue()));
                departmentSelect.setVisible(true);
                departmentSelect.setValue(departmentOptMap.get(empToDept.get(empId)));
                employeeSelect.setVisible(true);
                employeeSelect.setValue(employeeOptMap.get(empId));
            }
            case GLOBAL -> {
                scopeValue.setVisible(true);
                scopeValue.setReadOnly(true);
                scopeValue.setValue("*");
            }
        }
    }

    public void setFlowJsonToGrid(ApprovalFlowConfig bean, boolean isCreate) {
        if (isCreate) {
            for (Option<String> interval : intervalOpts) {
                DragAndSelect<Option<String>> selectGrid = reviewFlowGridMap.get(interval.getValue());
                selectGrid.setItems(reviewOpts);
                selectGrid.selectAll();
            }
        } else {
            Map<String, List<String>> flowMap;
            try {
                flowMap = objectMapper.readValue(bean.getFlowJson(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            for (Option<String> interval : intervalOpts) {
                List<String> selectedVals = flowMap.get(interval.getValue());
                if (selectedVals == null) continue;
                // 依 JSON 取得選中的 Option
                List<Option<String>> selected = selectedVals.stream()
                        .map(reviewOptMap::get)
                        .filter(Objects::nonNull)
                        .toList();
                // 未選中的就是 reviewOpts 減去 selected
                List<Option<String>> unselected = reviewOpts.stream()
                        .filter(opt -> !selected.contains(opt))
                        .toList();
                // 先放 selected（照 JSON 順序），再接 unselected
                List<Option<String>> ordered = new ArrayList<>();
                ordered.addAll(selected);
                ordered.addAll(unselected);

                DragAndSelect<Option<String>> selectGrid = reviewFlowGridMap.get(interval.getValue());

                // 更新 Grid 項目順序
                selectGrid.setItems(ordered);
                // 設定勾選狀態
                selectGrid.deselectAll();
                selectGrid.select(selected);
            }
        }
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            ApprovalFlowConfig bean = binder.getBean();
            setSelectorDataToScopeValue(bean);
            bean.setScopeValue(scopeValue.getValue());
            bean.setFlowJson(setFlowGridToFlowJson());
            fireEvent(new SaveEvent(this, bean));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            ApprovalFlowConfig bean = binder.getBean();
            bean.setFlowJson(setFlowGridToFlowJson());
            fireEvent(new UpdateEvent(this, bean));
        }
    }

    public void setSelectorDataToScopeValue(ApprovalFlowConfig bean) {
        Option<String> st = scopeType.getValue();
        if (st == null) {
            scopeType.setInvalid(true);
            scopeType.setErrorMessage("請選擇範圍類型");
            return;
        }
        ApprovalScopeType type = ApprovalScopeType.fromValue(st.getValue());

        switch (type) {
            case COMPANY -> {
                if (companySelect.isEmpty()) {
                    companySelect.setInvalid(true);
                    return;
                }
                scopeValue.setValue(companySelect.getValue().getValue());
            }
            case DEPARTMENT -> {
                if (departmentSelect.isEmpty()) {
                    departmentSelect.setInvalid(true);
                    return;
                }
                scopeValue.setValue(String.valueOf(departmentSelect.getValue().getValue()));

            }
            case EMPLOYEE -> {
                if (departmentSelect.isEmpty()) {
                    departmentSelect.setInvalid(true);
                    return;
                }
                if (employeeSelect.isEmpty()) {
                    employeeSelect.setInvalid(true);
                    return;
                }
                scopeValue.setValue(String.valueOf(employeeSelect.getValue().getValue()));

            }
            case GLOBAL -> {
                return;
            }
        }
    }

    public String setFlowGridToFlowJson() {
        Map<String, List<String>> flowMap = new LinkedHashMap<>();

        for (Option<String> interval : intervalOpts) {
            DragAndSelect<Option<String>> selectGrid = reviewFlowGridMap.get(interval.getValue());
            if (selectGrid == null) continue;
            // 把 Grid 已選取的項目取出來，轉成它們的 value
            List<Option<String>> selected = selectGrid.getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                Notification.show("請在「" + interval.getName() + "」至少選擇一位簽核人");
                return null;
            }
            flowMap.put(interval.getValue(), selected.stream().map(Option::getValue).toList());
        }
        try {
            return objectMapper.writeValueAsString(flowMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert flowMap to JSON", e);
        }
    }

    public void resetScopeInputs() {
        companySelect.setVisible(false);
        departmentSelect.setVisible(false);
        employeeSelect.setVisible(false);
        scopeValue.setVisible(false);
        scopeValue.setReadOnly(false);

        companySelect.clear();
        departmentSelect.clear();
        employeeSelect.clear();
    }

    public void setDialogView(Boolean isCreate) {
        save.setVisible(isCreate);
        update.setVisible(!isCreate);
        delete.setVisible(!isCreate);

        companySelect.setReadOnly(!isCreate);
        departmentSelect.setReadOnly(!isCreate);
        employeeSelect.setReadOnly(!isCreate);
        scopeType.setReadOnly(!isCreate);
        scopeValue.setReadOnly(!isCreate);
    }

    private Component createButtonsLayout() {
        confirmDialog = new ConfirmDialog();
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        delete.addClickListener(click -> {
            confirmDialog.openDialogWithParameter("確認執行刪除?", "刪除");
        });
        confirmDialog.setConfirmAction(() -> {
            fireEvent(new DeleteEvent(this, binder.getBean()));
            confirmDialog.close();
        });
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        addDialogCloseActionListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }


    public static abstract class ApprovalFlowConfigDialogEvent extends ComponentEvent<ApprovalFlowConfigDialog> {
        private final ApprovalFlowConfig afcfg;

        protected ApprovalFlowConfigDialogEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, false);
            this.afcfg = afcfg;
        }

        public ApprovalFlowConfig getApprovalFlowConfig() {
            return afcfg;
        }
    }

    public static class SaveEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        SaveEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }
    }

    public static class UpdateEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        UpdateEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }
    }

    public static class DeleteEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        DeleteEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }

    }

    public static class CloseEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        CloseEvent(ApprovalFlowConfigDialog source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<ApprovalFlowConfigDialog.DeleteEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<ApprovalFlowConfigDialog.SaveEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<ApprovalFlowConfigDialog.UpdateEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<ApprovalFlowConfigDialog.CloseEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.CloseEvent.class, listener);
    }
}
