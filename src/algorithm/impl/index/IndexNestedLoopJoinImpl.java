package algorithm.impl.index;
import algorithm.JoinOperation;
import config.Config;
import table.Table;
import util.ClassUtils;
import util.FileUtils;

import java.io.BufferedWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IndexNestedLoopJoinImpl implements  JoinOperation{
    private static int BTreeOrder = 1000;
    /**
     * 简单测试 JOIN 函数
     * @param rightList 左表
     * @param leftList 右表
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

        //创建索引表
        BPlusTree<K, String> b = new BPlusTree<>(BTreeOrder);
        for (K right:rightList) {
            Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            b.insert(right,rightValue.toString());
        }

        //创建连接
        for (U left:leftList) {
            // 获取左值
            final  Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            // 索引右值
            final  K[] value = b.find(leftValue.toString());
            K right=null;
            if (value!=null) {
                for (int i=0;i<value.length;i++){
                    if (value[i]!=null) {
                        right = value[i];
                    }
                    else
                        break;
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
                    assert rightValue!=null;


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

        //创建索引表
        BPlusTree<K, String> b = new BPlusTree<>(BTreeOrder);
        rightTable.startRead();
        for (K right = rightTable.readRowOnlyOne(); right != null; right = rightTable.readRowOnlyOne()) {
            Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            b.insert(right,rightValue.toString());
        }
        rightTable.endRead();

        //创建连接
        leftTable.startRead();
        for (U left = leftTable.readRowOnlyOne(); left != null; left = leftTable.readRowOnlyOne()) {
            // 获取左值
            final  Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            // 索引右值
            final  K[] value = b.find(leftValue.toString());
            K right;
            if (value!=null) {
                for (int i=0;i<value.length;i++){
                    if (value[i]!=null) {
                        right = value[i];
                    }
                    else
                        break;
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
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
        leftTable.endRead();
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        FileUtils.endWrite(writer);
    }

    @Override
    public <T, U, K> List<T> join(Table<U> leftTable, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        // 获取 FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        // 是否存在 join 的条件
        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
            System.out.println("Join error: no such property");
            return null;
        }

        List<T> entities = new ArrayList<>();

        //创建索引表
        BPlusTree<K, String> b = new BPlusTree<>(BTreeOrder);
        rightTable.startRead();
        for (K right = rightTable.readRowOnlyOne(); right != null; right = rightTable.readRowOnlyOne()) {
            Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            assert rightValue != null;
            b.insert(right,rightValue.toString());
        }
        rightTable.endRead();

        //创建连接
        leftTable.startRead();
        for (U left = leftTable.readRowOnlyOne(); left != null; left = leftTable.readRowOnlyOne()) {
            // 获取左值
            final  Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            // 索引右值
            assert leftValue != null;
            final  K[] value = b.find(leftValue.toString());
            K right=null;
            if (value!=null) {
                for (K k : value) {
                    if (k != null) {
                        right = k;
                    } else
                        break;
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
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

        //创建索引表
        BPlusTree<K, String> b = new BPlusTree<>(BTreeOrder);

        rightTable.startRead();
        for (K right = rightTable.readRowOnlyOne(); right != null; right = rightTable.readRowOnlyOne()) {
            Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            assert rightValue != null;
            b.insert(right,rightValue.toString());
        }
        rightTable.endRead();

        //创建连接
        for (U left: leftList) {
            // 获取左值
            final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            // 索引右值
            assert leftValue != null;
            final K[] value = b.find(leftValue.toString());
            K right;
            if (value!=null) {
                for (K k : value) {
                    if (k != null) {
                        right = k;
                    } else
                        break;
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
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
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }
}
