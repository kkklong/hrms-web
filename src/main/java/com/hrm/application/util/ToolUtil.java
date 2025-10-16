package com.hrm.application.util;

import com.hrm.application.entity.Permission;
import com.hrm.application.model.Option;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

//    public static <V> Converter<Set<V>, V> valueConverter(List<V> options) {
//        Map<Object, V> valueMap = options.stream()
//                .collect(Collectors.toMap(Object::hashCode, Function.identity())); // 可自定義 key
//        return new Converter<>() {
//            @Override
//            public Result<V> convertToModel(Set<V> fieldValues, ValueContext context) {
//                if (fieldValues == null || fieldValues.isEmpty()) {
//                    return Result.ok(null);
//                }
//                return Result.ok(fieldValues.iterator().next()); // 取第一個選項
//            }
//            @Override
//            public Set<V> convertToPresentation(V modelValue, ValueContext context) {
//                if (modelValue == null) {
//                    return Collections.emptySet();
//                }
//                return Set.of(modelValue);
//            }
//        };
//    }

    //---- Converter OptionData to binder ----
    public static <V> Converter<Option<V>, V> dataConverter(List<Option<V>> options) {
        Map<V, Option<V>> map = options.stream().collect(Collectors.toMap(Option::getValue, o -> o));
        return new Converter<>() {
            public Result<V> convertToModel(Option<V> fieldValue, ValueContext context) {
                return fieldValue == null ? Result.ok(null) : Result.ok(fieldValue.getValue());
            }

            public Option<V> convertToPresentation(V modelValue, ValueContext context) {
                return modelValue == null ? null : map.get(modelValue);
            }
        };
    }

    public static double getHoursWithMinutes(Temporal startDateTime, Temporal endDateTime) {
        Duration duration = Duration.between(startDateTime, endDateTime);
        return duration.toHours() + (duration.toMinutesPart() / 60.0);
    }

    //---- Converter nameList ----
    public static <V> String convertOptionValuesToNames(List<V> values, List<Option<V>> optionList) {
        if (values == null || values.isEmpty()) return "";
        Map<V, String> valueToNameMap = optionList.stream()
                .collect(Collectors.toMap(Option::getValue, Option::getName));
        return values.stream()
                .map(v -> valueToNameMap.getOrDefault(v, String.valueOf(v)))
                .collect(Collectors.joining(", "));
    }

    //---- Converter OptionList to binder ----
    public static <V> Converter<Set<Option<V>>, List<V>> getOptionConverter(List<Option<V>> optionList) {
        Map<V, Option<V>> valueMap = optionList.stream()
                .collect(Collectors.toMap(Option::getValue, o -> o));
        return new Converter<>() {
            @Override
            public Result<List<V>> convertToModel(Set<Option<V>> fieldValue, ValueContext context) {
                List<V> result = fieldValue.stream()
                        .map(Option::getValue)
                        .collect(Collectors.toList());
                return Result.ok(result);
            }

            @Override
            public Set<Option<V>> convertToPresentation(List<V> modelValue, ValueContext context) {
                if (modelValue == null) return Collections.emptySet();
                return modelValue.stream()
                        .map(valueMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        };
    }

}
