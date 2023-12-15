package com.example.springboot001.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class CommonUtils {

    public static <T> List<T> objectToList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                // use Class.cast
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }




}
