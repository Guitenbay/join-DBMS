package util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

    public static <T> T createEntityFor(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println(String.format("create %s error", clazz.getName()));
            e.printStackTrace();
            return null;
        }
    }

    public static <T> Map<String, Field> getFieldMapFor(Class<T> clazz) {
        Map<String, Field> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            map.put(field.getName(), field);
        }
        return map;
    }

    public static <T> Object getValueOfField(Field field, T entity) {
        String getMethodName = "get" + StringUtils.upperFirstChar(field.getName());
        try {
            Method getMethod = entity.getClass().getMethod(getMethodName);
            return getMethod.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(String.format("get %s from %s error", field.getName(), entity.getClass().getName()));
            e.printStackTrace();
            return null;
        }
    }

    public static <T, U> void setValueOfFieldFor(T entity, Field field, U value) {
        if (!TypeUtils.compareType(field.getType(), value.getClass())) {
            System.out.println(String.format("Field type: %s is not value type: %s", field.getType().getName(), value.getClass().getName()));
            return ;
        }
        String setMethodName = "set" + StringUtils.upperFirstChar(field.getName());
        try {
            Method setMethod = entity.getClass().getMethod(setMethodName, field.getType());
            setMethod.invoke(entity, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(String.format("set %s into %s error", value.getClass().getName(), entity.getClass().getName()));
            e.printStackTrace();
        }
    }

}
