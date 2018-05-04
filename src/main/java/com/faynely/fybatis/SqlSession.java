package com.faynely.fybatis;

import com.faynely.fybatis.executor.Executor;
import com.faynely.fybatis.executor.SimpleExecutor;
import com.faynely.fybatis.executor.parameter.ParameterHandler;
import com.faynely.fybatis.executor.result.ResultHandler;
import com.faynely.fybatis.executor.statement.StatementHandler;

/**
 * Sql 会话类（提供给用户使用）
 * @author Faynely 2018-05-03 22:47
 */
public class SqlSession {

    private static Executor executor = new SimpleExecutor<Student>(new ParameterHandler(), new StatementHandler(), new ResultHandler());

    /**
     * 获取操作 Student 表的 Mapper
     * @return
     */
    public static IStudentMapper getStudentMapper(){
        return new StudentMapperImpl(executor);
    }
}
