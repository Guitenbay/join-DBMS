package table;

import config.Config;
import javafx.application.Application;
import org.apache.commons.beanutils.ConvertUtils;
import util.ClassUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table<T> {
    private String name;
    private Class<T> tableClazz;
    private BufferedReader bufferedReader;
    private String[] fieldValues = new String[0];
    private Map<String, Field> fieldMap;
    private int row = 0;

    public Table(String name, Class<T> tableClazz) {
        this.name = name;
        this.tableClazz = tableClazz;
        this.fieldMap = ClassUtils.getFieldMapFor(tableClazz);
    }

    public boolean startRead() {
        try {
            System.out.println("开始读取 " + this.name + " 表");
            this.row = 0;
            this.bufferedReader = new BufferedReader(new FileReader(Config.PATH + "/" + name + ".txt"));
            // 读到末尾是 NULL
            String line = this.bufferedReader.readLine();
            if (null != line) {
                this.fieldValues = line.split(",");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean endRead() {
        try {
            System.out.println(name + "\t表总行数：" + row);
            this.bufferedReader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getTotalSpace() {
        return new File(Config.PATH + "/" + name + ".txt").getTotalSpace();
    }

    public List<T> readRow() {
        List<T> results = null;
        try {
            String line;
            // 读到末尾是 NULL
            while (null != (line = this.bufferedReader.readLine())) {
                T entity = lineToEntity(line);
                if (entity != null) {
                    row++;
//                    System.out.println(row + " " + line);
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.add(entity);
                }
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
            result = lineToEntity(line);
            if (result != null) {
                row++;
//                System.out.println(row + " " + line);
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
                T entity = lineToEntity(line);
                if (entity != null) {
                    row++;
//                    System.out.println(row + " " + line);
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private T lineToEntity(String line) {
        T entity = ClassUtils.createEntityFor(this.tableClazz);
        String[] values = line.split(",");
        if (this.fieldValues.length != values.length) return null;
        for (int i=0; i < this.fieldValues.length; i++) {
            if (fieldMap.containsKey(fieldValues[i])) {
                final Field field = fieldMap.get(fieldValues[i]);
                ClassUtils.setValueOfFieldFor(entity, field, ConvertUtils.convert(values[i], field.getType()));
            }
        }
        return entity;
    }
}
