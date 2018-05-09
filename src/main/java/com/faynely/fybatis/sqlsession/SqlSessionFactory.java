package com.faynely.fybatis.sqlsession;

import com.faynely.fybatis.configuration.Configuration;
import com.faynely.fybatis.executor.ExecutorFactory;

/**
 * SqlSession 工厂类
 * @author NickFayne 2018-05-06 22:27
 */
public class SqlSessionFactory {

    public static SqlSession newInstance(){
        Configuration configuration = new Configuration();

        return new SqlSession(configuration.newExecutor(ExecutorFactory.ExecutorType.CACHE), new Configuration());
    }
}
