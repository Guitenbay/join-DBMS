package algorithm.impl.hash;

import algorithm.JoinOperation;

import util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;

public class HashJoinImpl implements JoinOperation {
    /**
     * 简单测试 JOIN 函数
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
    public <T, U, K> List<T> join(
            List<U> leftList,
            List<K> rightList,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz) {
//        // 获取 FieldName 与 Field 的键值对
//        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
//        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
//        // 是否存在 join 的条件
//        if (!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)) {
//            System.out.println("Join error: no such property");
//            return null;
//        }

        //获取FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        //是否存在join的条件
        if(!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)){
            System.out.println("Join error: no such property");
            return null;
        }


        List<T> entities = new ArrayList<>();

        //对右表做hash处理
        HashMap<Integer, HashNode> rightTableHashMap = new HashMap<>();
        //遍历右表
        for(K right : rightList){
            //获取右值
            final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), right);
            //获取右值的hash值
            int rightHashCode = rightValue.toString().hashCode();
            //判断当前hash值是否存在，不存在则先插入一个头节点，便于join的优化操作
            if(!rightTableHashMap.containsKey(rightHashCode)){
                //新建头节点并插入链表
                HashNode headNode = new HashNode();
                rightTableHashMap.put(rightHashCode,headNode);
                //新建当前数据节点，并插入链表
                HashNode dataNode = new HashNode();
                dataNode.setData(right);
                dataNode.setNext(null);
                headNode.setNext(dataNode);
            }else {
                //获取头节点
                HashNode headNode = rightTableHashMap.get(rightHashCode);
                //新建当前数据节点并插入，头插法，方便，不用记录当前链表的最后一个节点
                HashNode dataNode = new HashNode();
                dataNode.setData(right);
                dataNode.setNext(headNode.getNext());
                headNode.setNext(dataNode);
            }
        }

        //join过程

        //遍历左表
        for (U left: leftList){
            // 获取左值
            final Object leftValue = ClassUtils.getValueOfField(leftFieldMap.get(leftProperty), left);
            //获取左值的hash值
            int leftHashCode = leftValue.toString().hashCode();
            //判断当前map里是否有对应的hashcode
            if(rightTableHashMap.containsKey(leftHashCode)){
                //根据左值的HashCode获取头节点
                HashNode headNode = rightTableHashMap.get(leftHashCode);
                //遍历链表
                while(headNode.getNext()!=null){
                    HashNode dataNode = headNode.getNext();
                    // 获取右值
                    final Object rightValue = ClassUtils.getValueOfField(rightFieldMap.get(rightProperty), dataNode.getData());
                    //判断 join 条件是否一致
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
                                        Objects.requireNonNull(ClassUtils.getValueOfField(rightFieldMap.get(responseFieldName), dataNode.getData())));
                            }
                        }
                        //join条件相同的话，将当前节点从链表中删除，对冲突节点的的查询进一步优化
                        headNode.setNext(dataNode.getNext());
                        entities.add(entity);
                        //？？？？？
                        //break;
                    }else {
                        //join条件不相同的话，讲当前节点表示为头节点
                        headNode = dataNode;
                    }
                }
            }
        }
            return entities;
    }
}
