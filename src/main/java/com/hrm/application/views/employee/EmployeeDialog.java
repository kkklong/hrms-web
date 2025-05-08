package com.hrm.application.views.employee;

import com.hrm.application.component.ConfirmDialog;
import com.hrm.application.entity.Employee;
import com.hrm.application.entity.Option;
import com.hrm.application.service.EmployeeService;
import com.hrm.application.util.ToolUtil;
import com.hrm.application.views.department.DepartmentDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmployeeDialog extends Dialog {

    List<Option<Integer>> companyList;
    Map<Integer, Option<Integer>> companyMap;
    List<Option<Integer>> departmentList;
    Map<Integer, Option<Integer>> departmentMap;
    Map<Integer, Option<Integer>> roleMap;
    private final EmployeeService service;

    TextField fullName = new TextField("全名");
    TextField nickName = new TextField("英文名");
    TextField position = new TextField("職位");
    TextField salary = new TextField("薪資");
    TextField account = new TextField("帳號");
    PasswordField password = new PasswordField("密碼");
    ComboBox<Option<Integer>> company = new ComboBox<>("公司");
    ComboBox<Option<Integer>> department = new ComboBox<>("部門");
    Select<String> gender = new Select<>();
    DatePicker birthday = new DatePicker("生日");
    TextField phone = new TextField("連絡電話");
    EmailField email = new EmailField("信箱");
    TextField emergencyContact = new TextField("緊急聯絡人");
    TextField address = new TextField("通訊地址");
    ComboBox<Option<Integer>> role = new ComboBox<>("角色");
    DatePicker entryDate = new DatePicker("入職日");
    TextField remark = new TextField("備註說明");
    TextField floor = new TextField("所在樓層");
    TextField seatNumber = new TextField("座位編號");
    RadioButtonGroup<Integer> overtimeType = new RadioButtonGroup("加班是否換錢");
    TextField laborInsuranceFee = new TextField("勞保費用");
    TextField healthInsuranceFee = new TextField("健保費用");
    TextField holidayDutyAllowance = new TextField("假日津貼");
    TextField afternoonShiftAllowance = new TextField("午班津貼");
    TextField nightShiftAllowance = new TextField("晚班津貼");
    TextField fullAttendanceBonus = new TextField("全勤津貼");
    ComboBox<Option<Byte>> status = new ComboBox<>("狀態");
    DatePicker outDate = new DatePicker("離職日");
    TextField relationship = new TextField("與緊急聯絡人的關係");
    TextField emergencyContactPhone = new TextField("緊急聯絡人電話");

    TextField skype = new TextField("skype帳號");
    TextField telegram = new TextField("telegram帳號");
    TextField idNumber = new TextField("身份證字號");
    TextField mealAllowance = new TextField("伙食津貼");
    TextField employeeNumber = new TextField("員工編號");
    TextField highestEducationLevel = new TextField("最高學歷");
    TextField emergencyContactAddress = new TextField("緊急連絡人通訊地址");
    TextField registeredAddress = new TextField("戶籍地址");
    TextField voluntaryPensionContribution = new TextField("戶籍勞退自提");
    TextField insuredDependentsCount = new TextField("投保眷口數");
    TextField withholdingTax = new TextField("代扣稅款");
    TextField companyLaborInsuranceFee = new TextField("公司付擔勞保費用");
    TextField companyHealthInsuranceFee = new TextField("公司付擔健保費用");


    Button resetPassword = new Button("重設密碼");
    Button save = new Button("儲存");
    Button update = new Button("更新");
    Button delete = new Button("刪除");
    Button close = new Button("取消");
    // Other fields omitted
    Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
    HorizontalLayout ht = new HorizontalLayout();
    ResetPasswordDialog resetPasswordDialog;
    ConfirmDialog confirmDialog;
    private Integer oldDepartmentId;

    public EmployeeDialog(EmployeeService service,
                          List<Option<Integer>> companyList,
                          List<Option<Byte>> employeeStatusEnumList,
                          List<Option<Integer>> departmentList,
                          List<Option<Integer>> roleList,
                          Map<Byte, Option<Byte>> employeeStatusEnumMap) {

        this.service = service;
        this.companyList = companyList;
        this.companyMap = ToolUtil.transToMap(companyList, Option::getValue);
        this.departmentList = departmentList;
        this.departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
        this.roleMap = ToolUtil.transToMap(roleList, Option::getValue);
        addClassName("employee-dialog");
        setComponentSize();
        resetPasswordDialog = new ResetPasswordDialog(service);
        resetPasswordDialog.addCloseListener(event -> resetPasswordDialog.close());
        company.setItems(companyList);
        company.setItemLabelGenerator(Option::getName);
        department.setItems(departmentList);
        department.setItemLabelGenerator(Option::getName);
        status.setItems(employeeStatusEnumList);
        status.setItemLabelGenerator(Option::getName);
        role.setItems(roleList);
        role.setItemLabelGenerator(Option::getName);
        gender.setLabel("性別");
        gender.setItems("其他", "男", "女");
        gender.setValue("其他");
        overtimeType.setItems(1, 2);
        overtimeType.setItemLabelGenerator(value -> value.equals(1) ? "是" : "否");

        VerticalLayout vt = new VerticalLayout();
        vt.add(
                getLayoutLine(fullName, nickName),
                getLayoutLine(account, password),
                getLayoutLine(company ,employeeNumber, idNumber),
                getLayoutLine(department, role, position),
                getLayoutLine(entryDate, outDate, status),
                getLayoutLine(highestEducationLevel, gender, birthday),
                getLayoutLine(phone, email),
                getLayoutLine(skype, telegram),
                getLayoutLine(address),
                getLayoutLine(registeredAddress),
                getLayoutLine(emergencyContact, relationship, emergencyContactPhone),
                getLayoutLine(emergencyContactAddress),
                getLayoutLine(floor, seatNumber),
                getLayoutLine(salary, laborInsuranceFee, healthInsuranceFee),
                getLayoutLine(holidayDutyAllowance, afternoonShiftAllowance, nightShiftAllowance),
                getLayoutLine(mealAllowance, fullAttendanceBonus, overtimeType),
                getLayoutLine(voluntaryPensionContribution, insuredDependentsCount, withholdingTax),
                getLayoutLine(companyLaborInsuranceFee, companyHealthInsuranceFee),
                getLayoutLine(remark)
        );
        add(vt);
        getFooter().add(createButtonsLayout());

        binder.bind(department, employee -> departmentMap.get(employee.getDepartmentId()),
                (employee, select) -> employee.setDepartmentId(select.getValue()));

        binder.bind(role, employee -> roleMap.get(employee.getRoleId()),
                (employee, select) -> employee.setRoleId(select.getValue()));

        binder.bind(status, employee -> employeeStatusEnumMap.get(employee.getStatus()),
                (employee, select) -> employee.setStatus(select.getValue()));

        binder.bind(company, employee -> companyMap.get(employee.getCompany()),
                (employee, select) -> employee.setCompany(select.getValue()));

        binder.bindInstanceFields(this);
    }

    private HorizontalLayout getLayoutLine(Component... fields) {
        HorizontalLayout layout = new HorizontalLayout(fields);
        layout.setWidth("40em");
        return layout;
    }

    private void setComponentSize() {
        List<HasSize> fields = Arrays.asList(
                fullName, nickName, position, salary, account, phone, emergencyContact, relationship, emergencyContactPhone,
                address, remark, floor, seatNumber, laborInsuranceFee, healthInsuranceFee, holidayDutyAllowance, afternoonShiftAllowance
                , nightShiftAllowance, fullAttendanceBonus, idNumber, mealAllowance, employeeNumber, highestEducationLevel,
                emergencyContactAddress, registeredAddress, voluntaryPensionContribution, insuredDependentsCount, withholdingTax,
                companyLaborInsuranceFee, companyHealthInsuranceFee, skype, telegram, role, password, company, department, gender, birthday, email
                , entryDate, outDate, status, overtimeType
        );
        fields.forEach(field -> {
            field.setWidthFull();
        });
    }

    private Component createButtonsLayout() {
        confirmDialog = new ConfirmDialog();
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        resetPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        resetPassword.addClickListener(event -> resetPassword());
        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        delete.addClickListener(click -> {
            confirmDialog.openDialogWithParameter("確認執行刪除?", "刪除");
        });
        confirmDialog.setConfirmAction(() -> {
            fireEvent(new DeleteEvent(this, binder.getBean()));
            confirmDialog.close();
        });
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(resetPassword, save, update, delete, close);
    }

    private void validateAndSave() {
            fireEvent(new SaveEvent(EmployeeDialog.this, binder.getBean()));
    }

    private void validateAndUpdate() {
        Employee employeeBeforeSave = binder.getBean();
        Integer newDepartment = employeeBeforeSave.getDepartmentId();

        if (binder.isValid()) {
            // 檢查部門是否變更
            if (!Objects.equals(newDepartment, oldDepartmentId)) {
                DepartmentChangeDialog departmentChangeDialog = new DepartmentChangeDialog(
                        new DepartmentChangeDialog.DepartmentChangeListener() {
                            @Override
                            public void onDefaultScheduleSelected() {
                                binder.getBean().setUpdateShiftToDefault(true);
                                fireEvent(new UpdateEvent(EmployeeDialog.this, binder.getBean()));
                            }

                            @Override
                            public void onKeepCurrentScheduleSelected() {
                                binder.getBean().setUpdateShiftToDefault(false);
                                fireEvent(new UpdateEvent(EmployeeDialog.this, binder.getBean()));
                            }
                        }
                );
                departmentChangeDialog.open();
            } else {
                fireEvent(new UpdateEvent(EmployeeDialog.this, binder.getBean()));
            }
        }
    }

    public void resetPassword() {
        Employee currentEmployee = binder.getBean();
        if (currentEmployee != null && currentEmployee.getId() != null) {
            resetPasswordDialog.setPassword(currentEmployee.getId());
            resetPasswordDialog.open();
        } else {
            Notification.show("請先選擇員工");
        }
    }

    public void setDialogView (Boolean isCreate) {
        save.setVisible(isCreate);
        update.setVisible(!isCreate);
        delete.setVisible(!isCreate);
        password.setVisible(isCreate);
        resetPassword.setVisible(!isCreate);
    }

    public void setEmployee(Employee employee) {
        binder.setBean(employee);
        if (employee != null && employee.getId() != null)
            this.oldDepartmentId = binder.getBean().getDepartmentId();
    }

    public Map<Integer, Option<Integer>> getDepartmentMap() {
        return departmentMap;
    }

    // Events
    public static abstract class EmployeeDialogEvent extends ComponentEvent<EmployeeDialog> {
        private final Employee employee;

        protected EmployeeDialogEvent(EmployeeDialog source, Employee employee) {
            super(source, false);
            this.employee = employee;
        }

        public Employee getEmployee() {
            return employee;
        }
    }

    public static class SaveEvent extends EmployeeDialogEvent {
        SaveEvent(EmployeeDialog source, Employee employee) {
            super(source, employee);
        }
    }

    public static class UpdateEvent extends EmployeeDialogEvent {
        UpdateEvent(EmployeeDialog source, Employee employee) {
            super(source, employee);
        }
    }

    public static class DeleteEvent extends EmployeeDialogEvent {
        DeleteEvent(EmployeeDialog source, Employee employee) {
            super(source, employee);
        }

    }

    public static class CloseEvent extends EmployeeDialogEvent {
        CloseEvent(EmployeeDialog source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}

