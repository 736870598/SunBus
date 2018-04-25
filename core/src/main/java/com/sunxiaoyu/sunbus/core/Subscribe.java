package com.sunxiaoyu.sunbus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * --  订阅事件的注解器
 * <p>
 * Created by sunxy on 2018/4/24 0024.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    String[] value();
}
