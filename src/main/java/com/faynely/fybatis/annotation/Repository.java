package com.faynely.fybatis.annotation;

import java.lang.annotation.*;

/**
 * 需要动态代理接口的标识
 * @author NickFayne 2018-05-04 16:34
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {

}
