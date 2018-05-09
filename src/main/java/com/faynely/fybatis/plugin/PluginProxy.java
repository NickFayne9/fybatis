package com.faynely.fybatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * 插件代理类
 * @author NickFayne 2018-05-09 14:04
 */
public class PluginProxy implements InvocationHandler {

    private Plugin interceptor;

    private Object target;

    public PluginProxy(Object target, Plugin interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Plugin plugin){
        Class clazz = target.getClass();

        //目前只针对 query 方法进行插件处理
        Set<Class<?>> interfacesSet = new HashSet<>();
        Class[] interfaces = clazz.getInterfaces();
        for(Class tmpClazz : interfaces){
            //if("query".equals(tmpClazz.getName())){
                interfacesSet.add(tmpClazz);
            //}
        }
        Class[] newInterfaces = interfacesSet.toArray(new Class[interfacesSet.size()]);
        return Proxy.newProxyInstance(clazz.getClassLoader(), newInterfaces, new PluginProxy(target, plugin));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("query".equals(method.getName())){
            return interceptor.intercept(new Invocation(target, method, args));
        }
        return method.invoke(target, args);
    }
}
