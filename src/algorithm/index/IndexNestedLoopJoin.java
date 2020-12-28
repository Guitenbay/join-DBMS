package algorithm.index;
import algorithm.JoinOperation;
import util.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IndexNestedLoopJoin implements  JoinOperation{
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
    public <T, U, K> List<T> join(
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
        BPlusTree<K, String> b = new BPlusTree<>(4);
        for (K right:rightList) {
            Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            b.insert(right,rightValue.toString());
        }

        //创建连接
        for (U left:leftList) {
            // 获取左值
            final  Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            // 索引右值
            final  K right = b.find(leftValue.toString());
//            System.out.println(right);
            assert leftValue != null;
            if(right==null)
                continue;
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
        return entities;
    }
}
