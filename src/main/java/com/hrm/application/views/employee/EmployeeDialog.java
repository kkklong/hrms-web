package com.hrm.application.views.employee;

import com.hrm.application.component.ConfirmDialog;
import com.hrm.application.entity.Department;
import com.hrm.application.entity.Employee;
import com.hrm.application.model.Option;
import com.hrm.application.service.EmployeeService;
import com.hrm.application.util.ToolUtil;
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

import static com.hrm.application.util.ToolUtil.dataConverter;

public class EmployeeDialog extends Dialog {

    private List<Option<Integer>> companyList;
    private Map<Integer, Option<Integer>> companyMap;
    private List<Option<Integer>> departmentList;
    private Map<Integer, Option<Integer>> departmentMap;
    private final EmployeeService service;

    private TextField fullName = new TextField("全名");
    private TextField nickName = new TextField("英文名");
    private TextField position = new TextField("職位");
    private TextField salary = new TextField("薪資");
    private TextField account = new TextField("帳號");
    private PasswordField password = new PasswordField("密碼");
    private ComboBox<Option<Integer>> company = new ComboBox<>("公司");
    private ComboBox<Option<Integer>> department = new ComboBox<>("部門");
    private Select<String> gender = new Select<>();
    private DatePicker birthday = new DatePicker("生日");
    private TextField phone = new TextField("連絡電話");
    private EmailField email = new EmailField("信箱");
    private TextField emergencyContact = new TextField("緊急聯絡人");
    private TextField address = new TextField("通訊地址");
    private ComboBox<Option<Integer>> role = new ComboBox<>("角色");
    private DatePicker entryDate = new DatePicker("入職日");
    private TextField remark = new TextField("備註說明");
    private TextField floor = new TextField("所在樓層");
    private TextField seatNumber = new TextField("座位編號");
    private RadioButtonGroup<Integer> overtimeType = new RadioButtonGroup("加班是否換錢");
    private TextField laborInsuranceFee = new TextField("勞保費用");
    private TextField healthInsuranceFee = new TextField("健保費用");
    private TextField holidayDutyAllowance = new TextField("假日津貼");
    private TextField afternoonShiftAllowance = new TextField("午班津貼");
    private TextField nightShiftAllowance = new TextField("晚班津貼");
    private TextField fullAttendanceBonus = new TextField("全勤津貼");
    private ComboBox<Option<Byte>> status = new ComboBox<>("狀態");
    private DatePicker outDate = new DatePicker("離職日");
    private TextField relationship = new TextField("與緊急聯絡人的關係");
    private TextField emergencyContactPhone = new TextField("緊急聯絡人電話");

    private TextField skype = new TextField("skype帳號");
    private TextField telegram = new TextField("telegram帳號");
    private TextField idNumber = new TextField("身份證字號");
    private TextField mealAllowance = new TextField("伙食津貼");
    private TextField employeeNumber = new TextField("員工編號");
    private TextField highestEducationLevel = new TextField("最高學歷");
    private TextField emergencyContactAddress = new TextField("緊急連絡人通訊地址");
    private TextField registeredAddress = new TextField("戶籍地址");
    private TextField voluntaryPensionContribution = new TextField("戶籍勞退自提");
    private TextField insuredDependentsCount = new TextField("投保眷口數");
    private TextField withholdingTax = new TextField("代扣稅款");
    private TextField companyLaborInsuranceFee = new TextField("公司付擔勞保費用");
    private TextField companyHealthInsuranceFee = new TextField("公司付擔健保費用");


    private Button resetPassword = new Button("重設密碼");
    private Button save = new Button("儲存");
    private Button update = new Button("更新");
    private Button delete = new Button("刪除");
    private Button close = new Button("取消");
    // Other fields omitted
    private Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
    private HorizontalLayout ht = new HorizontalLayout();
    private ResetPasswordDialog resetPasswordDialog;
    private ConfirmDialog confirmDialog;
    private Integer oldDepartmentId;

    public EmployeeDialog(EmployeeService service,
                          List<Option<Integer>> companyList,
                          List<Option<Byte>> employeeStatusEnumList,
                          List<Option<Integer>> departmentList,
                          List<Option<Integer>> roleList) {
        this.service = service;
        this.companyList = companyList;
        this.departmentList = departmentList;
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

        binder.forField(department)
                .withConverter(dataConverter(departmentList))
                .bind(Employee::getDepartmentId, Employee::setDepartmentId);

        binder.forField(role)
                .withConverter(dataConverter(roleList))
                .bind(Employee::getRoleId, Employee::setRoleId);

        binder.forField(status)
                .withConverter(dataConverter(employeeStatusEnumList))
                .bind(Employee::getStatus, Employee::setStatus);
        binder.forField(company)
                .withConverter(dataConverter(companyList))
                .bind(Employee::getCompany, Employee::setCompany);

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

