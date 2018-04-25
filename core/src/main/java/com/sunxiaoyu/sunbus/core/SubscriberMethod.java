package com.sunxiaoyu.sunbus.core;

import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/4/24 0024.
 */

public class SubscriberMethod {

    private String label;
    private Method method;
    private Class<?>[] parameterTypes;

    public SubscriberMethod(String label, Method method, Class<?>[] parameterTypes) {
        this.label = label;
        this.method = method;
        this.parameterTypes = parameterTypes;
    }

    public String getLabel() {
        return label;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
}
