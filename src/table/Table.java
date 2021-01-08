package table;

import org.apache.commons.beanutils.ConvertUtils;
import util.ClassUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table<T> {
    private static String PATH = ".\\dbs";
    private String name;
    private Class<T> tableClazz;
    private BufferedReader bufferedReader;
    private String[] fieldValues = new String[0];
    private Map<String, Field> fieldMap;

    public Table(String name, Class<T> tableClazz) {
        this.name = name;
        this.tableClazz = tableClazz;
        this.fieldMap = ClassUtils.getFieldMapFor(tableClazz);
    }

    public boolean startRead() {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(PATH + "/" + name + ".txt"));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean endRead() {
        try {
            this.bufferedReader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getTotalSpace() {
        return new File(PATH + "/" + name + ".txt").getTotalSpace();
    }

    public List<T> readRow() {
        List<T> results = new ArrayList<>();
        try {
            String line = null;
            if (this.fieldValues.length == 0) {
                // 读到末尾是 NULL
                if (null != (line = this.bufferedReader.readLine())) {
                    this.fieldValues = line.split(",");
                }
            }
            // 读到末尾是 NULL
            while (null != (line = this.bufferedReader.readLine())) {
                T entity = ClassUtils.createEntityFor(this.tableClazz);
                String[] values = line.split(",");
                for (int i=0; i < this.fieldValues.length; i++) {
                    if (fieldMap.containsKey(fieldValues[i])) {
                        final Field field = fieldMap.get(fieldValues[i]);
                        ClassUtils.setValueOfFieldFor(entity, field, ConvertUtils.convert(values[i], field.getType()));
                    }
                }
                results.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public T readRowOnlyOne() {
        T result = null;
        try {
            String line;
            if (this.fieldValues.length == 0) {
                // 读到末尾是 NULL
                if (null != (line = this.bufferedReader.readLine())) {
                    this.fieldValues = line.split(",");
                } else {
                    return null;
                }
            }
            // 读到末尾是 NULL
            line = this.bufferedReader.readLine();
            if (line == null) return null;
            result = ClassUtils.createEntityFor(this.tableClazz);
            String[] values = line.split(",");
            for (int i=0; i < this.fieldValues.length; i++) {
                if (fieldMap.containsKey(fieldValues[i])) {
                    final Field field = fieldMap.get(fieldValues[i]);
                    ClassUtils.setValueOfFieldFor(result, field, ConvertUtils.convert(values[i], field.getType()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<T> readRowLimit(int num) {
        List<T> results = null;
        try {
            String line;
            if (this.fieldValues.length == 0) {
                // 读到末尾是 NULL
                if (null != (line = this.bufferedReader.readLine())) {
                    this.fieldValues = line.split(",");
                } else {
                    return null;
                }
            }
            // 读到末尾是 NULL
            for (int lineNum = 0; lineNum < num; lineNum++) {
                line = this.bufferedReader.readLine();
                if (line == null) break;
                T entity = ClassUtils.createEntityFor(this.tableClazz);
                String[] values = line.split(",");
                for (int i=0; i < this.fieldValues.length; i++) {
                    if (fieldMap.containsKey(fieldValues[i])) {
                        final Field field = fieldMap.get(fieldValues[i]);
                        ClassUtils.setValueOfFieldFor(entity, field, ConvertUtils.convert(values[i], field.getType()));
                    }
                }
                if (results == null) {
                    results = new ArrayList<>();
                }
                results.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
