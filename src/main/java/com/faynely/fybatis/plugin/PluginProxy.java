package com.faynely.fybatis.plugin;

import com.faynely.fybatis.annotation.FybatisPlugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 插件代理类
 * @author NickFayne 2018-05-09 14:04
 */
public class PluginProxy implements InvocationHandler {

    private Plugin interceptor;

    private Object target;

    private static Set<String> pluginMethodSet = new HashSet<>();

    public PluginProxy(Object target, Plugin interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Plugin plugin){
        //将插件拦截的方法保存至 Set
        String pluginMethodName = plugin.getClass().getAnnotation(FybatisPlugin.class).methodName();
        pluginMethodSet.add(pluginMethodName);

        //生成代理类
        Class clazz = target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new PluginProxy(target, plugin));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class clazz = method.getDeclaringClass();
        if(pluginMethodSet.contains(method.getName())){
            return interceptor.intercept(new Invocation(target, method, args));
        }
        return method.invoke(target, args);
    }
}
