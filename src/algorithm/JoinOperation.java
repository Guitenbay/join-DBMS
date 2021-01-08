package algorithm;

import response.UserCartAndProductRelationResponse;
import response.UserCartAndProductResponse;
import response.UserCartResponse;
import table.ProductInShoppingCart;
import table.Table;

import java.util.List;

public interface JoinOperation {
    /**
     * 实现 Join 的算法
     * sql: SELECT {responseClazz} FROM {leftList} JOIN {rightList} ON {leftProperty} = {rightProperty}
     * @param leftList 左表
     * @param rightList 右表
     * @param leftProperty 左表参与 Join 的条件
     * @param rightProperty 右表参与 Join 的条件
     * @param responseClazz Join 后生成的类
     * @param leftTableClazz 左表项目的类型
     * @param rightTableClazz 右表项目的类型
     * @param <T> 泛型，指 Join 后生成的类
     * @param <U> 泛型，指 左表项目的类
     * @param <K> 泛型，指 右表项目的类
     * @return Join 后生成的新表
     */
    <T, U, K> List<T> joinForMemory(
            List<U> leftList,
            List<K> rightList,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz);

    /**
     * 实现 Join 的算法
     * sql: SELECT {responseClazz} FROM {leftList} JOIN {rightList} ON {leftProperty} = {rightProperty}
     * @param leftTable 左表
     * @param rightTable 右表
     * @param leftProperty 左表参与 Join 的条件
     * @param rightProperty 右表参与 Join 的条件
     * @param responseClazz Join 后生成的类
     * @param leftTableClazz 左表项目的类型
     * @param rightTableClazz 右表项目的类型
     * @param <T> 泛型，指 Join 后生成的类
     * @param <U> 泛型，指 左表项目的类
     * @param <K> 泛型，指 右表项目的类
     * @return Join 后生成的新表
     */
    <T, U, K> List<T> join(
            Table<U> leftTable,
            Table<K> rightTable,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz);

    /**
     * 实现 Join 的算法
     * sql: SELECT {responseClazz} FROM {leftList} JOIN {rightList} ON {leftProperty} = {rightProperty}
     * @param leftList 左表
     * @param rightTable 右表
     * @param leftProperty 左表参与 Join 的条件
     * @param rightProperty 右表参与 Join 的条件
     * @param responseClazz Join 后生成的类
     * @param leftTableClazz 左表项目的类型
     * @param rightTableClazz 右表项目的类型
     * @param <T> 泛型，指 Join 后生成的类
     * @param <U> 泛型，指 左表项目的类
     * @param <K> 泛型，指 右表项目的类
     * @return Join 后生成的新表
     */
    <T, U, K> List<T> join(
            List<U> leftList,
            Table<K> rightTable,
            String leftProperty,
            String rightProperty,
            Class<T> responseClazz,
            Class<U> leftTableClazz,
            Class<K> rightTableClazz);
}
