package util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtils {

    private static Map<Class, Class> likelyClassMap;

    static {
        likelyClassMap = new HashMap<Class, Class>() {
            {
                put(byte.class, Byte.class);
                put(short.class, Short.class);
                put(int.class, Integer.class);
                put(long.class, Long.class);
                put(float.class, Float.class);
                put(double.class, Double.class);
                put(char.class, Character.class);
                put(boolean.class, Boolean.class);
                put(void.class, Void.class);
            }
        };
    }

    public static boolean compareType(Class clazz1, Class clazz2) {
        if (clazz1 == clazz2) {
            return true;
        } else if (clazz1.isPrimitive()) {
            return likelyClassMap.get(clazz1) == clazz2;
        } else {
            return clazz1 == likelyClassMap.get(clazz2);
        }
    }
}
