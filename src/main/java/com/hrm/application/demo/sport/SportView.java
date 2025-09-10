package com.hrm.application.demo.sport;

import com.hrm.application.demo.api168.service.LotteryService;
import com.hrm.application.demo.sport.service.SportService;
import com.hrm.application.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "sport", layout = MainLayout.class)

public class SportView extends VerticalLayout {
    SportService service;

    public SportView(SportService service) {
        this.service = service;
        getLogin();
    }


    private void getLogin() {
//        service.getMemberLogin();
        service.fetchResult();
    }

}
