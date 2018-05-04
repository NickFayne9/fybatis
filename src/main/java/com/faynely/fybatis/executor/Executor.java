package com.faynely.fybatis.executor;

/**
 * SQL 执行器接口
 * @author NickFayne 2018-05-04 10:10
 */
public interface Executor {
    <T> T query(String statement, Object parameter, Class<T> clazz);
}
