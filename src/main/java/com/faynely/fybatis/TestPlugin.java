package com.faynely.fybatis;

import com.faynely.fybatis.annotation.Plugin;
import com.faynely.fybatis.plugin.Invocation;
import com.faynely.fybatis.plugin.PluginProxy;

/**
 * @author NickFayne 2018-05-09 21:55
 */
@Plugin
public class TestPlugin implements com.faynely.fybatis.plugin.Plugin {

    @Override
    public Object intercept(Invocation invocation) throws Throwable{
        System.out.println("我是插件，我来了");
        return invocation.process();
    }

    @Override
    public Object wrap(Object target) {
        return PluginProxy.wrap(target, this);
    }
}
