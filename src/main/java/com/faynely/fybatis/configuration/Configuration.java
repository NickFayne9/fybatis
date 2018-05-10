package com.faynely.fybatis.configuration;

import com.faynely.fybatis.annotation.FybatisPlugin;
import com.faynely.fybatis.annotation.Repository;
import com.faynely.fybatis.annotation.Select;
import com.faynely.fybatis.binding.MapperData;
import com.faynely.fybatis.binding.MapperProxy;
import com.faynely.fybatis.executor.Executor;
import com.faynely.fybatis.executor.ExecutorFactory;
import com.faynely.fybatis.plugin.Plugin;
import com.faynely.fybatis.sqlsession.SqlSession;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.*;

/**
 * 配置类
 * @author NickFayne 2018-05-04 13:38
 */
public class Configuration {

    private final static List<String> classNameList = new ArrayList<String>();

    /**
     * 存放 Mapper 接口中，接口全路径名与方法名与注解中的 SQL 语句的映射关系
     * 比如：{{"com.faynely.fybatis.IStudentMapper.selectStuById" -> {"select * from student where id = %d", "com.faynely.fybatis.Student"} }
     *      {"select * from student where id = %d", "com.faynely.fybatis.Student"} 这个抽象为 MapperData 对象
     *  也就是 key 为接口全限定名 + 方法名，value 为 MapperData 对象，MapperData 对象中保存了 SQL 和 ReturnType
     */
    private final static Map<String, MapperData> mapperMethodMap = new HashMap<>();

    /**
     * 获取 plugin 类的实例
     */
    private final static List<Plugin> pluginList = new ArrayList<>();

    /**
     * 初始化方法
     * 1. 获取被 @Repository 修饰的类
     * 2. 获取被 @Select 修饰的方法，取出 SQL 语句
     * 3. 组装成 Map，存至 mapperMethodMap
     * 4. 将 mapperProxy 赋值
     */
    static {
        try {
            scanBase("com.faynely.fybatis");
            registerMapperInterfaces();
            registerPlugin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册用户所有自定义的 Mapper 接口
     */
    private static void registerMapperInterfaces(){
        if(classNameList.size() == 0){
            return ;
        }

        for(String className : classNameList){
            try {
                Class interfaceClazz = Class.forName(className);
                if(interfaceClazz.isAnnotationPresent(Repository.class)){
                    Method[] methods = interfaceClazz.getDeclaredMethods();
                    for(Method perMethod : methods){
                        String methodName = perMethod.getName();
                        //组装 key
                        String key = className + "." + methodName;
                        //获取 SQL
                        String sql = perMethod.getAnnotation(Select.class).value();
                        //获取 returnType
                        Class returnType = perMethod.getReturnType();
                        //组装 value
                        MapperData mapperData = new MapperData(sql, returnType);
                        //保存至 Map 中
                        mapperMethodMap.put(key, mapperData);
                    }

                }else{
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册用户所有自定义的 PluginProxy
     */
    private static void registerPlugin(){
        if(classNameList.size() == 0){
            return ;
        }

        for(String className : classNameList){
            try{
                Class clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(FybatisPlugin.class)){
                    pluginList.add((Plugin) clazz.newInstance());
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
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

    public static List<Plugin> getPluginSet() {
        return pluginList;
    }


    public static Map<String, MapperData> getMapperMethodMap() {
        return mapperMethodMap;
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
                new MapperProxy(sqlSession, clazz));
    }

    /**
     * 将 FybatisPlugin 作为 Executor 的代理
     * @param executorType
     * @return
     */
    public Executor newExecutor(ExecutorFactory.ExecutorType executorType){
        Executor executor = ExecutorFactory.newInstance(executorType.name());

        if(pluginList.isEmpty()){

        }else{
            for(Plugin plugin : pluginList){
                executor = (Executor) plugin.wrap(executor);
            }
        }
        return executor;
    }
}
