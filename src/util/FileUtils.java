package util;

import org.apache.commons.beanutils.ConvertUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class FileUtils {
    public static BufferedWriter startWrite(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile(); //如果文件不存在则创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 不追加，直接覆盖
        try {
            return new BufferedWriter(new FileWriter(file, false));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeHead(BufferedWriter writer, String[] fieldValues) {
        writeStr(writer, fieldValues[0]);
        for (int i = 1; i < fieldValues.length; i++) {
            writeStr(writer, "," + fieldValues[i]);
        }
        writeStr(writer, "\n");
    }

    public static <T> void writeRow(BufferedWriter writer, String[] fieldValues, T entity, Class<T> clazz) {
        Map<String, Field> fieldMap = ClassUtils.getFieldMapFor(clazz);
        if (fieldMap.containsKey(fieldValues[0])) {
            final Field field = fieldMap.get(fieldValues[0]);
            writeStr(writer, "," + ClassUtils.getValueOfField(field, entity));
        } else {
            return;
        }
        for (int i=1; i < fieldValues.length; i++) {
            if (fieldMap.containsKey(fieldValues[i])) {
                final Field field = fieldMap.get(fieldValues[i]);
                writeStr(writer, "," + ClassUtils.getValueOfField(field, entity));
            } else {
                return;
            }
        }
    }

    public static void writeStr(BufferedWriter writer, String content) {
        try {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void endWrite(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
