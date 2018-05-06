package com.faynely.fybatis.sqlsession;

import com.faynely.fybatis.executor.Executor;
import com.faynely.fybatis.configuration.Configuration;

/**
 * Sql 会话类（提供给用户使用）
 * @author Faynely 2018-05-03 22:47
 */
public class SqlSession {

    private Executor executor;

    private Configuration configuration;

    public SqlSession(Executor executor, Configuration configuration) {
        this.executor = executor;
        this.configuration = configuration;
    }

    /**
     * 获取 Mapper 接口动态代理后实现类（不清楚动态的代理的同学也可以参考我的动态代理的项目学习）
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> clazz){
        return configuration.getMapper(clazz, this);
    }

    /**
     * 查询一条数据
     * @param statement
     * @param parameter
     * @param clazz
     * @param <E>
     * @return
     */
    public <E> E selectOne(String statement, Object[] parameter, Class<E> clazz){
        return executor.query(statement, parameter, clazz);
    }
}
