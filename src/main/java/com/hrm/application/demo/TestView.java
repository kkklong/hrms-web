package com.hrm.application.demo;

import com.hrm.application.component.DragAndSelect;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "test", layout = MainLayout.class)
public class TestView extends VerticalLayout {
    private final TextArea textArea = new TextArea();
    private final Button button = new Button("Click me");
    private final DragAndSelect<Person> dragAndSelect = new DragAndSelect<>();

    public TestView() {
        // 初始化資料
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice", 1));
        people.add(new Person("Bob", 2));
        people.add(new Person("Charlie", 3));

        List<Person> defaultSelectedPeople = people.stream().filter(person -> person.id > 1).toList();
        Person charlie = people.stream().filter(person -> person.id == 3).findFirst().get();
        Person alice = people.stream().filter(person -> person.id == 1).findFirst().get();

        dragAndSelect.setItems(people);
        dragAndSelect.addColumn(Person::getName).setHeader("Name");

        // test
        dragAndSelect.select(defaultSelectedPeople);
        dragAndSelect.deselectAll();
        dragAndSelect.selectAll();
        dragAndSelect.deselect(defaultSelectedPeople);
        dragAndSelect.select(charlie);
        dragAndSelect.deselect(alice);


        button.addClickListener(e -> {
            textArea.setValue(dragAndSelect.getSelectedItems().stream().map(Person::getName).reduce((a, b) -> a + ", " + b).orElse(""));
        });

        add(textArea, button, dragAndSelect);
    }

    // 資料類別
    public static class Person {
        private String name;
        private Integer id;

        public Person() {
        }

        public Person(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}
