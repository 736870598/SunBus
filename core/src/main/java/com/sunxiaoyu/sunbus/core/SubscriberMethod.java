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
    private boolean isOne;

    public SubscriberMethod(String label, Method method, Class<?>[] parameterTypes, boolean isOne) {
        this.label = label;
        this.method = method;
        this.isOne = isOne;
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

    public boolean isOne() {
        return isOne;
    }
}
