package com.hrm.application.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ShiftRouteStage {

    COMPLETED("COMPLETED", (byte) 0),
    LEADER_REVIEW("LEADER_REVIEW", (byte) 1),
    HR_REVIEW("HR_REVIEW", (byte) 2),
    TECH_LEAD_REVIEW("TECH_LEAD_REVIEW", (byte) 3),
    GM_REVIEW("GM_REVIEW", (byte) 4);

    private final String routeName;
    private final Byte stageCode;

    ShiftRouteStage(String routeName, Byte stageCode) {
        this.routeName = routeName;
        this.stageCode = stageCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public Byte getStageCode() {
        return stageCode;
    }

    private static final Map<String, ShiftRouteStage> BY_ROUTE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(ShiftRouteStage::getRouteName, Function.identity()));

    public static ShiftRouteStage fromRouteName(String routeName) {
        if (routeName == null) {
            return null;
        }
        return BY_ROUTE.get(routeName);
    }

    public static Byte getStageCodeByRouteName(String routeName) {
        ShiftRouteStage s = fromRouteName(routeName);
        return s != null ? s.getStageCode() : null;
    }
}
