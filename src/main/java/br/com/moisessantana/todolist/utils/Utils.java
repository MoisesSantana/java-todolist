package br.com.moisessantana.todolist.utils;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static boolean isInvalidDateRange(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime currentDate = LocalDateTime.now();

        boolean isStartAtAfterEndAt = startAt.isAfter(endAt);
        boolean isStartAtBeforeNow = startAt.isBefore(currentDate);
        boolean isEndAtBeforeNow = endAt.isBefore(currentDate);

        return isStartAtAfterEndAt || isStartAtBeforeNow || isEndAtBeforeNow;
    }
}
