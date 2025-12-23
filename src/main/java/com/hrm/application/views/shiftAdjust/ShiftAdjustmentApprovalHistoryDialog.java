package com.hrm.application.views.shiftAdjust;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.application.model.HistoryReview;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class ShiftAdjustmentApprovalHistoryDialog extends Dialog {
    private final ObjectMapper objectMapper = new ObjectMapper();
    List<HistoryReview> historyReviews = new ArrayList<>();
    Grid<HistoryReview> grid = new Grid<>(HistoryReview.class, false);
    Button close;

    public ShiftAdjustmentApprovalHistoryDialog(ShiftAdjustmentRequestVO shiftAdjustmentRequestVO) {
        setWidth("660px");

        List<HistoryReview> historyReviews = getHistoryReviewsFromLeaveRecords(shiftAdjustmentRequestVO);
        grid.setItems(historyReviews);
        grid.addColumn(HistoryReview::getBy).setHeader("操作人");
        grid.addColumn(HistoryReview::getAction).setHeader("結果");
        grid.addColumn(HistoryReview::getTime).setHeader("時間");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        add(new VerticalLayout(getContent()));
        getHeader().add(addCloseButton(), titleConfigure());
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("流程明細");
        titleHt.add(title);
        return titleHt;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        return content;
    }

    private List<HistoryReview> getHistoryReviewsFromLeaveRecords(ShiftAdjustmentRequestVO shiftAdjustmentRequestVO) {
        String historyReviewJson = shiftAdjustmentRequestVO.getHistoryReview();
        try {
            historyReviews = objectMapper.readValue(historyReviewJson, objectMapper.getTypeFactory().constructCollectionType(List.class, HistoryReview.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return historyReviews;
    }

    private Component addCloseButton() {
        close = new Button(new Icon("lumo", "cross"), event -> close());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        return close;
    }
}
