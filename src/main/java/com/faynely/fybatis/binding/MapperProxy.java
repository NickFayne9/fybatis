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
public class MapperProxy implements InvocationHandler {

    private SqlSession sqlSession;

    public MapperProxy(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
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
        //如果是 Mapper 接口中的方法，那就直接走数据库操作
        if(!methodName.equals(Object.class.getName())){
            Map<String, List<Map<String, Map<String, Object>>>> mapperMathedMapList = Configuration.getMapperMethodMapList();
            //TODO: 不应该写死，接下来需要修改成根据配置获取
            String sql = String.valueOf(mapperMathedMapList.get("com.faynely.fybatis.IStudentMapper").get(0).get("selectStuById").get("sql"));
            Class clazz = (Class) mapperMathedMapList.get("com.faynely.fybatis.IStudentMapper").get(0).get("selectStuById").get("returnType");
            return sqlSession.selectOne(sql, args[0], clazz);
        }
        //处理 Object 类中以及其父类中的方法，按正常的流程走
        return method.invoke(this, args);
    }
}
