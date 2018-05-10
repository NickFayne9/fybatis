package com.faynely.fybatis;

import com.faynely.fybatis.annotation.FybatisPlugin;
import com.faynely.fybatis.plugin.Invocation;
import com.faynely.fybatis.plugin.Plugin;
import com.faynely.fybatis.plugin.PluginProxy;

/**
 * @author NickFayne 2018-05-09 21:55
 */
@FybatisPlugin(methodName = "query")
public class Test1Plugin implements Plugin {

    @Override
    public Object intercept(Invocation invocation) throws Throwable{
        System.out.println("我是插件1，我来了");
        return invocation.process();
    }

    @Override
    public Object wrap(Object target) {
        return PluginProxy.wrap(target, this);
    }
}
