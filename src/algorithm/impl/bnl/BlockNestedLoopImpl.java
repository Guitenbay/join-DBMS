package algorithm.impl.bnl;

import algorithm.JoinOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import config.Config;
import table.Table;
import util.ClassUtils;
import util.FileUtils;

import java.io.BufferedWriter;
import java.lang.reflect.Field;
import java.util.*;

public class BlockNestedLoopImpl implements JoinOperation {
    /**
     * 简单测试 BNL JOIN 函数
     * @param leftList 左表
     * @param rightList 右表
     * @param leftProperty 左表参与 Join 的条件
     * @param rightProperty 右表参与 Join 的条件
     * @param responseClazz Join 后生成的类
     * @param leftTableClazz 左表项目的类型
     * @param rightTableClazz 右表项目的类型
     * @param <T>
     * @param <U>
     * @param <K>
     * @return
     */
    @Override
    public <T, U, K> List<T> joinForMemory(
            List<U> leftList,
            List<K> rightList,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz){
        // 获取 FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        // 是否存在 join 的条件
        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
            System.out.println("Join error: no such property");
            return null;
        }

        List<T> entities = new ArrayList<>();

        for (U left: leftList) {
            // 获取左值
            final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            for (K right: rightList) {
                // 获取右值
                final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
                assert leftValue != null;
                assert rightValue != null;
                // 判断 join 条件是否一致
                if (leftValue.toString().equals(rightValue.toString())) {
                    T entity = ClassUtils.createEntityFor(responseClazz);
                    // 设置 Join 后的对象的属性值
                    for (Field responseField : responseClazz.getDeclaredFields()) {
                        final String responseFieldName = responseField.getName();
                        if (leftFieldMap.containsKey(responseFieldName)) {
                            ClassUtils.setValueOfFieldFor(
                                    entity,
                                    responseField,
                                    Objects.requireNonNull(ClassUtils.getValueOfField(leftFieldMap.get(responseFieldName), left)));
                        } else {
                            ClassUtils.setValueOfFieldFor(
                                    entity,
                                    responseField,
                                    Objects.requireNonNull(ClassUtils.getValueOfField(rightFieldMap.get(responseFieldName), right)));
                        }
                    }
                    entities.add(entity);
                }
            }
        }
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }

    @Override
    public <T, U, K> void joinAndWrite(Table<U> leftTable, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        // 获取 FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        // 是否存在 join 的条件
        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
            System.out.println("Join error: no such property");
            return;
        }

        BufferedWriter writer = FileUtils.startWrite(Config.PATH + "/" + responseClazz.getSimpleName() + ".txt");
        if (writer == null) return;
        String[] fieldValues = ClassUtils.getFieldMapFor(responseClazz).keySet().toArray(new String[0]);
        FileUtils.writeHead(writer, fieldValues);
        List<U> leftList;
        List<K> rightList;

        leftTable.startRead();
        while (null != (leftList = leftTable.readRowLimit(BLOCK_SIZE))) {
            rightTable.startRead();
            while (null != (rightList = rightTable.readRowLimit(BLOCK_SIZE))) {
                for (U left: leftList) {
                    // 获取左值
                    final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
                    for (K right: rightList) {
                        // 获取右值
                        final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
                        assert leftValue != null;
                        assert rightValue != null;
                        // 判断 join 条件是否一致
                        if (leftValue.toString().equals(rightValue.toString())) {
                            T entity = ClassUtils.createEntityFor(responseClazz);
                            // 设置 Join 后的对象的属性值
                            for (Field responseField : responseClazz.getDeclaredFields()) {
                                final String responseFieldName = responseField.getName();
                                if (leftFieldMap.containsKey(responseFieldName)) {
                                    ClassUtils.setValueOfFieldFor(
                                            entity,
                                            responseField,
                                            Objects.requireNonNull(ClassUtils.getValueOfField(leftFieldMap.get(responseFieldName), left)));
                                } else {
                                    ClassUtils.setValueOfFieldFor(
                                            entity,
                                            responseField,
                                            Objects.requireNonNull(ClassUtils.getValueOfField(rightFieldMap.get(responseFieldName), right)));
                                }
                            }
                            FileUtils.writeRow(writer, fieldValues, entity, responseClazz);
                        }
                    }
                }
            }
            rightTable.endRead();
        }
        leftTable.endRead();
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        FileUtils.endWrite(writer);
    }

    private static final int BLOCK_SIZE = 1000000;

    @Override
    public <T, U, K> List<T> join(
            Table<U> leftTable,
            Table<K> rightTable,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz) {
        // 获取 FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        // 是否存在 join 的条件
        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
            System.out.println("Join error: no such property");
            return null;
        }

        List<T> entities = new ArrayList<>();
        List<U> leftList;
        List<K> rightList;

        leftTable.startRead();
        while (null != (leftList = leftTable.readRowLimit(BLOCK_SIZE))) {
            rightTable.startRead();
            while (null != (rightList = rightTable.readRowLimit(BLOCK_SIZE))) {
                for (U left: leftList) {
                    // 获取左值
                    final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
                    for (K right: rightList) {
                        // 获取右值
                        final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
                        assert leftValue != null;
                        assert rightValue != null;
                        // 判断 join 条件是否一致
                        if (leftValue.toString().equals(rightValue.toString())) {
                            T entity = ClassUtils.createEntityFor(responseClazz);
                            // 设置 Join 后的对象的属性值
                            for (Field responseField : responseClazz.getDeclaredFields()) {
                                final String responseFieldName = responseField.getName();
                                if (leftFieldMap.containsKey(responseFieldName)) {
                                    ClassUtils.setValueOfFieldFor(
                                            entity,
                                            responseField,
                                            Objects.requireNonNull(ClassUtils.getValueOfField(leftFieldMap.get(responseFieldName), left)));
                                } else {
                                    ClassUtils.setValueOfFieldFor(
                                            entity,
                                            responseField,
                                            Objects.requireNonNull(ClassUtils.getValueOfField(rightFieldMap.get(responseFieldName), right)));
                                }
                            }
                            entities.add(entity);
                        }
                    }
                }
            }
            rightTable.endRead();
        }
        leftTable.endRead();
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }

    @Override
    public <T, U, K> List<T> join(List<U> leftList, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        // 获取 FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        // 是否存在 join 的条件
        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
            System.out.println("Join error: no such property");
            return null;
        }
        List<T> entities = new ArrayList<>();
        List<K> rightList;

        rightTable.startRead();
        while (null != (rightList = rightTable.readRowLimit(BLOCK_SIZE))) {
            System.out.println("read right blocksize");
            for (U left: leftList) {
                // 获取左值
                final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
                for (K right: rightList) {
                    // 获取右值
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
                    assert leftValue != null;
                    assert rightValue != null;
                    // 判断 join 条件是否一致
                    if (leftValue.toString().equals(rightValue.toString())) {
                        T entity = ClassUtils.createEntityFor(responseClazz);
                        // 设置 Join 后的对象的属性值
                        for (Field responseField : responseClazz.getDeclaredFields()) {
                            final String responseFieldName = responseField.getName();
                            if (leftFieldMap.containsKey(responseFieldName)) {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(leftFieldMap.get(responseFieldName), left)));
                            } else {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(rightFieldMap.get(responseFieldName), right)));
                            }
                        }
                        entities.add(entity);
                    }
                }
            }
        }
        rightTable.endRead();
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }
}
