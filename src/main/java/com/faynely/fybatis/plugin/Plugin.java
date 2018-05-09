package com.faynely.fybatis.plugin;


/**
 * 插件接口
 * @author NickFayne 2018-05-09 10:16
 */
public interface Plugin {
    Object intercept(Invocation invocation) throws Throwable;

    Object wrap(Object target);
}