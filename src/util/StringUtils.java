package util;

public class StringUtils {

    public static String upperFirstChar(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }
}
