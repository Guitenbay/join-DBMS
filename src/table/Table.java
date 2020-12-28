package table;

import org.apache.commons.beanutils.ConvertUtils;
import util.ClassUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public List<T> readDataLimit(int num) {
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
                results.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

}
