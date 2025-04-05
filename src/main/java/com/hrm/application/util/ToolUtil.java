package com.hrm.application.util;

import com.vaadin.flow.server.StreamResource;
import org.springframework.util.Assert;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolUtil {

    public static <T, K> Map<K, T> transToMap(List<T> tList, Function<T, K> keyGetter) {
        HashMap<K, T> map = new HashMap<>();
        if (tList == null) {
            return map;
        }
        for (T type : tList) {
            map.put(keyGetter.apply(type), type);
        }
        return map;
    }

    public static String getTextColorForHexBackground(String backgroundColor) {
        Color bgColor = Color.WHITE;
        try {
            bgColor = Color.decode(backgroundColor);
        } catch (NumberFormatException ignored) {
        }
        double luminance = 0.299 * bgColor.getRed() +
                0.587 * bgColor.getGreen() +
                0.114 * bgColor.getBlue();
        // 如果亮度大於128，返回黑色文字，否則返回白色文字
        return luminance > 128 ? "#000000" : "#ffffff";
    }

    public static StreamResource toStreamResource(String fileName, byte[] content) {
        return new StreamResource(fileName, () -> new ByteArrayInputStream(content));
    }

//    public static StreamResource toStreamResource(NameAddedByteArrayResource resource) {
//        if (resource == null) {
//            return null;
//        }
//        Assert.notNull(resource.getFilename(), "File name must not be null");
//        return new StreamResource(resource.getFilename(), () -> new ByteArrayInputStream(resource.getByteArray()));
//    }

//    public static double getHoursWithMinutes(Temporal startDateTime, Temporal endDateTime) {
//        Duration duration = Duration.between(startDateTime, endDateTime);
//        return duration.toHours() + (duration.toMinutesPart() / 60.0);
//    }

}
