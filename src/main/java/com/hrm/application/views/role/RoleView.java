package com.hrm.application.views.role;

import com.hrm.application.entity.Role;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.service.RoleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Route(value = "role", layout = MainLayout.class)
public class RoleView extends VerticalLayout {

    RoleService service;

    TextField filterText = new TextField();
    Grid<Role> grid = new Grid<>(Role.class, false);
    RoleDialog dialog;

    public RoleView(RoleService service) {
        this.service = service;

        addClassName("role-view");
        setSizeFull();
        configureGrid();
        configureDialog();

        add(titleConfigure(), getToolbar(), getContent());
        this.addClassName("background-plan");
        updateList();
        closeEditor();
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("RoleManager");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("搜尋...");
        filterText.getStyle().set("--vaadin-input-field-border-width", "1px");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> applyFilter());

        Button addRoleButton = new Button("新增角色");
        addRoleButton.addClickListener(click -> addRole());

        var toolbar = new HorizontalLayout(filterText, addRoleButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, dialog);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, dialog);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }


    private void configureGrid() {
        grid.addClassNames("role-grid");
        grid.setSizeFull();
        grid.addColumn(Role::getRoleName).setHeader("角色名");
        grid.addColumn(r -> service.mapPermissionsToString(r.getMenuPermission(), dialog.getPermissionMap())).setHeader("權限列表").setWidth("70%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event ->
                editRole(event.getValue()));
    }

    private void configureDialog() {
        dialog = new RoleDialog(service);
        dialog.addSaveListener(this::saveRole);
        dialog.addDeleteListener(this::deleteRole);
        dialog.addCloseListener(e -> closeEditor());
    }


    private void updateList() {
        grid.setItems(service.getAll());
        applyFilter();
    }

    private void applyFilter() {
        ListDataProvider<Role> provider = (ListDataProvider<Role>) grid.getDataProvider();
        provider.clearFilters();

        if (!filterText.isEmpty()) {
            provider.addFilter(role -> StringUtils.containsIgnoreCase(role.getRoleName(), filterText.getValue()));
        }
    }

    private void addRole() {
        grid.asSingleSelect().clear();
        dialog.setDialogView(true, false);
        dialog.setRole(new Role());
        dialog.open();
        addClassName("editing");
    }

    private void editRole(Role Role) {
        if (Role == null) {
            closeEditor();
        } else {
            dialog.setRole(Role);
            dialog.setDialogView(false, true);
            dialog.open();
            addClassName("editing");
        }
    }

    private void closeEditor() {
        dialog.close();
        removeClassName("editing");
    }


    private void saveRole(RoleDialog.SaveEvent event) {
        boolean success = service.save(event.getRole());
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        }
    }

    private void deleteRole(RoleDialog.DeleteEvent event) {
        boolean success = service.delete(event.getRole());
        if (success) {
            Notification.show("刪除成功");
            updateList();
            closeEditor();
        }
    }

}
