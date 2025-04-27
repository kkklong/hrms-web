package com.hrm.application.views.employee;

import com.hrm.application.entity.Employee;
import com.hrm.application.entity.Option;
import com.hrm.application.entity.Role;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.service.DepartmentService;
import com.hrm.application.service.EmployeeService;
import com.hrm.application.service.RoleService;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Scope("prototype")
@Route(value = "employee", layout = MainLayout.class)
@PageTitle("員工資料 | 人力資源管理系統")
public class EmployeeView extends VerticalLayout {
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmployeeView.class);
//
//    Grid<Employee> grid = new Grid<>(Employee.class, false);
//    EmployeeDialog dialog;
//    EmployeeService employeeService;
//    DepartmentService departmentService;
//    RoleService roleService;
//    private ComboBox<Employee> nickNameFilter = new ComboBox<>();
//    private TextField accountFilter = new TextField();
//    private TextField fullNameFilter = new TextField();
//    private TextField genderFilter = new TextField();
//    private TextField positionFilter = new TextField();
//    private TextField floorFilter = new TextField();
//    private TextField mailFilter = new TextField();
//    private TextField employeeNumberFilter = new TextField();
//
//    private TextField seatNumberFilter = new TextField();
//    private DatePicker entryDateFilter = new DatePicker();
//
//    ComboBox<Option<Integer>> departmentFilter = new ComboBox<>();
//    ComboBox<Option<Byte>> employeeStatusFilter = new ComboBox<>();
//
//    private List<Option<Byte>> employeeStatusEnumList;
//    private Map<Byte, Option<Byte>> employeeStatusEnumMap;
//    private List<Option<Integer>> departmentList;
//    private Map<Integer, Option<Integer>> departmentMap;
//    private List<Option<Integer>> companyList;
//    private Map<Integer, Option<Integer>> companyMap;
//
//    private List<Employee> employeeList;
//    private ListDataProvider<Employee> dataProvider;
//    private HeaderRow headerRow;
//
//
//    public EmployeeView(EmployeeService employeeService, DepartmentService departmentService, RoleService roleService) {
//        this.employeeService = employeeService;
//        this.departmentService = departmentService;
//        this.roleService = roleService;
//        setData();
//        List<Option<Integer>> roles = employeeService.getRoleEnumList();
//        addClassName("employee-view");
//        setSizeFull();
//        configureGrid();
//        configureDialog(roles);
//        configureFilter();
//
//        add(titleConfigure(), getToolbar(), getContent());
//        updateList();
//        closeEditor();
//    }
//
//    private void setData() {
//        employeeStatusEnumList = employeeService.getEmployeeStatusOptionList();
//        employeeStatusEnumMap = ToolUtil.transToMap(employeeStatusEnumList, Option::getValue);
//        departmentList = departmentService.getDepartmentOptionList();
//        departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
//        departmentFilter.setItems(departmentList);
//        departmentFilter.setItemLabelGenerator(Option::getName);
//        employeeList = employeeService.getAll();
//        nickNameFilter.setItems(employeeList);
//        nickNameFilter.setItemLabelGenerator(Employee::getNickName);
//        employeeStatusFilter.setItems(employeeStatusEnumList);
//        employeeStatusFilter.setItemLabelGenerator(Option::getName);
//        companyList = employeeService.getCompanyTypeOptionList();
//        companyMap = ToolUtil.transToMap(companyList, Option::getValue);
//    }
//
//    private HorizontalLayout titleConfigure() {
//        HorizontalLayout titleHt = new HorizontalLayout();
//        H3 title = new H3("員工資料");
//        title.addClassName("employee-title");
//        titleHt.add(title);
//        return titleHt;
//    }
//
//    private HorizontalLayout getContent() {
//        HorizontalLayout content = new HorizontalLayout(grid, dialog);
//        content.setFlexGrow(2, grid);
//        content.setFlexGrow(1, dialog);
//        content.addClassNames("grid-content");
//        content.setSizeFull();
//        return content;
//    }
//
//    private void configureDialog(List<Option<Integer>> roles) {
//        dialog = new EmployeeDialog(companyList,departmentList, roles, employeeService, employeeStatusEnumList, employeeStatusEnumMap);
//        dialog.addSaveListener(this::saveEmployee);
//        dialog.addDeleteListener(this::deleteEmployee);
//        dialog.addCloseListener(e -> closeEditor());
//    }
//
//    private void saveEmployee(EmployeeDialog.SaveEvent event) {
//        Employee employee = event.getEmployee();
//        boolean success = employeeService.add(employee);
//        if (success) {
//            Notification.show("儲存成功");
//            updateList();
//            closeEditor();
//        }
//    }
//
//    private void deleteEmployee(EmployeeDialog.DeleteEvent event) {
//        boolean success = employeeService.delete(event.getEmployee());
//        if (success) {
//            Notification.show("刪除成功");
//            updateList();
//            closeEditor();
//        }
//    }
//
//    private void configureGrid() {
//        grid.addClassNames("employee-grid");
//        grid.setSizeFull();
//        Grid.Column<Employee> employeeNumber = grid.addColumn(Employee::getEmployeeNumber).setHeader("員工編號").setKey("employeeNumber");
//        grid.addColumn(Employee::getFullName).setHeader("員工全名").setKey("fullName");
//        grid.addColumn(Employee::getNickName).setHeader("員工英文名").setKey("nickName");
//        grid.addColumn(Employee::getGender).setHeader("性別").setKey("gender");
//        grid.addColumn(employee -> dialog.getDepartmentMap().get(employee.getDepartmentId()).getName()).setHeader("部門").setKey("departmentId");
//        grid.addColumn(Employee::getPosition).setHeader("職位").setKey("position");
//        grid.addColumn(Employee::getAccount).setHeader("帳號").setKey("account");
//        grid.addColumn(Employee::getEmail).setHeader("信箱").setKey("email");
//        grid.addColumn(Employee::getEntryDate).setHeader("入職時間").setKey("entryDate");
//        grid.addColumn(employee -> Optional.ofNullable(employeeStatusEnumMap.get(employee.getStatus())).map(Option::getName).orElse("未知狀態")).setHeader("狀態").setKey("status");
//        grid.addColumn(Employee::getFloor).setHeader("所在樓層").setKey("floor");
//        grid.addColumn(Employee::getSeatNumber).setHeader("座位編號").setKey("seatNumber");
//        grid.addColumn(Employee::getRemark).setHeader("備註說明").setKey("remark");
//        grid.getColumns().forEach(col -> col.setAutoWidth(true));
//        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
//        grid.sort(List.of(new GridSortOrder<>(employeeNumber, SortDirection.ASCENDING)));
//        grid.setSortableColumns("employeeNumber", "fullName", "nickName", "entryDate", "floor", "departmentId", "status");
//
//    }
//
//    private Component getToolbar() {
//        Button addEmployeeButton = new Button("新增員工", click -> addEmployee());
//        addEmployeeButton.getStyle().set("--vaadin-button-border", "1px solid");
//        Button clearFiltersButton = new Button("重置篩選", event -> initializeFilters());
//        clearFiltersButton.getStyle().set("--vaadin-button-border", "1px solid");
//        clearFiltersButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//        var toolbar = new HorizontalLayout(addEmployeeButton, clearFiltersButton);
//        toolbar.addClassName("toolbar");
//        return toolbar;
//    }
//
//    private void configureFilter() {
//        nickNameFilter.addValueChangeListener(event -> applyFilter());
//        nickNameFilter.setClearButtonVisible(true);
//        departmentFilter.addValueChangeListener(event -> applyFilter());
//        departmentFilter.setClearButtonVisible(true);
//        employeeStatusFilter.addValueChangeListener(event -> applyFilter());
//        employeeStatusFilter.setClearButtonVisible(true);
//        entryDateFilter.addValueChangeListener(event -> applyFilter());
//        entryDateFilter.setClearButtonVisible(true);
//        List<TextField> fields = Arrays.asList(
//                employeeNumberFilter, fullNameFilter, accountFilter, genderFilter, positionFilter, floorFilter, mailFilter, seatNumberFilter
//        );
//        fields.forEach(field -> {
//            field.addValueChangeListener(event -> applyFilter());
//            field.setClearButtonVisible(true);
//            field.setWidthFull();
//            field.setValueChangeMode(ValueChangeMode.LAZY);
//            field.getStyle().set("--vaadin-input-field-border-width", "1.5px");
//        });
//        employeeNumberFilter.setPlaceholder("員工編號...");
//        fullNameFilter.setPlaceholder("全名...");
//        accountFilter.setPlaceholder("帳號...");
//        genderFilter.setPlaceholder("性別...");
//        positionFilter.setPlaceholder("職位...");
//        floorFilter.setPlaceholder("樓層...");
//        mailFilter.setPlaceholder("信箱...");
//        seatNumberFilter.setPlaceholder("座位編號...");
//
//        nickNameFilter.setPlaceholder("英文名...");
//        nickNameFilter.setClearButtonVisible(true);
//        nickNameFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
//        departmentFilter.setPlaceholder("部門...");
//        departmentFilter.setClearButtonVisible(true);
//        departmentFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
//        employeeStatusFilter.setPlaceholder("狀態...");
//        employeeStatusFilter.setClearButtonVisible(true);
//        employeeStatusFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
//        entryDateFilter.setPlaceholder("...之後入職");
//        entryDateFilter.setClearButtonVisible(true);
//        entryDateFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
//        entryDateFilter.setWidth("10em");
//        nickNameFilter.setWidth("8em");
//        departmentFilter.setWidth("8em");
//        employeeStatusFilter.setWidth("6em");
//
//        employeeNumberFilter.setWidth("8em");
//        fullNameFilter.setWidth("8em");
//        accountFilter.setWidth("8em");
//        genderFilter.setWidth("4em");
//        positionFilter.setWidth("8em");
//        floorFilter.setWidth("4em");
//        mailFilter.setWidth("8em");
//        seatNumberFilter.setWidth("6em");
//    }
//
//    private void applyFilter() {
//        ListDataProvider<Employee> provider = (ListDataProvider<Employee>) grid.getDataProvider();
//        provider.clearFilters();
//
//        if (!employeeNumberFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getEmployeeNumber() != null && employee.getEmployeeNumber().toLowerCase().contains(employeeNumberFilter.getValue().toLowerCase()));
//        }
//        if (!fullNameFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getFullName() != null && employee.getFullName().toLowerCase().contains(fullNameFilter.getValue().toLowerCase()));
//        }
//        if (!nickNameFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getNickName() != null && employee.getNickName().contains(nickNameFilter.getValue().getNickName()));
//        }
//        if (!departmentFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getDepartmentId() != null && employee.getDepartmentId() == (departmentFilter.getValue().getValue()));
//        }
//        if (!employeeStatusFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getStatus() != null && employee.getStatus() == (employeeStatusFilter.getValue().getValue()).byteValue());
//        }
//        if (!accountFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getAccount() != null && employee.getAccount().toLowerCase().contains(accountFilter.getValue().toLowerCase()));
//        }
//        if (!genderFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getGender() != null && employee.getGender().toLowerCase().equals(genderFilter.getValue()));
//        }
//        if (!positionFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getPosition() != null && employee.getPosition().toLowerCase().contains(positionFilter.getValue().toLowerCase()));
//        }
//        if (!floorFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getFloor() != null && employee.getFloor().toLowerCase().contains(floorFilter.getValue().toLowerCase()));
//        }
//        if (!mailFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getEmail() != null && employee.getEmail().toLowerCase().contains(mailFilter.getValue().toLowerCase()));
//        }
//        if (!seatNumberFilter.isEmpty()) {
//            provider.addFilter(employee -> employee.getSeatNumber() != null && employee.getSeatNumber().toLowerCase().contains(seatNumberFilter.getValue().toLowerCase()));
//        }
//        if (entryDateFilter.getValue() != null) {
//            LocalDate filterDate = entryDateFilter.getValue();
//            provider.addFilter(employee ->
//                    employee.getEntryDate() != null && employee.getEntryDate().isAfter(filterDate));
//        }
//    }
//
//    public void createEmployee(Employee employee) {
//        if (employee == null) {
//            closeEditor();
//        } else {
//            dialog.setEmployee(employee);
//            dialog.role.setVisible(true);
//            dialog.password.setVisible(true);
//            dialog.open();
//            addClassName("editing");
//        }
//    }
//
//    public void editEmployee(Employee employee) {
//        if (employee == null) {
//            closeEditor();
//        } else {
//            dialog.setEmployee(employee);
//            dialog.password.setVisible(false);
//            dialog.open();
//            addClassName("editing");
//        }
//    }
//
//    private void closeEditor() {
//        if (dialog != null) {
//            Employee emptyEmployee = new Employee();
//            dialog.setEmployee(emptyEmployee);
//            dialog.close();
//            removeClassName("editing");
//        }
//    }
//
//    private void addEmployee() {
//        grid.asSingleSelect().clear();
//        createEmployee(new Employee());
//    }
//
//    private void updateList() {
//        employeeList = employeeService.getAll();
//        if (employeeList != null && !employeeList.isEmpty()) {
//
//            List<Role> roles = roleService.getAll();
//            Map<Integer, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, d -> d, (a, b) -> b));
//            employeeList.forEach(e -> e.setRole(roleMap.get(e.getId())));
//            dataProvider = new ListDataProvider<>(employeeList);
//            grid.setItems(employeeList);
//
//            if (headerRow == null) {
//                headerRow = grid.appendHeaderRow();
//                configureHeaderRow();
//            }
//        }
//    }
//
//    private void configureHeaderRow() {
//        headerRow.getCell(grid.getColumnByKey("employeeNumber")).setComponent(employeeNumberFilter);
//        headerRow.getCell(grid.getColumnByKey("fullName")).setComponent(fullNameFilter);
//        headerRow.getCell(grid.getColumnByKey("nickName")).setComponent(nickNameFilter);
//        headerRow.getCell(grid.getColumnByKey("account")).setComponent(accountFilter);
//        headerRow.getCell(grid.getColumnByKey("departmentId")).setComponent(departmentFilter);
//        headerRow.getCell(grid.getColumnByKey("status")).setComponent(employeeStatusFilter);
//        headerRow.getCell(grid.getColumnByKey("gender")).setComponent(genderFilter);
//        headerRow.getCell(grid.getColumnByKey("position")).setComponent(positionFilter);
//        headerRow.getCell(grid.getColumnByKey("floor")).setComponent(floorFilter);
//        headerRow.getCell(grid.getColumnByKey("email")).setComponent(mailFilter);
//        headerRow.getCell(grid.getColumnByKey("seatNumber")).setComponent(seatNumberFilter);
//        headerRow.getCell(grid.getColumnByKey("entryDate")).setComponent(entryDateFilter);
//    }
//
//    private void initializeFilters() {
//        employeeNumberFilter.clear();
//        fullNameFilter.clear();
//        nickNameFilter.clear();
//        accountFilter.clear();
//        departmentFilter.clear();
//        employeeStatusFilter.clear();
//        genderFilter.clear();
//        positionFilter.clear();
//        floorFilter.clear();
//        mailFilter.clear();
//        seatNumberFilter.clear();
//        entryDateFilter.clear();
//    }

}
