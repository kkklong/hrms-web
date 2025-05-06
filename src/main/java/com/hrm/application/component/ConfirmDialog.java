package com.hrm.application.component;

import com.hrm.application.model.Action;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Setter;

public class ConfirmDialog extends Dialog {
    private final Button confirmButton = new Button("Confirm");
    private final Button cancelButton = new Button("Cancel");
    private final Div textContainer = new Div();

    @Setter
    private Action confirmAction;
    @Setter
    private Action cancelAction;


    public ConfirmDialog() {
        resetAllAction();

        getFooter().add(createButtonsLayout());
        textContainer.getStyle()
                .set("text-align", "center")
                .set("width", "100%");
        add(textContainer);
    }

    private Action getDefaultAction() {
        return () -> {};
    }

    public void resetAllAction() {
        confirmAction = getDefaultAction();
        cancelAction = getDefaultAction();
    }

    private Component createButtonsLayout() {
        confirmButton.addClickListener(event -> confirmAction.apply());
        cancelButton.addClickListener(event -> closeDialog());
        addDialogCloseActionListener(event -> closeDialog());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return new HorizontalLayout(confirmButton, cancelButton);
    }

    public void openDialogWithParameter(String content) {
        openDialogWithParameter(null, content,null, null);
    }
    public void openDialogWithParameter(String content, String confirmText) {
        openDialogWithParameter(null, content, confirmText, null);
    }
    public void openDialogWithParameter(String title, String content, String confirmText) {
        openDialogWithParameter(title, content, confirmText, null);
    }
    public void openDialogWithParameter(String title, String content, String confirmText, String cancelText) {
        setHeaderTitle(title != null ? title : "");
        textContainer.setText(content != null ? content : "確認執行?");
        confirmButton.setText(confirmText != null ? confirmText : "確認");
        cancelButton.setText(cancelText != null ? cancelText : "取消");
        open();
    }

    private void closeDialog() {
        cancelAction.apply();
        close();
    }

}
