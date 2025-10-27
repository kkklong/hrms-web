package com.hrm.application.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DragAndSelect<T> extends VerticalLayout {

    private final Grid<T> grid = new Grid<>();
    private final Button invisibleObj = new Button();

    private final List<T> dataList = new ArrayList<>();
    private T draggedTarget = null;


    public DragAndSelect() {

        // 沒間隔
        setMargin(false);
        setPadding(false);
        setSpacing(false);

        // 設定Grid
        grid.setRowsDraggable(true); // 啟用拖曳列
        grid.setDropMode(GridDropMode.BETWEEN); // 放在項目之間
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        // 拖曳邏輯
        grid.addCellFocusListener(e -> {
            draggedTarget = e.getItem().orElse(null);
        });
        grid.addDropListener(this::onGridDrop);

        add(grid);
    }

    public DragAndSelect(List<T> items) {
        this();
        setItems(items);
    }

    private void onGridDrop(GridDropEvent<T> event) {

        if (draggedTarget == null) {
            return;
        }

        // 目前選取的項目
        T draggedItem = draggedTarget;
        // 放下位置的項目
        T targetItem = event.getDropTargetItem().orElse(null);

        // 更新順序
        int targetIndex = (targetItem != null) ? dataList.indexOf(targetItem) : dataList.size();
        dataList.remove(draggedItem);
        if (targetIndex > dataList.size()) {
            dataList.add(draggedItem);
        } else {
            dataList.add(targetIndex, draggedItem);
        }

        // 刷新Grid的資料展示
        grid.getDataProvider().refreshAll();

        // 移除Grid資料列的聚焦狀態，以便之後的點選能重新聚焦
        invisibleObj.focus();
    }

    public Grid.Column<T> addColumn(ValueProvider<T, ?> valueProvider) {
        return grid.addColumn(valueProvider);
    }

    public void setItems(List<T> items) {
        dataList.clear();
        dataList.addAll(items);
        grid.setItems(dataList);
    }

    public List<T> getSelectedItems() {
        Set<T> selectedSet = grid.getSelectedItems();
        return new ArrayList<>(dataList.stream().filter(selectedSet::contains).toList()); // 傳回 copy，避免外部修改
    }

    public void select(T... items) {
        grid.asMultiSelect().select(items);
    }

    public void select(Iterable<T> items) {
        grid.asMultiSelect().select(items);
    }

    public void selectAll() {
        grid.asMultiSelect().select(dataList);
    }

    public void deselect(T... items) {
        grid.asMultiSelect().deselect(items);
    }

    public void deselect(Iterable<T> items) {
        grid.asMultiSelect().deselect(items);
    }

    public void deselectAll() {
        grid.asMultiSelect().deselectAll();
    }

    public void setAllRowsVisible(Boolean isVisible) {
        grid.setAllRowsVisible(isVisible);
    }

    public void setRowsDraggable(Boolean isDraggable){
        grid.setRowsDraggable(isDraggable); // 啟用拖曳列
    }

}
