package com.sunxiaoyu.sunbus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * --  订阅事件的注解器
 * <p>
 *     value  订阅的标签 (string数组)
 *     isOne  其他地方通过postWait方式发送事件时，如果接受该事件的方法刚好不在线或者吊桶了onStop（）后，
 *            该事件将会被缓存下来，在接受者上线的时候，从缓存中拿到该事件并传递给接受者，如果接受者设置了
 *            isOne为true的话，那么如果该标签有很多事件的话只会调用接受者一次。  （默认为false）
 *
 *
 * Created by sunxy on 2018/4/24 0024.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    String[] value();
    boolean isOne() default false;
}
