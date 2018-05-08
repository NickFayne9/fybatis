package com.faynely.fybatis.sqlsession;

import com.faynely.fybatis.configuration.Configuration;
import com.faynely.fybatis.executor.CachingExecutor;
import com.faynely.fybatis.executor.SimpleExecutor;

/**
 * SqlSession 工厂类
 * @author NickFayne 2018-05-06 22:27
 */
public class SqlSessionFactory {

    public static SqlSession newInstance(){
        return new SqlSession(new CachingExecutor(new SimpleExecutor()), new Configuration());
    }
}
