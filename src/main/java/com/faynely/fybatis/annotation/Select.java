package com.faynely.fybatis.annotation;

import java.lang.annotation.*;

/**
 * 数据库 Select 操作注解
 * value 填具体要执行的 SQL
 * @author NickFayne 2018-05-04 14:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value();
}