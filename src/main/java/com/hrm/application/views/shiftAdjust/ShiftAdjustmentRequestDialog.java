package com.hrm.application.views.shiftAdjust;

import com.hrm.application.service.ShiftAdjustmentRequestService;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShiftAdjustmentRequestDialog extends Dialog {

    private final ShiftAdjustmentRequestService service;



    public ShiftAdjustmentRequestDialog(ShiftAdjustmentRequestService service) {
        this.service = service;
    }
}
