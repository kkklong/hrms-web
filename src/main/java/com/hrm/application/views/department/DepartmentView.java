package com.hrm.application.views.department;

import com.hrm.application.entity.Department;
import com.hrm.application.entity.Employee;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.DepartmentService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Scope("prototype")
@Route(value = "department", layout = MainLayout.class)
@MenuRouter(label = "Department", icon = VaadinIcon.OFFICE)
@PageTitle("部門資料 | 人力資源管理系統")
public class DepartmentView extends VerticalLayout {

    private DepartmentService service;

    private List<Option<Integer>> employeeList;
    private Map<Integer, Option<Integer>> employeeMap;
    private List<Option<String>> shiftTypeList;
    private Map<String, Option<String>> shiftTypeMap = new HashMap<>();


    Grid<Department> grid = new Grid<>(Department.class, false);
    TextField departmentFilter = new TextField();
    DepartmentDialog dialog;

    public DepartmentView(DepartmentService service) {
        this.service = service;
        this.addClassName("background-plan");
        configureGrid();
        configureFilter();
        add(titleConfigure(), getToolbar(), getContent());
        setSizeFull();
    }

    private void setData(){
        employeeList = service.getEmployeeOptionList();
        employeeMap = ToolUtil.transToMap(employeeList, Option::getValue);
        shiftTypeList = service.getShiftTypeOptionList();
        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, Option::getValue);
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("DepartmentData");
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

    private void configureDialog() {
        dialog = new DepartmentDialog(employeeList, shiftTypeList);
        dialog.addSaveListener(this::saveDepartment);
        dialog.addUpdateListener(this::updateDepartment);
        dialog.addDeleteListener(this::deleteDepartment);
        dialog.addCloseListener(e -> closeEditor());
    }

    private void saveDepartment(DepartmentDialog.SaveEvent event) {
        boolean success = service.save(event.getDepartment());
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        }
    }

    private void updateDepartment(DepartmentDialog.UpdateEvent event) {
        boolean success = service.update(event.getDepartment());
        if (success) {
            Notification.show("更新成功");
            updateList();
            closeEditor();
        }
    }

    private void deleteDepartment(DepartmentDialog.DeleteEvent event) {
        boolean success = service.delete(event.getDepartment());
        if (success) {
            Notification.show("刪除成功");
            updateList();
            closeEditor();
        }
    }

    private void configureGrid() {
        grid.addClassNames("department-grid");
        grid.setSizeFull();
        grid.addColumn(Department::getDepartmentName).setHeader("部門名稱");
        grid.addColumn(Department::getDescription).setHeader("描述");
        grid.addColumn(d -> employeeMap.get(d.getManagerId()).getName()).setHeader("部門主管");
        grid.addColumn(d -> Optional.ofNullable(shiftTypeMap.get(d.getWorkType())).map(Option::getName).orElse(d.getWorkType())).setHeader("預設班別");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addColumn(Department::getEveryDayMorningCount).setHeader("早班最少人數").setTextAlign(ColumnTextAlign.CENTER).setWidth("3em");
        grid.addColumn(Department::getEveryDayAfternoonCount).setHeader("午班最少人數").setTextAlign(ColumnTextAlign.CENTER).setWidth("3em");
        grid.addColumn(Department::getEveryDayNightCount).setHeader("晚班最少人數").setTextAlign(ColumnTextAlign.CENTER).setWidth("3em");
        grid.addColumn(Department::getCreatedDate).setHeader("創建時間");
        grid.addColumn(Department::getUpdatedDate).setHeader("更新時間");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event ->
                editDepartment(event.getValue()));
    }

    private Component getToolbar() {
        departmentFilter.setPlaceholder("搜尋部門...");
        departmentFilter.setValueChangeMode(ValueChangeMode.LAZY);
        Button addDepartmentButton = new Button("新增部門");
        departmentFilter.getStyle().set("--vaadin-input-field-border-width", "1px");
        addDepartmentButton.addClickListener(click -> createDepartment());
        var toolbar = new HorizontalLayout(departmentFilter, addDepartmentButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureFilter() {
        departmentFilter.addValueChangeListener(event -> applyFilter());
        departmentFilter.setClearButtonVisible(true);
    }

    private void applyFilter() {
        ListDataProvider<Department> provider = (ListDataProvider<Department>) grid.getDataProvider();
        provider.clearFilters();

        if (!departmentFilter.isEmpty()) {
            provider.addFilter(department -> department.getDepartmentName().contains(departmentFilter.getValue()));
        }
    }

    public void createDepartment() {
        grid.asSingleSelect().clear();
        dialog.setDepartment(new Department());
        dialog.setDialogView(true);
        dialog.open();
    }

    public void editDepartment(Department department) {
        if (department == null) {
            closeEditor();
        } else {
            dialog.setDepartment(department);
            dialog.setDialogView(false);
            dialog.open();
        }
    }

    private void closeEditor() {
        dialog.close();
    }

    private void addDepartment() {
        grid.asSingleSelect().clear();
        editDepartment(new Department());
    }

    private void updateList() {
            grid.setItems(service.getAll());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.access(() -> {
            try {
                setData(); // 執行會觸發 webClient.block() 的方法
                configureDialog();
                updateList();
            } catch (Exception e) {
                NotificationUtil.error("載入資料失敗：" + e.getMessage());
            }
        });
    }
}
