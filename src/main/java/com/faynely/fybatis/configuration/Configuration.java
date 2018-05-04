package com.faynely.fybatis.configuration;

import com.faynely.fybatis.annotation.Repository;
import com.faynely.fybatis.annotation.Select;
import com.faynely.fybatis.binding.MapperProxy;
import com.faynely.fybatis.sqlsession.SqlSession;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置类
 * @author NickFayne 2018-05-04 13:38
 */
public class Configuration {


    private static List<String> classNameList = new ArrayList<String>();

    /**
     * 存放 Mapper 接口中，接口全路径名与方法名与注解中的 SQL 语句的映射关系
     * 比如：{"com.faynely,fybatis.IStudentMapper" -> ["selectStuById" -> {"sql", "select * from student where id = %d"},{"parameter", 1}, {"returnType" -> "com.faynely.fybatis.Student"}, ...]}
     */
    private static Map<String, List<Map<String, Map<String, Object>>>> mapperMethodMapList = new HashMap<String, List<Map<String, Map<String, Object>>>>();

    /**
     * 初始化方法
     * 1. 获取被 @Repository 修饰的类
     * 2. 获取被 @Select 修饰的方法，取出 SQL 语句
     * 3. 组装成 Map，存至 mapperMethodMapList
     * 4. 将 mapperProxy 赋值
     */
    static  {
        try {
            scanBase("com.faynely.fybatis");
            filter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤所有类中包含 @Reposity 注解的类和其中的 @Select
     */
    private static void filter(){
        if(classNameList.size() == 0){
            return ;
        }

        for(String className : classNameList){
            try {
                Class interfaceClazz = Class.forName(className);
                if(interfaceClazz.isAnnotationPresent(Repository.class)){
                    Method[] methods = interfaceClazz.getDeclaredMethods();
                    List<Map<String, Map<String, Object>>> methodList = new ArrayList<Map<String, Map<String, Object>>>();

                    for(Method perMethod : methods){
                        Map<String, Map<String, Object>> methodMap = new HashMap<String, Map<String, Object>>();
                        Map<String, Object> objMap = new HashMap<String, Object>();

                        String methodName = perMethod.getName();

                        String sql = perMethod.getAnnotation(Select.class).value();
                        objMap.put("sql", sql);

                        Class returnType = perMethod.getReturnType();
                        objMap.put("returnType", returnType);

                        methodMap.put(methodName, objMap);

                        methodList.add(methodMap);
                    }

                    mapperMethodMapList.put(className, methodList);
                }else{
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定包名下的类名
     */
    private static void scanBase(String basePackages) throws Exception{
        String realPath = basePackages.replace(".", "/");
        URL url = Configuration.class.getClassLoader().getResource("\\" + realPath);
        String path = url.getPath();
        path = path.replace("%5c", "");
        File file = new File(path);
        String[] strFiles = file.list();
        for(String strFile : strFiles){
            File perFile = new File(path + "/" + strFile);
            if(perFile.isDirectory()){
                scanBase(basePackages + "." + perFile.getName());
            }else{
                classNameList.add(basePackages + "." + perFile.getName().replace(".class", ""));
            }
        }
    }

    public static Map<String, List<Map<String, Map<String, Object>>>> getMapperMethodMapList() {
        return mapperMethodMapList;
    }

    public static void setMapperMethodMapList(Map<String, List<Map<String, Map<String, Object>>>> mapperMethodMapListReq) {
        mapperMethodMapList = mapperMethodMapListReq;
    }

    /**
     * 生成动态代理 Mapper 类
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> clazz, SqlSession sqlSession){
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{clazz},
                new MapperProxy(sqlSession));
    }
}
