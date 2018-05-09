package com.faynely.fybatis.executor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickFayne 2018-05-08 20:59
 */
public class CachingExecutor implements Executor {

    private Map<String, Object> localCache = new HashMap<>();

    private SimpleExecutor delegate;

    public CachingExecutor(SimpleExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T query(String statement, Object[] parameter, Class<T> clazz) {
        //获取缓存
        Object result = localCache.get(statement);

        //缓存没有命中
        if(result == null) {
            //查数据库
            result = delegate.query(statement, parameter, clazz);
            //设置缓存
            localCache.put(statement, result);
        }else{
            System.out.println("******** 缓存命中 ********");
        }

        return (T) result;
    }

}