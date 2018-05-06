package com.faynely.fybatis.binding;

import com.faynely.fybatis.configuration.Configuration;
import com.faynely.fybatis.sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Mapper Proxy
 * @author NickFayne 2018-05-04 13:38
 */
public class MapperProxy<T> implements InvocationHandler {

    private SqlSession sqlSession;

    /**
     * mapper 接口的 Class 信息
     */
    private Class<T> mapperInterfaces;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterfaces) {
        this.sqlSession = sqlSession;
        this.mapperInterfaces = mapperInterfaces;
    }

    /**
     * 代理类回调方法
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getDeclaringClass().getName();
        String simpleMethodName = method.getName();
        //如果是 Mapper 接口中的方法，那就直接走数据库操作
        if(!methodName.equals(Object.class.getName())){
            Map<String, MapperData> mapperMethodMap = Configuration.getMapperMethodMap();
            //获取 mapperMethodMap 的key
            String key = mapperInterfaces.getName() + "." + simpleMethodName;
            //获取 MapperData 对象
            MapperData mapperData = mapperMethodMap.get(key);

            String sql = mapperData.getSql();
            Class clazz = mapperData.getReturnType();
            return sqlSession.selectOne(sql, args, clazz);
        }
        //处理 Object 类中以及其父类中的方法，按正常的流程走
        return method.invoke(this, args);
    }
}