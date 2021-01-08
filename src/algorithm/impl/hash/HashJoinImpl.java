package algorithm.impl.hash;

import algorithm.JoinOperation;

import config.Config;
import table.Table;
import util.ClassUtils;
import util.FileUtils;

import java.io.BufferedWriter;
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
    public <T, U, K> List<T> joinForMemory(
            List<U> leftList,
            List<K> rightList,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz) {
        //获取FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        //是否存在join的条件
        if(!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)){
            System.out.println("Join error: no such property");
            return null;
        }
        List<T> entities = null;
        Map<Integer,HashNode> tableHashMap = null;
        //判断左表右表的大小
        //左表大小
        int leftFieldNum = leftFieldMap.keySet().size();
        int leftTableColNum = leftList.size();
        int leftTableSize = leftFieldNum * leftTableColNum;
        //右表大小
        int rightFieldNum = rightFieldMap.keySet().size();
        int rightTableColNum = rightList.size();
        int rightTableSize = rightFieldNum * rightTableColNum;
        //对小表进行hash处理，大表进行join匹配
        if(leftTableSize>=rightTableSize){
            tableHashMap = doHash(rightList,rightFieldMap,rightProperty);
            entities = doJoin(leftList,leftFieldMap,leftProperty,tableHashMap,rightFieldMap,rightProperty,responseClazz);
        }else{
            tableHashMap = doHash(leftList,leftFieldMap,leftProperty);
            entities = doJoin(rightList,rightFieldMap,rightProperty,tableHashMap,leftFieldMap,leftProperty,responseClazz);
        }
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }

    @Override
    public <T, U, K> void joinAndWrite(Table<U> leftTable, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        //获取FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        //是否存在join的条件
        if(!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)){
            System.out.println("Join error: no such property");
            return;
        }
        Map<Integer,HashNode> tableHashMap;
        //判断左表右表的大小
        //对小表进行hash处理，大表进行join匹配
        if(leftTable.getTotalSpace()>=rightTable.getTotalSpace()){
            tableHashMap = doHash(rightTable,rightFieldMap,rightProperty);
            doJoinAndWrite(leftTable,leftFieldMap,leftProperty,tableHashMap,rightFieldMap,rightProperty,responseClazz);
        }else{
            tableHashMap = doHash(leftTable,leftFieldMap,leftProperty);
            doJoinAndWrite(rightTable,rightFieldMap,rightProperty,tableHashMap,leftFieldMap,leftProperty,responseClazz);
        }
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
    }

    @Override
    public <T, U, K> List<T> join(Table<U> leftTable, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        //获取FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        //是否存在join的条件
        if(!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)){
            System.out.println("Join error: no such property");
            return null;
        }
        List<T> entities = null;
        Map<Integer,HashNode> tableHashMap = null;
        //判断左表右表的大小
        //对小表进行hash处理，大表进行join匹配
        if(leftTable.getTotalSpace()>=rightTable.getTotalSpace()){
            tableHashMap = doHash(rightTable,rightFieldMap,rightProperty);
            entities = doJoin(leftTable,leftFieldMap,leftProperty,tableHashMap,rightFieldMap,rightProperty,responseClazz);
        }else{
            tableHashMap = doHash(leftTable,leftFieldMap,leftProperty);
            entities = doJoin(rightTable,rightFieldMap,rightProperty,tableHashMap,leftFieldMap,leftProperty,responseClazz);
        }
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }

    @Override
    public <T, U, K> List<T> join(List<U> leftList, Table<K> rightTable, String leftProperty, String rightProperty, Class<T> responseClazz, Class<U> leftTableClazz, Class<K> rightTableClazz) {
        //获取FieldName 与 Field 的键值对
        Map<String, Field> leftFieldMap = ClassUtils.getFieldMapFor(leftTableClazz);
        Map<String, Field> rightFieldMap = ClassUtils.getFieldMapFor(rightTableClazz);
        //是否存在join的条件
        if(!leftFieldMap.containsKey(leftProperty) || !rightFieldMap.containsKey(rightProperty)){
            System.out.println("Join error: no such property");
            return null;
        }
        List<T> entities = null;
        Map<Integer,HashNode> tableHashMap = null;
        //判断左表右表的大小
        //对小表进行hash处理，大表进行join匹配
        tableHashMap = doHash(leftList,leftFieldMap,leftProperty);
        entities = doJoin(rightTable,rightFieldMap,rightProperty,tableHashMap,leftFieldMap,leftProperty,responseClazz);
        System.out.println(leftTableClazz.getName() + " 与 " + rightTableClazz.getName() + " Join 结束");
        return entities;
    }

    private <T> Map<Integer, HashNode> doHash(
            Table<T> smallTable,
            Map<String, Field> smallFieldMap,
            String smallProperty) {
        //对小表做hash处理
        HashMap<Integer, HashNode> tableHashMap = new HashMap<>();
        smallTable.startRead();
        //遍历小表
        for (T smallData = smallTable.readRowOnlyOne(); smallData != null; smallData = smallTable.readRowOnlyOne()) {
            //获取小值
            final Object smallDataValue = ClassUtils.getValueOfField(smallFieldMap.get(smallProperty), smallData);
            //获取小值的hash值
            int smallDataHashCode = smallDataValue.toString().hashCode();
            //判断当前hash值是否存在，不存在则先插入一个头节点，便于join的优化操作
            if (!tableHashMap.containsKey(smallDataHashCode)) {
                //新建头节点并插入链表
                HashNode headNode = new HashNode();
                tableHashMap.put(smallDataHashCode, headNode);
                //新建当前数据节点，并插入链表
                HashNode dataNode = new HashNode();
                dataNode.setData(smallData);
                dataNode.setNext(null);
                headNode.setNext(dataNode);
            } else {
                //获取头节点
                HashNode headNode = tableHashMap.get(smallDataHashCode);
                //新建当前数据节点并插入，头插法，方便，不用记录当前链表的最后一个节点
                HashNode dataNode = new HashNode();
                dataNode.setData(smallData);
                dataNode.setNext(headNode.getNext());
                headNode.setNext(dataNode);
            }
        }
        smallTable.endRead();
        return tableHashMap;
    }

    //hash处理函数
    private <T> Map<Integer, HashNode> doHash(
            List<T> smallList,
            Map<String, Field> smallFieldMap,
            String smallProperty) {
        //对小表做hash处理
        HashMap<Integer, HashNode> tableHashMap = new HashMap<>();
        //遍历小表
        for (T smallData : smallList) {
            //获取小值
            final Object smallDataValue = ClassUtils.getValueOfField(smallFieldMap.get(smallProperty), smallData);
            //获取小值的hash值
            int smallDataHashCode = smallDataValue.toString().hashCode();
            //判断当前hash值是否存在，不存在则先插入一个头节点，便于join的优化操作
            if (!tableHashMap.containsKey(smallDataHashCode)) {
                //新建头节点并插入链表
                HashNode headNode = new HashNode();
                tableHashMap.put(smallDataHashCode, headNode);
                //新建当前数据节点，并插入链表
                HashNode dataNode = new HashNode();
                dataNode.setData(smallData);
                dataNode.setNext(null);
                headNode.setNext(dataNode);
            } else {
                //获取头节点
                HashNode headNode = tableHashMap.get(smallDataHashCode);
                //新建当前数据节点并插入，头插法，方便，不用记录当前链表的最后一个节点
                HashNode dataNode = new HashNode();
                dataNode.setData(smallData);
                dataNode.setNext(headNode.getNext());
                headNode.setNext(dataNode);
            }
        }
        return tableHashMap;
    }

    private <T,U> void doJoinAndWrite(
            Table<U> bigTable,
            Map<String, Field> bigFieldMap,
            String bigProperty,
            Map<Integer, HashNode> tableHashMap,
            Map<String, Field> smallFieldMap,
            String smallProperty,
            Class<T> responseClazz){
        BufferedWriter writer = FileUtils.startWrite(Config.PATH + "/" + responseClazz.getSimpleName() + ".txt");
        if (writer == null) return;
        String[] fieldValues = ClassUtils.getFieldMapFor(responseClazz).keySet().toArray(new String[0]);
        FileUtils.writeHead(writer, fieldValues);
        //遍历大表
        bigTable.startRead();
        for (U bigData = bigTable.readRowOnlyOne(); bigData != null; bigData = bigTable.readRowOnlyOne()){
            // 获取大值
            final Object bigDataValue = ClassUtils.getValueOfField(bigFieldMap.get(bigProperty), bigData);
            //获取大值的hash值
            int bigDataHashCode = bigDataValue.toString().hashCode();
            //判断当前map里是否有对应的hashcode
            if(tableHashMap.containsKey(bigDataHashCode)){
                //根据大值的HashCode获取头节点
                HashNode headNode = tableHashMap.get(bigDataHashCode);
                //遍历链表
                while (headNode.getNext()!=null) {
                    HashNode dataNode = headNode.getNext();
                    // 获取小值
                    final Object smallDataValue = ClassUtils.getValueOfField(smallFieldMap.get(smallProperty), dataNode.getData());
                    //判断 join 条件是否一致
                    if (bigDataValue.toString().equals(smallDataValue.toString())) {
                        T entity = ClassUtils.createEntityFor(responseClazz);
                        // 设置 Join 后的对象的属性值
                        for (Field responseField : responseClazz.getDeclaredFields()) {
                            final String responseFieldName = responseField.getName();
                            if (bigFieldMap.containsKey(responseFieldName)) {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(bigFieldMap.get(responseFieldName), bigData)));
                            } else {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(smallFieldMap.get(responseFieldName), dataNode.getData())));
                            }
                        }
                        FileUtils.writeRow(writer, fieldValues, entity, responseClazz);
                        headNode = dataNode;
                        //break;
                    }else {
                        //join条件不相同的话，将当前节点表示为头节点
                        headNode = dataNode;
                    }
                }
            }
        }
        bigTable.endRead();
        FileUtils.endWrite(writer);
    }


    private <T,U> List<T> doJoin(
            Table<U> bigTable,
            Map<String, Field> bigFieldMap,
            String bigProperty,
            Map<Integer, HashNode> tableHashMap,
            Map<String, Field> smallFieldMap,
            String smallProperty,
            Class<T> responseClazz){
        List<T> entities = new ArrayList<>();
        //遍历大表
        bigTable.startRead();
        for (U bigData = bigTable.readRowOnlyOne(); bigData != null; bigData = bigTable.readRowOnlyOne()){
            // 获取大值
            final Object bigDataValue = ClassUtils.getValueOfField(bigFieldMap.get(bigProperty), bigData);
            //获取大值的hash值
            int bigDataHashCode = bigDataValue.toString().hashCode();
            //判断当前map里是否有对应的hashcode
            if(tableHashMap.containsKey(bigDataHashCode)){
                //根据大值的HashCode获取头节点
                HashNode headNode = tableHashMap.get(bigDataHashCode);
                //遍历链表
                while(headNode.getNext()!=null){
                    HashNode dataNode = headNode.getNext();
                    // 获取小值
                    final Object smallDataValue = ClassUtils.getValueOfField(smallFieldMap.get(smallProperty), dataNode.getData());
                    //判断 join 条件是否一致
                    if (bigDataValue.toString().equals(smallDataValue.toString())) {
                        T entity = ClassUtils.createEntityFor(responseClazz);
                        // 设置 Join 后的对象的属性值
                        for (Field responseField : responseClazz.getDeclaredFields()) {
                            final String responseFieldName = responseField.getName();
                            if (bigFieldMap.containsKey(responseFieldName)) {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(bigFieldMap.get(responseFieldName), bigData)));
                            } else {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(smallFieldMap.get(responseFieldName), dataNode.getData())));
                            }
                        }
                        entities.add(entity);
                        headNode = dataNode;
                        //break;
                    }else {
                        //join条件不相同的话，将当前节点表示为头节点
                        headNode = dataNode;
                    }
                }
            }
        }
        bigTable.endRead();
        return entities;
    }

    //join处理函数
    private <T,U> List<T> doJoin(
            List<U> bigList,
            Map<String, Field> bigFieldMap,
            String bigProperty,
            Map<Integer, HashNode> tableHashMap,
            Map<String, Field> smallFieldMap,
            String smallProperty,
            Class<T> responseClazz){
        List<T> entities = new ArrayList<>();
        //遍历大表
        for (U bigData: bigList){
            // 获取大值
            final Object bigDataValue = ClassUtils.getValueOfField(bigFieldMap.get(bigProperty), bigData);
            //获取大值的hash值
            int bigDataHashCode = bigDataValue.toString().hashCode();
            //判断当前map里是否有对应的hashcode
            if(tableHashMap.containsKey(bigDataHashCode)){
                //根据大值的HashCode获取头节点
                HashNode headNode = tableHashMap.get(bigDataHashCode);
                //遍历链表
                while(headNode.getNext()!=null){
                    HashNode dataNode = headNode.getNext();
                    // 获取小值
                    final Object smallDataValue = ClassUtils.getValueOfField(smallFieldMap.get(smallProperty), dataNode.getData());
                    //判断 join 条件是否一致
                    if (bigDataValue.toString().equals(smallDataValue.toString())) {
                        T entity = ClassUtils.createEntityFor(responseClazz);
                        // 设置 Join 后的对象的属性值
                        for (Field responseField : responseClazz.getDeclaredFields()) {
                            final String responseFieldName = responseField.getName();
                            if (bigFieldMap.containsKey(responseFieldName)) {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(bigFieldMap.get(responseFieldName), bigData)));
                            } else {
                                ClassUtils.setValueOfFieldFor(
                                        entity,
                                        responseField,
                                        Objects.requireNonNull(ClassUtils.getValueOfField(smallFieldMap.get(responseFieldName), dataNode.getData())));
                            }
                        }
                        entities.add(entity);
                        headNode = dataNode;
                        //break;
                    }else {
                        //join条件不相同的话，将当前节点表示为头节点
                        headNode = dataNode;
                    }
                }
            }
        }
        return entities;
    }

}
