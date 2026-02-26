package com.amichettinestor.booknext.booknext.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatchUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(
                source,
                target,
                getNullPropertyNames(source)
        );
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);

        return Arrays.stream(wrapper.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> wrapper.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}
