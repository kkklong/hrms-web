package com.hrm.application.views.role;

import com.hrm.application.entity.Permission;
import com.hrm.application.entity.Role;
import com.hrm.application.service.RoleService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Map;

public class RoleDialog extends Dialog {

    RoleService service;

    List<Permission> permissionList;
    Map<String, Permission> permissionMap;

    TextField roleName = new TextField("角色名稱");
    MultiSelectListBox<Permission> menuPermission = new MultiSelectListBox<>();

    Binder<Role> binder = new BeanValidationBinder<>(Role.class);

    Button save = new Button("儲存");
    Button delete = new Button("刪除");
    Button close = new Button("取消");


    public RoleDialog(RoleService service) {
        this.service = service;

        permissionList = service.getPermissionList();
        permissionMap = ToolUtil.transToMap(permissionList, Permission::getCode);

        addClassName("role-dialog");
        addBodyContent();
        getFooter().add(createButtonsLayout());

        binder.bind(menuPermission, r -> service.mapPermissionsToSet(r.getMenuPermission(), permissionMap)
                , (r, pSet) -> r.setMenuPermission(service.transPermissionsSetToCodes(pSet)));
        binder.bindInstanceFields(this);
    }

    public void setTitle(String title) {
        setHeaderTitle(title);
    }

    private void addBodyContent() {
        roleName.setWidth("40em");
        menuPermission.setWidth("40em");
        menuPermission.setAriaLabel("權限");
        menuPermission.setItemLabelGenerator(p -> String.format("%s%s%s%s", "[", p.getCode(), "] - ", p.getText()));
        menuPermission.setItems(permissionList);
        Scroller scroller = new Scroller(menuPermission);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setHeight("40em");
        add(new VerticalLayout(roleName, scroller));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

//        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }


    public void setRole(Role role) {
        binder.setBean(role);
    }

    public void setDialogView(boolean isCreate, boolean isEdit) {
        delete.setVisible(isEdit);
    }

    public Map<String, Permission> getPermissionMap() {
        return permissionMap;
    }

    // Events
    public static abstract class RoleDialogEvent extends ComponentEvent<RoleDialog> {
        private final Role role;

        protected RoleDialogEvent(RoleDialog source, Role role) {
            super(source, false);
            this.role = role;
        }

        public Role getRole() {
            return role;
        }
    }

    public static class SaveEvent extends RoleDialogEvent {
        SaveEvent(RoleDialog source, Role role) {
            super(source, role);
        }
    }

    public static class DeleteEvent extends RoleDialogEvent {
        DeleteEvent(RoleDialog source, Role role) {
            super(source, role);
        }

    }

    public static class CloseEvent extends RoleDialogEvent {
        CloseEvent(RoleDialog source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }


}

