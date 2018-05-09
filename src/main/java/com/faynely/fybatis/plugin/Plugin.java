package com.faynely.fybatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 插件代理类
 * @author NickFayne 2018-05-09 14:04
 */
public class Plugin implements InvocationHandler {

    private Interceptor interceptor;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
