package com.hrm.application.views.role;

import com.hrm.application.entity.Permission;
import com.hrm.application.entity.Role;
import com.hrm.application.service.RoleService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Slf4j
public class Role2Dialog extends Dialog {

    RoleService service;
    List<Permission> permissionList = new ArrayList<>();
    Map<String, Permission> permissionMap;
    TextField roleName = new TextField("角色名稱");
    Binder<Role> binder = new BeanValidationBinder<>(Role.class);

    Button save = new Button("儲存");
    Button delete = new Button("刪除");
    Button close = new Button("取消");
    private TextField menuPermission = new TextField();
    private String allPermission;

    TreeGrid<Permission> permissionGrid = new TreeGrid<>();

    public Role2Dialog(RoleService service) {
        this.service = service;

        List<Permission> permissionRawList = service.getPermissionList();
        permissionMap = ToolUtil.transToMap(permissionRawList, Permission::getCode);
        allPermission = service.convertPermissionList2String(permissionRawList);
        addClassName("role-dialog");

        addBodyContent();
        getFooter().add(createButtonsLayout());

        binder.bindInstanceFields(this);
    }

    public void setTitle(String title) {
        setHeaderTitle(title);
    }

    private void addBodyContent() {
        roleName.setWidth("40em");
        add(new VerticalLayout(roleName, permissionGrid));

    }

    private void configureGrid() {
        permissionGrid.removeAllColumns();
        Role role;
//        if (binder.getBean() == null) {
//            role = new Role();
//        } else {
//            role = binder.getBean();
//        }

        role = binder.getBean() == null ? new Role() : binder.getBean();

        permissionGrid.addComponentColumn(permission -> {
            Checkbox checkbox = new Checkbox();
            boolean isSelected = isPermissionSelected(permission, role);
            checkbox.setValue(isSelected);

            checkbox.addValueChangeListener(event -> {
                if (event.getValue()) {
                    addPermissionToRole(permission, role);
                } else {
                    removePermissionFromRole(permission, role);
                }
            });
            return checkbox;
        }).setKey("selector").setWidth("2em");
        permissionGrid.addHierarchyColumn(
                p -> String.format("%s%s%s%s", "[", p.getCode(), "] - ", permissionMap.get(p.getCode()).getText())
        ).setKey("permission").setWidth("25em").setSortable(false);
        permissionGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        permissionGrid.setHeight("60em");
    }

    private boolean isPermissionSelected(Permission permission, Role role) {
        // 判斷 role 的 permissions 是否包含該行
        if (role == null) {
            return false;
        }
        String menuPermission = role.getMenuPermission();
        if (menuPermission == null || menuPermission.isEmpty()) {
            return false;
        }
        return Arrays.asList(menuPermission.split(",")).contains(permission.getCode());
    }

    private void addPermissionToRole(Permission permission, Role role) {
        // 將選中的 permission 加入 role 的 menuPermission
        String menuPermission = role.getMenuPermission();
        if (menuPermission == null || menuPermission.isEmpty()) {
            role.setMenuPermission(permission.getCode());
        } else if (!menuPermission.contains(permission.getCode())) {
            role.setMenuPermission(menuPermission + "," + permission.getCode());
        }
    }

    private void removePermissionFromRole(Permission permission, Role role) {
        // 從 role 的 menuPermission 中移除未選中的 permission
        String menuPermission = role.getMenuPermission();
        if (menuPermission != null && !menuPermission.isEmpty()) {
            List<String> permissions = new ArrayList<>(Arrays.asList(menuPermission.split(",")));
            permissions.remove(permission.getCode());
            role.setMenuPermission(String.join(",", permissions));
        }
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
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
        permissionList = service.buildPermissionTree(allPermission);
//        log.info("permissionList :" + permissionList.toString());
        configureGrid();

        permissionGrid.setItems(
                permissionList,
                Permission::getChildren
        );
        expandAll(permissionList);
        configureGrid();
    }

    private void expandAll(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        permissionGrid.expand(permissions); // 展開當前節點
        permissions.forEach(permission -> expandAll(permission.getChildren())); // 遞迴展開子節點
    }

    public void setDialogView(boolean isCreate, boolean isEdit) {
        delete.setVisible(isEdit);
    }

    public Map<String, Permission> getPermissionMap() {
        return permissionMap;
    }

    // Events
    public static abstract class Role2DialogEvent extends ComponentEvent<Role2Dialog> {
        private final Role role;

        protected Role2DialogEvent(Role2Dialog source, Role role) {
            super(source, false);
            this.role = role;
        }

        public Role getRole() {
            return role;
        }
    }

    public static class SaveEvent extends Role2DialogEvent {
        SaveEvent(Role2Dialog source, Role role) {
            super(source, role);
        }
    }

    public static class DeleteEvent extends Role2DialogEvent {
        DeleteEvent(Role2Dialog source, Role role) {
            super(source, role);
        }

    }

    public static class CloseEvent extends Role2DialogEvent {
        CloseEvent(Role2Dialog source) {
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

